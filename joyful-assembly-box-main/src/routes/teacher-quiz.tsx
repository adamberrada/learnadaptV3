import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useMemo, useState } from "react";

export const Route = createFileRoute("/teacher-quiz")({
  component: TeacherQuizPage,
  head: () => ({
    meta: [
      { title: "Teacher · Quiz Builder — LearnAdapt" },
      {
        name: "description",
        content: "Create a quiz and add questions (teacher prototype).",
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
  courseId: string;
  chapterId: string;
  title: string;
  description?: string | null;
  timeLimitInMinutes?: number | null;
  passingScore?: number | null;
  totalPoints?: number | null;
  status?: string | null;
  questions?: Array<{
    id: string;
    text: string;
    orderIndex?: number | null;
    points?: number | null;
    options?: Array<{ id: string; text: string; correct?: boolean | null }>;
  }>;
};

type QuestionResponse = {
  id: string;
  text: string;
  orderIndex?: number | null;
  points?: number | null;
  options?: Array<{ id: string; text: string; correct?: boolean | null }>;
};

function readLocalStorage(key: string): string {
  if (typeof window === "undefined") return "";
  return localStorage.getItem(key) ?? "";
}

function TeacherQuizPage() {
  const [userId, setUserId] = useState<string>(() => readLocalStorage("la-dev-user-id") || "teacher-dev");
  const role = "TEACHER";

  const [courseId, setCourseId] = useState<string>("");
  const [chapterId, setChapterId] = useState<string>("");
  const [title, setTitle] = useState<string>("Chapter Quiz");
  const [description, setDescription] = useState<string>("");
  const [timeLimitInMinutes, setTimeLimitInMinutes] = useState<number>(10);
  const [passingScore, setPassingScore] = useState<number>(2);

  const [quiz, setQuiz] = useState<QuizResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [qText, setQText] = useState<string>("");
  const [qPoints, setQPoints] = useState<number>(1);
  const [qOrder, setQOrder] = useState<number>(0);

  const [o1, setO1] = useState<string>("");
  const [o2, setO2] = useState<string>("");
  const [o3, setO3] = useState<string>("");
  const [o4, setO4] = useState<string>("");
  const [correctIndex, setCorrectIndex] = useState<number>(0);

  const [questionResult, setQuestionResult] = useState<QuestionResponse | null>(null);

  useEffect(() => {
    if (typeof window === "undefined") return;
    localStorage.setItem("la-dev-user-id", userId);
  }, [userId]);

  const headers = useMemo(() => {
    const h = new Headers();
    h.set("content-type", "application/json");
    h.set("X-User-Role", role);
    if (userId.trim()) h.set("X-User-Id", userId.trim());
    return h;
  }, [role, userId]);

  const createQuiz = async () => {
    setLoading(true);
    setError(null);
    setQuiz(null);
    setQuestionResult(null);

    try {
      const res = await fetch("/api/teacher/quizzes", {
        method: "POST",
        headers,
        body: JSON.stringify({
          courseId: courseId.trim(),
          chapterId: chapterId.trim(),
          title: title.trim(),
          description: description.trim() || null,
          timeLimitInMinutes,
          passingScore,
        }),
      });

      const payload = (await res.json().catch(() => null)) as ApiResponse<QuizResponse> | null;
      if (!res.ok || !payload?.success) {
        throw new Error(payload?.message ?? `Create quiz failed (${res.status})`);
      }

      setQuiz(payload.data);
      setQOrder(payload.data.questions?.length ?? 0);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Create quiz failed");
    } finally {
      setLoading(false);
    }
  };

  const addQuestion = async () => {
    if (!quiz?.id) {
      setError("Create a quiz first.");
      return;
    }

    const options = [o1, o2, o3, o4]
      .map((t) => t.trim())
      .filter(Boolean);

    if (!qText.trim()) {
      setError("Question text is required.");
      return;
    }

    if (options.length < 2) {
      setError("Provide at least 2 options.");
      return;
    }

    setLoading(true);
    setError(null);
    setQuestionResult(null);

    try {
      const optionPayload = [o1, o2, o3, o4]
        .map((t, idx) => ({
          text: t.trim(),
          correct: idx === correctIndex,
        }))
        .filter((o) => o.text);

      const res = await fetch(`/api/teacher/quizzes/${encodeURIComponent(quiz.id)}/questions`, {
        method: "POST",
        headers,
        body: JSON.stringify({
          text: qText.trim(),
          points: qPoints,
          orderIndex: qOrder,
          options: optionPayload,
        }),
      });

      const payload = (await res.json().catch(() => null)) as ApiResponse<QuestionResponse> | null;
      if (!res.ok || !payload?.success) {
        throw new Error(payload?.message ?? `Add question failed (${res.status})`);
      }

      setQuestionResult(payload.data);
      setQText("");
      setO1("");
      setO2("");
      setO3("");
      setO4("");
      setCorrectIndex(0);
      setQOrder((n) => n + 1);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Add question failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-3xl px-6 py-14">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-medium text-accent">Teacher</p>
          <h1 className="mt-1 font-display text-3xl font-semibold">Quiz Builder</h1>
          <p className="mt-2 text-sm text-muted-foreground">
            Prototype: create a quiz, add questions, then publish it from the admin page.
          </p>
        </div>
        <div className="text-right text-sm">
          <p className="font-semibold text-foreground">Role: {role}</p>
          <Link to="/admin-quiz" className="text-primary hover:underline">
            Go to Admin
          </Link>
        </div>
      </div>

      <div className="mt-8 rounded-2xl border border-border bg-card p-6 shadow-card">
        <p className="text-sm font-semibold">Dev auth headers</p>
        <p className="mt-1 text-sm text-muted-foreground">
          These are sent as `X-User-Id` and `X-User-Role`.
        </p>
        <div className="mt-4 grid gap-4 sm:grid-cols-2">
          <Field label="User Id" value={userId} onChange={setUserId} placeholder="teacher-dev" />
          <Field label="Role" value={role} onChange={() => {}} disabled />
        </div>
      </div>

      <div className="mt-6 rounded-2xl border border-border bg-card p-6 shadow-card">
        <p className="text-sm font-semibold">Create quiz</p>
        <div className="mt-4 grid gap-4 sm:grid-cols-2">
          <Field label="Course Id" value={courseId} onChange={setCourseId} placeholder="courseId" />
          <Field label="Chapter Id" value={chapterId} onChange={setChapterId} placeholder="chapterId" />
          <Field label="Title" value={title} onChange={setTitle} placeholder="Chapter Quiz" />
          <Field
            label="Time limit (minutes)"
            value={String(timeLimitInMinutes)}
            onChange={(v) => setTimeLimitInMinutes(Number(v) || 1)}
            placeholder="10"
          />
          <Field
            label="Passing score"
            value={String(passingScore)}
            onChange={(v) => setPassingScore(Number(v) || 0)}
            placeholder="2"
          />
          <Field label="Description" value={description} onChange={setDescription} placeholder="(optional)" />
        </div>

        <div className="mt-5 flex items-center gap-3">
          <button
            onClick={() => void createQuiz()}
            disabled={loading}
            className="rounded-md bg-foreground px-5 py-2.5 text-sm font-semibold text-background disabled:opacity-60"
          >
            Create quiz
          </button>
          {quiz?.id && (
            <p className="text-sm text-muted-foreground">
              Created quizId: <span className="font-semibold text-foreground">{quiz.id}</span>
            </p>
          )}
        </div>

        {quiz?.id && (
          <p className="mt-3 text-sm text-muted-foreground">
            Next: add questions below, then publish via{" "}
            <Link
              to="/admin-quiz"
              search={{ quizId: quiz.id, chapterId: quiz.chapterId }}
              className="text-primary hover:underline"
            >
              /admin-quiz
            </Link>
            .
          </p>
        )}

        {error && <p className="mt-4 text-sm text-muted-foreground">{error}</p>}
      </div>

      <div className="mt-6 rounded-2xl border border-border bg-card p-6 shadow-card">
        <p className="text-sm font-semibold">2) Add question</p>
        <p className="mt-1 text-sm text-muted-foreground">
          Add at least one question before publishing.
        </p>

        <div className="mt-4 grid gap-4">
          <Field label="Question text" value={qText} onChange={setQText} placeholder="Question..." />

          <div className="grid gap-4 sm:grid-cols-3">
            <Field
              label="Points"
              value={String(qPoints)}
              onChange={(v) => setQPoints(Math.max(1, Number(v) || 1))}
              placeholder="1"
            />
            <Field
              label="Order index"
              value={String(qOrder)}
              onChange={(v) => setQOrder(Math.max(0, Number(v) || 0))}
              placeholder="0"
            />
            <label className="block">
              <span className="text-sm font-medium text-foreground">Correct option</span>
              <select
                value={String(correctIndex)}
                onChange={(e) => setCorrectIndex(Number(e.target.value) || 0)}
                className="mt-1.5 w-full rounded-md border border-input bg-background px-3 py-2 text-sm shadow-sm outline-none focus:border-ring focus:ring-2 focus:ring-ring/30"
              >
                <option value="0">Option A</option>
                <option value="1">Option B</option>
                <option value="2">Option C</option>
                <option value="3">Option D</option>
              </select>
            </label>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <Field label="Option A" value={o1} onChange={setO1} placeholder="..." />
            <Field label="Option B" value={o2} onChange={setO2} placeholder="..." />
            <Field label="Option C" value={o3} onChange={setO3} placeholder="..." />
            <Field label="Option D" value={o4} onChange={setO4} placeholder="..." />
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={() => void addQuestion()}
              disabled={loading || !quiz?.id}
              className="rounded-md bg-primary px-5 py-2.5 text-sm font-semibold text-primary-foreground disabled:opacity-50"
            >
              Add question
            </button>
            {!quiz?.id && (
              <p className="text-sm text-muted-foreground">Create a quiz first.</p>
            )}
          </div>

          {questionResult?.id && (
            <p className="text-sm text-muted-foreground">
              Added questionId: <span className="font-semibold text-foreground">{questionResult.id}</span>
            </p>
          )}
        </div>
      </div>

      <div className="mt-6 rounded-2xl border border-border bg-card p-6 shadow-card">
        <p className="text-sm font-semibold">3) Test learner fetch</p>
        <p className="mt-1 text-sm text-muted-foreground">
          After publishing, open the learner quiz page and load the same chapterId.
        </p>
        <div className="mt-4 flex flex-wrap items-center gap-3">
          <Link
            to="/quiz"
            search={{ chapterId: chapterId.trim() || quiz?.chapterId || "" }}
            className="rounded-md bg-foreground px-5 py-2.5 text-sm font-semibold text-background"
          >
            Open /quiz
          </Link>
          <p className="text-sm text-muted-foreground">(works after admin validates)</p>
        </div>
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
