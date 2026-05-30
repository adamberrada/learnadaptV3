import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";

export const Route = createFileRoute("/lesson")({
  component: LessonPage,
  head: () => ({ meta: [{ title: "Lesson — LearnAdapt" }] }),
});

type ApiResponse<T> = { success: boolean; message: string; data: T };

type LessonResponse = {
  id: string;
  courseId?: string | null;
  chapterId?: string | null;
  title?: string | null;
  content?: string | null;
};

export default function LessonPage() {
  const [chapterId, setChapterId] = useState<string>(() => {
    if (typeof window === "undefined") return "";
    return new URLSearchParams(window.location.search).get("chapterId") ?? "";
  });
  const [lesson, setLesson] = useState<LessonResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!chapterId) return;
    const controller = new AbortController();
    setLoading(true);
    setError(null);
    (async () => {
      try {
        const res = await fetch(`/api/public/lessons/chapter/${encodeURIComponent(chapterId)}`, { signal: controller.signal });
        const payload = (await res.json().catch(() => null)) as ApiResponse<LessonResponse> | null;
        if (!res.ok || !payload?.success) {
          throw new Error(payload?.message ?? `Lesson request failed (${res.status})`);
        }
        setLesson(payload.data);
      } catch (e) {
        if (e instanceof DOMException && e.name === "AbortError") return;
        setError(e instanceof Error ? e.message : "Failed to load lesson");
      } finally {
        setLoading(false);
      }
    })();
    return () => controller.abort();
  }, [chapterId]);

  return (
    <div className="mx-auto max-w-3xl px-6 py-14">
      <div className="mb-6">
        <h1 className="font-display text-3xl font-semibold">Lesson</h1>
        <p className="mt-1 text-sm text-muted-foreground">Lesson content and chapter quiz.</p>
      </div>

      {!chapterId && (
        <p className="text-sm text-muted-foreground">No chapter selected. Provide ?chapterId= in the URL.</p>
      )}

      {loading && <p className="text-sm text-muted-foreground">Loading lesson…</p>}

      {error && <p className="text-sm text-red-600">{error}</p>}

      {lesson && (
        <div className="rounded-2xl border border-border bg-card p-6 shadow-card">
          <h2 className="font-semibold text-xl">{lesson.title ?? "Untitled lesson"}</h2>
          <div className="mt-4 prose max-w-none" dangerouslySetInnerHTML={{ __html: lesson.content ?? "" }} />
          <div className="mt-6 flex items-center gap-3">
            <Link to="/quiz" search={{ chapterId: lesson.chapterId ?? "" }} className="rounded-md bg-foreground px-4 py-2 text-sm font-semibold text-background">
              Start Chapter Quiz
            </Link>
          </div>
        </div>
      )}
    </div>
  );
}
