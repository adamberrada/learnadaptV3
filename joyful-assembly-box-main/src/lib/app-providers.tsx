import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";

/* ---------------- Theme ---------------- */

type Theme = "light" | "dark";
type ThemeCtx = { theme: Theme; toggle: () => void; setTheme: (t: Theme) => void };
const ThemeContext = createContext<ThemeCtx | null>(null);

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<Theme>("light");

  useEffect(() => {
    const stored = (typeof localStorage !== "undefined" &&
      (localStorage.getItem("la-theme") as Theme | null)) || null;
    const prefersDark =
      typeof window !== "undefined" &&
      window.matchMedia?.("(prefers-color-scheme: dark)").matches;
    setTheme(stored ?? (prefersDark ? "dark" : "light"));
  }, []);

  useEffect(() => {
    const root = document.documentElement;
    root.classList.toggle("dark", theme === "dark");
    localStorage.setItem("la-theme", theme);
  }, [theme]);

  const value = useMemo<ThemeCtx>(
    () => ({
      theme,
      toggle: () => setTheme((t) => (t === "dark" ? "light" : "dark")),
      setTheme,
    }),
    [theme],
  );
  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
}

export function useTheme() {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error("useTheme must be used inside ThemeProvider");
  return ctx;
}

/* ---------------- i18n ---------------- */

type Lang = "en" | "fr";

