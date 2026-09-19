import introduction from "./introduction";
import installation from "./installation";
import firstStream from "./first-stream";
import concepts from "./concepts";
import platforms from "./platforms";
import aiModel from "./ai-model";
import voiceAudio from "./voice-audio";
import agentBehavior from "./agent-behavior";
import unifiedChat from "./unified-chat";
import superchats from "./superchats";
import notesIdeas from "./notes-ideas";
import postStreamSummary from "./post-stream-summary";
import dataConfiguration from "./data-configuration";
import localModels from "./local-models";
import plugins from "./plugins";
import contributing from "./contributing";
import useCases from "./use-cases";
import enIntroduction from "./en/introduction";
import enInstallation from "./en/installation";
import enFirstStream from "./en/first-stream";
import enConcepts from "./en/concepts";
import enPlatforms from "./en/platforms";
import enAiModel from "./en/ai-model";
import enVoiceAudio from "./en/voice-audio";
import enAgentBehavior from "./en/agent-behavior";
import enChatUnificado from "./en/chat-unificado";
import enSuperchats from "./en/superchats";
import enNotasEIdeias from "./en/notas-e-ideias";
import enResumoPosLive from "./en/resumo-pos-live";
import enConfigToml from "./en/config-toml";
import enModelosLocais from "./en/modelos-locais";
import enPlugins from "./en/plugins";
import enContribuir from "./en/contribuir";
import enDepoimentos from "./en/depoimentos";

const pageIds = {
  introduction: [introducao, "Introdução", enIntroducao],
  installation: [instalacao, "Instalação", enInstalacao],
  firstStream: [primeiraLive, "Primeira live", enPrimeiraLive],
  concepts: [conceitos, "Conceitos", enConceitos],
  platforms: [plataformas, "Plataformas", enPlataformas],
  aiModel: [modeloDeIa, "Modelo de IA", enModeloDeIa],
  voiceAudio: [vozEAudio, "Voz e áudio", enVozEAudio],
  agentBehavior: [comportamentoDoAgente, "Comportamento do agente", enComportamentoDoAgente],
  chatQuestions: [chatUnificado, "Chat e perguntas", enChatUnificado],
  superchats: [superchats, "Superchats", enSuperchats],
  notesIdeas: [notasEIdeias, "Notas e ideias", enNotasEIdeias],
  postStream: [resumoPosLive, "Resumo pós-live", enResumoPosLive],
  dataConfiguration: [configToml, "Dados e configuração", enConfigToml],
  localModels: [modelosLocais, "Modelos locais", enModelosLocais],
  plugins: [plugins, "Plugins", enPlugins],
  contributing: [contribuir, "Contribuir", enContribuir],
  useCases: [depoimentos, "Cenários de uso", enDepoimentos],
};

const groups = [
  ["Getting started", "Começando", ["introduction", "installation", "firstStream", "concepts"]],
  ["Configuration", "Configuração", ["platforms", "aiModel", "voiceAudio", "agentBehavior"]],
  ["Features", "Recursos", ["chatQuestions", "superchats", "notesIdeas", "postStream"]],
  ["Advanced", "Avançado", ["dataConfiguration", "localModels", "plugins", "contributing"]],
  ["Showcase", "Showcase", ["useCases"]],
];

export function getDocsContent(locale) {
  const isPortuguese = locale === "pt-BR";
  const pages = Object.fromEntries(Object.entries(pageIds).map(([id, [portuguese, title, english]]) => [
    id,
    isPortuguese ? { ...portuguese, title } : english,
  ]));
  const nav = groups.map(([english, portuguese, items]) => ({ title: isPortuguese ? portuguese : english, items }));
  return { pages, nav, flat: nav.flatMap(group => group.items), firstPage: "introduction" };
}

export const slug = text => text.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
