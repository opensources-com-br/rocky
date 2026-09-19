import introduction from "./introduction";
import installation from "./installation";
import firstStream from "./first-stream";
import concepts from "./concepts";
import platforms from "./platforms";
import aiModel from "./ai-model";
import voiceAudio from "./voice-audio";
import agentBehavior from "./agent-behavior";
import chatUnificado from "./chat-unificado";
import superchats from "./superchats";
import notasEIdeias from "./notas-e-ideias";
import resumoPosLive from "./resumo-pos-live";
import configToml from "./config-toml";
import modelosLocais from "./modelos-locais";
import plugins from "./plugins";
import contribuir from "./contribuir";
import depoimentos from "./depoimentos";
import enIntroducao from "./en/introducao";
import enInstalacao from "./en/instalacao";
import enPrimeiraLive from "./en/primeira-live";
import enConceitos from "./en/conceitos";
import enPlataformas from "./en/plataformas";
import enModeloDeIa from "./en/modelo-de-ia";
import enVozEAudio from "./en/voz-e-audio";
import enComportamentoDoAgente from "./en/comportamento-do-agente";
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
