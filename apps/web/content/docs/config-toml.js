const content = {
  group: "Avançado",
  lead: "Toda a configuração do app vive em um arquivo de texto. A tela de configurações escreve nele; você também pode editar à mão e o app recarrega sozinho.",
  blocks: [
    {"type": "h2", "text": "Arquivo completo"},
    {"type": "code", "file": "~/.rocky/config.toml", "text": "[ai]\nprovider = \"ollama\"\nmodel    = \"llama3.1:8b\"\nbase_url = \"http://localhost:11434\"\ncontext_window = \"10min\"\n\n[voice]\nengine = \"piper\"\nvoice  = \"pt_BR-faber\"\nrate   = 1.0\nmonitor_volume = 0.7\nducking = true\n\n[agent]\ntone      = \"direto\"\ninterrupt = false\ninterventions_per_10min = 3\n\n[superchats]\nauto_read = true\nmin_value = 10.0\n\n[priorities]\nmoney      = 1.0\nunanswered = 0.9\nrepetition = 0.7\n\n[window]\nposition = \"top-right\"\ncollapsed_on_start = false\nalways_on_top = true"},
    {"type": "h2", "text": "Perfis"},
    {"type": "para", "text": "Vários arquivos de perfil permitem trocar a configuração inteira entre tipos de live — uma aula pede tom analítico e sem interrupção; uma watch party pede o contrário."},
    {"type": "code", "file": "Terminal", "text": "$ open -a Rocky --args --profile aula\n$ open -a Rocky --args --profile watchparty"},
    {"type": "h2", "text": "Variáveis de ambiente"},
    {"type": "table", "cols": "minmax(0,1.2fr) minmax(0,1.8fr)", "head": ["VARIÁVEL", "EFEITO"], "rows": [["ROCKY_CONFIG", "Caminho alternativo do arquivo de configuração"], ["ROCKY_DATA_DIR", "Onde salvar sessões, notas e resumos"], ["ROCKY_LOG", "Nível de log: error, warn, info, debug"]]},
  ],
};

export default content;
