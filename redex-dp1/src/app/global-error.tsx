"use client";

export default function GlobalError({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <html>
      <body>
        <h2>Lo sentimos, algo salió mal</h2>
        <button onClick={() => reset()}>Reintentar</button>
      </body>
    </html>
  );
}
