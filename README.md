# Projeto POO Final - Java Library

## 🚀 Como Executar o Projeto

Para rodar o projeto, você pode ou utilizar os scripts de automação inclusos ou rodar diretamente pelo **Visual Studio Code**.

### Pré-requisitos
* Ter o **JDK 11 (ou superior)** instalado e configurado nas variáveis de ambiente do seu sistema (`PATH`).

### 💻 Executando via VS Code (Método mais prático)
1. Abra a pasta raiz do projeto no VS Code.
2. Certifique-se de ter instalada a extensão oficial: **Extension Pack for Java** (da Microsoft).
3. Abra o arquivo `src/Main.java`.
4. Clique no ícone de **Play (Run Code)** que aparece no canto superior direito do editor (ou clique no link *Run* que aparece logo acima do método `public static void main`).

---

### 🪟 Executando via Terminal (Windows)
Na pasta raiz do projeto, basta dar **dois cliques** no arquivo:
* `run.bat`
  
*O script criará a pasta `out/`, compilará todos os arquivos `.java` na ordem correta de dependências e inicializará a tela de login automaticamente.*

---

### 🐧 🍏 Executando via Terminal (Linux / macOS)
Abra o terminal na pasta raiz do projeto e execute os seguintes comandos:
```bash
# Conceda permissão de execução ao script (necessário apenas na primeira vez)
chmod +x run.sh

# Execute o script
./run.sh
