const portuguese = [
  {
    "name": "Twitch · disponível",
    "color": "#9146ff"
  },
  {
    "name": "Kick · disponível com webhook",
    "color": "#53fc18"
  },
  {
    "name": "YouTube · disponível",
    "color": "#ff164a"
  },
  {
    "name": "Facebook · disponível",
    "color": "#1877f2"
  },
  {
    "name": "TikTok LIVE · disponível via WebCast",
    "color": "#25f4ee"
  }
];

const english = [
  { "name": "Twitch · available", "color": "#9146ff" },
  { "name": "Kick · available with webhook", "color": "#53fc18" },
  { "name": "YouTube · available", "color": "#ff164a" },
  { "name": "Facebook · available", "color": "#1877f2" },
  { "name": "TikTok LIVE · available through WebCast", "color": "#25f4ee" }
];

export const getPlatforms = locale => locale === "pt-BR" ? portuguese : english;
