<p align="center">
  <img width="300" src="docs/assets/icons/bff.png" />
</p>
<p align="center">
  <img src="https://sonarcloud.io/api/project_badges/measure?project=lucabelezal_pokedex-bff&metric=alert_status" />
  <img src=https://sonarcloud.io/api/project_badges/measure?project=lucabelezal_pokedex-bff&metric=coverage />
  <img src="https://img.shields.io/badge/status-active-brightgreen" />
  <img src="https://img.shields.io/badge/version-1.0.0-blue" />
  <img src="https://img.shields.io/badge/license-Apache%202.0-orange" />
</p>

## 📚 Documentação

### 🏗️ **Arquitetura**
* [**Clean Architecture**](docs/architecture/CLEAN_ARCHITECTURE.md) - _Implementação detalhada com Hexagonal Architecture_
* [**Arquitetura do Sistema**](docs/architecture/ARCHITECTURE.md) - _Camadas, responsabilidades e organização_

### 🗄️ **Banco de Dados**
* [**Database**](docs/database/DATABASE.md) - _Modelagem e estrutura PostgreSQL_
* [**Schema**](docs/database/SCHEMA.md) - _Scripts e diagramas do banco_
* [**Data Sources**](docs/database/DATA_SOURCES.md) - _Origens dos dados utilizados_

### 🚀 **Desenvolvimento**
* [**Getting Started**](docs/GETTING_STARTED.md) - _Guia de instalação e execução_
* [**Style Guide**](docs/development/STYLE_GUIDE.md) - _Padrões de código e code review_
* [**Pokédex App**](docs/development/POKEDEX_APP.md) - _Design e interação front-end_

### � **AI Development**
* [**AI Prompt Template**](docs/ai/PROMPT_TEMPLATE.md) - _Templates para desenvolvimento assistido por IA_
* [**Development Guide**](docs/ai/DEVELOPMENT_GUIDE.md) - _Guias para usar IA mantendo Clean Architecture_

### � **API**
* [**Swagger Documentation**](docs/api/SWAGGER.md) - _Endpoints e contratos REST_

### 📋 **Geral**
* [**Overview**](docs/OVERVIEW.md) - _Contexto geral e objetivos do projeto_
* [**Technologies**](docs/TECHNOLOGIES.md) - _Kotlin, Spring Boot, PostgreSQL, etc._
* [**Context**](docs/CONTEXT.md) - _Contexto completo do projeto e arquitetura_


---

## 🛠️ **Setup Rápido**

### **🚀 Início Rápido**
```bash
# 1. Verificar dependências
make check-deps

# 2. Gerar dados SQL
make generate-sql-data

# 3. Subir banco + aplicação
make up

# 4. Validar funcionamento
make validate-db
```

### **🏗️ Arquitetura Implementada**

O projeto utiliza **Clean Architecture** + **Hexagonal Architecture**:

```
🏗️ Clean Architecture Layers:
├── � Domain Layer (Entities, Value Objects, Repository Interfaces)
├── 🎯 Application Layer (Use Cases, Ports, DTOs)
├── 🔧 Infrastructure Layer (Adapters, JPA, External APIs)
└── 🌐 Interface Layer (REST Controllers, GraphQL, etc.)

🔌 Ports & Adapters Pattern:
├── Input Ports: PokedexUseCases, PokemonUseCases
├── Output Ports: PokemonRepository, ExternalApiPort
├── Input Adapters: PokedexUseCasesAdapter, Controllers
└── Output Adapters: PokemonRepositoryAdapter, ExternalApiAdapter
```

**Principais Implementações**:
- ✅ **Value Objects**: `PokemonId`, `PokemonNumber` com validações
- ✅ **Use Cases**: `GetPaginatedPokemonsUseCase`, `SearchPokemonByNameUseCase`
- ✅ **Ports & Adapters**: Interfaces e implementações separadas
- ✅ **Domain-First**: Lógica de negócio protegida em domain layer

### **📂 Estrutura do Projeto**

