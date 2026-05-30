import "./lib/error-capture";

import { consumeLastCapturedError } from "./lib/error-capture";
import { renderErrorPage } from "./lib/error-page";
import { handleAiApiRequest } from "./lib/ai/server-api";

function readEnvString(env: unknown, key: string): string | undefined {
  const envRecord = (env ?? {}) as Record<string, unknown>;
  const workerValue = envRecord[key];
  if (typeof workerValue === "string" && workerValue.trim()) return workerValue;

  const nodeValue = typeof process !== "undefined" ? process.env[key] : undefined;
  if (typeof nodeValue === "string" && nodeValue.trim()) return nodeValue;

  const viteValue = (import.meta as unknown as { env?: Record<string, unknown> }).env?.[key];
  if (typeof viteValue === "string" && viteValue.trim()) return viteValue;

  const vitePrefixed = (import.meta as unknown as { env?: Record<string, unknown> }).env?.[`VITE_${key}`];
  if (typeof vitePrefixed === "string" && vitePrefixed.trim()) return vitePrefixed;

  return undefined;
}

function resolveBaseUrl(env: unknown, key: string, fallback: string): string {
  const raw = readEnvString(env, key);
  const value = (raw ?? fallback).trim();
  return value.endsWith("/") ? value.replace(/\/+$/, "") : value;
}

function selectBackendBaseUrl(env: unknown, pathname: string): string | null {
  if (pathname.startsWith("/api/auth")) {
    return resolveBaseUrl(env, "AUTH_SERVICE_URL", "http://localhost:8081");
  }

  if (pathname.startsWith("/api/analytics") || pathname.startsWith("/api/internal/analytics")) {
    return resolveBaseUrl(env, "ANALYTICS_SERVICE_URL", "http://localhost:8085");
  }

  if (pathname.startsWith("/api/notifications") || pathname.startsWith("/api/internal/notifications")) {
    return resolveBaseUrl(env, "NOTIFICATION_SERVICE_URL", "http://localhost:8084");
  }

  if (pathname.startsWith("/api/public/platform") || pathname.startsWith("/api/admin/platform")) {
    return resolveBaseUrl(env, "PLATFORM_SERVICE_URL", "http://localhost:8087");
  }

  if (
    pathname.startsWith("/api/teacher/quizzes") ||
    pathname.startsWith("/api/learner/quizzes") ||
    pathname.startsWith("/api/admin/quizzes")
  ) {
    return resolveBaseUrl(env, "QUIZ_SERVICE_URL", "http://localhost:8083");
  }

  if (pathname.startsWith("/api/teacher/courses")) {
    return resolveBaseUrl(env, "COURSE_SERVICE_URL", "http://localhost:8082");
  }

  // Course service owns the remaining role-based course APIs.
  if (
    pathname.startsWith("/api/public") ||
    pathname.startsWith("/api/learner") ||
    pathname.startsWith("/api/teacher") ||
    pathname.startsWith("/api/admin")
  ) {
    return resolveBaseUrl(env, "COURSE_SERVICE_URL", "http://localhost:8082");
  }

  return null;
}

