# Pokedex BFF

## Visão Geral

Este repositório contém o código-fonte para o **Pokedex BFF (Backend For Frontend)**. Este serviço atua como uma camada intermediária otimizada entre as fontes de dados externas sobre Pokémon e as aplicações frontend que consomem essas informações (web, mobile, etc.).

O principal objetivo do Pokedex BFF é:
* **Simplificar o Consumo de Dados:** Centralizar a lógica de agregação e transformação de dados de diversas fontes, fornecendo uma API unificada e fácil de usar para o frontend.
* **Otimizar Desempenho:** Reduzir o número de requisições que o frontend precisa fazer, combinando dados de múltiplas fontes em uma única resposta.
* **Adaptar Formato:** Oferecer dados no formato exato que o frontend necessita, minimizando a lógica de processamento e adaptação na interface de usuário.
* **Cache e Desempenho:** Implementar estratégias de cache para dados frequentemente acessados, melhorando a velocidade de resposta.

## Fontes de Dados

Os dados utilizados para popular o banco de dados local e alimentar as respostas do BFF são compilados de diversas fontes confiáveis sobre o universo Pokémon. Este processo de coleta e validação é crucial para garantir a precisão e integridade das informações.

* **[Pokemon DB](https://pokemondb.net/)**: Uma das fontes mais abrangentes para informações detalhadas sobre Pokémon, incluindo estatísticas de base, conjuntos de movimentos, habilidades, informações de evolução e localização.
* **[Liga Pokemon](https://www.ligapokemon.com.br/?view=pokedex/home)**: Uma plataforma completa dedicada ao universo Pokémon, com foco especial no card game (TCG). O site oferece informações atualizadas sobre cartas, negociações, torneios, eventos, spoilers de lançamentos, além de fóruns, leilões, lojas parceiras e comparador de preços. Ideal para colecionadores, jogadores e fãs da franquia.
* **[Hey Pikachu! Pokedex](https://www.heypikachu.com/pokedex)**: Fornece dados visuais e descrições úteis para complementar as informações estruturadas, como sprites e obras de arte oficiais.
* **[Pokemon Oficial (Brasil) - Pokedex](https://www.pokemon.com/br/pokedex)**: A Pokedex oficial da The Pokémon Company International, garantindo dados verificados e localizados para o público brasileiro.

## Pokédex / Pokémon App

<p><strong>🎨 Design por:</strong> Junior Saraiva</p>

<p><img src="doc/icons/figma.png" width="24" height="24" /> <strong>Figma</strong> — Protótipo de interface e fluxo do aplicativo Pokédex. <a href="https://www.figma.com/community/file/1202971127473077147">Abrir no Figma</a></p>

## Informações do Banco de Dados (PostgreSQL)

O Pokedex BFF utiliza um banco de dados PostgreSQL para armazenar os dados de Pokémon de forma estruturada e relacional. A documentação completa do esquema do banco de dados é essencial para entender como os dados são organizados e relacionados.

* **[Diagrama e Documentação Detalhada](doc/DATABASE.md)**: Este arquivo Markdown é o ponto de partida para compreender a arquitetura do banco de dados. Ele inclui um diagrama ERD (Entidade-Relacionamento) interativo (gerado com Mermaid), descrições de cada tabela, suas colunas, tipos de dados e os relacionamentos entre elas. **Altamente recomendado para uma compreensão profunda da estrutura de dados.**
* **[DBML](doc/pokedex_db.dbml)**: O código DBML (Database Markup Language) que define o esquema do banco de dados de forma concisa e legível. Pode ser facilmente importado em ferramentas visuais como `dbdiagram.io` para uma representação gráfica interativa.
* **[SQL](doc/pokedex_db.sql)**: O script SQL completo para a criação das tabelas e índices no seu banco de dados PostgreSQL. Ideal para configuração inicial ou recriação do esquema.

---

## Tecnologias e Softwares Utilizados

Este projeto utiliza uma pilha de tecnologias modernas e ferramentas práticas para garantir eficiência, organização e facilidade de desenvolvimento.

### 🛠️ Tecnologias

<p><img src="doc/icons/springboot.png" width="24" height="24" /> <strong>Spring Boot</strong> — Framework robusto para backend em Java/Kotlin. <a href="https://spring.io/projects/spring-boot">Documentação</a></p>

<p><img src="doc/icons/java.png" width="24" height="24" /> <strong>Kotlin & JDK 21</strong> — Linguagem principal na JVM, usando JDK 21. <a href="https://www.oracle.com/java/technologies/downloads/">Download JDK 21</a></p>

<p><img src="doc/icons/springdata.png" width="24" height="24" /> <strong>Spring Data JPA</strong> — Interação com banco relacional de forma orientada a objetos. <a href="https://spring.io/projects/spring-data-jpa">Documentação</a></p>

<p><img src="doc/icons/gradle.png" width="24" height="24" /> <strong>Gradle</strong> — Automação de build e gerenciamento de dependências. <a href="https://gradle.org/">Documentação</a></p>

<p><img src="doc/icons/postgresql.png" width="24" height="24" /> <strong>PostgreSQL</strong> — Banco de dados relacional robusto e extensível. <a href="https://www.postgresql.org/">Site Oficial</a></p>

<p>⚙️ <strong>Apache Commons CSV</strong> — Biblioteca para leitura e processamento de arquivos CSV. <a href="https://commons.apache.org/proper/commons-csv/">Documentação</a></p>

### 💻 Softwares e Ferramentas

<p><img src="doc/icons/intellij.png" width="24" height="24" /> <strong>IntelliJ IDEA</strong> — IDE recomendada para desenvolvimento em Kotlin e Spring Boot. <a href="https://www.jetbrains.com/idea/">Download</a></p>

<p><img src="doc/icons/docker.png" width="24" height="24" /> <strong>Docker & Docker Compose</strong> — Criação e orquestração de contêineres para ambientes isolados. <a href="https://www.docker.com/products/docker-desktop/">Download</a></p>

<p><img src="doc/icons/beekeeperstudio.png" width="24" height="24" /> <strong>Beekeeper Studio</strong> — Cliente SQL visual para gerenciar e consultar o banco PostgreSQL. <a href="https://www.beekeeperstudio.io/">Download</a></p>

<p><img src="doc/icons/bruno.png" width="24" height="24" /> <strong>Bruno</strong> — Cliente para testar e documentar APIs REST de forma intuitiva. <a href="https://www.usebruno.com/">Site Oficial</a></p>

Essas tecnologias e ferramentas tornam o desenvolvimento e a manutenção do **Pokedex BFF** mais produtivos, organizados e escaláveis.

---

## Como Começar

Siga estas instruções para configurar e executar o Pokedex BFF em seu ambiente de desenvolvimento local.

### Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas:
* [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (inclui Docker e Docker Compose)
* **GNU Make** (ou `make` no seu sistema, geralmente pré-instalado em sistemas Unix/Linux/macOS; para Windows, você pode usar WSL ou ferramentas como Chocolatey para instalar `make`).

### Instalação e Configuração Rápida

1.  **Clone o Repositório:**
    ```bash
    git clone [https://github.com/seu-usuario/pokedex-bff.git](https://github.com/seu-usuario/pokedex-bff.git) # Substitua 'seu-usuario' e 'pokedex-bff'
    cd pokedex-bff
    ```

2.  **Configuração do Ambiente e Início (Recomendado):**
    * Para uma configuração rápida e completa do ambiente de desenvolvimento (iniciar DB e carregar dados):
        ```bash
        make dev-setup
        ```
      Este comando cuidará de:
        1.  Iniciar o contêiner PostgreSQL via Docker Compose.
        2.  Aguardar o banco de dados estar pronto.
        3.  Iniciar o BFF, que por sua vez executará as migrações (se configuradas para rodar no `bootRun` do perfil `dev`) e populará o DB com os dados CSV.

### Executando Apenas o Servidor (se o DB já estiver configurado)

Se o banco de dados já estiver rodando e populado, você pode iniciar apenas a aplicação BFF:

```bash
make run-bff
```

### Caso precise ver as opções do comando make
Rode o comando abaixo:
```bash
make
```
```bach
===================================================================
                 Comandos do Makefile para Pokedex BFF             
===================================================================
  make help                   - Exibe esta mensagem de ajuda.

  make dev-setup              - Configura e inicia o ambiente (Linux/macOS).
  make dev-setup-for-windows - Configura e inicia o ambiente (Git Bash/WSL no Windows).

  make start-db               - Inicia o banco PostgreSQL com Docker Compose.
  make stop-db                - Para o contêiner do banco.
  make clean-db               - Remove o banco e os volumes (apaga os dados!).
  make load-data              - Executa o BFF e carrega os dados JSON.
  make run-bff                - Executa o BFF sem importar dados.
  make clean-bff              - Executa './gradlew clean'.

  make clean-all              - Para tudo, limpa DB, Gradle e contêineres.
  make force-remove-db-container - Força a remoção do contêiner 'pokedex-db'.
  make deep-clean-gradle      - Limpa caches e artefatos do Gradle.
===================================================================
```
---

# Arquitetura do Sistema — Pokédex BFF

Se quiser, posso ajudar também a montar o arquivo [ARCHITECTURE.md](doc/ARCHITECTURE.md) com uma documentação mais detalhada e explicações técnicas. Quer?

---