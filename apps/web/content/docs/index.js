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
import enUnifiedChat from "./en/unified-chat";
import enSuperchats from "./en/superchats";
import enNotesIdeas from "./en/notes-ideas";
import enPostStreamSummary from "./en/post-stream-summary";
import enDataConfiguration from "./en/data-configuration";
import enLocalModels from "./en/local-models";
import enPlugins from "./en/plugins";
import enContributing from "./en/contributing";
import enUseCases from "./en/use-cases";

const pageIds = {
  introduction: [introduction, "Introdução", enIntroduction],
  installation: [installation, "Instalação", enInstallation],
  firstStream: [firstStream, "Primeira live", enFirstStream],
  concepts: [concepts, "Conceitos", enConcepts],
  platforms: [platforms, "Plataformas", enPlatforms],
  aiModel: [aiModel, "Modelo de IA", enAiModel],
  voiceAudio: [voiceAudio, "Voz e áudio", enVoiceAudio],
  agentBehavior: [agentBehavior, "Comportamento do agente", enAgentBehavior],
  chatQuestions: [unifiedChat, "Chat e perguntas", enUnifiedChat],
  superchats: [superchats, "Superchats", enSuperchats],
  notesIdeas: [notesIdeas, "Notas e ideias", enNotesIdeas],
  postStream: [postStreamSummary, "Resumo pós-live", enPostStreamSummary],
  dataConfiguration: [dataConfiguration, "Dados e configuração", enDataConfiguration],
  localModels: [localModels, "Modelos locais", enLocalModels],
  plugins: [plugins, "Plugins", enPlugins],
  contributing: [contributing, "Contribuir", enContributing],
  useCases: [useCases, "Cenários de uso", enUseCases],
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
