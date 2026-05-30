import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useMemo, useState } from "react";
import { QRCodeSVG } from "qrcode.react";

import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export const Route = createFileRoute("/qr")({
  component: QrRoute,
  head: () => ({
    meta: [{ title: "LearnAdapt — Mobile QR" }],
  }),
});

function QrRoute() {
  const [url, setUrl] = useState("");

  useEffect(() => {
    setUrl(`${window.location.origin}/`);
  }, []);

  const isLocalhostUrl = useMemo(() => {
    try {
      const parsedUrl = new URL(url);
      return parsedUrl.hostname === "localhost" || parsedUrl.hostname === "127.0.0.1";
    } catch {
      return false;
    }
  }, [url]);

  const qrValue = url.trim();

  return (
    <div className="mx-auto w-full max-w-3xl px-6 py-12">
      <h1 className="font-display text-3xl font-semibold tracking-tight text-foreground">
        Mobile QR
      </h1>
      <p className="mt-2 text-sm text-muted-foreground">
        Scan this QR code to open LearnAdapt on your phone’s browser.
      </p>

      {isLocalhostUrl ? (
        <Alert className="mt-6">
          <AlertTitle>Heads up: localhost won’t work on your phone</AlertTitle>
          <AlertDescription>
            Use the <span className="font-mono">Network</span> URL printed by Vite
            (for example <span className="font-mono">http://192.168.x.x:8080/</span>)
            and make sure your phone and PC are on the same Wi‑Fi.
          </AlertDescription>
        </Alert>
      ) : null}

      <Card className="mt-6">
        <CardHeader>
          <CardTitle>QR code</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid gap-2">
            <Label htmlFor="qr-url">URL to encode</Label>
            <Input
              id="qr-url"
              value={url}
              onChange={(event) => setUrl(event.target.value)}
              placeholder="http://192.168.1.5:8080/"
              inputMode="url"
              autoCapitalize="none"
              autoCorrect="off"
              spellCheck={false}
            />
            <p className="text-xs text-muted-foreground">
              Tip: for local dev, use the Network URL printed in your terminal.
            </p>
          </div>

          <div className="mt-8 flex justify-center">
            {qrValue ? (
              <div className="rounded-xl border border-border bg-background p-4 shadow-card">
                <QRCodeSVG value={qrValue} size={220} includeMargin />
              </div>
            ) : (
              <p className="text-sm text-muted-foreground">Enter a URL to generate a QR code.</p>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
