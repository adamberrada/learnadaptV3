import { createFileRoute, Link, Outlet, useRouterState } from "@tanstack/react-router";
import {
  LayoutGrid,
  BookOpen,
  Activity,
  Users,
  Bell,
  Settings,
} from "lucide-react";
import { useI18n } from "@/lib/app-providers";

export const Route = createFileRoute("/dashboard")({
  component: DashboardLayout,
  head: () => ({
    meta: [
      { title: "Dashboard — LearnAdapt" },
      {
        name: "description",
        content:
          "Track your progress, see active courses, and read the cognitive load heatmap.",
      },
    ],
  }),
});

function DashboardLayout() {
  const { t } = useI18n();
  const pathname = useRouterState({ select: (s) => s.location.pathname });

  const items: { to: string; icon: typeof LayoutGrid; label: string; exact?: boolean }[] = [
    { to: "/dashboard", icon: LayoutGrid, label: t("dash.nav.overview"), exact: true },
    { to: "/dashboard/courses", icon: BookOpen, label: t("dash.nav.courses") },
    { to: "/dashboard/performance", icon: Activity, label: t("dash.nav.performance") },
    { to: "/dashboard/peers", icon: Users, label: t("dash.nav.peers") },
    { to: "/dashboard/notifications", icon: Bell, label: t("dash.nav.notifications") },
    { to: "/dashboard/settings", icon: Settings, label: t("dash.nav.settings") },
  ];

  return (
    <div className="mx-auto max-w-7xl px-6 py-10">
      <div className="grid gap-8 lg:grid-cols-[260px_1fr]">
        <aside className="hidden lg:block">
          <div className="sticky top-24 rounded-2xl border border-border bg-card p-5 shadow-card">
            <div className="flex items-center gap-3">
              <div className="grid h-12 w-12 place-items-center rounded-full bg-primary text-lg font-semibold text-primary-foreground">
                AJ
              </div>
              <div>
                <p className="font-semibold">Anouar J.</p>
                <p className="text-xs text-muted-foreground">Student · Premium</p>
              </div>
            </div>
            <nav className="mt-6 space-y-1 text-sm">
              {items.map((i) => {
                const active = i.exact
                  ? pathname === i.to
                  : pathname.startsWith(i.to);
                return (
                  <Link
                    key={i.to}
                    to={i.to as "/dashboard"}
                    className={`flex w-full items-center gap-3 rounded-md px-3 py-2 text-left transition-colors ${
                      active
                        ? "bg-secondary font-semibold text-foreground"
                        : "text-muted-foreground hover:bg-secondary/60 hover:text-foreground"
                    }`}
                  >
                    <i.icon className="h-4 w-4" />
                    {i.label}
                  </Link>
                );
              })}
            </nav>
          </div>
        </aside>

        <div>
          <Outlet />
        </div>
      </div>
    </div>
  );
}
