import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./styles/globals.css";
import OperationProvider from "./context/operation-provider";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "RedEx DP1",
  description: "Grupo 1a",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="es">
      <body className={inter.className}>
        <OperationProvider>{children}</OperationProvider>
      </body>
    </html>
  );
}
