<p align="center">
  <img width="300" src="doc/icons/bff.png" />
</p>
<p align="center">
  <img src="https://sonarcloud.io/api/project_badges/measure?project=lucabelezal_pokedex-bff&metric=alert_status" />
  <img src=https://sonarcloud.io/api/project_badges/measure?project=lucabelezal_pokedex-bff&metric=coverage />
  <img src="https://img.shields.io/badge/status-active-brightgreen" />
  <img src="https://img.shields.io/badge/version-1.0.0-blue" />
  <img src="https://img.shields.io/badge/license-Apache%202.0-orange" />
</p>

## 📚 Sumário

Aqui estão as seções importantes para explorar o **Pokedex BFF**:

* 📖 [**Visão Geral**](doc/OVERVIEW.md)  
  _Contexto geral e objetivos do projeto._

* 🌐 [**Fontes de Dados**](doc/DATA_SOURCES.md)  
  _Descrição das origens dos dados utilizados._

* 🎨 [**Pokédex / Pokémon App (Design)**](doc/POKEDEX_APP.md)  
  _Visão de como o front interage com o BFF._

* 🗄️ [**Informações do Banco de Dados (PostgreSQL)**](doc/DATABASE.md)  
  _Modelagem e estrutura da base de dados._

* 🧰 [**Tecnologias e Softwares Utilizados**](doc/TECHNOLOGIES.md)  
  _Kotlin, Spring Boot, PostgreSQL, Swagger, etc._

* 🚀 [**Como Começar**](doc/GETTING_STARTED.md)  
  _Guia de instalação e execução local._

* 📘 [**Documentação da API (Swagger)**](doc/SWAGGER.md)  
  _Endpoints e contratos REST expostos._

* 🏗️ [**Arquitetura do Sistema**](doc/ARCHITECTURE.md)  
  _Camadas, responsabilidades e organização._

---

## 🚀 Novo Fluxo de Inicialização e Carga de Dados

A partir da versão 2025.09, o BFF está **totalmente desacoplado da carga e inicialização de dados**. O banco de dados é populado via scripts SQL, sem dependência de seeder ou carga automática no BFF.

### 1. Gerar os dados SQL a partir dos JSONs

Execute o script abaixo para converter todos os arquivos JSON de `src/main/resources/data` em comandos SQL:

```sh
python3 scripts/json2sql.py
```

Isso irá gerar/atualizar o arquivo `docker/db/init-data.sql`.

### 2. Subir o ambiente com Docker Compose

O banco será criado e populado automaticamente ao subir o ambiente:

```sh
docker-compose up --build
```

- O serviço `db` executa `schema.sql` e `init-data.sql`.
- O serviço `bff` sobe sem executar nenhuma carga de dados.

### 3. Atualizar dados

Para atualizar os dados:
- Edite os arquivos JSON em `src/main/resources/data`.
- Execute novamente o script Python.
- Reinicie o banco de dados.

> **Nota:** Os arquivos JSON em `src/main/resources/data` devem ser nomeados com prefixos numéricos (ex: `01_region.json`, `02_type.json`, etc.) para garantir a ordem correta de importação e evitar problemas de integridade relacional. O script de importação respeita essa ordem automaticamente. Certifique-se de que os dados estejam consistentes e que todas as referências de chave estrangeira existam nos arquivos anteriores.

---

## ❌ O que foi removido
- Todo o código de seeder, runners, estratégias e utilitários.
- Qualquer dependência de carga automática de dados no ciclo do BFF.
- O subprojeto `pokedex-seeder`.

---

## ✅ O que mudou
- O BFF agora **apenas consome dados já existentes no banco**.
- O banco é inicializado e populado de forma independente, via scripts SQL.
- O fluxo está mais limpo, reprodutível e alinhado com boas práticas de arquitetura.
