import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { MessageCircle, Mic, Send, Timer, Sparkles } from "lucide-react";
import { toast } from "sonner";
import { findPeerMatch } from "@/lib/peer-matching";

export const Route = createFileRoute("/peers")({
  component: PeersPage,
  head: () => ({
    meta: [
      { title: "Peer Matching — LearnAdapt" },
      {
        name: "description",
        content:
          "Get matched with a peer stuck on the same concept for a 10-minute Protégé Effect session.",
      },
    ],
  }),
});

type Peer = {
  id: string;
  name: string;
  initials: string;
  concept: string;
  course: string;
  blockedFor: string;
};

const peers: Peer[] = [
  { id: "1", name: "Marco D.", initials: "MD", concept: "Eigenvectors", course: "Linear Algebra", blockedFor: "12 min" },
  { id: "2", name: "Sofia R.", initials: "SR", concept: "Confidence intervals", course: "Statistics", blockedFor: "8 min" },
  { id: "3", name: "Daniel K.", initials: "DK", concept: "useEffect cleanup", course: "Full-Stack Web", blockedFor: "21 min" },
  { id: "4", name: "Yara N.", initials: "YN", concept: "Backpropagation", course: "Machine Learning", blockedFor: "5 min" },
];

const steps = [
  { n: 1, t: "Detection", b: "We notice you and a peer stuck on the same concept." },
  { n: 2, t: "Discreet match", b: "A non-intrusive invite for a 10-minute exchange." },
  { n: 3, t: "Role swap", b: "One explains, one questions — then swap roles." },
  { n: 4, t: "Impact check", b: "Quiz score before vs after measures real lift." },
];

