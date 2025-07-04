# AutoRewardSkipper 🪙🎞️

Automatiza a detecção de recompensas em vídeos do Kwai e TikTok e pula automaticamente para o próximo vídeo.

---

## 🔧 Funcionalidades

- 📸 OCR usando ML Kit para identificar palavras como "ganhou", "moedas", etc.
- 🤖 Swipe automático ao detectar a recompensa
- 🕵️‍♂️ Monitor de apps: pausa OCR se usuário sair do TikTok/Kwai
- ⏹️ Botão flutuante para parar a automação a qualquer momento
- 🛠️ Tela de configuração para definir palavras-chave

---

## 📲 Como usar

1. Clone o projeto ou baixe o `.zip`
2. Abra no Android Studio
3. Dê as permissões necessárias no seu dispositivo:
   - Acessibilidade
   - Sobreposição de tela
   - Uso do app
4. Construa o `.apk` em modo **release**
5. Instale no celular, abra o app e toque em **“Iniciar Automação”**

---

## 🧠 Tecnologias usadas

- Kotlin
- Android Services
- AccessibilityService
- ML Kit OCR
- MediaProjection API

---

## 📝 Licença

Distribuído sob a licença MIT. Veja `LICENSE` para mais detalhes.
