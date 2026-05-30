import { Link, useRouterState } from "@tanstack/react-router";
import { GraduationCap, Moon, Sun, Languages } from "lucide-react";
import { useI18n, useTheme } from "@/lib/app-providers";

export function Header() {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const { t, lang, toggle: toggleLang } = useI18n();
  const { theme, toggle: toggleTheme } = useTheme();

  const links = [
    { to: "/", label: t("nav.home") },
    { to: "/courses", label: t("nav.courses") },
    { to: "/dashboard", label: t("nav.dashboard") },
    { to: "/quiz", label: t("nav.quiz") },
    { to: "/peers", label: t("nav.peers") },
  ] as const;

  return (
    <header className="sticky top-0 z-40 border-b border-border/70 bg-background/80 backdrop-blur-md">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-6">
        <Link to="/" className="flex items-center gap-2">
          <div className="grid h-9 w-9 place-items-center rounded-lg bg-primary text-primary-foreground">
            <GraduationCap className="h-5 w-5" />
          </div>
          <span className="font-display text-xl font-semibold tracking-tight">
            LearnAdapt
          </span>
        </Link>

        <nav className="hidden items-center gap-1 md:flex">
          {links.map((l) => {
            const active =
              l.to === "/" ? pathname === "/" : pathname.startsWith(l.to);
            return (
              <Link
                key={l.to}
                to={l.to}
                className={`rounded-md px-3 py-2 text-sm font-medium transition-colors ${
                  active
                    ? "bg-secondary text-foreground"
                    : "text-muted-foreground hover:bg-secondary/70 hover:text-foreground"
                }`}
              >
                {l.label}
              </Link>
            );
          })}
        </nav>

        <div className="flex items-center gap-1">
          <button
            onClick={toggleLang}
            aria-label={t("lang.toggle")}
            title={t("lang.toggle")}
            className="inline-flex items-center gap-1 rounded-md px-2 py-2 text-sm font-medium text-muted-foreground hover:bg-secondary hover:text-foreground"
          >
            <Languages className="h-4 w-4" />
            <span className="text-xs font-semibold uppercase">{lang}</span>
          </button>
          <button
            onClick={toggleTheme}
            aria-label={t("theme.toggle")}
            title={t("theme.toggle")}
            className="inline-flex items-center rounded-md p-2 text-muted-foreground hover:bg-secondary hover:text-foreground"
          >
            {theme === "dark" ? (
              <Sun className="h-4 w-4" />
            ) : (
              <Moon className="h-4 w-4" />
            )}
          </button>
          <Link
            to="/signin"
            className="hidden rounded-md px-3 py-2 text-sm font-medium text-muted-foreground hover:text-foreground sm:inline-flex"
          >
            {t("nav.signin")}
          </Link>
          <Link
            to="/get-started"
            className="inline-flex items-center rounded-md bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground shadow-card transition-transform hover:-translate-y-px hover:shadow-elevated"
          >
            {t("nav.getstarted")}
          </Link>
        </div>
      </div>
    </header>
  );
}
