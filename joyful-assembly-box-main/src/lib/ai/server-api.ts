import { z } from "zod";

const QuizRemediationSchema = z.object({
  question: z.string().min(1),
  options: z.array(z.string().min(1)).min(2),
  correctIndex: z.number().int().nonnegative(),
  selectedIndex: z.number().int().nonnegative(),
});

const RewriteSchema = z.object({
  text: z.string().min(1),
  audience: z
    .enum(["middle-school", "high-school", "first-year-university", "general"]) // keep simple
    .optional()
    .default("general"),
});

const ChatSchema = z.object({
  messages: z
    .array(
      z.object({
        role: z.enum(["user", "assistant"]),
        content: z.string().min(1),
      }),
    )
    .min(1)
    .max(24),
});

function json(data: unknown, init?: ResponseInit): Response {
  return new Response(JSON.stringify(data), {
    ...init,
    headers: {
      "content-type": "application/json; charset=utf-8",
      ...(init?.headers ?? {}),
    },
  });
}

function getEnvValue(env: unknown, key: string): string | undefined {
  if (env && typeof env === "object" && key in (env as Record<string, unknown>)) {
    const v = (env as Record<string, unknown>)[key];
    if (typeof v === "string" && v.trim()) return v;
  }

  // Local dev often runs in Node during SSR.
  const nodeEnv =
    typeof process !== "undefined" && process.env ? (process.env as Record<string, string | undefined>) : undefined;
  const v2 = nodeEnv?.[key];
  if (typeof v2 === "string" && v2.trim()) return v2;

  return undefined;
}

async function readJsonBody(request: Request): Promise<unknown> {
  const contentType = request.headers.get("content-type") ?? "";
  if (!contentType.includes("application/json")) {
    throw new Error("Expected application/json body");
  }
  return await request.json();
}

type LlmProvider = "gemini" | "anthropic" | "openrouter" | "openai";

function normalizeWhitespace(s: string): string {
  return s.replace(/\s+/g, " ").trim();
}

function localQuizRemediation(input: {
  question: string;
  options: string[];
  correctIndex: number;
  selectedIndex: number;
}): string {
  const correct = input.options[input.correctIndex] ?? "";
  const selected = input.options[input.selectedIndex] ?? "";

  // Known-question enrichments (kept tiny + deterministic).
  const q = normalizeWhitespace(input.question).toLowerCase();
  if (q.includes("protégé effect")) {
    return (
      `Why it's wrong: The Protégé Effect is about learning by teaching, not passive review.\n` +
      `Simplified explanation: When you prepare to explain something, you organise ideas and notice gaps in your understanding. That active effort improves learning more than just re-reading.\n` +
      `Quick tip: After each lesson, explain it out loud in 3 sentences.`
    );
  }
  if (q.includes("cognitive load heatmap") && q.includes("red cell")) {
    return (
      `Why it's wrong: A red cell usually signals difficulty, not speed or skipping.\n` +
      `Simplified explanation: Red combines signals like long time-on-page, repeated re-reads, higher failure rate, and drop-offs. It's a clue the section needs simplification or a better example.\n` +
      `Quick tip: Identify the exact sentence students re-read most, then rewrite that sentence first.`
    );
  }
  if (q.includes("not used") && q.includes("blocked")) {
    return (
      `Why it's wrong: Social data (like friends online) doesn't measure understanding.\n` +
      `Simplified explanation: Blockage detection uses behaviour on the content: repeated failures, unusually long time, and re-reading without progress. Those signals show confusion on the concept itself.\n` +
      `Quick tip: If time-on-page spikes and scores don’t move, add a short worked example.`
    );
  }
  if (q.includes("peer struggle matching") && q.includes("triggers")) {
    return (
      `Why it's wrong: Peer matching isn't scheduled or manual by default.\n` +
      `Simplified explanation: The key trigger is overlap: two learners are stuck on the same concept at the same time. Then the system can offer a quick session to teach/ask and unblock.\n` +
      `Quick tip: Match on concept + recent failures, not just course.`
    );
  }

  return (
    `Why it's wrong: Your choice (“${selected}”) doesn't match the correct idea.\n` +
    `Simplified explanation: The correct answer is “${correct}”. Focus on the definition implied by the question, then eliminate options that describe a different concept.\n` +
    `Quick tip: Highlight the keyword in the question and restate it in your own words.`
  );
}

