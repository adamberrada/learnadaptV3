import { createFileRoute } from "@tanstack/react-router";
import {
  BookOpen,
  CheckCircle2,
  Clock,
  Flame,
} from "lucide-react";
import { useI18n } from "@/lib/app-providers";
import { buildHeatmap } from "@/lib/heatmap";
import { useMemo, useState } from "react";

export const Route = createFileRoute("/dashboard/")({
  component: OverviewPage,
});

const activeCourses = [
  { title: "Statistics for Decision Making", chapter: "Ch. 6 · Hypothesis Testing", progress: 62 },
  { title: "Full-Stack Web Engineering", chapter: "Ch. 2 · React Fundamentals", progress: 18 },
  { title: "Cognitive Science Foundations", chapter: "Ch. 4 · Memory Systems", progress: 78 },
];

const heatmapLabels = [
  "Intro to probability",
  "Random variables",
  "Distributions",
  "Expected value",
  "Variance",
  "Sampling",
  "Central limit theorem",
  "Confidence intervals",
  "Null hypothesis",
  "p-values",
  "t-tests",
  "Chi-square",
  "Regression intro",
  "Correlation vs cause",
];

const heatColors = [
  "bg-[color:var(--color-heat-low)] text-emerald-950",
  "bg-[color:var(--color-heat-mid)] text-yellow-950",
  "bg-[color:var(--color-heat-high)] text-red-950",
];
const heatLabels = ["Easy", "Watch", "Blocker"];