```
pokedex-bff/
├── docs/                    # 📚 Documentação organizada
│   ├── architecture/        # 🏗️ Clean Architecture docs
│   ├── database/           # 🗄️ Database schema e migrations
│   ├── development/        # 🚀 Development guides
│   ├── ai/                # 🤖 AI development guidelines
│   ├── api/               # 📡 API documentation
│   └── assets/            # 🎨 Icons, schemas, Postman
├── src/main/kotlin/com/pokedex/bff/
│   ├── domain/            # 💎 Core business logic
│   │   ├── entities/      # Business entities
│   │   ├── valueobjects/  # Value objects with validation
│   │   └── repositories/  # Repository interfaces
│   ├── application/       # 🎯 Use cases & application logic
│   │   ├── ports/         # Input/Output ports
│   │   ├── usecases/      # Specific use cases
│   │   └── adapters/      # Port implementations
│   ├── infrastructure/    # 🔧 Technical implementations
│   │   ├── adapters/      # Repository & external adapters
│   │   ├── persistence/   # JPA entities & repos
│   │   └── configurations/ # Spring configurations
│   └── interfaces/        # 🌐 External interfaces
│       └── rest/          # REST controllers & DTOs

```

## �️ **Comandos de Desenvolvimento**

### **🐳 Docker & Database**
```bash
# Ambiente completo
make up                  # Sobe banco + aplicação
make down               # Para tudo
make restart            # Reinicia tudo

# Apenas banco
make db-only-up         # Sobe apenas PostgreSQL
make db-only-down       # Para apenas banco
make db-only-restart    # Reinicia banco

# Dados e validação
make generate-sql-data  # Gera SQL dos JSONs
make validate-db        # Valida banco e dados
make db-info           # Info de conexão
```

### **🧪 Testes e Qualidade**
```bash
# Testes
./gradlew test                    # Unit tests
./gradlew integrationTest         # Integration tests
./gradlew testReport             # Relatório de cobertura

# Code quality
./gradlew detekt                 # Static analysis
./gradlew check                  # All quality checks
```

### **📦 Build e Deploy**
```bash
# Local build
./gradlew build                  # Build completo
./gradlew bootRun               # Run local

# Docker
docker build -t pokedex-bff .   # Build image
docker run -p 8080:8080 pokedex-bff  # Run container
```

## 🤖 **AI-Assisted Development**

Este projeto oferece **guidelines específicas para desenvolvimento assistido por IA** mantendo a **Clean Architecture**:

### **📋 Templates para IA**
- [**Prompt Template**](docs/ai/PROMPT_TEMPLATE.md) - Template completo para solicitações
- [**Development Guide**](docs/ai/DEVELOPMENT_GUIDE.md) - Guias para usar IA corretamente

### **🎯 Princípios para IA**
1. **Domain-First**: Sempre começar pelo domain layer
2. **Ports & Adapters**: Manter separação clara de responsabilidades  
3. **Value Objects**: Criar VOs ricos com validações
4. **Specific Use Cases**: Evitar services genéricos
5. **Test-Driven**: Incluir testes unitários sempre

### **⚠️ Cuidados com IA**
- ❌ Não permitir que IA misture camadas
- ❌ Não aceitar anotações JPA em domain entities
- ❌ Não criar use cases genéricos
- ✅ Sempre revisar código gerado seguindo [Style Guide](docs/development/STYLE_GUIDE.md)

## 📊 **Status do Projeto**

### **✅ Clean Architecture Implementada**
- [x] **Domain Layer**: Entities, Value Objects, Repository interfaces
- [x] **Application Layer**: Use Cases específicos, Ports & Adapters
- [x] **Infrastructure Layer**: JPA adapters, External service adapters
- [x] **Interface Layer**: REST controllers thin e focados

### **✅ Value Objects Ricos**
- [x] `PokemonId` com validações de range
- [x] `PokemonNumber` com formatação e validações
- [x] Testes unitários para todos Value Objects

### **✅ Use Cases Específicos**
- [x] `GetPaginatedPokemonsUseCase` 
- [x] `SearchPokemonByNameUseCase`
- [x] `GetPokemonByIdUseCase`
- [x] Testes unitários com mocks

### **🔄 Em Desenvolvimento**
- [ ] Event-driven architecture com Domain Events
- [ ] CQRS implementation para queries complexas
- [ ] Circuit breaker para external APIs
- [ ] Observability com OpenTelemetry

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

