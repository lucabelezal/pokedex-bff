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

# Pokédx BFF - Backend for Frontend

## 🎯 **Visão Geral**

**Pokédx BFF** é um backend especializado que atua como intermediário entre aplicações frontend e múltiplas fontes de dados de Pokémon. Projetado com **MVC estruturado** e **princípios SOLID**, oferece APIs RESTful otimizadas para diferentes necessidades de interface.

## 📁 **Estrutura do Projeto**

```
pokedex-bff/
├── docs/                    # 📚 Documentação técnica
│   ├── architecture/        # Decisões arquiteturais
│   ├── development/         # Guias de desenvolvimento
│   ├── deployment/          # Deploy e infraestrutura
│   ├── api/                 # Documentação da API
│   └── assets/             # Imagens e recursos
├── src/                    # 💻 Código fonte
│   ├── main/kotlin/        # Aplicação principal
│   └── test/kotlin/        # Testes automatizados
├── docker/                 # 🐳 Configurações Docker
├── scripts/                # 🔧 Scripts de automação
└── build.gradle.kts        # ⚙️ Configuração Gradle
```

## 🛠️ **Comandos de Desenvolvimento**

### **🐳 Docker & Database**

#### **Linux/macOS** 🐧🍎
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

#### **Windows** 🪟
> 📖 **Para instruções detalhadas no Windows, consulte**: [**Guia Windows**](docs/WINDOWS_GUIDE.md)

```cmd
# Scripts Batch (.bat)
scripts\windows\setup.bat           # Verificar dependências
scripts\windows\generate-data.bat   # Gerar dados SQL
scripts\windows\start-db.bat        # Subir banco
scripts\windows\start-app.bat       # Subir aplicação
scripts\windows\validate-db.bat     # Validar banco
scripts\windows\stop.bat            # Parar tudo
scripts\windows\logs.bat            # Ver logs
scripts\windows\test.bat            # Executar testes
scripts\windows\build.bat           # Build da aplicação

# PowerShell (.ps1) 
.\scripts\powershell\Setup.ps1          # Verificar dependências
.\scripts\powershell\Generate-Data.ps1   # Gerar dados
.\scripts\powershell\Start-Database.ps1  # Subir banco

# Comandos diretos Docker
docker compose -f docker\docker-compose.dev.yml up -d     # Subir
docker compose -f docker\docker-compose.dev.yml down      # Parar
docker compose -f docker\docker-compose.dev.yml logs -f   # Logs
```

### **🚀 Aplicação**

```bash
# Desenvolvimento local
./gradlew bootRun           # Inicia aplicação
./gradlew test              # Executa testes
./gradlew build             # Build completo
./gradlew bootJar           # Gera JAR executável

# Qualidade de código
./gradlew check             # Análise estática
./gradlew jacocoTestReport  # Relatório cobertura
```

## 🏗️ **Arquitetura**

O projeto utiliza **MVC estruturado** com **princípios SOLID**:

📖 **Documentação Arquitetural**:
- [**Comparação Arquitetural**](docs/architecture/ARCHITECTURE_COMPARISON.md) - Clean vs MVC
- [**Guia de Estilo**](docs/development/STYLE_GUIDE.md) - Padrões de código
- [**Visão Geral**](docs/OVERVIEW.md) - Documentação completa

## 📚 **Documentação**

### 🏗️ **Arquitetura** 
* [**MVC Architecture**](docs/architecture/ARCHITECTURE_COMPARISON.md) - _Comparação Clean vs MVC e implementação_

### 🗄️ **Banco de Dados**
* [**Database**](docs/database/DATABASE.md) - _Modelagem e estrutura PostgreSQL_
* [**Schema**](docs/database/SCHEMA.md) - _Scripts e diagramas do banco_
* [**Data Sources**](docs/database/DATA_SOURCES.md) - _Origens dos dados utilizados_

### 🚀 **Desenvolvimento**
* [**Getting Started**](docs/GETTING_STARTED.md) - _Guia de instalação e execução_
* [**Style Guide**](docs/development/STYLE_GUIDE.md) - _Padrões de código e code review_
* [**Pokédex App**](docs/development/POKEDEX_APP.md) - _Design e interação front-end_

