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
* **[Hey Pikachu! Pokedex](https://www.heypikachu.com/pokedex)**: Fornece dados visuais e descrições úteis para complementar as informações estruturadas, como sprites e obras de arte oficiais.
* **[Pokemon Oficial (Brasil) - Pokedex](https://www.pokemon.com/br/pokedex)**: A Pokedex oficial da The Pokémon Company International, garantindo dados verificados e localizados para o público brasileiro.

## Informações do Banco de Dados (PostgreSQL)

O Pokedex BFF utiliza um banco de dados PostgreSQL para armazenar os dados de Pokémon de forma estruturada e relacional. A documentação completa do esquema do banco de dados é essencial para entender como os dados são organizados e relacionados.

* **[Diagrama e Documentação Detalhada](doc/pokedex_db_info.md)**: Este arquivo Markdown é o ponto de partida para compreender a arquitetura do banco de dados. Ele inclui um diagrama ERD (Entidade-Relacionamento) interativo (gerado com Mermaid), descrições de cada tabela, suas colunas, tipos de dados e os relacionamentos entre elas. **Altamente recomendado para uma compreensão profunda da estrutura de dados.**
* **[DBML](doc/new_pokedex_db.dbml)**: O código DBML (Database Markup Language) que define o esquema do banco de dados de forma concisa e legível. Pode ser facilmente importado em ferramentas visuais como `dbdiagram.io` para uma representação gráfica interativa.
* **[SQL](doc/new_pokedex_db.sql)**: O script SQL completo para a criação das tabelas e índices no seu banco de dados PostgreSQL. Ideal para configuração inicial ou recriação do esquema.

## Tecnologias Utilizadas

Este projeto é construído utilizando uma pilha de tecnologias modernas para garantir eficiência e escalabilidade:

* **Spring Boot**: Framework robusto para o desenvolvimento de aplicações Java/Kotlin.
* **Kotlin**: Linguagem de programação moderna e concisa para a JVM.
* **Gradle**: Ferramenta de automação de build para o gerenciamento de dependências e tarefas do projeto.
* **PostgreSQL**: Sistema de gerenciamento de banco de dados relacional robusto e extensível.
* **Spring Data JPA**: Para interação com o banco de dados e persistência de dados.
* **Docker / Docker Compose**: Utilizado para criar um ambiente de desenvolvimento isolado e replicável para o banco de dados.
* **Apache Commons CSV**: Para o processamento de arquivos CSV (para carregamento de dados).

---

## Como Começar

Siga estas instruções para configurar e executar o Pokedex BFF em seu ambiente de desenvolvimento local.

### Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas:
* [Java Development Kit (JDK) 17](https://www.oracle.com/java/technologies/downloads/)
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
        make setup-dev
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

  make setup-dev              - Configura e inicia o ambiente de desenvolvimento completo:
                                  1. Inicia o PostgreSQL via Docker Compose.
                                  2. Aguarda o DB estar acessível.
                                  3. Inicia o BFF, que popula o DB com os CSVs.

  make start-db               - Apenas inicia o contêiner do banco de dados PostgreSQL.
  make stop-db                - Para o contêiner do banco de dados.
  make clean-db               - Remove o contêiner do DB e seus volumes de dados (CUIDADO: APAGA OS DADOS!).

  make load-data              - Inicia o BFF para carregar os dados CSV no DB.
                                  (Requer que o DB já esteja rodando e acessível).
  make clean-bff              - Limpa o projeto BFF (executa './gradlew clean').
  make run-bff                - Inicia o BFF (sem carregar dados automaticamente, a menos que o perfil 'dev' esteja ativo).

  make clean-all              - Limpa o ambiente de desenvolvimento completo:
                                  1. Para o DB. 2. Remove o DB e seus volumes. 3. Limpa o BFF (incluindo caches do Gradle).
  make force-remove-db-container - Força a remoção de um contêiner 'pokedex-db' órfão ou travado.
  make deep-clean-gradle      - Realiza uma limpeza profunda dos caches e artefatos do Gradle.
===================================================================
```
