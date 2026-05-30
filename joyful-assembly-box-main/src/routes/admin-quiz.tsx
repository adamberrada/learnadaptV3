import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useMemo, useState } from "react";

export const Route = createFileRoute("/admin-quiz")({
  component: AdminQuizPage,
  validateSearch: (search: Record<string, unknown>) => {
    const quizId = typeof search.quizId === "string" ? search.quizId : "";
    const chapterId = typeof search.chapterId === "string" ? search.chapterId : "";
    return { quizId, chapterId };
  },
  head: () => ({
    meta: [
      { title: "Admin · Publish Quiz — LearnAdapt" },
      {
        name: "description",
        content: "Validate and publish a quiz (admin prototype).",
      },
    ],
  }),
});

type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

type QuizResponse = {
  id: string;
  chapterId: string;
  status?: string | null;
  totalPoints?: number | null;
};

function readLocalStorage(key: string): string {
  if (typeof window === "undefined") return "";
  return localStorage.getItem(key) ?? "";
}

function AdminQuizPage() {
  const search = Route.useSearch();

  const [userId, setUserId] = useState<string>(() => readLocalStorage("la-dev-admin-id") || "admin-dev");
  const role = "ADMIN";

  const [quizId, setQuizId] = useState<string>(search.quizId || "");
  const [chapterId, setChapterId] = useState<string>(search.chapterId || "");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<QuizResponse | null>(null);

  useEffect(() => {
    if (typeof window === "undefined") return;
    localStorage.setItem("la-dev-admin-id", userId);
  }, [userId]);

  const headers = useMemo(() => {
    const h = new Headers();
    h.set("content-type", "application/json");
    h.set("X-User-Role", role);
    if (userId.trim()) h.set("X-User-Id", userId.trim());
    return h;
  }, [role, userId]);

  const validateAndPublish = async () => {
    if (!quizId.trim()) {
      setError("quizId is required.");
      return;
    }

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const res = await fetch(`/api/admin/quizzes/${encodeURIComponent(quizId.trim())}/validate`, {
        method: "POST",
        headers,
      });

      const payload = (await res.json().catch(() => null)) as ApiResponse<QuizResponse> | null;
      if (!res.ok || !payload?.success) {
        throw new Error(payload?.message ?? `Validate failed (${res.status})`);
      }

      setResult(payload.data);
      if (!chapterId.trim() && payload.data.chapterId) {
        setChapterId(payload.data.chapterId);
      }
    } catch (e) {
      setError(e instanceof Error ? e.message : "Validate failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-3xl px-6 py-14">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-medium text-accent">Admin</p>
          <h1 className="mt-1 font-display text-3xl font-semibold">Publish Quiz</h1>
          <p className="mt-2 text-sm text-muted-foreground">
            Prototype: validate/publish a quiz so learners can fetch it by chapter.
          </p>
        </div>
        <div className="text-right text-sm">
          <p className="font-semibold text-foreground">Role: {role}</p>
          <Link to="/teacher-quiz" className="text-primary hover:underline">
            Go to Teacher
          </Link>
        </div>
      </div>

      <div className="mt-8 rounded-2xl border border-border bg-card p-6 shadow-card">
        <p className="text-sm font-semibold">Dev auth headers</p>
        <p className="mt-1 text-sm text-muted-foreground">
          These are sent as `X-User-Id` and `X-User-Role`.
        </p>
        <div className="mt-4 grid gap-4 sm:grid-cols-2">
          <Field label="User Id" value={userId} onChange={setUserId} placeholder="admin-dev" />
          <Field label="Role" value={role} onChange={() => {}} disabled />
        </div>
      </div>

      <div className="mt-6 rounded-2xl border border-border bg-card p-6 shadow-card">
        <p className="text-sm font-semibold">Validate / publish</p>
        <div className="mt-4 grid gap-4 sm:grid-cols-2">
          <Field label="Quiz Id" value={quizId} onChange={setQuizId} placeholder="quizId" />
          <Field label="Chapter Id" value={chapterId} onChange={setChapterId} placeholder="(optional)" />
        </div>

        <div className="mt-5 flex items-center gap-3">
          <button
            onClick={() => void validateAndPublish()}
            disabled={loading}
            className="rounded-md bg-foreground px-5 py-2.5 text-sm font-semibold text-background disabled:opacity-60"
          >
            Validate & publish
          </button>
          {result?.id && (
            <p className="text-sm text-muted-foreground">
              Status: <span className="font-semibold text-foreground">{result.status ?? ""}</span>
            </p>
          )}
        </div>

        {error && <p className="mt-4 text-sm text-muted-foreground">{error}</p>}

        {result?.id && (
          <div className="mt-5 rounded-xl border border-border bg-background p-4">
            <p className="text-sm font-semibold">Published</p>
            <p className="mt-1 text-sm text-muted-foreground">
              quizId: <span className="font-semibold text-foreground">{result.id}</span>
            </p>
            <p className="mt-1 text-sm text-muted-foreground">
              chapterId: <span className="font-semibold text-foreground">{chapterId || result.chapterId}</span>
            </p>

            <div className="mt-4 flex flex-wrap items-center gap-3">
              <Link
                to="/quiz"
                search={{ chapterId: (chapterId || result.chapterId || "").trim() }}
                className="rounded-md bg-primary px-5 py-2.5 text-sm font-semibold text-primary-foreground"
              >
                Open learner /quiz
              </Link>
              <Link
                to="/teacher-quiz"
                className="rounded-md border border-border bg-card px-5 py-2.5 text-sm font-semibold"
              >
                Back to teacher
              </Link>
            </div>
          </div>
        )}
      </div>

      <div className="mt-6 rounded-2xl border border-border bg-card p-6 shadow-card">
        <p className="text-sm font-semibold">Expected fix</p>
        <p className="mt-1 text-sm text-muted-foreground">
          After publishing, the learner endpoint should stop returning “Published quiz not found for chapter”.
        </p>
      </div>
    </div>
  );
}

function Field({
  label,
  value,
  onChange,
  placeholder,
  disabled,
}: {
  label: string;
  value: string;
  onChange: (v: string) => void;
  placeholder?: string;
  disabled?: boolean;
}) {
  return (
    <label className="block">
      <span className="text-sm font-medium text-foreground">{label}</span>
      <input
        value={value}
        disabled={disabled}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="mt-1.5 w-full rounded-md border border-input bg-background px-3 py-2 text-sm shadow-sm outline-none focus:border-ring focus:ring-2 focus:ring-ring/30 disabled:opacity-70"
      />
    </label>
  );
}
