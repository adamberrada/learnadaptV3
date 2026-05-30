import { createFileRoute } from "@tanstack/react-router";
import { useI18n } from "@/lib/app-providers";
import { Bell, Users, Award, BookOpen } from "lucide-react";

export const Route = createFileRoute("/dashboard/notifications")({
  component: NotificationsPage,
});

const items = [
  { icon: Users, title: "Peer match found", body: "Marco is also stuck on eigenvectors.", when: "2m ago", unread: true },
  { icon: Award, title: "Badge unlocked", body: "You earned the 'Streak 12' badge.", when: "1h ago", unread: true },
  { icon: BookOpen, title: "New chapter available", body: "Statistics — Chapter 7 is live.", when: "Yesterday", unread: false },
  { icon: Bell, title: "Reminder", body: "Your daily quiz resets in 3 hours.", when: "Yesterday", unread: false },
];

function NotificationsPage() {
  const { t } = useI18n();
  return (
    <div>
      <h1 className="font-display text-4xl font-semibold tracking-tight">{t("notif.title")}</h1>
      <p className="mt-2 text-sm text-muted-foreground">{t("notif.sub")}</p>

      <ul className="mt-8 divide-y divide-border overflow-hidden rounded-2xl border border-border bg-card shadow-card">
        {items.map((n, i) => (
          <li key={i} className="flex items-start gap-4 px-5 py-4">
            <div
              className={`grid h-10 w-10 shrink-0 place-items-center rounded-lg ${
                n.unread ? "bg-primary text-primary-foreground" : "bg-secondary text-muted-foreground"
              }`}
            >
              <n.icon className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <div className="flex items-center justify-between gap-2">
                <p className="font-semibold">{n.title}</p>
                <span className="text-xs text-muted-foreground">{n.when}</span>
              </div>
              <p className="mt-0.5 text-sm text-muted-foreground">{n.body}</p>
            </div>
            {n.unread && <span className="mt-2 h-2 w-2 rounded-full bg-accent" />}
          </li>
        ))}
      </ul>
    </div>
  );
}
