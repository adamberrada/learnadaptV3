import { Link } from "@tanstack/react-router";

export function Footer() {
  return (
    <footer className="mt-24 border-t border-border bg-background">
      <div className="mx-auto grid max-w-7xl grid-cols-2 gap-10 px-6 py-12 md:grid-cols-4">
        <div className="col-span-2 md:col-span-1">
          <p className="font-display text-lg font-semibold">LearnAdapt</p>
          <p className="mt-2 max-w-xs text-sm text-muted-foreground">
            Adaptive, collaborative learning grounded in cognitive science.
          </p>
        </div>
        <FooterCol
          title="Platform"
          links={[
            { to: "/courses", label: "Courses" },
            { to: "/dashboard", label: "Dashboard" },
            { to: "/quiz", label: "Quizzes" },
            { to: "/peers", label: "Peer matching" },
          ]}
        />
        <FooterCol
          title="For teachers"
          links={[
            { to: "/dashboard", label: "Heatmap" },
            { to: "/dashboard", label: "AI rewrite" },
            { to: "/dashboard", label: "Analytics" },
          ]}
        />
        <FooterCol
          title="Company"
          links={[
            { to: "/", label: "About" },
            { to: "/", label: "Research" },
            { to: "/", label: "Contact" },
          ]}
        />
      </div>
      <div className="border-t border-border">
        <p className="mx-auto max-w-7xl px-6 py-5 text-xs text-muted-foreground">
          © {new Date().getFullYear()} LearnAdapt. Built around the Protégé Effect.
        </p>
      </div>
    </footer>
  );
}

function FooterCol({
  title,
  links,
}: {
  title: string;
  links: { to: string; label: string }[];
}) {
  return (
    <div>
      <p className="text-sm font-semibold">{title}</p>
      <ul className="mt-3 space-y-2 text-sm text-muted-foreground">
        {links.map((l, i) => (
          <li key={i}>
            <Link to={l.to} className="hover:text-foreground">
              {l.label}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