### 🤖 **AI Development**
* [**AI Prompt Template**](docs/ai/PROMPT_TEMPLATE.md) - _Templates para desenvolvimento assistido por IA_
* [**Development Guide**](docs/ai/DEVELOPMENT_GUIDE.md) - _Guias para usar IA mantendo padrões MVC_

### 📡 **API**
* [**Swagger Documentation**](docs/api/SWAGGER.md) - _Endpoints e contratos REST_

### 📋 **Geral**
* [**Overview**](docs/OVERVIEW.md) - _Contexto geral e objetivos do projeto_
* [**Technologies**](docs/TECHNOLOGIES.md) - _Kotlin, Spring Boot, PostgreSQL, etc._
* [**Context**](docs/CONTEXT.md) - _Contexto completo do projeto e arquitetura_

### 🪟 **Windows**
* [**Windows Guide**](docs/WINDOWS_GUIDE.md) - _Guia completo com 4 opções para Windows_
* [**Windows Make Setup**](docs/WINDOWS_MAKE_SETUP.md) - _Como instalar make no Windows_

---

## 🛠️ **Setup Rápido**

#### **Linux/macOS** 🐧🍎
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

#### **Windows** 🪟

**Opção 1 - Instalar Make** ⭐ (Mesmos comandos)
```bash
# 1. Instalar make via Chocolatey (como Admin)
choco install make

# 2. Usar comandos normais igual Linux/macOS
make check-deps
make generate-sql-data
make up
make validate-db
```

**Opção 2 - Scripts Batch (.bat)**
```cmd
# 1. Verificar dependências
scripts\windows\setup.bat

# 2. Gerar dados SQL  
scripts\windows\generate-data.bat

# 3. Subir banco
scripts\windows\start-db.bat

# 4. Validar funcionamento
scripts\windows\validate-db.bat

# 5. Subir aplicação completa
scripts\windows\start-app.bat
```

**Opção 3 - PowerShell (.ps1)**
```powershell
# 1. Verificar dependências
.\scripts\powershell\Setup.ps1

# 2. Gerar dados SQL
.\scripts\powershell\Generate-Data.ps1

# 3. Subir e validar
.\scripts\powershell\Start-Database.ps1
.\scripts\powershell\Validate-Database.ps1
```

**Opção 4 - Comandos Diretos**
```cmd
# Gerar dados
python scripts\json_to_sql.py

# Subir banco
docker compose -f docker\docker-compose.dev.yml up -d db

# Validar banco  
python scripts\check_db.py

# Subir aplicação
docker compose -f docker\docker-compose.dev.yml up -d
```

### **🏗️ Arquitetura Implementada**

O projeto utiliza **MVC estruturado** com **princípios SOLID**:

```
🏗️ MVC Estruturado:
├── 🌐 Controller Layer (REST endpoints, coordenação)
├── 🎯 Service Layer (Business logic, validações)
├── 🗄️ Repository Layer (Data access, persistência)  
└── 📊 Entity Layer (Domain models com comportamentos)

🔧 Principais Componentes:
├── Controllers: PokemonController, PokedexController
├── Services: PokemonService, PokemonSearchService
├── Repositories: PokemonRepository, TypeRepository
└── Entities: Pokemon, Type, Ability
```

**Principais Implementações**:
- ✅ **Services Específicos**: Cada service tem responsabilidade única
- ✅ **Controllers Thin**: Apenas coordenação e mapeamento HTTP
- ✅ **Entities Rica**: Modelos com comportamentos e validações  
- ✅ **Dependency Inversion**: Services dependem de interfaces

### **📂 Estrutura do Projeto**

```
pokedex-bff/
├── docs/                    # 📚 Documentação organizada
│   ├── architecture/        # 🏗️ Documentação arquitetural
│   ├── database/           # 🗄️ Database schema e migrations
│   ├── development/        # 🚀 Development guides
│   ├── ai/                # 🤖 AI development guidelines
│   ├── api/               # 📡 API documentation
│   └── assets/            # 🎨 Icons, schemas, Postman
├── scripts/                # 🔧 Scripts multiplataforma
│   ├── windows/            # 🪟 Scripts .bat para Windows
│   ├── powershell/         # 🪟 Scripts .ps1 para PowerShell
│   ├── json_to_sql.py      # 🐍 Gerador SQL Python
│   └── check_db.py         # 🐍 Validador banco Python
├── src/main/kotlin/com/pokedex/bff/
│   ├── controller/         # 🌐 REST Controllers
│   ├── service/            # 🎯 Business Logic
│   ├── repositories/       # 🗄️ Data Access
│   ├── entity/             # 📊 JPA Entities
│   ├── dto/                # 📄 Data Transfer Objects
│   ├── config/             # ⚙️ Configurations
│   └── exception/          # ❌ Exception Handling
├── docker/                 # 🐳 Docker configurations
│   ├── docker-compose.dev.yml    # Development environment
│   ├── docker-compose.prod.yml   # Production environment
│   └── db/                       # Database initialization
└── gradle/                 # 🔧 Gradle wrapper
```

