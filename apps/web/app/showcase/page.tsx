import type { Metadata } from "next";
import Showcase from "@/components/showcase/Showcase";
export const metadata: Metadata = { title: "Showcase · Rocky", description: "Conheça os casos e configurações do showcase do Rocky." };
export default function Page() { return <Showcase />; }
