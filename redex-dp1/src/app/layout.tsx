"use client";

import type { ReactNode } from "react";
import { Inter } from "next/font/google";
import "@/styles/globals.css";
import Sidebar from "@/components/layout/Sidebar";

const inter = Inter({ subsets: ["latin"] });

type Props = {
  children: ReactNode; // Define children as ReactNode instead of the implicit 'any'
};

export default function RootLayout({ children }: Props) {
  return (
    <html lang="es">
      <head>
        <title>REDEX</title>
      </head>

      <body className={`${inter.className} bg-[#EFEFEF] w-auto h-screen`}>
        <main className={`h-screen`}>
          <div className="grid grid-cols-[1fr_10fr_1fr]">
            <Sidebar />
            {children}
          </div>
        </main>
      </body>
    </html>
  );
}