## 🚀 **Features Implementadas**

### **✅ API REST Completa**
- [x] **CRUD de Pokémon**: Criar, consultar, atualizar e deletar
- [x] **Busca Avançada**: Por nome, tipo, geração, habilidades
- [x] **Paginação**: Listagem otimizada com paginação customizável
- [x] **Filtros**: Múltiplos critérios de busca combinados

### **✅ Gerenciamento de Dados**
- [x] **Data Seeding**: Importação automática de dados JSON
- [x] **Validação**: Integridade referencial e validações de negócio
- [x] **Migração**: Scripts SQL organizados e versionados

### **✅ Infraestrutura**
- [x] **Docker**: Ambiente containerizado completo
- [x] **Scripts Cross-Platform**: Suporte Windows, Linux e macOS
- [x] **Health Checks**: Monitoramento de status da aplicação
- [x] **Logging**: Sistema de logs estruturado

## 🛡️ **Qualidade de Código**

### **🧪 Testes**
```bash
./gradlew test              # Executa todos os testes
./gradlew jacocoTestReport  # Relatório de cobertura
./gradlew check             # Análise estática
```

### **📊 Métricas**
- ✅ **Cobertura de Testes**: >80% de cobertura
- ✅ **SonarQube**: Análise contínua de qualidade
- ✅ **Gradle Check**: Validações automáticas

### **🔧 DevOps**
```bash
# Build e empacotamento
./gradlew build                    # Build completo
./gradlew bootJar                  # Gera JAR executável
docker build -t pokedex-bff .     # Build da imagem Docker
docker run -p 8080:8080 pokedex-bff  # Run container
```

## 🤖 **AI-Assisted Development**

Este projeto oferece **guidelines específicas para desenvolvimento assistido por IA** mantendo **padrões MVC estruturados**:

### **📋 Templates para IA**
- [**Prompt Template**](docs/ai/PROMPT_TEMPLATE.md) - Template completo para solicitações
- [**Development Guide**](docs/ai/DEVELOPMENT_GUIDE.md) - Guias para usar IA corretamente

### **🎯 Princípios para IA**
1. **Service-First**: Sempre começar pelos services de negócio
2. **SOLID Principles**: Manter responsabilidades claras
3. **Thin Controllers**: Controllers apenas para coordenação
4. **Specific Services**: Evitar services genéricos
5. **Test-Driven**: Incluir testes unitários sempre

### **⚠️ Cuidados com IA**
- ❌ Não permitir controllers gordos com lógica de negócio
- ❌ Não aceitar services genéricos demais
- ❌ Não criar repositories que fazem mais que acesso a dados
- ✅ Sempre revisar código gerado seguindo [Style Guide](docs/development/STYLE_GUIDE.md)

## 📊 **Status do Projeto**

### **✅ MVC Estruturado Implementado**
- [x] **Controller Layer**: REST endpoints thin e focados
- [x] **Service Layer**: Lógica de negócio específica e testável
- [x] **Repository Layer**: Acesso a dados simples e direto
- [x] **Entity Layer**: Modelos ricos com comportamentos

### **🚧 Em Desenvolvimento**
- [ ] **GraphQL API**: Endpoint GraphQL adicional
- [ ] **Cache Layer**: Redis para otimização
- [ ] **Event Sourcing**: Sistema de eventos
- [ ] **API Rate Limiting**: Controle de taxa de requisições

### **🎯 Roadmap**
- [ ] **Microservices**: Decomposição em serviços menores
- [ ] **Kubernetes**: Deploy em cluster K8s
- [ ] **Observability**: Metrics, tracing e monitoring
- [ ] **Security**: OAuth2 e JWT authentication

## ⚡ **Performance**

