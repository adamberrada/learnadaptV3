import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useMemo, useRef, useState } from "react";
import { Check, X, RotateCcw, Trophy } from "lucide-react";

export const Route = createFileRoute("/quiz")({
  component: QuizPage,
  head: () => ({
    meta: [
      { title: "Quiz — LearnAdapt" },
      {
        name: "description",
        content:
          "An interactive adaptive quiz with instant feedback and error analysis.",
      },
    ],
  }),
});

type Q = {
  id: string;
  question: string;
  options: { id: string; text: string; correct?: boolean | null }[];
  correctIndex: number | null;
};

type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

type QuizResponse = {
  id: string;
  title?: string | null;
  description?: string | null;
  chapterId?: string | null;
  questions?: Array<{
    id: string;
    text: string;
    options?: Array<{ id: string; text: string; correct?: boolean | null }>;
  }>;
};

type QuizSubmissionResponse = {
  id: string;
  quizId: string;
  learnerId: string;
  scoreObtained: number;
  maxScore: number;
  passingScore: number;
  passed: boolean;
};

const demoQuestions: Q[] = [
  {
    id: "demo-1",
    question: "What does the Protégé Effect describe?",
    options: [
      { id: "demo-1-a", text: "Memorising material by re-reading it" },
      { id: "demo-1-b", text: "Learning more deeply by explaining a concept to someone else", correct: true },
      { id: "demo-1-c", text: "Studying with background music" },
      { id: "demo-1-d", text: "Spacing study sessions over time" },
    ],
    correctIndex: 1,
  },
  {
    id: "demo-2",
    question:
      "In a Cognitive Load Heatmap, what does a red cell most often mean?",
    options: [
      { id: "demo-2-a", text: "The lesson was skipped" },
      { id: "demo-2-b", text: "Most students finished quickly" },
      { id: "demo-2-c", text: "Massive blockage — content should likely be rewritten", correct: true },
      { id: "demo-2-d", text: "The teacher hasn't reviewed it yet" },
    ],
    correctIndex: 2,
  },
  {
    id: "demo-3",
    question:
      "Which signal is NOT used to detect that a student is blocked on a concept?",
    options: [
      { id: "demo-3-a", text: "Repeated failures on the same question" },
      { id: "demo-3-b", text: "Abnormally long time on a passage" },
      { id: "demo-3-c", text: "Multiple re-reads without score progress" },
      { id: "demo-3-d", text: "Number of friends online", correct: true },
    ],
    correctIndex: 3,
  },
  {
    id: "demo-4",
    question: "What triggers a Peer Struggle Matching session?",
    options: [
      { id: "demo-4-a", text: "A teacher manually pairs students" },
      {
        id: "demo-4-b",
        text: "Two or more students are blocked on the same concept at the same time",
        correct: true,
      },
      { id: "demo-4-c", text: "A student fails any quiz" },
      { id: "demo-4-d", text: "Once a week on Sundays" },
    ],
    correctIndex: 1,
  },
];

