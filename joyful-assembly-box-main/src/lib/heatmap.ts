export type LessonBehavior = {
  timeOnPageSeconds: number;
  rereads: number;
  failRate: number; // 0..1
  dropOffRate: number; // 0..1
};

export type HeatmapCell = {
  label: string;
  behavior: LessonBehavior;
  score: number; // 0..100
  level: 0 | 1 | 2;
};

function clamp(n: number, min: number, max: number): number {
  return Math.max(min, Math.min(max, n));
}

function hashStringToUnit(label: string): number {
  // Simple deterministic hash -> [0, 1)
  let h = 2166136261;
  for (let i = 0; i < label.length; i++) {
    h ^= label.charCodeAt(i);
    h = Math.imul(h, 16777619);
  }
  // Convert to unsigned and normalize.
  return ((h >>> 0) % 1_000_000) / 1_000_000;
}

export function simulateBehavior(label: string): LessonBehavior {
  const u = hashStringToUnit(label);
  const u2 = hashStringToUnit(label + "::2");
  const u3 = hashStringToUnit(label + "::3");
  const u4 = hashStringToUnit(label + "::4");

  // Values are intentionally plausible, not random at runtime.
  const timeOnPageSeconds = Math.round(45 + u * 240); // 45s..285s
  const rereads = Math.round(u2 * 4); // 0..4
  const failRate = clamp(0.05 + u3 * 0.55, 0, 1); // 0.05..0.60
  const dropOffRate = clamp(0.03 + u4 * 0.32, 0, 1); // 0.03..0.35

  return { timeOnPageSeconds, rereads, failRate, dropOffRate };
}

export function scoreBehavior(b: LessonBehavior): number {
  // Normalize each signal to 0..1, then weight.
  const timeN = clamp((b.timeOnPageSeconds - 45) / (285 - 45), 0, 1);
  const rereadsN = clamp(b.rereads / 4, 0, 1);
  const failN = clamp(b.failRate / 0.6, 0, 1);
  const dropN = clamp(b.dropOffRate / 0.35, 0, 1);

  const score01 =
    0.30 * timeN +
    0.20 * rereadsN +
    0.35 * failN +
    0.15 * dropN;

  return Math.round(clamp(score01, 0, 1) * 100);
}

export function levelFromScore(score: number): 0 | 1 | 2 {
  if (score >= 70) return 2;
  if (score >= 40) return 1;
  return 0;
}

export function buildHeatmap(labels: string[]): HeatmapCell[] {
  return labels.map((label) => {
    const behavior = simulateBehavior(label);
    const score = scoreBehavior(behavior);
    const level = levelFromScore(score);
    return { label, behavior, score, level };
  });
}