function OverviewPage() {
  const { t } = useI18n();
  const heatmap = useMemo(() => buildHeatmap(heatmapLabels), []);

  const [draft, setDraft] = useState(
    "Cognitive load is the amount of mental effort your working memory uses right now. When a lesson overloads working memory, students slow down, re-read, and start making more mistakes — even if they're trying hard.",
  );
  const [rewriteLoading, setRewriteLoading] = useState(false);
  const [rewriteText, setRewriteText] = useState<string | null>(null);
  const [rewriteError, setRewriteError] = useState<string | null>(null);
  const [rewriteSource, setRewriteSource] = useState<"local" | "cloud" | null>(null);
  const stats = [
    { icon: BookOpen, label: t("dash.stats.courses"), value: "6", tint: "text-primary" },
    { icon: CheckCircle2, label: t("dash.stats.quizzes"), value: "42", tint: "text-success" },
    { icon: Clock, label: t("dash.stats.hours"), value: "87", tint: "text-accent" },
    { icon: Flame, label: t("dash.stats.streak"), value: "12", tint: "text-warning" },
  ];

  return (
    <>
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="text-sm font-medium text-accent">{t("dash.welcome")}</p>
          <h1 className="mt-1 font-display text-4xl font-semibold tracking-tight">
            {t("dash.hi")} 🔥
          </h1>
        </div>
        <button className="rounded-md border border-border bg-card px-4 py-2 text-sm font-medium hover:bg-secondary">
          {t("dash.thisweek")} ▾
        </button>
      </div>

      <div className="mt-8 grid grid-cols-2 gap-4 md:grid-cols-4">
        {stats.map((s) => (
          <div
            key={s.label}
            className="rounded-2xl border border-border bg-card p-5 shadow-card"
          >
            <s.icon className={`h-5 w-5 ${s.tint}`} />
            <p className="mt-3 font-display text-3xl font-semibold">{s.value}</p>
            <p className="mt-1 text-sm text-muted-foreground">{s.label}</p>
          </div>
        ))}
      </div>

      <section className="mt-10">
        <h2 className="font-display text-2xl font-semibold">{t("dash.continue")}</h2>
        <div className="mt-4 grid gap-4 md:grid-cols-3">
          {activeCourses.map((c) => (
            <div
              key={c.title}
              className="rounded-2xl border border-border bg-card p-5 shadow-card"
            >
              <Ring value={c.progress} />
              <h3 className="mt-4 font-display text-lg font-semibold leading-tight">
                {c.title}
              </h3>
              <p className="mt-1 text-xs text-muted-foreground">{c.chapter}</p>
              <button className="mt-4 w-full rounded-md bg-primary px-3 py-2 text-sm font-semibold text-primary-foreground">
                {t("dash.resume")}
              </button>
            </div>
          ))}
        </div>
      </section>

      <section className="mt-10 rounded-2xl border border-border bg-card p-6 shadow-card">
        <div className="flex flex-wrap items-end justify-between gap-3">
          <div>
            <p className="text-sm font-medium text-accent">{t("dash.heatmap.title")}</p>
            <h2 className="mt-1 font-display text-2xl font-semibold">
              Statistics for Decision Making
            </h2>
            <p className="mt-1 text-sm text-muted-foreground">{t("dash.heatmap.sub")}</p>
          </div>
          <div className="flex items-center gap-3 text-xs">
            {heatLabels.map((l, i) => (
              <span key={l} className="inline-flex items-center gap-1.5">
                <span className={`h-3 w-3 rounded-sm ${heatColors[i].split(" ")[0]}`} />
                {l}
              </span>
            ))}
          </div>
        </div>

        <div className="mt-5 grid grid-cols-2 gap-2 sm:grid-cols-4 lg:grid-cols-7">
          {heatmap.map((h) => (
            <div
              key={h.label}
              className={`flex h-24 flex-col justify-between rounded-lg p-3 text-xs font-semibold ${heatColors[h.level]}`}
              title={`${h.label} — ${heatLabels[h.level]} (score ${h.score}/100)`}
            >
              <span className="opacity-70">Lesson</span>
              <span className="leading-tight">{h.label}</span>
            </div>
          ))}
        </div>

        <div className="mt-5 text-xs text-muted-foreground">
          Difficulty is simulated from behaviour signals (time on page, re-reads,
          fail rate, and drop-off).
        </div>
      </section>

      <section className="mt-10 rounded-2xl border border-border bg-card p-6 shadow-card">
        <div className="max-w-2xl">
          <p className="text-sm font-medium text-accent">AI rewrite assistant</p>
          <h2 className="mt-1 font-display text-2xl font-semibold">
            Simplify a red-flagged paragraph
          </h2>
          <p className="mt-1 text-sm text-muted-foreground">
            Generate a clearer rewrite (local offline mode or cloud AI).
          </p>
        </div>

        <div className="mt-5 grid gap-4 lg:grid-cols-2">
          <div className="rounded-xl border border-border bg-background p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
              Original
            </p>
            <textarea
              value={draft}
              onChange={(e) => setDraft(e.target.value)}
              rows={7}
              className="mt-2 w-full resize-none rounded-md border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-2 focus:ring-ring/30"
            />
            <div className="mt-3 flex items-center justify-between gap-3">
              <p className="text-xs text-muted-foreground">
                Tip: paste any paragraph from a lesson.
              </p>
              <button
                disabled={rewriteLoading || !draft.trim()}
                onClick={async () => {
                  setRewriteLoading(true);
                  setRewriteError(null);
                  setRewriteText(null);
                  setRewriteSource(null);
                  try {
                    const res = await fetch("/api/ai/rewrite", {
                      method: "POST",
                      headers: { "content-type": "application/json" },
                      body: JSON.stringify({ text: draft, audience: "general" }),
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
                    setRewriteText(text);
                    setRewriteSource(data?.source ?? null);
                  } catch (e) {
                    setRewriteError(e instanceof Error ? e.message : "AI request failed");
                  } finally {
                    setRewriteLoading(false);
                  }
                }}
                className="rounded-md bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground disabled:opacity-40"
              >
                {rewriteLoading ? "Rewriting…" : "Rewrite with AI"}
              </button>
            </div>
          </div>

          <div className="rounded-xl border border-border bg-secondary/30 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
              Suggested rewrite
            </p>

            {rewriteSource && (
              <p className="mt-1 text-xs text-muted-foreground">
                Source: {rewriteSource === "cloud" ? "cloud" : "local fallback"}
              </p>
            )}

            {rewriteError && (
              <p className="mt-2 text-sm text-muted-foreground">{rewriteError}</p>
            )}

            {!rewriteError && !rewriteText && (
              <p className="mt-2 text-sm text-muted-foreground">
                Click “Rewrite with AI” to generate a suggestion.
              </p>
            )}

            {rewriteText && (
              <div className="mt-2 whitespace-pre-wrap text-sm text-foreground">
                {rewriteText}
              </div>
            )}
          </div>
        </div>
      </section>
    </>
  );
}

function Ring({ value }: { value: number }) {
  const r = 26;
  const c = 2 * Math.PI * r;
  const offset = c - (value / 100) * c;
  return (
    <div className="relative h-16 w-16">
      <svg viewBox="0 0 64 64" className="h-16 w-16 -rotate-90">
        <circle cx="32" cy="32" r={r} stroke="var(--color-secondary)" strokeWidth="6" fill="none" />
        <circle
          cx="32" cy="32" r={r}
          stroke="var(--color-primary)" strokeWidth="6" fill="none"
          strokeLinecap="round"
          strokeDasharray={c} strokeDashoffset={offset}
        />
      </svg>
      <span className="absolute inset-0 grid place-items-center text-sm font-semibold">
        {value}%
      </span>
    </div>
  );
}
