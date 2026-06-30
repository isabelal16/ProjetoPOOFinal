# 📚 Sistema de Gerenciamento de Biblioteca (JavaLibrary)

**JavaLibrary** é uma aplicação desktop desenvolvida em **Java** que implementa um sistema completo para gerenciamento de acervos, clientes, empréstimos e notificações de atraso.

O sistema utiliza **interface gráfica (GUI) desenvolvida com Swing** e foi criado como forma de consolidar os aprendizados adquiridos na disciplina **SCC0204 — Programação Orientada a Objetos (2026)**.

O projeto foi estruturado seguindo princípios de **Programação Orientada a Objetos (POO)**, padrões de projeto e persistência de dados utilizando arquivos de texto simples (`.txt`).

---

# 📋 Descrição do Sistema

O sistema contempla os seguintes fluxos principais:

* **Autenticação de Usuários**
  Controle de sessão e gerenciamento de permissões com base no perfil de acesso (**Administrador** ou **Bibliotecário**).

* **Gerenciamento de Livros (CRUD)**
  Cadastro, consulta, atualização e remoção de livros com validação de **ISBN único** e controle de quantidade de exemplares.

* **Gerenciamento de Clientes (Patronos)**
  Cadastro de membros e rastreamento automático do histórico individual de empréstimos.

* **Gerenciamento de Empréstimos**
  Controle de empréstimos e devoluções com cálculo automático de multas por atraso.

* **Relatórios e Auditoria**
  Geração de registros textuais contendo itens em atraso em formato semelhante a notificações por e-mail.

---

# 🛠️ Requisitos do Sistema

* **Java:** JDK 11 ou superior (configurado no `PATH`)
* **Sistema Operacional:** Windows, Linux ou macOS
* **Memória RAM:** mínimo de 512 MB (recomendado 1 GB)

---

# 📁 Estrutura de Pastas

```text
JavaLibrary/
├── src/
│   ├── exception/        # Exceções personalizadas (LibraryException)
│   ├── model/            # Entidades (Book, User, Loan, Patron...)
│   ├── controller/       # Regras de negócio, persistência e fachada
│   ├── view/             # Interface gráfica (Swing)
│   └── Main.java         # Ponto de entrada da aplicação
│
├── books.txt             # Base de livros (gerado automaticamente)
├── patrons.txt           # Base de clientes (gerado automaticamente)
├── loans.txt             # Base de empréstimos (gerado automaticamente)
```

---

# 💾 Persistência de Dados

Os dados são armazenados localmente na pasta raiz do projeto utilizando arquivos de texto (`.txt`) com valores separados por ponto e vírgula (**CSV manual**).

A persistência é gerenciada pela classe **DataManager**, responsável pela criação, leitura e atualização automática dos arquivos conforme as operações realizadas na interface gráfica.

Além dos arquivos principais, o sistema também gera registros de auditoria automaticamente.

---

# 🔐 Credenciais de Acesso (Login Padrão)

O sistema possui dois níveis de acesso implementados por meio de polimorfismo:

| Usuário | Senha    | Perfil        | Permissões                         |
| ------- | -------- | ------------- | ---------------------------------- |
| admin   | admin123 | Administrador | Acesso completo ao sistema         |
| lib     | lib123   | Bibliotecário | Consulta, empréstimos e devoluções |

### Administrador

* Gerenciamento completo de livros
* Gerenciamento de clientes
* Empréstimos e devoluções
* Relatórios e auditoria

### Bibliotecário

* Consulta ao acervo
* Registro de empréstimos
* Registro de devoluções

---

# 🚀 Como Executar o Projeto

## Opção A — VS Code (Recomendado)

1. Abra a pasta **JavaLibrary** no VS Code.
2. Instale a extensão **Extension Pack for Java** (Microsoft).
3. Abra o arquivo:

```text
src/Main.java
```

4. Clique em **Run** (▶️) no canto superior direito.

---

## Opção B — Windows

Execute o arquivo:

```text
run.bat
```

O script irá:

* Criar a pasta `out/`
* Compilar o projeto
* Executar a aplicação automaticamente

---

## Opção C — Linux / macOS

No terminal:

```bash
chmod +x run.sh

./run.sh
```

---

# 👑 Detalhes Técnicos e Padrões Aplicados

### Facade Pattern

Implementado na classe **LibraryManager**, funcionando como ponto central de comunicação entre a interface gráfica e as regras de negócio.

### Encapsulamento e Integridade

O sistema impede a exclusão de livros ou clientes que possuam empréstimos ativos, preservando a consistência dos dados.

### Persistência em Arquivos

Implementação baseada em arquivos `.txt`, dispensando o uso de banco de dados externo.

### Regra de Negócio — Multas por Atraso

Os empréstimos possuem prazo padrão de **14 dias corridos**.

Após esse período, é aplicada uma multa fixa de:

**R$ 2,00 por dia de atraso.**

O cálculo é realizado automaticamente no momento da devolução.

---

# 🎯 Objetivo Acadêmico

Este projeto foi desenvolvido como atividade da disciplina **SCC0204 — Programação Orientada a Objetos (2026)** com foco na aplicação prática de:

* Programação Orientada a Objetos (POO)
* Interface gráfica com Swing
* Persistência de dados
* Padrões de projeto
* Organização em camadas
* Boas práticas de desenvolvimento em Java
