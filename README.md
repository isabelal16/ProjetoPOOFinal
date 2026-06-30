# 📚 JavaLibrary - Sistema de Gerenciamento de Biblioteca

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge)
![POO](https://img.shields.io/badge/POO-Programação_Orientada_a_Objetos-success?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Concluído-brightgreen?style=for-the-badge)

---

# 📖 Sobre o Projeto

O **JavaLibrary** é um sistema de gestão de biblioteca desenvolvido em **Java** com interface construída em **Swing**.

O propósito do projeto é aplicar conceitos de **Programação Orientada a Objetos (POO)** e apresentar um fluxo completo de cadastro de livros, gestão de usuários, controle de empréstimos e geração de relatórios.

O sistema também oferece controle de acesso por perfil, validando privilégios para as operações de administração e uso diário.

O sistema permite o gerenciamento de:

- 📚 Livros
- 👨‍🎓 Usuários
- 🔄 Empréstimos
- 💰 Multas
- 📊 Relatórios
- 🔐 Permissões de acesso

---

# 📋 Requisitos

O sistema foi desenvolvido para atender aos seguintes requisitos gerais:

- Gerenciar o acervo de livros da biblioteca;
- Cadastrar e atualizar usuários do sistema;
- Registrar empréstimos e devoluções;
- Calcular multas por atraso;
- Gerar relatórios de empréstimos e atrasos;
- Fazer controle de acesso entre administrador e bibliotecário;
- Persistir os dados entre execuções.

As funcionalidades principais estão descritas na seção **🚀 Funcionalidades Principais**.

---

# 👨‍💻 Integrantes

| Nome | NUSP |
|---|---|
| Ana Clara Stolses | 15654835 |
| Isabela Lima | 15678780 |

---

# 🖥️ Interfaces do Sistema

A interface gráfica utiliza **Java Swing** e foi organizada em painéis para facilitar o uso em operações de consulta, cadastro e controle de empréstimos.

O layout é dividido em abas que suportam acesso rápido a livros, usuários, empréstimos e relatórios.

Os mockups abaixo representam algumas das telas implementadas no sistema.

---

# 📷 Mockups das Interfaces do Sistema

## 📚 Tela de Gerenciamento de Livros

![Tela Livros Admin](mockups/Livros%20ADMIN.png)

---

## 🔄 Tela de Empréstimos

![Tela Empréstimos Bibliotecário](mockups/emprestimo-bibliotecario.png)

---

## 📊 Tela de Relatórios

![Tela Relatórios Admin](mockups/relatorio-Admin.png)

---

## 👨‍🎓 Tela de Usuários

![Tela Usuários Admin](mockups/Usuarios-admin.png)

---

# 🚀 Funcionalidades Principais

## 📚 Gestão de Livros

- Cadastro de livros
- Edição de informações do livro
- Exclusão de livros
- Controle de quantidade de exemplares
- Busca por título, autor, ISBN e gênero

---

## 👨‍🎓 Gestão de Usuários

- Cadastro de usuários
- Edição de perfil de usuário
- Exclusão de usuários
- Busca por nome ou identificador

---

## 🔄 Sistema de Empréstimos

- Registro de empréstimos com prazo padrão de **14 dias**
- Verificação de disponibilidade de exemplares
- Renovação de empréstimos
- Registro de devolução de livros
- Atualização automática do estoque de exemplares
- Histórico de empréstimos

---

## 💰 Sistema de Multas

- Cálculo automático de multas de atraso
- Identificação de empréstimos vencidos
- Valor da multa:
  - **R$ 2,00 por dia de atraso**
- Bloqueio de operações em caso de pendências

---

## 📊 Relatórios

O sistema gera relatórios de:

- Empréstimos ativos
- Empréstimos atrasados
- Histórico por usuário
- Histórico geral

---

## 🔐 Controle de Acesso

O sistema possui dois perfis de usuário principais:

| Perfil | Permissões |
|---|---|
| Administrador | Cadastro, edição e exclusão de livros e usuários; geração de relatórios; controle de permissões |
| Bibliotecário | Registro de empréstimos e devoluções; consulta ao acervo |

---

## 💾 Persistência de Dados

Os dados são mantidos em arquivos de texto no diretório raiz do projeto.

Arquivos utilizados:

```text
books.txt
patrons.txt
loans.txt
```

A classe **DataManager** faz a leitura e escrita desses arquivos sempre que o sistema atualiza livros, usuários ou empréstimos.

---

# 🧠 Estrutura do Projeto

## 📂 Principais Classes

| Classe | Responsabilidade |
|---|---|
| `Book` | Representa os livros do acervo |
| `Patron` | Representa os usuários/patronos |
| `Loan` | Registra empréstimos e devoluções |
| `User` | Modelo base para autenticação |
| `Administrator` | Perfil de administrador |
| `Librarian` | Perfil de bibliotecário |
| `LibraryManager` | Lógica de negócio e validações |
| `DataManager` | Persistência em arquivos de texto |
| `AuthenticationService` | Autenticação de login |
| `BookService` | Operações sobre livros |
| `PatronService` | Operações sobre usuários |
| `LoanService` | Controle de empréstimos |
| `ReportService` | Geração de relatórios |
| `LibraryGUI` | Interface gráfica principal |
| `Main` | Ponto de entrada da aplicação |

---

# 🛠️ Conceitos de POO Aplicados

## 1️⃣ Herança

O sistema usa herança para definir perfis de usuário a partir de uma classe base e para estruturar entidades de domínio.

---

## 2️⃣ Encapsulamento

Os atributos das classes são protegidos com modificadores `private` e acessados via getters e setters.

---

## 3️⃣ Polimorfismo

Perfis distintos de usuário têm comportamentos específicos por meio de classes especializadas.

---

## 4️⃣ Abstração

As classes representam entidades do domínio com foco nos atributos essenciais para a biblioteca.

---

## 5️⃣ Organização em Camadas

A separação entre interface, serviço e persistência permite manter o código mais modular e fácil de manter.

---

# 💬 Comentários sobre o Código

A arquitetura do projeto separa responsabilidades em camadas:

- A interface gráfica em `view/` cuida da interação com o usuário;
- As classes em `controller/` realizam as regras de negócio e validações;
- A persistência em `DataManager` mantém os dados em arquivos de texto.

Esse desacoplamento facilita futuras melhorias e manutenção do sistema.

---

# 🧪 Como Executar o Projeto

## ✅ Pré-requisitos

- Java Development Kit (JDK) 11 ou superior instalado;
- Terminal ou prompt de comando;
- VS Code com extensão Java, se for usar a IDE.

## ▶️ Executando em VS Code

1. Abra o diretório do projeto em VS Code.
2. Abra `src/Main.java`.
3. Use a opção de execução do Java no editor.

## ▶️ Executando pelo terminal (Windows)

```powershell
run.bat
```

## ▶️ Executando pelo terminal (Linux/macOS)

```bash
chmod +x run.sh
./run.sh
```

## 🔐 Login padrão

Utilize as credenciais abaixo para acessar o sistema:

| Perfil | Login | Senha |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Bibliotecário | `lib` | `lib123` |

---

# 📄 Licença

Projeto desenvolvido para fins acadêmicos na disciplina de Programação Orientada a Objetos.
