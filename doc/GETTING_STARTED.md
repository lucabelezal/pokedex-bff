# Como Começar

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
        3.  Iniciar o BFF, que por sua vez executará as migrações (se configuradas para rodar no `bootRun` do perfil `dev`) e populará o DB com os dados dos arquivos jsons na pasta resource.

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
