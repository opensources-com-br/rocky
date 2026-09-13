import type { Metadata } from "next";
import Showcase from "@/components/showcase/Showcase";
export const metadata: Metadata = { title: "Showcase · Rocky", description: "Rocky use cases are coming soon." };
export default function Page() { return <Showcase />; }
