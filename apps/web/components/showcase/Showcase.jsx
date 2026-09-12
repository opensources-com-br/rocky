"use client";
import { useState } from "react";
import { CASES } from "@/content/showcase";
import { Chord } from "@/components/Chord";
import ShowcaseView from "./ShowcaseView";
import { buildFile } from "./build-file";
import { formFields } from "./form-fields";

export default function Showcase() {
  const [filter, setFilter] = useState("Todos");
  const [open, setOpen] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [copyLabel, setCopyLabel] = useState("Copiar o arquivo");
  const [form, setForm] = useState({ name: "", category: "Programação", desc: "", quote: "", platforms: ["Twitch"], urls: { Twitch: "", Kick: "", YouTube: "", Facebook: "" }, provider: "ollama", model: "", voice: "system", story: "", changes: "" });
  const update = (key, value) => { setForm(current => ({ ...current, [key]: value })); setCopyLabel("Copiar o arquivo"); };
  const setUrl = (name, value) => { setForm(current => ({ ...current, urls: { ...current.urls, [name]: value } })); setCopyLabel("Copiar o arquivo"); };
  const toggle = name => { setForm(current => ({ ...current, platforms: current.platforms.includes(name) ? current.platforms.filter(p => p !== name) : [...current.platforms, name] })); setCopyLabel("Copiar o arquivo"); };
  const toTop = () => window.scrollTo({ top: 0 });
  const openCase = id => { setOpen(id); setSubmitting(false); toTop(); };
  const back = () => { setOpen(null); setSubmitting(false); toTop(); };
  const card = c => ({ ...c, open: () => openCase(c.id) });
  const selected = CASES.find(c => c.id === open);
  const visible = filter === "Todos" ? CASES : CASES.filter(c => c.filter === filter);
  const built = buildFile(form);
  const complete = [form.name, form.quote, form.desc, form.story].every(value => value.trim());
  async function copyFile() {
    try { await navigator.clipboard.writeText(built.text); setCopyLabel("Copiado"); }
    catch { setCopyLabel("Não foi possível copiar. Selecione o texto abaixo."); }
  }
  const values = {
    chord: <Chord compact />, isIndex: !selected && !submitting, isDetail: !!selected && !submitting, isSubmit: submitting,
    openSubmit: () => { setSubmitting(true); setOpen(null); setCopyLabel("Copiar o arquivo"); toTop(); },
    filename: built.filename, preview: built.text, copyLabel, copyFile,
    readyLabel: complete ? "rascunho pronto para copiar" : "preencha nome, frase, resumo e depoimento",
    fields: formFields(form, update, setUrl, toggle), d: selected || CASES[0],
    related: selected ? CASES.filter(c => c.id !== selected.id).slice(0, 3).map(card) : [],
    cases: visible.map(card), count: `${visible.length} ${visible.length === 1 ? "cenário" : "cenários"}`,
    backToIndex: back,
    filters: ["Todos", "Programação", "Games", "Educação", "Podcast", "Esportes", "Arte"].map(name => ({ name, select: () => { setFilter(name); setOpen(null); setSubmitting(false); }, bg: name === filter ? "rgba(255,255,255,.09)" : "transparent", border: name === filter ? "rgba(255,255,255,.14)" : "rgba(255,255,255,.08)", color: name === filter ? "#f2efec" : "rgba(255,255,255,.5)" })),
    prSteps: [{ n: "01", text: "Faça um fork de opensources-com-br/rocky no GitHub." }, { n: "02", text: `Crie o arquivo ${built.filename} com o conteúdo ao lado.` }, { n: "03", text: "Se quiser, adicione uma captura da sua janela do Rocky em showcase/img/." }, { n: "04", text: `Abra o PR com o título “showcase: ${form.name || "seu canal"}”.` }],
  };
  return <ShowcaseView {...values} />;
}