function simplifyTextLocal(text: string): { rewrite: string; bullets: string[] } {
  // Deterministic, fast, and offline. Not perfect, but useful.
  const original = text.trim();
  const cleaned = original
    .replace(/\([^)]*\)/g, "")
    .replace(/\s+/g, " ")
    .trim();

  const replacements: Array<[RegExp, string]> = [
    [/\butili[sz]e\b/gi, "use"],
    [/\bapproximately\b/gi, "about"],
    [/\bindicate\b/gi, "show"],
    [/\btherefore\b/gi, "so"],
    [/\bhowever\b/gi, "but"],
    [/\bmoreover\b/gi, "also"],
    [/\bcognitive load\b/gi, "mental effort"],
    [/\bworking memory\b/gi, "short-term memory"],
  ];

  let s = cleaned;
  for (const [re, val] of replacements) {
    s = s.replace(re, val);
  }

  // Split into shorter sentences.
  const sentences = s
    .split(/(?<=[.!?])\s+/)
    .flatMap((sent) => sent.split(/\s*;\s*/))
    .flatMap((sent) => sent.split(/\s+—\s+/))
    .map((x) => x.trim())
    .filter(Boolean);

  const short = sentences
    .flatMap((sent) => {
      if (sent.length <= 120) return [sent];
      return sent
        .split(/\s*,\s*/)
        .map((p) => p.trim())
        .filter(Boolean);
    })
    .map((sent) => (sent.endsWith(".") || sent.endsWith("!") || sent.endsWith("?") ? sent : `${sent}.`));

  const rewrite = short.join(" ");
  const bullets = [
    "Shortened sentences to reduce mental effort",
    "Removed extra asides and filler",
    "Swapped a few complex words for simpler ones",
  ];

  return { rewrite, bullets };
}

function pickProvider(env: unknown): { provider: LlmProvider; key: string; model: string } | null {
  const explicit = getEnvValue(env, "AI_PROVIDER")?.toLowerCase();
  const geminiKey = getEnvValue(env, "GEMINI_API_KEY");
  const anthropicKey = getEnvValue(env, "ANTHROPIC_API_KEY");
  const openrouterKey = getEnvValue(env, "OPENROUTER_API_KEY");
  const openaiKey = getEnvValue(env, "OPENAI_API_KEY");

  // Local/offline mode: free + unlimited, no keys.
  if (explicit === "local") return null;

  if (explicit === "gemini" && geminiKey) {
    return {
      provider: "gemini",
      key: geminiKey,
      model: getEnvValue(env, "GEMINI_MODEL") ?? "gemini-1.5-flash",
    };
  }

  if (explicit === "anthropic" && anthropicKey) {
    return {
      provider: "anthropic",
      key: anthropicKey,
      model: getEnvValue(env, "ANTHROPIC_MODEL") ?? "claude-3-5-sonnet-20240620",
    };
  }
  if (explicit === "openrouter" && openrouterKey) {
    return {
      provider: "openrouter",
      key: openrouterKey,
      model: getEnvValue(env, "OPENROUTER_MODEL") ?? "openai/gpt-4o-mini",
    };
  }
  if (explicit === "openai" && openaiKey) {
    return {
      provider: "openai",
      key: openaiKey,
      model: getEnvValue(env, "OPENAI_MODEL") ?? "gpt-4o-mini",
    };
  }

  // Default precedence for "$0 hosted" setup: Gemini first if present.
  if (geminiKey) {
    return {
      provider: "gemini",
      key: geminiKey,
      model: getEnvValue(env, "GEMINI_MODEL") ?? "gemini-1.5-flash",
    };
  }

  if (anthropicKey) {
    return {
      provider: "anthropic",
      key: anthropicKey,
      model: getEnvValue(env, "ANTHROPIC_MODEL") ?? "claude-3-5-sonnet-20240620",
    };
  }

  if (openrouterKey) {
    return {
      provider: "openrouter",
      key: openrouterKey,
      model: getEnvValue(env, "OPENROUTER_MODEL") ?? "openai/gpt-4o-mini",
    };
  }

  if (openaiKey) {
    return {
      provider: "openai",
      key: openaiKey,
      model: getEnvValue(env, "OPENAI_MODEL") ?? "gpt-4o-mini",
    };
  }

  return null;
}

