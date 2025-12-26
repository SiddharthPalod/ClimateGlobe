import type { ReactNode } from "react";
import "./globals.css";

export const metadata = {
  title: "Climate Globe",
  description: "Real-time climate globe"
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <body className="bg-white text-slate-900 h-screen">{children}</body>
    </html>
  );
}


