import { createFileRoute } from "@tanstack/react-router";
import { useI18n } from "@/lib/app-providers";
import { TrendingUp, Target, Award, Brain } from "lucide-react";

export const Route = createFileRoute("/dashboard/performance")({
  component: PerformancePage,
});

const kpis = [
  { icon: TrendingUp, label: "Avg quiz score", value: "87%", delta: "+4%" },
  { icon: Target, label: "Mastery", value: "72%", delta: "+9%" },
  { icon: Award, label: "Badges", value: "14", delta: "+2" },
  { icon: Brain, label: "Concepts grasped", value: "208", delta: "+18" },
];

const weeks = [40, 55, 48, 62, 70, 65, 78, 82, 74, 88, 92, 86];

function PerformancePage() {
  const { t } = useI18n();
  const max = Math.max(...weeks);
  return (
    <div>
      <h1 className="font-display text-4xl font-semibold tracking-tight">{t("perf.title")}</h1>
      <p className="mt-2 text-sm text-muted-foreground">{t("perf.sub")}</p>

      <div className="mt-8 grid grid-cols-2 gap-4 md:grid-cols-4">
        {kpis.map((k) => (
          <div key={k.label} className="rounded-2xl border border-border bg-card p-5 shadow-card">
            <k.icon className="h-5 w-5 text-primary" />
            <p className="mt-3 font-display text-3xl font-semibold">{k.value}</p>
            <p className="mt-1 text-sm text-muted-foreground">{k.label}</p>
            <p className="mt-2 text-xs font-semibold text-success">{k.delta}</p>
          </div>
        ))}
      </div>

      <section className="mt-10 rounded-2xl border border-border bg-card p-6 shadow-card">
        <h2 className="font-display text-2xl font-semibold">Weekly score trend</h2>
        <div className="mt-6 flex h-48 items-end gap-2">
          {weeks.map((v, i) => (
            <div key={i} className="flex flex-1 flex-col items-center gap-1">
              <div
                className="w-full rounded-md bg-primary/80 transition-all hover:bg-primary"
                style={{ height: `${(v / max) * 100}%` }}
                title={`Week ${i + 1}: ${v}%`}
              />
              <span className="text-[10px] text-muted-foreground">W{i + 1}</span>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
