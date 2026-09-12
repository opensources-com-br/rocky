import type { Metadata } from "next";
import Showcase from "@/components/showcase/Showcase";
export const metadata: Metadata = { title: "Showcase · Rocky", description: "Casos de uso do Rocky em breve." };
export default function Page() { return <Showcase />; }
