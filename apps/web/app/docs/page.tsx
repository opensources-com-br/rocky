import type { Metadata } from "next";
import Docs from "@/components/docs/Docs";
export const metadata: Metadata = { title: "Docs · Rocky", description: "Rocky installation, configuration, and usage guides." };
export default function Page() { return <Docs />; }
