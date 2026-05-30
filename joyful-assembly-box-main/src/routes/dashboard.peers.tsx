import { createFileRoute } from "@tanstack/react-router";
import { useI18n } from "@/lib/app-providers";
import { Users, Clock, CheckCircle2 } from "lucide-react";

export const Route = createFileRoute("/dashboard/peers")({
  component: PeerSessionsPage,
});

const sessions = [
  { peer: "Marco D.", topic: "Eigenvectors", status: "Completed", outcome: "Unblocked", when: "Yesterday" },
  { peer: "Sophia L.", topic: "p-values", status: "Completed", outcome: "Unblocked", when: "2 days ago" },
  { peer: "Yuki T.", topic: "React hooks", status: "Upcoming", outcome: "—", when: "Tomorrow 18:30" },
  { peer: "Amine K.", topic: "Big-O notation", status: "Completed", outcome: "Partial", when: "Last week" },
];

function PeerSessionsPage() {
  const { t } = useI18n();
  return (
    <div>
      <h1 className="font-display text-4xl font-semibold tracking-tight">{t("peers.title")}</h1>
      <p className="mt-2 text-sm text-muted-foreground">{t("peers.sub")}</p>

      <div className="mt-8 overflow-hidden rounded-2xl border border-border bg-card shadow-card">
        <table className="w-full text-sm">
          <thead className="bg-secondary/60 text-left text-xs uppercase text-muted-foreground">
            <tr>
              <th className="px-5 py-3">Peer</th>
              <th className="px-5 py-3">Topic</th>
              <th className="px-5 py-3">Status</th>
              <th className="px-5 py-3">Outcome</th>
              <th className="px-5 py-3">When</th>
            </tr>
          </thead>
          <tbody>
            {sessions.map((s, i) => (
              <tr key={i} className="border-t border-border">
                <td className="px-5 py-3 font-medium">
                  <span className="inline-flex items-center gap-2">
                    <Users className="h-4 w-4 text-primary" />
                    {s.peer}
                  </span>
                </td>
                <td className="px-5 py-3">{s.topic}</td>
                <td className="px-5 py-3">
                  <span
                    className={`inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-xs font-semibold ${
                      s.status === "Completed"
                        ? "bg-[color:var(--color-heat-low)] text-emerald-950"
                        : "bg-[color:var(--color-heat-mid)] text-yellow-950"
                    }`}
                  >
                    {s.status === "Completed" ? (
                      <CheckCircle2 className="h-3 w-3" />
                    ) : (
                      <Clock className="h-3 w-3" />
                    )}
                    {s.status}
                  </span>
                </td>
                <td className="px-5 py-3 text-muted-foreground">{s.outcome}</td>
                <td className="px-5 py-3 text-muted-foreground">{s.when}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
