const content = {
  "id": "oficina",
  "name": "Oficina Aberta",
  "category": "Educação",
  "filter": "Educação",
  "quote": "As dúvidas repetidas viram a pauta da aula seguinte, sem eu anotar nada.",
  "desc": "Aulas ao vivo de marcenaria. Resumo pós-live vai direto para a newsletter.",
  "stack": ["Anthropic", "Voz do macOS"],
  "dots": ["#ff0033", "#1877f2"],
  "tint": "rgba(24,119,242,.06)",
  "links": [{"label": "youtube.com/@oficinaaberta", "url": "https://youtube.com/@oficinaaberta", "dot": "#ff0033"}],
  "stats": [{"label": "PLATAFORMAS", "value": "2"}, {"label": "PICO DE CHAT", "value": "480"}, {"label": "AULAS / MÊS", "value": "8"}, {"label": "MODELO", "value": "Remoto"}],
  "story": ["Dou aula de marcenaria ao vivo, com as mãos na madeira. Não existe a possibilidade de eu digitar ou ler chat: estou com serra na mão e serragem no óculos.", "Para mim o Rocky é uma pessoa na oficina que lê o chat e me conta em voz alta. Respondo falando, ele confirma. Em nenhum momento eu toco no computador durante a aula.", "O que mudou minha produção foi o resumo. Ele lista as dúvidas que ficaram sem resposta e quantas pessoas perguntaram cada uma. Essa lista é literalmente a pauta da aula seguinte, e o texto vai quase sem edição para a newsletter da semana."],
  "changes": ["A pauta da próxima aula sai pronta do resumo da anterior.", "Aluno com dúvida repetida recebe resposta na mesma aula, não na próxima.", "A newsletter semanal deixou de ser uma hora de trabalho."],
  "setup": [{"label": "IA", "value": "anthropic"}, {"label": "Voz", "value": "system · Luciana"}, {"label": "Tom", "value": "direto"}, {"label": "Contexto", "value": "live inteira"}, {"label": "Resumo", "value": "ligado"}],
  "config": "[ai]\nprovider = \"anthropic\"\ncontext_window = \"full\"\n\n[agent]\ntone = \"direto\"\ninterventions_per_10min = 4",
  "sample": "Quatro pessoas perguntaram qual grão de lixa usar nesta etapa.",
};

export default content;