async function callAnthropic(opts: {
  apiKey: string;
  model: string;
  system: string;
  user: string;
  maxTokens?: number;
}): Promise<string> {
  const res = await fetch("https://api.anthropic.com/v1/messages", {
    method: "POST",
    headers: {
      "content-type": "application/json",
      "anthropic-version": "2023-06-01",
      "x-api-key": opts.apiKey,
    },
    body: JSON.stringify({
      model: opts.model,
      max_tokens: opts.maxTokens ?? 600,
      system: opts.system,
      messages: [{ role: "user", content: opts.user }],
      temperature: 0.2,
    }),
  });

  const data = (await res.json().catch(() => null)) as any;
  if (!res.ok) {
    const msg =
      typeof data?.error?.message === "string"
        ? data.error.message
        : `Anthropic error (${res.status})`;
    throw new Error(msg);
  }

  const parts = Array.isArray(data?.content) ? data.content : [];
  const text = parts
    .map((p: any) => (p?.type === "text" && typeof p.text === "string" ? p.text : ""))
    .join("")
    .trim();
  if (!text) throw new Error("Anthropic returned no text");
  return text;
}

async function callOpenAI(opts: {
  apiKey: string;
  model: string;
  system: string;
  user: string;
  maxTokens?: number;
}): Promise<string> {
  const res = await fetch("https://api.openai.com/v1/chat/completions", {
    method: "POST",
    headers: {
      "content-type": "application/json",
      authorization: `Bearer ${opts.apiKey}`,
    },
    body: JSON.stringify({
      model: opts.model,
      messages: [
        { role: "system", content: opts.system },
        { role: "user", content: opts.user },
      ],
      max_tokens: opts.maxTokens ?? 600,
      temperature: 0.2,
    }),
  });

  const data = (await res.json().catch(() => null)) as any;
  if (!res.ok) {
    const msg =
      typeof data?.error?.message === "string" ? data.error.message : `OpenAI error (${res.status})`;
    throw new Error(msg);
  }

  const text = data?.choices?.[0]?.message?.content;
  if (typeof text !== "string" || !text.trim()) throw new Error("OpenAI returned no text");
  return text.trim();
}

async function callOpenRouter(opts: {
  apiKey: string;
  model: string;
  system: string;
  user: string;
  maxTokens?: number;
  siteUrl?: string;
  appName?: string;
}): Promise<string> {
  const headers: Record<string, string> = {
    "content-type": "application/json",
    authorization: `Bearer ${opts.apiKey}`,
  };

  // Optional but recommended by OpenRouter for attribution/analytics.
  if (opts.siteUrl) headers["HTTP-Referer"] = opts.siteUrl;
  if (opts.appName) headers["X-OpenRouter-Title"] = opts.appName;

  const res = await fetch("https://openrouter.ai/api/v1/chat/completions", {
    method: "POST",
    headers,
    body: JSON.stringify({
      model: opts.model,
      messages: [
        { role: "system", content: opts.system },
        { role: "user", content: opts.user },
      ],
      max_tokens: opts.maxTokens ?? 600,
      temperature: 0.2,
    }),
  });

  const data = (await res.json().catch(() => null)) as any;
  if (!res.ok) {
    const msg =
      typeof data?.error?.message === "string"
        ? data.error.message
        : typeof data?.message === "string"
          ? data.message
          : `OpenRouter error (${res.status})`;
    throw new Error(msg);
  }

  const text = data?.choices?.[0]?.message?.content;
  if (typeof text !== "string" || !text.trim()) throw new Error("OpenRouter returned no text");
  return text.trim();
}

function splitModelCandidates(model: string): string[] {
  return model
    .split(/[|,]/g)
    .map((m) => m.trim())
    .filter(Boolean);
}

