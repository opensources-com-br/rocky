import type { Metadata } from "next";
import Docs from "@/components/docs/Docs";
export const metadata: Metadata = { title: "Docs · Rocky", description: "Guias de instalação, configuração e uso do Rocky." };
export default function Page() { return <Docs />; }
