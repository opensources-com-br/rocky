const content = {
  "group": "Avançado",
  "lead": "A versão atual usa as telas de Configurações, preferências do sistema e armazenamento local. Não lê config.toml.",
  "blocks": [
    {
      "type": "h2",
      "text": "Configurações"
    },
    {
      "type": "para",
      "text": "Use Plataformas, IA, Voz, Agente e Dados na interface. Não existem perfis via --profile, recarga automática de TOML ou variáveis ROCKY_CONFIG, ROCKY_DATA_DIR e ROCKY_LOG implementadas para esse fluxo."
    },
    {
      "type": "h2",
      "text": "Armazenamento"
    },
    {
      "type": "para",
      "text": "No macOS, os dados ficam em ~/Library/Application Support/Rocky; no Windows, em %APPDATA%/Rocky. Preferências não secretas, incluindo o nome do canal do TikTok, usam o namespace dev/rocky. Chaves de IA e ElevenLabs, Client Secrets da Kick e do YouTube e App Secret do Facebook usam o cofre do sistema; tokens das plataformas ficam na memória. SQLite não é criptografado pelo Rocky."
    },
    {
      "type": "h2",
      "text": "Backup e importação"
    },
    {
      "type": "para",
      "text": "Em Dados, exporte um backup JSON dos registros e fontes, sem credenciais. A importação pede confirmação, ignora cópias idênticas e cancela diante de IDs conflitantes; não substitui registros existentes. Para copiar o banco diretamente, feche Rocky primeiro."
    },
    {
      "type": "h2",
      "text": "Diagnóstico e atualização"
    },
    {
      "type": "para",
      "text": "Dados permite visualizar e exportar diagnóstico sem mensagens ou chaves. A consulta de atualização acessa o GitHub e pode ser desativada nas configurações. Baixar atualização seleciona o pacote compatível, mostra progresso e verifica o SHA-256 publicado. Você pode cancelar e tentar novamente. Desconecte as plataformas e clique em Atualizar e reiniciar para instalar e reabrir Rocky, preservando os dados e as credenciais salvas. Versões antigas sem esse botão precisam receber a versão com atualização integrada uma vez pelo instalador oficial. Redefinir configurações é uma ação separada que remove as credenciais salvas e encerra a sessão; reabra o app para aplicar todos os padrões."
    }
  ]
};

export default content;
