import { createFileRoute, Link } from "@tanstack/react-router";
import {
  Sparkles,
  Users,
  Activity,
  BookOpen,
  Brain,
  LineChart,
  ArrowRight,
} from "lucide-react";

export const Route = createFileRoute("/")({
  component: Home,
  head: () => ({
    meta: [
      { title: "LearnAdapt — Adaptive & Collaborative E-Learning" },
      {
        name: "description",
        content:
          "Personalised learning paths, peer struggle matching, and a cognitive load heatmap that turns difficulty into improvement.",
      },
    ],
  }),
});

const stats = [
  { value: "48k+", label: "Active learners" },
  { value: "1,200", label: "Adaptive courses" },
  { value: "92%", label: "Completion rate" },
  { value: "3", label: "Roles supported" },
];

const features = [
  {
    icon: Brain,
    title: "Adaptive learning paths",
    body: "AI pedagogical engine tailors content to each learner's level, pace, and style.",
  },
  {
    icon: Sparkles,
    title: "Intelligent remediation",
    body: "Error pattern analysis proposes simpler explanations and targeted revision exercises.",
  },
  {
    icon: Users,
    title: "Peer struggle matching",
    body: "Two learners stuck on the same concept get matched for a 10-minute Protégé Effect session.",
  },
  {
    icon: Activity,
    title: "Cognitive load heatmap",
    body: "Teachers see exactly where students slow down, fail, or drop off — paragraph by paragraph.",
  },
  {
    icon: BookOpen,
    title: "Structured courses",
    body: "Courses → modules → chapters → lessons, with prerequisites, tags, and rich media.",
  },
  {
    icon: LineChart,
    title: "Progress that means something",
    body: "Curves, histograms, and streaks track learning — not just clicks.",
  },
];

