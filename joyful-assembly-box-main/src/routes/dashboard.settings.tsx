import { createFileRoute } from "@tanstack/react-router";
import { useI18n, useTheme } from "@/lib/app-providers";
import { Moon, Sun } from "lucide-react";

export const Route = createFileRoute("/dashboard/settings")({
  component: SettingsPage,
});

function SettingsPage() {
  const { t, lang, setLang } = useI18n();
  const { theme, setTheme } = useTheme();

  return (
    <div>
      <h1 className="font-display text-4xl font-semibold tracking-tight">{t("settings.title")}</h1>
      <p className="mt-2 text-sm text-muted-foreground">{t("settings.sub")}</p>

      <section className="mt-8 rounded-2xl border border-border bg-card p-6 shadow-card">
        <h2 className="font-display text-xl font-semibold">{t("settings.appearance")}</h2>
        <div className="mt-4 grid grid-cols-2 gap-3 sm:max-w-md">
          <button
            onClick={() => setTheme("light")}
            className={`flex items-center justify-center gap-2 rounded-lg border px-4 py-3 text-sm font-medium transition-colors ${
              theme === "light"
                ? "border-primary bg-primary/10 text-primary"
                : "border-border bg-background text-muted-foreground hover:text-foreground"
            }`}
          >
            <Sun className="h-4 w-4" /> {t("settings.light")}
          </button>
          <button
            onClick={() => setTheme("dark")}
            className={`flex items-center justify-center gap-2 rounded-lg border px-4 py-3 text-sm font-medium transition-colors ${
              theme === "dark"
                ? "border-primary bg-primary/10 text-primary"
                : "border-border bg-background text-muted-foreground hover:text-foreground"
            }`}
          >
            <Moon className="h-4 w-4" /> {t("settings.dark")}
          </button>
        </div>
      </section>

      <section className="mt-6 rounded-2xl border border-border bg-card p-6 shadow-card">
        <h2 className="font-display text-xl font-semibold">{t("settings.language")}</h2>
        <div className="mt-4 grid grid-cols-2 gap-3 sm:max-w-md">
          {(["en", "fr"] as const).map((l) => (
            <button
              key={l}
              onClick={() => setLang(l)}
              className={`rounded-lg border px-4 py-3 text-sm font-semibold uppercase transition-colors ${
                lang === l
                  ? "border-primary bg-primary/10 text-primary"
                  : "border-border bg-background text-muted-foreground hover:text-foreground"
              }`}
            >
              {l === "en" ? "🇬🇧 English" : "🇫🇷 Français"}
            </button>
          ))}
        </div>
      </section>

      <section className="mt-6 rounded-2xl border border-border bg-card p-6 shadow-card">
        <h2 className="font-display text-xl font-semibold">Account</h2>
        <div className="mt-4 grid gap-4 sm:grid-cols-2">
          <Field label="Name" value="Anouar J." />
          <Field label="Email" value="anouar@learnadapt.io" />
        </div>
        <button className="mt-6 rounded-md bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground">
          Save changes
        </button>
      </section>
    </div>
  );
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <label className="block">
      <span className="text-sm font-medium text-foreground">{label}</span>
      <input
        defaultValue={value}
        className="mt-1.5 w-full rounded-md border border-input bg-background px-3 py-2 text-sm shadow-sm outline-none focus:border-ring focus:ring-2 focus:ring-ring/30"
      />
    </label>
  );
}
