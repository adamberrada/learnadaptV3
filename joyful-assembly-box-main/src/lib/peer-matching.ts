export type PeerPresence = {
  id: string;
  name: string;
  concept: string;
  blockedFor: string; // e.g. "12 min"
};

function parseBlockedMinutes(blockedFor: string): number {
  // Accept formats like "12 min" / "5 mins" / "1 h".
  const s = blockedFor.toLowerCase().trim();
  const m = s.match(/(\d+)\s*(min|mins|minute|minutes)/);
  if (m) return Number(m[1]);
  const h = s.match(/(\d+)\s*(h|hr|hrs|hour|hours)/);
  if (h) return Number(h[1]) * 60;
  const n = s.match(/(\d+)/);
  return n ? Number(n[1]) : 0;
}

export function findPeerMatch(opts: {
  myConcept: string;
  peers: PeerPresence[];
  excludePeerId?: string;
}): PeerPresence | null {
  const wanted = opts.myConcept.trim().toLowerCase();
  if (!wanted) return null;

  const candidates = opts.peers
    .filter((p) => p.id !== opts.excludePeerId)
    .filter((p) => p.concept.trim().toLowerCase() === wanted)
    .map((p) => ({ peer: p, minutes: parseBlockedMinutes(p.blockedFor) }))
    .sort((a, b) => b.minutes - a.minutes);

  return candidates[0]?.peer ?? null;
}