async function handleBackendApiProxy(request: Request, env: unknown): Promise<Response | null> {
  const url = new URL(request.url);

  // Keep the frontend's AI API routes handled locally.
  if (url.pathname.startsWith("/api/ai/")) return null;
  if (!url.pathname.startsWith("/api/")) return null;

  const baseUrl = selectBackendBaseUrl(env, url.pathname);
  if (!baseUrl) return null;

  const target = new URL(baseUrl);
  target.pathname = url.pathname;
  target.search = url.search;

  // Clone so we can safely read the body if needed.
  const inbound = request.clone();

  // Build outbound headers from inbound while stripping hop-by-hop headers.
  // Some runtimes (notably edge/worker fetch implementations) will throw if
  // you forward certain headers like `content-length`.
  const outboundHeaders = new Headers(inbound.headers);
  for (const header of [
    "connection",
    "keep-alive",
    "proxy-authenticate",
    "proxy-authorization",
    "te",
    "trailer",
    "transfer-encoding",
    "upgrade",
    "host",
    "content-length",
    // Avoid leaking browser-only origin context to backend services.
    // Some Spring CORS configs will reject requests with an Origin header.
    "origin",
    "referer",
  ]) {
    outboundHeaders.delete(header);
  }

  // Dev-only convenience: quiz-service (and other services) accept header-based
  // auth. If the browser doesn't send these headers, allow injecting them from
  // env vars so local development works without a full auth integration.
  const userIdHeader = (outboundHeaders.get("x-user-id") ?? "").trim();
  const userRoleHeader = (outboundHeaders.get("x-user-role") ?? "").trim();

  const hasUserId = Boolean(userIdHeader);
  const hasUserRole = Boolean(userRoleHeader);

  if (!hasUserId) {
    const devUserId = readEnvString(env, "DEV_USER_ID");
    if (devUserId) outboundHeaders.set("X-User-Id", devUserId);
  }

  if (!hasUserRole) {
    const devUserRole = readEnvString(env, "DEV_USER_ROLE");
    if (devUserRole) outboundHeaders.set("X-User-Role", devUserRole);
  }

  const method = request.method.toUpperCase();
  const init: RequestInit = {
    method,
    headers: outboundHeaders,
  };

  if (method !== "GET" && method !== "HEAD") {
    // Buffer the body to avoid issues with streaming bodies across runtimes.
    init.body = await inbound.arrayBuffer();
  }

  return fetch(new Request(target.toString(), init));
}

type ServerEntry = {
  fetch: (request: Request, env: unknown, ctx: unknown) => Promise<Response> | Response;
};

let serverEntryPromise: Promise<ServerEntry> | undefined;

async function getServerEntry(): Promise<ServerEntry> {
  if (!serverEntryPromise) {
    serverEntryPromise = import("@tanstack/react-start/server-entry").then(
      (m) => ((m as { default?: ServerEntry }).default ?? (m as unknown as ServerEntry)),
    );
  }
  return serverEntryPromise;
}

function brandedErrorResponse(): Response {
  return new Response(renderErrorPage(), {
    status: 500,
    headers: { "content-type": "text/html; charset=utf-8" },
  });
}

function isCatastrophicSsrErrorBody(body: string, responseStatus: number): boolean {
  let payload: unknown;
  try {
    payload = JSON.parse(body);
  } catch {
    return false;
  }

  if (!payload || Array.isArray(payload) || typeof payload !== "object") {
    return false;
  }

  const fields = payload as Record<string, unknown>;
  const expectedKeys = new Set(["message", "status", "unhandled"]);
  if (!Object.keys(fields).every((key) => expectedKeys.has(key))) {
    return false;
  }

  return (
    fields.unhandled === true &&
    fields.message === "HTTPError" &&
    (fields.status === undefined || fields.status === responseStatus)
  );
}

// h3 swallows in-handler throws into a normal 500 Response with body
// {"unhandled":true,"message":"HTTPError"} — try/catch alone never fires for those.
async function normalizeCatastrophicSsrResponse(response: Response): Promise<Response> {
  if (response.status < 500) return response;
  const contentType = response.headers.get("content-type") ?? "";
  if (!contentType.includes("application/json")) return response;

  const body = await response.clone().text();
  if (!isCatastrophicSsrErrorBody(body, response.status)) {
    return response;
  }

  console.error(consumeLastCapturedError() ?? new Error(`h3 swallowed SSR error: ${body}`));
  return brandedErrorResponse();
}

export default {
  async fetch(request: Request, env: unknown, ctx: unknown) {
    try {
      const backendResponse = await handleBackendApiProxy(request, env);
      if (backendResponse) return backendResponse;

      const aiResponse = await handleAiApiRequest(request, env);
      if (aiResponse) return aiResponse;

      const handler = await getServerEntry();
      const response = await handler.fetch(request, env, ctx);
      return await normalizeCatastrophicSsrResponse(response);
    } catch (error) {
      console.error(error);
      return brandedErrorResponse();
    }
  },
};
