import { createFileRoute } from "@tanstack/react-router";
import { useI18n } from "@/lib/app-providers";
import { BookOpen } from "lucide-react";

export const Route = createFileRoute("/dashboard/courses")({
  component: MyCoursesPage,
});

const courses = [
  { title: "Statistics for Decision Making", chapter: "Ch. 6 · Hypothesis Testing", progress: 62, hours: "12h left" },
  { title: "Full-Stack Web Engineering", chapter: "Ch. 2 · React Fundamentals", progress: 18, hours: "28h left" },
  { title: "Cognitive Science Foundations", chapter: "Ch. 4 · Memory Systems", progress: 78, hours: "5h left" },
  { title: "Intro to Machine Learning", chapter: "Ch. 1 · What is ML?", progress: 4, hours: "32h left" },
  { title: "Academic Writing", chapter: "Ch. 3 · Argument Structure", progress: 45, hours: "9h left" },
  { title: "Linear Algebra Refresher", chapter: "Ch. 5 · Eigenvalues", progress: 88, hours: "2h left" },
];

function MyCoursesPage() {
  const { t } = useI18n();
  return (
    <div>
      <h1 className="font-display text-4xl font-semibold tracking-tight">{t("courses.title")}</h1>
      <p className="mt-2 text-sm text-muted-foreground">{t("courses.sub")}</p>

      <div className="mt-8 grid gap-4 md:grid-cols-2">
        {courses.map((c) => (
          <div key={c.title} className="rounded-2xl border border-border bg-card p-5 shadow-card">
            <div className="flex items-center gap-3">
              <div className="grid h-10 w-10 place-items-center rounded-lg bg-secondary text-primary">
                <BookOpen className="h-5 w-5" />
              </div>
              <div className="flex-1">
                <h3 className="font-display text-lg font-semibold leading-tight">{c.title}</h3>
                <p className="text-xs text-muted-foreground">{c.chapter}</p>
              </div>
              <span className="text-xs text-muted-foreground">{c.hours}</span>
            </div>
            <div className="mt-4 h-2 w-full rounded-full bg-secondary">
              <div
                className="h-2 rounded-full bg-primary"
                style={{ width: `${c.progress}%` }}
              />
            </div>
            <div className="mt-2 flex justify-between text-xs text-muted-foreground">
              <span>{c.progress}% complete</span>
              <button className="font-semibold text-primary hover:underline">
                {t("dash.resume")} →
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