function PeersPage() {
  const [active, setActive] = useState<Peer | null>(null);
  const [secondsLeft, setSecondsLeft] = useState(600);
  const [messages, setMessages] = useState<{ from: "me" | "them"; text: string }[]>(
    [],
  );
  const [draft, setDraft] = useState("");

  const conceptOptions = Array.from(new Set(peers.map((p) => p.concept)));
  const [myConcept, setMyConcept] = useState(conceptOptions[0] ?? "");
  const [watching, setWatching] = useState(false);
  const [lastMatchId, setLastMatchId] = useState<string | null>(null);

  useEffect(() => {
    if (!active) return;
    setSecondsLeft(600);
    setMessages([
      {
        from: "them",
        text: `Hey! I've been stuck on ${active.concept.toLowerCase()} too 😅`,
      },
    ]);
  }, [active]);

  useEffect(() => {
    if (!watching) return;
    if (!myConcept.trim()) return;
    if (active) return; // don't auto-match when already in a session

    const t = setInterval(() => {
      const match = findPeerMatch({
        myConcept,
        peers: peers.map((p) => ({
          id: p.id,
          name: p.name,
          concept: p.concept,
          blockedFor: p.blockedFor,
        })),
        excludePeerId: lastMatchId ?? undefined,
      });

      if (!match) return;
      if (match.id === lastMatchId) return;

      setLastMatchId(match.id);

      const peer = peers.find((p) => p.id === match.id);
      if (!peer) return;

      toast.success("Peer match found", {
        description: `${peer.name} is also stuck on ${peer.concept}.`,
        action: {
          label: "Start session",
          onClick: () => setActive(peer),
        },
      });
    }, 1500);

    return () => clearInterval(t);
  }, [active, lastMatchId, myConcept, watching]);

  useEffect(() => {
    if (!active) return;
    const t = setInterval(() => setSecondsLeft((s) => Math.max(0, s - 1)), 1000);
    return () => clearInterval(t);
  }, [active]);

  const send = () => {
    if (!draft.trim()) return;
    setMessages((m) => [...m, { from: "me", text: draft }]);
    setDraft("");
  };

  const mm = Math.floor(secondsLeft / 60).toString().padStart(2, "0");
  const ss = (secondsLeft % 60).toString().padStart(2, "0");

  return (
    <div className="mx-auto max-w-7xl px-6 py-12">
      <div className="max-w-2xl">
        <p className="text-sm font-medium text-accent">Peer struggle matching</p>
        <h1 className="mt-2 font-display text-5xl font-semibold tracking-tight">
          You're not stuck alone.
        </h1>
        <p className="mt-3 text-muted-foreground">
          Real-time matching with another learner blocked on the same concept.
          Ten minutes, two roles, one shared breakthrough.
        </p>
      </div>

      <div className="mt-8 max-w-2xl rounded-2xl border border-border bg-card p-5 shadow-card">
        <p className="text-sm font-semibold">Your current struggle</p>
        <div className="mt-3 flex flex-wrap items-center gap-3">
          <label className="text-sm text-muted-foreground">Concept</label>
          <select
            value={myConcept}
            onChange={(e) => setMyConcept(e.target.value)}
            disabled={watching}
            className="rounded-md border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-2 focus:ring-ring/30 disabled:opacity-60"
          >
            {conceptOptions.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
          <button
            onClick={() => {
              setWatching((w) => !w);
              setLastMatchId(null);
            }}
            className={`rounded-md px-4 py-2 text-sm font-semibold ${
              watching
                ? "border border-border bg-background text-foreground hover:bg-secondary"
                : "bg-primary text-primary-foreground hover:-translate-y-px"
            }`}
          >
            {watching ? "Stop watching" : "I'm stuck — find a peer"}
          </button>
        </div>
        <p className="mt-2 text-xs text-muted-foreground">
          When another learner is stuck on the same concept, we’ll notify you.
        </p>
      </div>

      <div className="mt-10 grid gap-6 lg:grid-cols-[1fr_360px]">
        {/* Live peers + session */}
        <div className="space-y-6">
          <section className="rounded-2xl border border-border bg-card p-6 shadow-card">
            <div className="flex items-center justify-between">
              <h2 className="font-display text-xl font-semibold">
                Available peers right now
              </h2>
              <span className="inline-flex items-center gap-2 rounded-full bg-success/10 px-3 py-1 text-xs font-semibold text-success">
                <span className="h-1.5 w-1.5 animate-pulse rounded-full bg-success" />
                {peers.length} online
              </span>
            </div>
            <ul className="mt-5 divide-y divide-border">
              {peers.map((p) => (
                <li
                  key={p.id}
                  className="flex flex-wrap items-center justify-between gap-3 py-4"
                >
                  <div className="flex items-center gap-3">
                    <div className="grid h-11 w-11 place-items-center rounded-full bg-secondary font-semibold text-foreground">
                      {p.initials}
                    </div>
                    <div>
                      <p className="font-semibold">{p.name}</p>
                      <p className="text-xs text-muted-foreground">
                        Stuck on{" "}
                        <span className="font-medium text-foreground">
                          {p.concept}
                        </span>{" "}
                        · {p.course} · {p.blockedFor}
                      </p>
                    </div>
                  </div>
                  <button
                    onClick={() => setActive(p)}
                    className="rounded-md bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground hover:-translate-y-px"
                  >
                    Connect
                  </button>
                </li>
              ))}
            </ul>
          </section>

          {active && (
            <section className="rounded-2xl border border-primary/30 bg-card shadow-elevated">
              <div className="flex flex-wrap items-center justify-between gap-3 border-b border-border p-5">
                <div className="flex items-center gap-3">
                  <div className="grid h-10 w-10 place-items-center rounded-full bg-primary font-semibold text-primary-foreground">
                    {active.initials}
                  </div>
                  <div>
                    <p className="font-semibold">{active.name}</p>
                    <p className="text-xs text-muted-foreground">
                      Topic · {active.concept}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <span className="inline-flex items-center gap-2 rounded-full bg-warning/10 px-3 py-1 text-xs font-semibold text-warning">
                    <Timer className="h-3.5 w-3.5" />
                    {mm}:{ss}
                  </span>
                  <button
                    onClick={() => setActive(null)}
                    className="rounded-md border border-border px-3 py-1.5 text-xs font-medium hover:bg-secondary"
                  >
                    End session
                  </button>
                </div>
              </div>

              <div className="grid gap-4 p-5 sm:grid-cols-2">
                <div className="rounded-xl bg-secondary/60 p-4 text-sm">
                  <p className="text-xs font-semibold text-muted-foreground">
                    Your role
                  </p>
                  <p className="mt-1 font-display text-lg font-semibold">
                    Explainer 🎤
                  </p>
                  <p className="mt-1 text-xs text-muted-foreground">
                    Try to teach the concept in your own words. You'll swap at
                    5:00.
                  </p>
                </div>
                <div className="rounded-xl bg-secondary/60 p-4 text-sm">
                  <p className="text-xs font-semibold text-muted-foreground">
                    Their role
                  </p>
                  <p className="mt-1 font-display text-lg font-semibold">
                    Curious learner 🙋
                  </p>
                  <p className="mt-1 text-xs text-muted-foreground">
                    They'll ask questions until something clicks.
                  </p>
                </div>
              </div>

              <div className="border-t border-border">
                <div className="h-64 space-y-3 overflow-y-auto p-5">
                  {messages.map((m, i) => (
                    <div
                      key={i}
                      className={`flex ${m.from === "me" ? "justify-end" : "justify-start"}`}
                    >
                      <div
                        className={`max-w-[75%] rounded-2xl px-4 py-2 text-sm ${
                          m.from === "me"
                            ? "bg-primary text-primary-foreground"
                            : "bg-secondary text-foreground"
                        }`}
                      >
                        {m.text}
                      </div>
                    </div>
                  ))}
                </div>
                <div className="flex items-center gap-2 border-t border-border p-3">
                  <button className="grid h-10 w-10 place-items-center rounded-md hover:bg-secondary">
                    <Mic className="h-4 w-4" />
                  </button>
                  <input
                    value={draft}
                    onChange={(e) => setDraft(e.target.value)}
                    onKeyDown={(e) => e.key === "Enter" && send()}
                    placeholder="Type your message…"
                    className="flex-1 rounded-md border border-border bg-background px-3 py-2 text-sm outline-none focus:border-primary"
                  />
                  <button
                    onClick={send}
                    className="inline-flex items-center gap-1 rounded-md bg-primary px-3 py-2 text-sm font-semibold text-primary-foreground"
                  >
                    <Send className="h-4 w-4" /> Send
                  </button>
                </div>
              </div>
            </section>
          )}
        </div>

        {/* Side */}
        <aside className="space-y-6">
          <div className="rounded-2xl border border-border bg-card p-6 shadow-card">
            <h3 className="font-display text-lg font-semibold">How it works</h3>
            <ol className="mt-4 space-y-4">
              {steps.map((s) => (
                <li key={s.n} className="flex gap-3">
                  <span className="grid h-7 w-7 shrink-0 place-items-center rounded-full bg-primary text-xs font-semibold text-primary-foreground">
                    {s.n}
                  </span>
                  <div>
                    <p className="text-sm font-semibold">{s.t}</p>
                    <p className="text-xs text-muted-foreground">{s.b}</p>
                  </div>
                </li>
              ))}
            </ol>
          </div>

          <div className="rounded-2xl border border-border bg-gradient-to-br from-primary/10 to-accent/10 p-6 shadow-card">
            <Sparkles className="h-5 w-5 text-primary" />
            <h3 className="mt-3 font-display text-lg font-semibold">
              The Protégé Effect
            </h3>
            <p className="mt-2 text-sm text-muted-foreground">
              Preparing to explain a concept forces deeper cognitive
              organisation than re-reading. Two learners stuck on the same
              point build a more robust understanding together — and feel less
              alone in the process.
            </p>
          </div>

          <div className="rounded-2xl border border-border bg-card p-6 shadow-card">
            <MessageCircle className="h-5 w-5 text-accent" />
            <h3 className="mt-3 font-display text-lg font-semibold">
              After the session
            </h3>
            <p className="mt-2 text-sm text-muted-foreground">
              We re-quiz both learners on the same concept and ask a one-tap
              feedback: did this help? Real lift, measured every time.
            </p>
          </div>
        </aside>
      </div>
    </div>
  );
}
