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

## 🛠️ Setup de Desenvolvimento e Estrutura do Projeto

### 📂 Estrutura de Arquivos

O projeto está organizado para facilitar o desenvolvimento e manutenção:

```
pokedex-bff/
├── data/               # 📊 Dados fonte
│   └── json/          # Arquivos JSON numerados (01-10)
├── database/          # 🗄️ Scripts de banco
│   ├── schema/        # DDL - estrutura das tabelas
│   ├── seeds/         # DML - dados iniciais gerados
│   └── migrations/    # Scripts de migração
├── tools/             # 🔧 Ferramentas de desenvolvimento
│   └── database/      # Scripts Python para banco
└── docker/            # 🐳 Configurações Docker
```

### 🚀 Como Iniciar o Desenvolvimento

#### 0. ⚡ Verificar Dependências (PRIMEIRO PASSO)
Antes de começar, verifique se todas as dependências estão instaladas:
```sh
make check-deps
```
- **O que verifica**: Python 3.7+, Docker, Docker Compose, Make, psycopg2
- **Compatibilidade**: Linux (Debian/Ubuntu), macOS, Windows (WSL/Git Bash)
- **Se algo faltar**: O script mostra instruções de instalação específicas para seu sistema

#### 1. Gerar dados SQL dos JSONs
Converte os arquivos JSON numerados em comandos SQL:
```sh
make generate-sql-data
```
- **O que faz**: Lê os 10 arquivos JSON em sequência e gera `database/seeds/init-data.sql`
- **Script**: `tools/database/generate_sql_from_json.py`

#### 2. Subir banco de desenvolvimento
Inicia apenas o banco PostgreSQL com dados:
```sh
make db-only-up
```
- **O que faz**: Executa schema, popula dados e disponibiliza banco em `localhost:5434`
- **Quando usar**: Para desenvolvimento focado no banco ou testes de dados

#### 3. Validar banco
Verifica se todas as tabelas e dados foram carregados corretamente:
```sh
make validate-db
```
- **O que faz**: Conecta ao banco e valida 13 tabelas esperadas com contagem de registros
- **Script**: `tools/database/validate_database.py`

#### 4. Gerenciar banco
```sh
make db-only-restart    # Reinicia banco com dados atualizados
make db-only-down       # Para o banco
make db-info           # Exibe informações de conexão
```

### 📊 Sequência de Criação dos Dados

Os arquivos JSON seguem uma **ordem específica** para respeitar dependências de chaves estrangeiras:

1. `01_region.json` → Regiões base
2. `02_type.json` → Tipos de Pokémon  
3. `03_egg_group.json` → Grupos de ovos
4. `04_generation.json` → Gerações
5. `05_ability.json` → Habilidades
6. `06_species.json` → Espécies (depende de regiões/gerações)
7. `07_stats.json` → Estatísticas
8. `08_evolution_chains.json` → Cadeias evolutivas
9. `09_pokemon.json` → Pokémons (depende de species/abilities/stats)
10. `10_weaknesses.json` → Fraquezas (depende de pokémons)

---

## 💻 Requisitos e Compatibilidade

### 📋 Dependências Necessárias

| Ferramenta | Versão Mínima | Propósito |
|------------|---------------|-----------|
| **Python** | 3.7+ | Scripts de geração e validação |
| **Docker** | 20.0+ | Containerização do banco |
| **Docker Compose** | 2.0+ | Orquestração de serviços |
| **Make** | 3.8+ | Automação de comandos |
| **psycopg2** | 2.8+ | Conexão Python-PostgreSQL |

### 🌐 Compatibilidade Multiplataforma

#### ✅ **Linux (Debian/Ubuntu)**
```bash
# Instalar dependências
sudo apt update
sudo apt install python3 python3-pip build-essential
curl -fsSL https://get.docker.com -o get-docker.sh && sh get-docker.sh
sudo apt install docker-compose-plugin
pip3 install psycopg2-binary
```

#### ✅ **macOS**
```bash
# Com Homebrew
brew install python3 docker make
pip3 install psycopg2-binary
```

#### ✅ **Windows**
- **Opção 1 - WSL2** (Recomendado): Use Ubuntu no WSL2 + Docker Desktop
- **Opção 2 - Git Bash**: Docker Desktop + Python + Make (via chocolatey)
- **Opção 3 - PowerShell**: Use `docker` e `python` diretamente

### 🔧 Verificação Automática
O comando `make check-deps` verifica automaticamente todas as dependências e fornece instruções de instalação específicas para seu sistema operacional.

---

### ➕ Adicionando Novos Dados

Para adicionar novos Pokémons ou dados:

1. **Edite os JSONs**: Atualize os arquivos em `data/json/` respeitando a sequência
2. **Gere SQL**: Execute `make generate-sql-data` 
3. **Atualize banco**: Execute `make db-only-restart`
4. **Valide**: Execute `make validate-db`

> ⚠️ **Importante**: Mantenha a numeração sequencial dos arquivos e respeite as dependências. Novos inserts devem ser adicionados aos JSONs correspondentes, nunca diretamente no SQL.

> **Nota:** Os arquivos JSON em `data/json/` devem ser nomeados com prefixos numéricos (ex: `01_region.json`, `02_type.json`, etc.) para garantir a ordem correta de importação e evitar problemas de integridade relacional. O script de importação respeita essa ordem automaticamente. Certifique-se de que os dados estejam consistentes e que todas as referências de chave estrangeira existam nos arquivos anteriores.

