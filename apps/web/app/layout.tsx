import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import LocaleProvider from "@/components/LocaleProvider";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Rocky — The chat has a voice",
  description: "The voice of chat, in chords. Meet Rocky, read the documentation, and explore the showcase.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} dark h-full antialiased`}
    >
      <body className="min-h-full flex flex-col"><LocaleProvider>{children}</LocaleProvider></body>
    </html>
  );
}