function Home() {
  return (
    <div>
      {/* Hero */}
      <section className="bg-hero">
        <div className="mx-auto max-w-7xl px-6 pt-20 pb-24 md:pt-28 md:pb-32">
          <div className="max-w-3xl">
            <span className="inline-flex items-center gap-2 rounded-full border border-border bg-card/70 px-3 py-1 text-xs font-medium text-muted-foreground shadow-card backdrop-blur">
              <span className="h-1.5 w-1.5 rounded-full bg-accent" />
              Built on cognitive science · v3.0
            </span>
            <h1 className="mt-6 font-display text-5xl font-semibold leading-[1.05] tracking-tight md:text-7xl">
              Learning that adapts <span className="text-primary">to you</span>,
              not the other way around.
            </h1>
            <p className="mt-6 max-w-2xl text-lg text-muted-foreground">
              LearnAdapt personalises every lesson, matches you with a peer
              stuck on the same concept, and gives teachers a real-time map of
              where understanding breaks down.
            </p>
            <div className="mt-8 flex flex-wrap items-center gap-3">
              <Link
                to="/courses"
                className="inline-flex items-center gap-2 rounded-md bg-primary px-5 py-3 text-sm font-semibold text-primary-foreground shadow-card transition-transform hover:-translate-y-px hover:shadow-elevated"
              >
                Browse courses <ArrowRight className="h-4 w-4" />
              </Link>
              <Link
                to="/dashboard"
                className="inline-flex items-center gap-2 rounded-md border border-border bg-card px-5 py-3 text-sm font-semibold text-foreground hover:bg-secondary"
              >
                Open dashboard
              </Link>
            </div>
          </div>

          {/* Stats */}
          <div className="mt-16 grid grid-cols-2 gap-4 md:grid-cols-4">
            {stats.map((s) => (
              <div
                key={s.label}
                className="rounded-xl border border-border bg-card p-5 shadow-card"
              >
                <p className="font-display text-3xl font-semibold text-foreground">
                  {s.value}
                </p>
                <p className="mt-1 text-sm text-muted-foreground">{s.label}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="mx-auto max-w-7xl px-6 py-24">
        <div className="flex flex-wrap items-end justify-between gap-6">
          <div>
            <p className="text-sm font-medium text-accent">What's inside</p>
            <h2 className="mt-2 max-w-xl font-display text-4xl font-semibold tracking-tight md:text-5xl">
              Six systems working together for one outcome: understanding.
            </h2>
          </div>
          <Link
            to="/peers"
            className="text-sm font-semibold text-primary hover:underline"
          >
            See peer matching in action →
          </Link>
        </div>

        <div className="mt-12 grid gap-5 md:grid-cols-2 lg:grid-cols-3">
          {features.map((f) => (
            <article
              key={f.title}
              className="group rounded-2xl border border-border bg-card p-6 shadow-card transition-all hover:-translate-y-1 hover:shadow-elevated"
            >
              <div className="grid h-11 w-11 place-items-center rounded-lg bg-secondary text-primary transition-colors group-hover:bg-primary group-hover:text-primary-foreground">
                <f.icon className="h-5 w-5" />
              </div>
              <h3 className="mt-5 font-display text-xl font-semibold">
                {f.title}
              </h3>
              <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
                {f.body}
              </p>
            </article>
          ))}
        </div>
      </section>

      {/* Where Real AI Should Be */}
      <section className="mx-auto max-w-7xl px-6 pb-24">
        <div className="rounded-3xl border border-border bg-card p-10 shadow-card md:p-14">
          <h2 className="font-display text-3xl font-semibold tracking-tight">
            Where Real AI Should Be 🟢
          </h2>
          <p className="mt-3 text-muted-foreground">
            According to your Cahier des Charges, the AI lives in these 4 places:
          </p>

          <ol className="mt-6 space-y-5 text-sm">
            <li>
              <p className="font-semibold">1. 🧩 Quiz Remediation Engine</p>
              <p className="mt-1 text-muted-foreground">
                Real AI = Send wrong answers to Claude/OpenAI API → get a personalised explanation back.
                Student answers wrong → API call → “Here’s why you got it wrong + simplified explanation”.
              </p>
            </li>
            <li>
              <p className="font-semibold">2. 🔥 Cognitive Load Heatmap</p>
              <p className="mt-1 text-muted-foreground">
                Real AI = Analyse real student behaviour data (time on page, re-reads, fail rate) → calculate difficulty score per section automatically.
              </p>
            </li>
            <li>
              <p className="font-semibold">3. 🤝 Peer Struggle Matching</p>
              <p className="mt-1 text-muted-foreground">
                Real AI = Algorithm that watches all students in real time → detects 2 students stuck on same concept → triggers match notification.
              </p>
            </li>
            <li>
              <p className="font-semibold">4. ✍️ AI Rewrite Assistant</p>
              <p className="mt-1 text-muted-foreground">
                Real AI = Send a red-flagged lesson paragraph to Claude/OpenAI → get a simplified rewrite suggestion back.
              </p>
            </li>
          </ol>

          <div className="mt-8 rounded-2xl border border-border bg-secondary/40 p-6">
            <p className="font-semibold">The Simplest Way to Add Real AI Now</p>
            <p className="mt-2 text-sm text-muted-foreground">
              Simplest implementation: use a server proxy for cloud AI (keeps keys private), or run a free offline mode locally.
            </p>
            <div className="mt-4 grid gap-2 text-sm">
              <p>✅ Quiz feedback & rewrite assistant (cloud or offline)</p>
              <p>✅ Simulated heatmap scoring logic</p>
              <p>✅ Peer matching algorithm</p>
            </div>
          </div>
        </div>
      </section>

      {/* Protégé CTA */}
      <section className="mx-auto max-w-7xl px-6 pb-8">
        <div className="overflow-hidden rounded-3xl border border-border bg-card p-10 shadow-card md:p-14">
          <div className="grid items-center gap-10 md:grid-cols-2">
            <div>
              <p className="text-sm font-medium text-accent">
                The Protégé Effect
              </p>
              <h2 className="mt-2 font-display text-4xl font-semibold tracking-tight">
                Explaining a concept reorganises your own understanding of it.
              </h2>
              <p className="mt-4 text-muted-foreground">
                When two students get stuck on the same idea, LearnAdapt quietly
                pairs them. One explains, one questions — then they swap. Most
                blockages dissolve in under ten minutes.
              </p>
              <Link
                to="/peers"
                className="mt-6 inline-flex items-center gap-2 rounded-md bg-foreground px-5 py-3 text-sm font-semibold text-background hover:opacity-90"
              >
                Try peer matching <ArrowRight className="h-4 w-4" />
              </Link>
            </div>
            <div className="relative h-64 rounded-2xl bg-hero p-6 md:h-80">
              <div className="absolute left-6 top-6 w-56 rounded-xl border border-border bg-card p-4 shadow-card">
                <p className="text-xs font-medium text-muted-foreground">
                  Anna · stuck on
                </p>
                <p className="mt-1 text-sm font-semibold">Eigenvectors</p>
                <div className="mt-3 h-1.5 w-full rounded-full bg-secondary">
                  <div className="h-1.5 w-2/3 rounded-full bg-danger" />
                </div>
              </div>
              <div className="absolute right-6 bottom-6 w-56 rounded-xl border border-border bg-card p-4 shadow-card">
                <p className="text-xs font-medium text-muted-foreground">
                  Marco · stuck on
                </p>
                <p className="mt-1 text-sm font-semibold">Eigenvectors</p>
                <div className="mt-3 h-1.5 w-full rounded-full bg-secondary">
                  <div className="h-1.5 w-3/5 rounded-full bg-danger" />
                </div>
              </div>
              <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 rounded-full bg-primary px-4 py-2 text-xs font-semibold text-primary-foreground shadow-elevated">
                Match ↔
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