function QuizPage() {
  const [chapterId, setChapterId] = useState<string>(() => {
    if (typeof window === "undefined") return "";
    return new URLSearchParams(window.location.search).get("chapterId") ?? "";
  });
  const [chapterIdInput, setChapterIdInput] = useState<string>(chapterId);
  const [quiz, setQuiz] = useState<QuizResponse | null>(null);
  const [quizLoading, setQuizLoading] = useState(false);
  const [quizError, setQuizError] = useState<string | null>(null);

  const [index, setIndex] = useState(0);
  const [selected, setSelected] = useState<number | null>(null);
  const [confirmed, setConfirmed] = useState(false);
  const [score, setScore] = useState(0);
  const [done, setDone] = useState(false);

  const [answers, setAnswers] = useState<Record<string, string>>({});
  const [submission, setSubmission] = useState<QuizSubmissionResponse | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const [aiLoading, setAiLoading] = useState(false);
  const [aiText, setAiText] = useState<string | null>(null);
  const [aiError, setAiError] = useState<string | null>(null);
  const [aiSource, setAiSource] = useState<"local" | "cloud" | null>(null);
  const lastAiKeyRef = useRef<string | null>(null);

  const questions: Q[] = useMemo(() => {
    const backend = quiz?.questions;
    if (!backend || backend.length === 0) return demoQuestions;

    return backend.map((q) => {
      const options = (q.options ?? []).map((o) => ({
        id: o.id,
        text: o.text,
        correct: o.correct,
      }));

      const correctIndex = options.findIndex((o) => o.correct === true);
      return {
        id: q.id,
        question: q.text,
        options,
        correctIndex: correctIndex >= 0 ? correctIndex : null,
      };
    });
  }, [quiz]);

  const q = questions[index];
  const isCorrect = q?.correctIndex !== null && selected === q?.correctIndex;

  const aiKey = useMemo(() => {
    // Key prevents double-calls if React re-renders.
    if (!q || selected === null) return null;
    return `${index}:${selected}:${q.correctIndex ?? "n/a"}`;
  }, [index, q, selected]);

  useEffect(() => {
    if (!chapterId) {
      setQuiz(null);
      setQuizError(null);
      setQuizLoading(false);
      return;
    }

    const controller = new AbortController();
    setQuizLoading(true);
    setQuizError(null);

    (async () => {
      try {
        const res = await fetch(`/api/learner/quizzes/chapter/${encodeURIComponent(chapterId)}`, {
          signal: controller.signal,
        });

        const payload = (await res.json().catch(() => null)) as ApiResponse<QuizResponse> | null;
        if (!res.ok || !payload?.success) {
          const msg = payload?.message ?? `Quiz request failed (${res.status})`;
          if (res.status === 401 || res.status === 403) {
            throw new Error(
              `${msg}. This endpoint requires X-User-Id and X-User-Role (set DEV_USER_ID/DEV_USER_ROLE in .env.local for local dev).`,
            );
          }
          throw new Error(msg);
        }

        setQuiz(payload.data);
      } catch (e) {
        if (e instanceof DOMException && e.name === "AbortError") return;
        setQuiz(null);
        setQuizError(e instanceof Error ? e.message : "Failed to load quiz");
      } finally {
        setQuizLoading(false);
      }
    })();

    return () => controller.abort();
  }, [chapterId]);

  useEffect(() => {
    // Reset quiz run state when we load a different quiz.
    setIndex(0);
    setSelected(null);
    setConfirmed(false);
    setScore(0);
    setDone(false);
    setAnswers({});
    setSubmission(null);
    setSubmitLoading(false);
    setSubmitError(null);
  }, [quiz?.id]);

  const confirm = () => {
    if (selected === null) return;
    setConfirmed(true);
    if (q && q.options[selected]) {
      setAnswers((prev) => ({ ...prev, [q.id]: q.options[selected]!.id }));
    }
    if (isCorrect) setScore((s) => s + 1);
  };

  const submitIfPossible = async () => {
    if (!quiz?.id) return;
    const answerList = Object.entries(answers).map(([questionId, optionId]) => ({
      questionId,
      optionId,
    }));

    if (answerList.length === 0) return;

    setSubmitLoading(true);
    setSubmitError(null);

    try {
      const res = await fetch(`/api/learner/quizzes/${encodeURIComponent(quiz.id)}/submit`, {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ answers: answerList }),
      });

      const payload = (await res.json().catch(() => null)) as
        | ApiResponse<QuizSubmissionResponse>
        | null;

      if (!res.ok || !payload?.success) {
        throw new Error(payload?.message ?? `Submit failed (${res.status})`);
      }

      setSubmission(payload.data);
    } catch (e) {
      setSubmitError(e instanceof Error ? e.message : "Failed to submit quiz");
    } finally {
      setSubmitLoading(false);
    }
  };

  const next = () => {
    if (index + 1 >= questions.length) {
      setDone(true);
      void submitIfPossible();
    } else {
      setIndex((i) => i + 1);
      setSelected(null);
      setConfirmed(false);
    }
  };

  const restart = () => {
    setIndex(0);
    setSelected(null);
    setConfirmed(false);
    setScore(0);
    setDone(false);
    setAnswers({});
    setSubmission(null);
    setSubmitLoading(false);
    setSubmitError(null);
  };

  useEffect(() => {
    setAiLoading(false);
    setAiText(null);
    setAiError(null);
    setAiSource(null);
    lastAiKeyRef.current = null;
  }, [index]);

  useEffect(() => {
    if (!confirmed) return;
    if (!q) return;
    if (selected === null) return;
    if (isCorrect) return;
    if (!aiKey) return;
    if (lastAiKeyRef.current === aiKey) return;
    if (q.correctIndex === null) return;

    lastAiKeyRef.current = aiKey;

    const controller = new AbortController();

    setAiLoading(true);
    setAiError(null);

    (async () => {
      try {
        const res = await fetch("/api/ai/quiz-remediation", {
          method: "POST",
          headers: { "content-type": "application/json" },
          signal: controller.signal,
          body: JSON.stringify({
            question: q.question,
            options: q.options.map((o) => o.text),
            correctIndex: q.correctIndex,
            selectedIndex: selected,
          }),
        });
        const data = (await res.json().catch(() => null)) as
          | { text?: string; error?: string; source?: "local" | "cloud" }
          | null;

        if (!res.ok) {
          throw new Error(data?.error ?? `AI request failed (${res.status})`);
        }

        const text = data?.text;
        if (typeof text !== "string" || !text.trim()) {
          throw new Error("AI returned no text");
        }

        setAiText(text);
        setAiSource(data?.source ?? null);
      } catch (e) {
        if (e instanceof DOMException && e.name === "AbortError") return;
        setAiError(e instanceof Error ? e.message : "AI request failed");
      } finally {
        setAiLoading(false);
      }
    })();

    return () => {
      controller.abort();
    };
  }, [aiKey, confirmed, q, selected]);

  if (done) {
    const outScore = submission?.scoreObtained ?? score;
    const outMax = submission?.maxScore ?? questions.length;
    const pct = outMax ? Math.round((outScore / outMax) * 100) : 0;
    return (
      <div className="mx-auto max-w-2xl px-6 py-20">
        <div className="rounded-2xl border border-border bg-card p-10 text-center shadow-card">
          <div className="mx-auto grid h-16 w-16 place-items-center rounded-full bg-secondary text-primary">
            <Trophy className="h-8 w-8" />
          </div>
          <h1 className="mt-5 font-display text-4xl font-semibold">
            {pct >= 75 ? "Excellent work!" : "Good effort!"}
          </h1>
          <p className="mt-2 text-muted-foreground">
            You scored {outScore} / {outMax} ({pct}%)
          </p>
          {submitLoading && (
            <p className="mt-2 text-sm text-muted-foreground">Submitting your answers…</p>
          )}
          {submitError && (
            <p className="mt-2 text-sm text-muted-foreground">{submitError}</p>
          )}
          <div className="mt-6 h-2 w-full rounded-full bg-secondary">
            <div
              className="h-2 rounded-full bg-primary"
              style={{ width: `${pct}%` }}
            />
          </div>
          <button
            onClick={restart}
            className="mt-8 inline-flex items-center gap-2 rounded-md bg-primary px-5 py-2.5 text-sm font-semibold text-primary-foreground"
          >
            <RotateCcw className="h-4 w-4" /> Try again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl px-6 py-14">
      <div className="mb-8 rounded-2xl border border-border bg-card p-6 shadow-card">
        <p className="text-sm font-medium text-foreground">Load a chapter quiz</p>
        <p className="mt-1 text-sm text-muted-foreground">
          Enter a chapterId to fetch a published quiz from the backend.
        </p>
        <form
          className="mt-4 flex flex-col gap-3 sm:flex-row"
          onSubmit={(e) => {
            e.preventDefault();
            setChapterId(chapterIdInput.trim());
          }}
        >
          <input
            value={chapterIdInput}
            onChange={(e) => setChapterIdInput(e.target.value)}
            placeholder="chapterId (e.g. 9f0b...)"
            className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm shadow-sm outline-none focus:border-ring focus:ring-2 focus:ring-ring/30"
          />
          <button
            type="submit"
            className="rounded-md bg-foreground px-5 py-2.5 text-sm font-semibold text-background"
          >
            Load quiz
          </button>
        </form>

        {quizLoading && (
          <p className="mt-3 text-sm text-muted-foreground">Loading quiz…</p>
        )}
        {quizError && (
          <p className="mt-3 text-sm text-muted-foreground">{quizError}</p>
        )}
        {quiz?.title && (
          <p className="mt-3 text-sm text-muted-foreground">Loaded: {quiz.title}</p>
        )}
      </div>

      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-accent">
            Cognitive Science · Quiz
          </p>
          <h1 className="mt-1 font-display text-3xl font-semibold">
            {quiz?.title ?? "Adaptive Learning Concepts"}
          </h1>
          {quiz?.description && (
            <p className="mt-2 text-sm text-muted-foreground">{quiz.description}</p>
          )}
        </div>
        <p className="text-sm font-semibold">
          Score <span className="text-primary">{score}</span> / {questions.length}
        </p>
      </div>

      {/* Progress dots */}
      <div className="mt-6 flex items-center gap-2">
        {questions.map((_, i) => (
          <div
            key={i}
            className={`h-2 flex-1 rounded-full transition-colors ${
              i < index
                ? "bg-success"
                : i === index
                  ? "bg-primary"
                  : "bg-secondary"
            }`}
          />
        ))}
      </div>

      <div className="mt-10 rounded-2xl border border-border bg-card p-8 shadow-card">
        <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
          Question {index + 1} of {questions.length}
        </p>
        <h2 className="mt-2 font-display text-2xl font-semibold leading-snug">
          {q.question}
        </h2>

        <div className="mt-6 space-y-3">
          {q.options.map((opt, i) => {
            const letter = String.fromCharCode(65 + i);
            const isSelected = selected === i;
            const showCorrect = confirmed && q.correctIndex !== null && i === q.correctIndex;
            const showWrong =
              confirmed &&
              isSelected &&
              q.correctIndex !== null &&
              i !== q.correctIndex;
            return (
              <button
                key={i}
                disabled={confirmed}
                onClick={() => setSelected(i)}
                className={`flex w-full items-center gap-4 rounded-xl border p-4 text-left transition-all ${
                  showCorrect
                    ? "border-success bg-success/10"
                    : showWrong
                      ? "border-danger bg-danger/10"
                      : isSelected
                        ? "border-primary bg-primary/5"
                        : "border-border bg-card hover:border-primary/40"
                }`}
              >
                <span
                  className={`grid h-9 w-9 shrink-0 place-items-center rounded-full text-sm font-semibold ${
                    showCorrect
                      ? "bg-success text-white"
                      : showWrong
                        ? "bg-danger text-white"
                        : isSelected
                          ? "bg-primary text-primary-foreground"
                          : "bg-secondary text-foreground"
                  }`}
                >
                  {showCorrect ? (
                    <Check className="h-4 w-4" />
                  ) : showWrong ? (
                    <X className="h-4 w-4" />
                  ) : (
                    letter
                  )}
                </span>
                <span className="text-sm font-medium">{opt.text}</span>
              </button>
            );
          })}
        </div>

        {confirmed && (
          <div
            className={`mt-6 rounded-xl p-4 text-sm ${
              q.correctIndex === null
                ? "bg-secondary/40 text-muted-foreground"
                : isCorrect
                  ? "bg-success/10 text-success"
                  : "bg-danger/10 text-danger"
            }`}
          >
            <p className="font-semibold">
              {q.correctIndex === null
                ? "Answer saved."
                : isCorrect
                  ? "Correct."
                  : "Not quite."}
            </p>
            {q.correctIndex !== null && (
              <p className="mt-1 text-foreground/80">
                Correct answer: {q.options[q.correctIndex]?.text}
              </p>
            )}
          </div>
        )}

        {confirmed && !isCorrect && (
          <div className="mt-4 rounded-xl border border-border bg-secondary/40 p-4 text-sm">
            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
              AI remediation
            </p>

            {aiSource && (
              <p className="mt-1 text-xs text-muted-foreground">
                Source: {aiSource === "cloud" ? "cloud" : "local fallback"}
              </p>
            )}

            {aiLoading && (
              <p className="mt-2 text-sm text-muted-foreground">
                Generating a personalised explanation…
              </p>
            )}

            {aiError && (
              <p className="mt-2 text-sm text-muted-foreground">
                {aiError}
              </p>
            )}

            {aiText && (
              <div className="mt-2 whitespace-pre-wrap text-sm text-foreground">
                {aiText}
              </div>
            )}

            {!aiLoading && !aiText && !aiError && (
              <p className="mt-2 text-sm text-muted-foreground">
                Waiting for your answer…
              </p>
            )}
          </div>
        )}

        <div className="mt-6 flex justify-end">
          {!confirmed ? (
            <button
              onClick={confirm}
              disabled={selected === null}
              className="rounded-md bg-primary px-5 py-2.5 text-sm font-semibold text-primary-foreground disabled:opacity-40"
            >
              Confirm answer
            </button>
          ) : (
            <button
              onClick={next}
              className="rounded-md bg-foreground px-5 py-2.5 text-sm font-semibold text-background"
            >
              {index + 1 === questions.length ? "See results" : "Next question"}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
