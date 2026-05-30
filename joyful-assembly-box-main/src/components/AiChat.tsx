import * as React from "react";

import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Textarea } from "@/components/ui/textarea";

type ChatMessage = {
  role: "user" | "assistant";
  content: string;
};

async function postChat(messages: ChatMessage[]): Promise<{ text: string; source: string }> {
  const res = await fetch("/api/ai/chat", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ messages }),
  });

  const data = (await res.json().catch(() => null)) as any;
  if (!res.ok) {
    const msg = typeof data?.error === "string" ? data.error : `AI error (${res.status})`;
    throw new Error(msg);
  }

  return {
    text: typeof data?.text === "string" ? data.text : "",
    source: typeof data?.source === "string" ? data.source : "unknown",
  };
}

export function AiChat() {
  const [open, setOpen] = React.useState(false);
  const [messages, setMessages] = React.useState<ChatMessage[]>([
    {
      role: "assistant",
      content: "Hi — I’m your LearnAdapt study assistant. What are you working on?",
    },
  ]);
  const [draft, setDraft] = React.useState("");
  const [sending, setSending] = React.useState(false);
  const scrollRef = React.useRef<HTMLDivElement | null>(null);

  React.useEffect(() => {
    if (!open) return;
    const t = window.setTimeout(() => {
      scrollRef.current?.scrollIntoView({ behavior: "smooth", block: "end" });
    }, 50);
    return () => window.clearTimeout(t);
  }, [open, messages.length]);

  async function onSend() {
    const content = draft.trim();
    if (!content || sending) return;

    setDraft("");
    setSending(true);

    const next = [...messages, { role: "user", content } as const];
    setMessages(next);

    try {
      const out = await postChat(next.slice(-24));
      setMessages((prev) => [...prev, { role: "assistant", content: out.text.trim() || "(empty response)" }]);
    } catch (e) {
      const msg = e instanceof Error ? e.message : "Unknown error";
      setMessages((prev) => [
        ...prev,
        {
          role: "assistant",
          content: `Sorry — I couldn’t respond (${msg}).`,
        },
      ]);
    } finally {
      setSending(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button
          type="button"
          variant="secondary"
          className="fixed left-3 top-1/2 z-50 -translate-y-1/2"
        >
          Chat
        </Button>
      </SheetTrigger>

      <SheetContent side="left" className="flex h-full flex-col gap-4">
        <SheetHeader>
          <SheetTitle>AI Chat</SheetTitle>
          <SheetDescription>Ask anything — study help, explanations, or quick summaries.</SheetDescription>
        </SheetHeader>

        <ScrollArea className="flex-1 rounded-md border border-input">
          <div className="space-y-3 p-3">
            {messages.map((m, idx) => {
              const isUser = m.role === "user";
              return (
                <div
                  key={idx}
                  className={
                    isUser
                      ? "ml-auto w-fit max-w-[85%] rounded-md bg-primary px-3 py-2 text-sm text-primary-foreground"
                      : "mr-auto w-fit max-w-[85%] rounded-md bg-muted px-3 py-2 text-sm text-foreground"
                  }
                >
                  {m.content}
                </div>
              );
            })}
            <div ref={scrollRef} />
          </div>
        </ScrollArea>

        <div className="space-y-2">
          <Textarea
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            placeholder="Type a message…"
            className="min-h-[72px]"
            onKeyDown={(e) => {
              if (e.key === "Enter" && (e.ctrlKey || e.metaKey)) {
                e.preventDefault();
                void onSend();
              }
            }}
          />
          <div className="flex items-center justify-between gap-2">
            <p className="text-xs text-muted-foreground">Send: Ctrl+Enter</p>
            <Button type="button" onClick={() => void onSend()} disabled={sending || !draft.trim()}>
              {sending ? "Sending…" : "Send"}
            </Button>
          </div>
        </div>
      </SheetContent>
    </Sheet>
  );
}