const dict = {
  en: {
    "nav.home": "Home",
    "nav.courses": "Courses",
    "nav.dashboard": "Dashboard",
    "nav.quiz": "Quiz",
    "nav.peers": "Peers",
    "nav.signin": "Sign in",
    "nav.getstarted": "Get started",
    "theme.toggle": "Toggle theme",
    "lang.toggle": "Language",

    "dash.welcome": "Welcome back",
    "dash.hi": "Hi Anouar — let's keep the streak alive",
    "dash.thisweek": "This week",
    "dash.continue": "Continue learning",
    "dash.resume": "Resume",
    "dash.stats.courses": "Courses enrolled",
    "dash.stats.quizzes": "Quizzes completed",
    "dash.stats.hours": "Hours learned",
    "dash.stats.streak": "Day streak",
    "dash.nav.overview": "Overview",
    "dash.nav.courses": "My courses",
    "dash.nav.performance": "Performance",
    "dash.nav.peers": "Peer sessions",
    "dash.nav.notifications": "Notifications",
    "dash.nav.settings": "Settings",
    "dash.heatmap.title": "Cognitive load heatmap",
    "dash.heatmap.sub": "Aggregated across 412 learners · refreshed 6 min ago",

    "courses.title": "My courses",
    "courses.sub": "All courses you are currently enrolled in.",
    "perf.title": "Performance",
    "perf.sub": "Your learning metrics across the last 30 days.",
    "peers.title": "Peer sessions",
    "peers.sub": "Recent and upcoming Protégé Effect sessions.",
    "notif.title": "Notifications",
    "notif.sub": "Stay on top of matches, reminders, and feedback.",
    "settings.title": "Settings",
    "settings.sub": "Manage your account, appearance, and language.",
    "settings.appearance": "Appearance",
    "settings.language": "Language",
    "settings.dark": "Dark mode",
    "settings.light": "Light mode",

    "auth.signin.title": "Welcome back",
    "auth.signin.sub": "Sign in to continue your learning path.",
    "auth.email": "Email",
    "auth.password": "Password",
    "auth.signin.cta": "Sign in",
    "auth.signin.alt": "New to LearnAdapt?",
    "auth.signin.alt.cta": "Create an account",
    "auth.start.title": "Get started — free",
    "auth.start.sub": "Create your account and unlock adaptive learning.",
    "auth.name": "Full name",
    "auth.start.cta": "Create account",
    "auth.start.alt": "Already have an account?",
    "auth.start.alt.cta": "Sign in",
    "auth.or": "or continue with",
  },
  fr: {
    "nav.home": "Accueil",
    "nav.courses": "Cours",
    "nav.dashboard": "Tableau de bord",
    "nav.quiz": "Quiz",
    "nav.peers": "Pairs",
    "nav.signin": "Connexion",
    "nav.getstarted": "Commencer",
    "theme.toggle": "Changer de thème",
    "lang.toggle": "Langue",

    "dash.welcome": "Content de te revoir",
    "dash.hi": "Salut Anouar — garde la série en vie",
    "dash.thisweek": "Cette semaine",
    "dash.continue": "Continuer l'apprentissage",
    "dash.resume": "Reprendre",
    "dash.stats.courses": "Cours suivis",
    "dash.stats.quizzes": "Quiz terminés",
    "dash.stats.hours": "Heures apprises",
    "dash.stats.streak": "Jours d'affilée",
    "dash.nav.overview": "Vue d'ensemble",
    "dash.nav.courses": "Mes cours",
    "dash.nav.performance": "Performance",
    "dash.nav.peers": "Sessions en pair",
    "dash.nav.notifications": "Notifications",
    "dash.nav.settings": "Paramètres",
    "dash.heatmap.title": "Carte de charge cognitive",
    "dash.heatmap.sub": "Agrégé sur 412 apprenants · actualisé il y a 6 min",

    "courses.title": "Mes cours",
    "courses.sub": "Tous les cours auxquels vous êtes inscrit.",
    "perf.title": "Performance",
    "perf.sub": "Vos métriques d'apprentissage sur les 30 derniers jours.",
    "peers.title": "Sessions en pair",
    "peers.sub": "Sessions Protégé Effect récentes et à venir.",
    "notif.title": "Notifications",
    "notif.sub": "Restez au courant des matchs, rappels et retours.",
    "settings.title": "Paramètres",
    "settings.sub": "Gérez votre compte, apparence et langue.",
    "settings.appearance": "Apparence",
    "settings.language": "Langue",
    "settings.dark": "Mode sombre",
    "settings.light": "Mode clair",

    "auth.signin.title": "Bon retour",
    "auth.signin.sub": "Connectez-vous pour continuer votre apprentissage.",
    "auth.email": "E-mail",
    "auth.password": "Mot de passe",
    "auth.signin.cta": "Se connecter",
    "auth.signin.alt": "Nouveau sur LearnAdapt ?",
    "auth.signin.alt.cta": "Créer un compte",
    "auth.start.title": "Commencer — gratuit",
    "auth.start.sub": "Créez votre compte et débloquez l'apprentissage adaptatif.",
    "auth.name": "Nom complet",
    "auth.start.cta": "Créer le compte",
    "auth.start.alt": "Vous avez déjà un compte ?",
    "auth.start.alt.cta": "Se connecter",
    "auth.or": "ou continuer avec",
  },
} as const;

export type TKey = keyof (typeof dict)["en"];

type I18nCtx = {
  lang: Lang;
  setLang: (l: Lang) => void;
  toggle: () => void;
  t: (key: TKey) => string;
};
const I18nContext = createContext<I18nCtx | null>(null);

export function I18nProvider({ children }: { children: ReactNode }) {
  const [lang, setLang] = useState<Lang>("en");

  useEffect(() => {
    const stored = (typeof localStorage !== "undefined" &&
      (localStorage.getItem("la-lang") as Lang | null)) || null;
    if (stored === "en" || stored === "fr") setLang(stored);
  }, []);

  useEffect(() => {
    localStorage.setItem("la-lang", lang);
    document.documentElement.lang = lang;
  }, [lang]);

  const value = useMemo<I18nCtx>(
    () => ({
      lang,
      setLang,
      toggle: () => setLang((l) => (l === "en" ? "fr" : "en")),
      t: (key: TKey) => dict[lang][key] ?? dict.en[key] ?? key,
    }),
    [lang],
  );

  return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
}

export function useI18n() {
  const ctx = useContext(I18nContext);
  if (!ctx) throw new Error("useI18n must be used inside I18nProvider");
  return ctx;
}