async function callGemini(opts: {
  apiKey: string;
  model: string;
  system: string;
  user: string;
}): Promise<string> {
  const normalizeModel = (m: string): string => m.replace(/^models\//, "").trim();

  const prompt = `System: ${opts.system}\n\nUser: ${opts.user}`;

  async function generateOnce(apiVersion: "v1" | "v1beta", model: string): Promise<string> {
    const url = `https://generativelanguage.googleapis.com/${apiVersion}/models/${encodeURIComponent(
      normalizeModel(model),
    )}:generateContent?key=${encodeURIComponent(opts.apiKey)}`;

    const res = await fetch(url, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({
        contents: [{ role: "user", parts: [{ text: prompt }] }],
        generationConfig: { temperature: 0.2 },
      }),
    });

    const data = (await res.json().catch(() => null)) as any;
    if (!res.ok) {
      const msg =
        typeof data?.error?.message === "string"
          ? data.error.message
          : `Gemini error (${res.status})`;
      throw new Error(msg);
    }

    const text = data?.candidates?.[0]?.content?.parts
      ?.map((p: any) => (typeof p?.text === "string" ? p.text : ""))
      .join("")
      ?.trim();
    if (typeof text !== "string" || !text.trim()) throw new Error("Gemini returned no text");
    return text.trim();
  }

  async function listModels(apiVersion: "v1" | "v1beta"): Promise<
    { name: string; supportedGenerationMethods?: string[] }[]
  > {
    const url = `https://generativelanguage.googleapis.com/${apiVersion}/models?key=${encodeURIComponent(
      opts.apiKey,
    )}`;
    const res = await fetch(url);
    const data = (await res.json().catch(() => null)) as any;
    if (!res.ok) {
      const msg =
        typeof data?.error?.message === "string"
          ? data.error.message
          : `Gemini listModels error (${res.status})`;
      throw new Error(msg);
    }
    const models = Array.isArray(data?.models) ? data.models : [];
    return models
      .filter((m: any) => typeof m?.name === "string")
      .map((m: any) => ({
        name: String(m.name),
        supportedGenerationMethods: Array.isArray(m.supportedGenerationMethods)
          ? m.supportedGenerationMethods.map(String)
          : undefined,
      }));
  }

  function pickBestModel(models: { name: string; supportedGenerationMethods?: string[] }[]): string | null {
    const supported = models
      .filter((m) => (m.supportedGenerationMethods ?? []).includes("generateContent"))
      .map((m) => m.name.replace(/^models\//, ""));

    const preferred = [
      "gemini-2.0-flash",
      "gemini-2.0-flash-lite",
      "gemini-1.5-flash",
      "gemini-1.5-flash-latest",
      "gemini-1.5-flash-8b",
      "gemini-1.0-pro",
    ];

    for (const p of preferred) {
      const found = supported.find((s) => s === p);
      if (found) return found;
    }

    const flash = supported.find((s) => s.includes("flash"));
    return flash ?? supported[0] ?? null;
  }

  const initialModel = normalizeModel(opts.model);

  // 1) Try stable v1 first (model catalogs differ by API version).
  try {
    return await generateOnce("v1", initialModel);
  } catch (e1) {
    // 2) Try v1beta with same model.
    try {
      return await generateOnce("v1beta", initialModel);
    } catch (e2) {
      const msg = (e2 instanceof Error ? e2.message : "").toLowerCase();
      const looksLikeModelIssue =
        msg.includes("is not found") ||
        msg.includes("not supported") ||
        msg.includes("listmodels") ||
        msg.includes("call modelservice.listmodels");

      if (!looksLikeModelIssue) {
        throw e2;
      }

      // 3) Auto-detect a supported model and retry.
      let models: { name: string; supportedGenerationMethods?: string[] }[] = [];
      try {
        models = await listModels("v1");
      } catch {
        models = await listModels("v1beta");
      }
      const picked = pickBestModel(models);
      if (!picked) throw e2;

      // Prefer v1 for the retried model, then v1beta.
      try {
        return await generateOnce("v1", picked);
      } catch {
        return await generateOnce("v1beta", picked);
      }
    }
  }
}

async function callLlm(env: unknown, system: string, user: string): Promise<string> {
  const picked = pickProvider(env);
  if (!picked) {
    throw new Error("LOCAL_FALLBACK");
  }

  if (picked.provider === "gemini") {
    return await callGemini({ apiKey: picked.key, model: picked.model, system, user });
  }
  if (picked.provider === "anthropic") {
    return await callAnthropic({ apiKey: picked.key, model: picked.model, system, user });
  }
  if (picked.provider === "openrouter") {
    const candidates = splitModelCandidates(picked.model);
    const siteUrl = getEnvValue(env, "OPENROUTER_SITE_URL");
    const appName = getEnvValue(env, "OPENROUTER_APP_NAME");

    let lastError: unknown;
    for (const model of candidates.length ? candidates : [picked.model]) {
      try {
        return await callOpenRouter({
          apiKey: picked.key,
          model,
          system,
          user,
          siteUrl,
          appName,
        });
      } catch (e) {
        lastError = e;
        const msg = (e instanceof Error ? e.message : "").toLowerCase();
        const retryable =
          msg.includes("rate") ||
          msg.includes("quota") ||
          msg.includes("too many") ||
          msg.includes("overloaded") ||
          msg.includes("no available") ||
          msg.includes("not found") ||
          msg.includes("model") ||
          msg.includes("provider");
        if (!retryable) throw e;
      }
    }
    throw lastError instanceof Error ? lastError : new Error("OpenRouter request failed");
  }

  return await callOpenAI({ apiKey: picked.key, model: picked.model, system, user });
}

export async function handleAiApiRequest(request: Request, env: unknown): Promise<Response | null> {
  const url = new URL(request.url);
  if (!url.pathname.startsWith("/api/ai/")) return null;

  if (request.method !== "POST") {
    return json({ error: "Method not allowed" }, { status: 405 });
  }

  try {
    if (url.pathname === "/api/ai/quiz-remediation") {
      const parsed = QuizRemediationSchema.safeParse(await readJsonBody(request));
      if (!parsed.success) {
        return json({ error: "Invalid request" }, { status: 400 });
      }

      const { question, options, correctIndex, selectedIndex } = parsed.data;
      const correct = options[correctIndex] ?? "";
      const selected = options[selectedIndex] ?? "";

      const system =
        "You are a supportive tutor. Explain misconceptions clearly, without shaming. Keep it short, concrete, and beginner-friendly.";
      const user =
        `A student answered a multiple-choice question incorrectly.\n\n` +
        `Question: ${question}\n` +
        `Options: ${options.map((o, i) => `${i + 1}. ${o}`).join(" | ")}\n` +
        `Student chose: ${selected}\n` +
        `Correct answer: ${correct}\n\n` +
        `Return:\n` +
        `1) "Why it's wrong" (1-2 sentences)\n` +
        `2) "Simplified explanation" (2-4 sentences)\n` +
        `3) "Quick tip" (one actionable tip)\n`;

      try {
        const text = await callLlm(env, system, user);
        return json({ text, source: "cloud" }, { status: 200 });
      } catch (e) {
        const msg = e instanceof Error ? e.message : "";
        if (msg === "LOCAL_FALLBACK") {
          const text = localQuizRemediation(parsed.data);
          return json({ text, source: "local" }, { status: 200 });
        }
        throw e;
      }
    }

    if (url.pathname === "/api/ai/rewrite") {
      const parsed = RewriteSchema.safeParse(await readJsonBody(request));
      if (!parsed.success) {
        return json({ error: "Invalid request" }, { status: 400 });
      }

      const { text, audience } = parsed.data;
      const system =
        "You rewrite learning content to reduce cognitive load. Keep meaning, remove jargon, shorten sentences, and add gentle structure.";
      const user =
        `Rewrite the paragraph below for a ${audience} audience.\n` +
        `Constraints: do not add new facts, keep it under ~120 words if possible.\n\n` +
        `Paragraph:\n${text}\n\n` +
        `Return:\n` +
        `Simplified rewrite:\n...\n\n` +
        `Key changes (3 bullets max):\n- ...\n`;

      try {
        const rewritten = await callLlm(env, system, user);
        return json({ text: rewritten, source: "cloud" }, { status: 200 });
      } catch (e) {
        const msg = e instanceof Error ? e.message : "";
        if (msg === "LOCAL_FALLBACK") {
          const out = simplifyTextLocal(text);
          const rewritten =
            `Simplified rewrite:\n${out.rewrite}\n\n` +
            `Key changes (3 bullets max):\n- ${out.bullets.slice(0, 3).join("\n- ")}`;
          return json({ text: rewritten, source: "local" }, { status: 200 });
        }
        throw e;
      }
    }

    if (url.pathname === "/api/ai/chat") {
      const parsed = ChatSchema.safeParse(await readJsonBody(request));
      if (!parsed.success) {
        return json({ error: "Invalid request" }, { status: 400 });
      }

      const system =
        "You are LearnAdapt's helpful study assistant. Answer questions clearly and briefly. If the user asks for help studying, give a small step-by-step plan and one quick check question.";

      const transcript = parsed.data.messages
        .map((m) => `${m.role === "user" ? "User" : "Assistant"}: ${m.content}`)
        .join("\n");

      const user =
        `Continue this chat. Respond as the Assistant.\n\n` +
        `Chat so far:\n${transcript}\n\n` +
        `Assistant:`;

      try {
        const text = await callLlm(env, system, user);
        return json({ text, source: "cloud" }, { status: 200 });
      } catch (e) {
        const msg = e instanceof Error ? e.message : "";
        if (msg === "LOCAL_FALLBACK") {
          return json(
            {
              text:
                "I’m running in offline mode (no cloud AI configured). Set `AI_PROVIDER=openrouter` and `OPENROUTER_API_KEY` then restart the dev server.",
              source: "local",
            },
            { status: 200 },
          );
        }
        throw e;
      }
    }

    return json({ error: "Not found" }, { status: 404 });
  } catch (error) {
    const message = error instanceof Error ? error.message : "Unknown error";
    const status = 502;
    return json({ error: message }, { status });
  }
}