### **📈 Otimizações Implementadas**
- ✅ **Connection Pooling**: Pool otimizado de conexões DB
- ✅ **Lazy Loading**: Carregamento otimizado de entidades
- ✅ **Query Optimization**: Consultas SQL otimizadas
- ✅ **Pagination**: Evita carregamento desnecessário

### **📊 Métricas**
- **Response Time**: <100ms para consultas simples
- **Throughput**: >1000 req/s em ambiente de teste
- **Memory Usage**: <512MB heap em produção

## 🔄 **CI/CD & GitHub Actions**

### **🚀 Workflows Automatizados**
- ✅ **Feature CI**: Executa apenas com Pull Request aberto
- ✅ **Main CI/CD**: Deploy automático para produção
- ✅ **SonarQube**: Análise de qualidade semanal/manual
- ✅ **Conventional Commits**: Validação obrigatória de naming

### **📊 Otimizado para 300 min/mês**
```bash
# Estimativa mensal (GitHub Actions):
Feature PRs: ~20 × 10 min = 200 min
Main pushes: ~8 × 18 min = 144 min  
SonarQube: ~4 × 15 min = 60 min
TOTAL: ~280 min ✅ (dentro do limite)
```

### **🎯 Branch Naming Convention**
```bash
# ✅ Aceitos (conventional commits):
feat/add-pokemon-search        # Nova funcionalidade
fix/authentication-bug         # Correção de bug
docs/update-readme            # Documentação
refactor/clean-architecture   # Refatoração
test/add-integration-tests    # Testes
ci/optimize-workflows         # CI/CD
chore/update-dependencies     # Manutenção

# ❌ Rejeitados:
pokemon-search               # Sem prefixo
bug-fix                     # Formato incorreto
random-branch-name          # Não segue padrão
```

### **📖 Documentação CI/CD**
- **[Guia Completo](docs/ci/README.md)**: Documentação detalhada dos workflows
- **[Guia de Migração](docs/ci/MIGRATION.md)**: Processo de migração dos workflows
- **[Validação](docs/ci/validate-workflows.sh)**: Script para testar workflows localmente

### **🔧 Validação Local**
```bash
# Validar workflows antes do commit
./docs/ci/validate-workflows.sh
```

## 🤝 **Contribuindo**

### **📋 Pré-requisitos**
- Java 17+
- Docker & Docker Compose
- Make (Linux/macOS) ou scripts Windows

### **🔄 Workflow**
1. Fork o repositório
2. Crie feature branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Add: nova funcionalidade'`)
4. Push para branch (`git push origin feature/nova-funcionalidade`)
5. Crie Pull Request

### **📝 Padrões**
- Seguir [Style Guide](docs/development/STYLE_GUIDE.md)
- Incluir testes para novas funcionalidades
- Manter cobertura >80%
- Validar com `./gradlew check`

## 📄 **Licença**

Este projeto está licenciado sob a **Apache License 2.0** - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 📞 **Suporte**

### **📚 Documentação**
- [**Visão Geral Completa**](docs/OVERVIEW.md)
- [**Getting Started**](docs/GETTING_STARTED.md)
- [**Troubleshooting**](docs/TROUBLESHOOTING.md)

### **🐛 Issues**
Encontrou um bug? [Abra uma issue](https://github.com/lucabelezal/pokedex-bff/issues)

### **💬 Discussões**
Dúvidas? [Inicie uma discussão](https://github.com/lucabelezal/pokedex-bff/discussions)

---

<p align="center">
  <img src="https://img.shields.io/badge/Made%20with-Kotlin-blueviolet" />
  <img src="https://img.shields.io/badge/Powered%20by-Spring%20Boot-brightgreen" />
  <img src="https://img.shields.io/badge/Database-PostgreSQL-blue" />
  <img src="https://img.shields.io/badge/Containerized-Docker-blue" />
</p>

<p align="center">
  <strong>Pokédx BFF - Connecting trainers to their Pokémon data! 🚀</strong>
</p>

---

> **Nota:** Os arquivos JSON em `data/json/` devem ser nomeados com prefixos numéricos (ex: `01_region.json`, `02_type.json`, etc.) para garantir a ordem correta de importação e evitar problemas de integridade relacional. O script de importação respeita essa ordem automaticamente. Certifique-se de que os dados estejam consistentes e que todas as referências de chave estrangeira existam nos arquivos anteriores.