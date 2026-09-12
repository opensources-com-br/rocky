import introducao from "./introducao";
import instalacao from "./instalacao";
import primeira_live from "./primeira-live";
import conceitos from "./conceitos";
import plataformas from "./plataformas";
import modelo_de_ia from "./modelo-de-ia";
import voz_e_audio from "./voz-e-audio";
import comportamento_do_agente from "./comportamento-do-agente";
import chat_unificado from "./chat-unificado";
import superchats from "./superchats";
import notas_e_ideias from "./notas-e-ideias";
import resumo_pos_live from "./resumo-pos-live";
import config_toml from "./config-toml";
import modelos_locais from "./modelos-locais";
import plugins from "./plugins";
import contribuir from "./contribuir";
import depoimentos from "./depoimentos";
export const PAGES = {
  "Introdução": introducao,
  "Instalação": instalacao,
  "Primeira live": primeira_live,
  "Conceitos": conceitos,
  "Plataformas": plataformas,
  "Modelo de IA": modelo_de_ia,
  "Voz e áudio": voz_e_audio,
  "Comportamento do agente": comportamento_do_agente,
  "Chat unificado": chat_unificado,
  "Superchats": superchats,
  "Notas e ideias": notas_e_ideias,
  "Resumo pós-live": resumo_pos_live,
  "config.toml": config_toml,
  "Modelos locais": modelos_locais,
  "Plugins": plugins,
  "Contribuir": contribuir,
  "Depoimentos": depoimentos,
};
export const NAV = [{"title": "Começando", "items": ["Introdução", "Instalação", "Primeira live", "Conceitos"]}, {"title": "Configuração", "items": ["Plataformas", "Modelo de IA", "Voz e áudio", "Comportamento do agente"]}, {"title": "Recursos", "items": ["Chat unificado", "Superchats", "Notas e ideias", "Resumo pós-live"]}, {"title": "Avançado", "items": ["config.toml", "Modelos locais", "Plugins", "Contribuir"]}, {"title": "Showcase", "items": ["Depoimentos"]}];
export const FLAT = NAV.flatMap(group => group.items);
export const slug = text => text.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
