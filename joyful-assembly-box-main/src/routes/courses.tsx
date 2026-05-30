import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Clock, BookOpen, Star } from "lucide-react";

export const Route = createFileRoute("/courses")({
  component: CoursesPage,
  head: () => ({
    meta: [
      { title: "Courses — LearnAdapt" },
      {
        name: "description",
        content:
          "Filterable catalog of adaptive courses across data, design, business, and more.",
      },
    ],
  }),
});

type Course = {
  id: string;
  emoji: string;
  title: string;
  category: string;
  tag: string;
  lessons: number;
  hours: number;
  level: "Beginner" | "Intermediate" | "Advanced";
  instructor: string;
  progress?: number;
};

const courses: Course[] = [
  { id: "1", emoji: "📊", title: "Statistics for Decision Making", category: "Data", tag: "Popular", lessons: 28, hours: 14, level: "Intermediate", instructor: "Dr. Lina Okafor", progress: 62 },
  { id: "2", emoji: "🧠", title: "Cognitive Science Foundations", category: "Science", tag: "New", lessons: 22, hours: 10, level: "Beginner", instructor: "Prof. Anouar Jouali" },
  { id: "3", emoji: "💻", title: "Full-Stack Web Engineering", category: "Tech", tag: "Trending", lessons: 46, hours: 32, level: "Advanced", instructor: "Maya Chen", progress: 18 },
  { id: "4", emoji: "🎨", title: "Design Systems in Practice", category: "Design", tag: "Featured", lessons: 18, hours: 8, level: "Intermediate", instructor: "Jonas Weiss" },
  { id: "5", emoji: "📈", title: "Marketing Analytics 101", category: "Business", tag: "Popular", lessons: 24, hours: 11, level: "Beginner", instructor: "Sara Bennani", progress: 100 },
  { id: "6", emoji: "🤖", title: "Applied Machine Learning", category: "Data", tag: "Pro", lessons: 38, hours: 26, level: "Advanced", instructor: "Dr. Yusuf Rahman" },
  { id: "7", emoji: "🗣️", title: "Public Speaking & Storytelling", category: "Business", tag: "New", lessons: 14, hours: 6, level: "Beginner", instructor: "Elena Park" },
  { id: "8", emoji: "🔐", title: "Cybersecurity Essentials", category: "Tech", tag: "Trending", lessons: 30, hours: 16, level: "Intermediate", instructor: "Tomás Reyes" },
];

const categories = ["All", "Data", "Tech", "Design", "Business", "Science"];

function CoursesPage() {
  const [filter, setFilter] = useState<string>("All");
  const list = filter === "All" ? courses : courses.filter((c) => c.category === filter);

  return (
    <div className="mx-auto max-w-7xl px-6 py-14">
      <div className="max-w-2xl">
        <p className="text-sm font-medium text-accent">Catalog</p>
        <h1 className="mt-2 font-display text-5xl font-semibold tracking-tight">
          Find your next course
        </h1>
        <p className="mt-3 text-muted-foreground">
          Every course adapts to your pace, surfaces your blockages, and
          connects you with peers who are learning the same thing right now.
        </p>
      </div>

      <div className="mt-8 flex flex-wrap gap-2">
        {categories.map((c) => (
          <button
            key={c}
            onClick={() => setFilter(c)}
            className={`rounded-full px-4 py-1.5 text-sm font-medium transition-colors ${
              filter === c
                ? "bg-foreground text-background"
                : "border border-border bg-card text-muted-foreground hover:text-foreground"
            }`}
          >
            {c}
          </button>
        ))}
      </div>

      <div className="mt-10 grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
        {list.map((c) => (
          <article
            key={c.id}
            className="group flex flex-col overflow-hidden rounded-2xl border border-border bg-card shadow-card transition-all hover:-translate-y-1 hover:shadow-elevated"
          >
            <div className="flex h-36 items-center justify-center bg-hero text-6xl">
              {c.emoji}
            </div>
            <div className="flex flex-1 flex-col p-5">
              <div className="flex items-center justify-between">
                <span className="rounded-full bg-secondary px-2.5 py-0.5 text-xs font-semibold text-foreground">
                  {c.tag}
                </span>
                <span className="text-xs font-medium text-muted-foreground">
                  {c.level}
                </span>
              </div>
              <h3 className="mt-3 font-display text-lg font-semibold leading-tight">
                {c.title}
              </h3>
              <p className="mt-1 text-xs text-muted-foreground">
                by {c.instructor}
              </p>

              <div className="mt-4 flex items-center gap-4 text-xs text-muted-foreground">
                <span className="inline-flex items-center gap-1">
                  <BookOpen className="h-3.5 w-3.5" /> {c.lessons} lessons
                </span>
                <span className="inline-flex items-center gap-1">
                  <Clock className="h-3.5 w-3.5" /> {c.hours}h
                </span>
                <span className="inline-flex items-center gap-1">
                  <Star className="h-3.5 w-3.5" /> 4.{(c.id.charCodeAt(0) % 6) + 3}
                </span>
              </div>

              {c.progress !== undefined && (
                <div className="mt-4">
                  <div className="flex justify-between text-xs">
                    <span className="text-muted-foreground">Progress</span>
                    <span className="font-semibold">{c.progress}%</span>
                  </div>
                  <div className="mt-1 h-1.5 w-full rounded-full bg-secondary">
                    <div
                      className="h-1.5 rounded-full bg-primary"
                      style={{ width: `${c.progress}%` }}
                    />
                  </div>
                </div>
              )}

              <button className="mt-5 w-full rounded-md bg-primary px-3 py-2 text-sm font-semibold text-primary-foreground transition-transform hover:-translate-y-px">
                {c.progress !== undefined ? "Continue" : "Enroll"}
              </button>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
