import { createFileRoute, Link } from "@tanstack/react-router";
import { useI18n } from "@/lib/app-providers";
import { Sparkles } from "lucide-react";

export const Route = createFileRoute("/get-started")({
  component: GetStartedPage,
  head: () => ({
    meta: [
      { title: "Get started — LearnAdapt" },
      { name: "description", content: "Create your free LearnAdapt account." },
    ],
  }),
});

function GetStartedPage() {
  const { t } = useI18n();
  return (
    <div className="mx-auto flex max-w-md flex-col px-6 py-16">
      <div className="rounded-2xl border border-border bg-card p-8 shadow-card">
        <div className="grid h-11 w-11 place-items-center rounded-lg bg-accent text-accent-foreground">
          <Sparkles className="h-5 w-5" />
        </div>
        <h1 className="mt-5 font-display text-3xl font-semibold tracking-tight">
          {t("auth.start.title")}
        </h1>
        <p className="mt-2 text-sm text-muted-foreground">{t("auth.start.sub")}</p>

        <form
          className="mt-6 space-y-4"
          onSubmit={(e) => {
            e.preventDefault();
          }}
        >
          <Field label={t("auth.name")} type="text" placeholder="Ada Lovelace" />
          <Field label={t("auth.email")} type="email" placeholder="you@example.com" />
          <Field label={t("auth.password")} type="password" placeholder="••••••••" />
          <button
            type="submit"
            className="w-full rounded-md bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground shadow-card hover:shadow-elevated"
          >
            {t("auth.start.cta")}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-muted-foreground">
          {t("auth.start.alt")}{" "}
          <Link to="/signin" className="font-semibold text-primary hover:underline">
            {t("auth.start.alt.cta")}
          </Link>
        </p>
      </div>
    </div>
  );
}

function Field({
  label,
  type,
  placeholder,
}: {
  label: string;
  type: string;
  placeholder?: string;
}) {
  return (
    <label className="block">
      <span className="text-sm font-medium text-foreground">{label}</span>
      <input
        type={type}
        placeholder={placeholder}
        className="mt-1.5 w-full rounded-md border border-input bg-background px-3 py-2 text-sm shadow-sm outline-none focus:border-ring focus:ring-2 focus:ring-ring/30"
      />
    </label>
  );
}
