import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";

export const Route = createFileRoute("/teacher-lesson")({
  component: TeacherLessonPage,
  head: () => ({ meta: [{ title: "Teacher · Lesson — LearnAdapt" }] }),
});

type ApiResponse<T> = { success: boolean; message: string; data: T };

function readLocalStorage(key: string): string {
  if (typeof window === "undefined") return "";
  return localStorage.getItem(key) ?? "";
}

export default function TeacherLessonPage() {
  const [userId, setUserId] = useState<string>(() => readLocalStorage("la-dev-user-id") || "teacher-dev");
  const role = "TEACHER";

  const urlParams = typeof window === "undefined" ? new URLSearchParams() : new URLSearchParams(window.location.search);
  const [courseId, setCourseId] = useState<string>(() => urlParams.get("courseId") ?? "");
  const [chapterId, setChapterId] = useState<string>(() => urlParams.get("chapterId") ?? "");
  const [title, setTitle] = useState<string>("");
  const [content, setContent] = useState<string>("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [createdId, setCreatedId] = useState<string | null>(null);

  useEffect(() => {
    if (typeof window === "undefined") return;
    localStorage.setItem("la-dev-user-id", userId);
  }, [userId]);

  const createLesson = async () => {
    setLoading(true);
    setError(null);
    setCreatedId(null);
    try {
      const headers = new Headers();
      headers.set("content-type", "application/json");
      headers.set("X-User-Role", role);
      if (userId.trim()) headers.set("X-User-Id", userId.trim());

      const res = await fetch("/api/teacher/lessons", {
        method: "POST",
        headers,
        body: JSON.stringify({ courseId: courseId || null, chapterId: chapterId || null, title: title.trim(), content: content.trim() || null }),
      });
      const payload = (await res.json().catch(() => null)) as ApiResponse<{ id: string }> | null;
      if (!res.ok || !payload?.success) throw new Error(payload?.message ?? `Create lesson failed (${res.status})`);
      setCreatedId(payload.data.id);
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-3xl px-6 py-14">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-medium text-accent">Teacher</p>
          <h1 className="mt-1 font-display text-3xl font-semibold">Create Lesson</h1>
          <p className="mt-2 text-sm text-muted-foreground">Add lesson content for a specific chapter.</p>
        </div>
        <div className="text-right text-sm">
          <p className="font-semibold text-foreground">Role: {role}</p>
          <Link to="/teacher-quiz" className="text-primary hover:underline">Back to Quiz Builder</Link>
        </div>
      </div>

      <div className="mt-8 rounded-2xl border border-border bg-card p-6 shadow-card">
        <div className="grid gap-4 sm:grid-cols-2">
          <label className="block">
            <span className="text-sm font-medium text-foreground">User Id</span>
            <input value={userId} onChange={(e) => setUserId(e.target.value)} placeholder="teacher-dev" className="mt-1.5 w-full rounded-md border border-input bg-background px-3 py-2 text-sm" />
          </label>
          <label className="block">
            <span className="text-sm font-medium text-foreground">Course Id</span>
            <input value={courseId} onChange={(e) => setCourseId(e.target.value)} placeholder="courseId" className="mt-1.5 w-full rounded-md border border-input bg-background px-3 py-2 text-sm" />
          </label>
          <label className="block">
            <span className="text-sm font-medium text-foreground">Chapter Id</span>
            <input value={chapterId} onChange={(e) => setChapterId(e.target.value)} placeholder="chapterId" className="mt-1.5 w-full rounded-md border border-input bg-background px-3 py-2 text-sm" />
          </label>
          <label className="block">
            <span className="text-sm font-medium text-foreground">Lesson Title</span>
            <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="Lesson title" className="mt-1.5 w-full rounded-md border border-input bg-background px-3 py-2 text-sm" />
          </label>
          <label className="block sm:col-span-2">
            <span className="text-sm font-medium text-foreground">Lesson Content (HTML allowed)</span>
            <textarea value={content} onChange={(e) => setContent(e.target.value)} placeholder="Lesson content" className="mt-1.5 h-48 w-full rounded-md border border-input bg-background px-3 py-2 text-sm" />
          </label>
        </div>

        <div className="mt-4 flex items-center gap-3">
          <button onClick={() => void createLesson()} disabled={loading} className="rounded-md bg-foreground px-4 py-2 text-sm font-semibold text-background">Create lesson</button>
          {createdId && (
            <Link to="/lesson" search={{ chapterId }} className="text-sm text-primary hover:underline">Open created lesson</Link>
          )}
        </div>

        {error && <p className="mt-4 text-sm text-red-600">{error}</p>}
      </div>
    </div>
  );
}
