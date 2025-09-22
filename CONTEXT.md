# CONTEXTO DO PROJETO POKÉDX BFF

**Última atualização:** 22 de setembro de 2025

---

## 🏗️ REORGANIZAÇÃO ARQUITETURAL (Setembro 2025)

### 🎯 **Clean Architecture Implementada**

O projeto foi completamente refatorado seguindo os princípios do **Clean Architecture**, com separação rigorosa de responsabilidades e dependências apontando sempre para o centro (domínio).

#### ✅ **ESTRUTURA ATUAL (Clean Architecture)**:
```
src/main/kotlin/com/pokedex/bff/
├── application/                     # 🎯 Casos de uso e orquestração
│   ├── dto/                        # DTOs de request/response
│   ├── services/                   # Application Services
│   └── usecase/                    # Use Cases
├── domain/                         # � Regras de negócio puras
│   ├── entities/                   # Entities de domínio (sem anotações)
│   ├── valueobjects/              # Value Objects
│   ├── repository/                # Interfaces de repositório
│   └── exceptions/                # Exceções de domínio
├── infrastructure/                 # 🔧 Detalhes técnicos e frameworks
│   ├── persistence/entities/       # Entities JPA (com anotações)
│   ├── repository/                # Implementações de repositório
│   ├── configurations/            # Configs do Spring Boot
│   ├── config/                    # Configs de use cases
│   └── migration/                 # Migrações de banco
├── interfaces/                    # 🌐 Controladores e DTOs externos
│   ├── controllers/               # REST Controllers
│   └── dto/                      # DTOs da API
└── shared/                       # 🤝 Utilitários compartilhados
    └── exceptions/               # Exceções globais
```

### � **Refatoração Realizada**

| Ação | Antes | Depois | Benefício |
|------|-------|--------|-----------|
| **Unificação** | `interface/` + `interfaces/` | `interfaces/` único | Estrutura consistente |
| **Separação** | JPA entities no domain | `infrastructure/persistence/entities/` | Domain puro |
| **Relocação** | Value Objects em application | `domain/valueobjects/` | Seguir Clean Architecture |
| **Limpeza** | Arquivos `.keep` desnecessários | Removidos | Projeto limpo |
| **Remoção** | Seeders Kotlin não utilizados | Removidos | Foco no essencial |

### 📁 **Estrutura de Dados Organizada**

```
pokedex-bff/
├── data/
│   └── json/              # 📊 Dados fonte JSON numerados (01-10)
├── database/
│   ├── schema/            # 🗄️ DDL - estrutura das tabelas
│   ├── seeds/             # 🌱 DML - dados iniciais gerados  
│   └── migrations/        # 🔄 Scripts de migração futuros
├── tools/
│   └── database/          # 🔧 Scripts Python para banco
└── docker/                # 🐳 Configurações Docker limpas
```

---

## 🔄 Estrutura e Fluxo de Dados

### 🏛️ **Princípios Clean Architecture**

1. **Regra de Dependência**: `Interfaces → Application → Domain ← Infrastructure`
2. **Domain Puro**: Sem dependências externas, apenas regras de negócio
3. **Inversão de Dependência**: Interfaces definidas no domain, implementadas na infrastructure
4. **Separação de Entidades**: 
   - `domain/entities/`: Objetos puros de negócio
   - `infrastructure/persistence/entities/`: Mapeamento JPA

### 📋 Sequência de Dados (Dependências de Chaves Estrangeiras)

| Ordem | Arquivo | Tabela | Dependências |
|-------|---------|--------|--------------|
| 1 | `01_region.json` | `regions` | - |
| 2 | `02_type.json` | `types` | - |
| 3 | `03_egg_group.json` | `egg_groups` | - |
| 4 | `04_generation.json` | `generations` | - |
| 5 | `05_ability.json` | `abilities` | - |
| 6 | `06_species.json` | `species` | regions, generations |
| 7 | `07_stats.json` | `stats` | - |
| 8 | `08_evolution_chains.json` | `evolution_chains` | - |
| 9 | `09_pokemon.json` | `pokemons` + relacionamentos | species, abilities, stats |
| 10 | `10_weaknesses.json` | `pokemon_weaknesses` | pokemons |

### 🎯 Fluxo de Desenvolvimento

1. **Clean Architecture**: Separação rigorosa de camadas com domain independente
2. **Desacoplamento Total**: BFF sem seeder ou carga automática
3. **Inicialização por SQL**: `database/schema/schema.sql` + `database/seeds/init-data.sql`
4. **Geração Automática**: `tools/database/generate_sql_from_json.py` converte JSONs
5. **Validação**: `tools/database/validate_database.py` verifica banco

---

## 💻 Comandos Principais

### 🏗️ **Comandos de Arquitetura**

```bash
# Compilação e verificação
./gradlew compileKotlin      # Verifica estrutura Clean Architecture
./gradlew test              # Executa testes unitários e integração
./gradlew build             # Build completo com validações

# Análise de código  
./gradlew check             # Análise estática e qualidade
```

### 🔧 Comandos de Desenvolvimento

```bash
# Verificação de dependências
make check-deps           # Executa: tools/database/check_dependencies.py

# Gerar SQL a partir dos JSONs
make generate-sql-data      # Executa: tools/database/generate_sql_from_json.py

# Gerenciamento do banco
make db-only-up            # Sobe banco isolado com dados
make db-only-restart       # Reinicia banco com dados atualizados  
make db-only-down          # Para o banco
make db-info              # Informações de conexão

# Validação
make validate-db          # Executa: tools/database/validate_database.py
```

### 🌐 Compatibilidade Multiplataforma

O projeto é **totalmente compatível** com:
- **Linux**: Debian, Ubuntu (testado)
- **macOS**: Intel e Apple Silicon (testado)  
- **Windows**: WSL2, Git Bash, PowerShell (suporte via instruções automáticas)

**Dependências verificadas automaticamente:**
- Python 3.7+, Docker 20.0+, Docker Compose 2.0+, Make 3.8+, psycopg2 2.8+

### 📊 Status da Validação

- ✅ **Clean Architecture**: Estrutura refatorada seguindo princípios rigorosos
- ✅ **Compilação**: Zero erros após refatoração de packages e imports
- ✅ **Estrutura**: 13 tabelas criadas
- ✅ **Dados**: 1223+ registros inseridos (incluindo correções de gender fields)
- ✅ **Integridade**: 0 problemas encontrados
- ✅ **Comandos**: Todos os targets make funcionando

---

## ➕ Processo para Novos Dados

### 🔄 Fluxo Step-by-Step

1. **Editar JSONs**: Modificar arquivos em `data/json/` (manter sequência numérica)
2. **Gerar SQL**: `make generate-sql-data`  
3. **Atualizar Banco**: `make db-only-restart`
4. **Validar**: `make validate-db`

### ⚠️ Regras Importantes

- **Sequência numérica**: Manter ordem dos arquivos (`01` a `10`)
- **Dependências**: Respeitar chaves estrangeiras na ordem
- **Naming**: Nome da tabela = arquivo sem prefixo numérico (ex: `01_region.json` → `regions`)
- **Logs**: Scripts Python mostram progresso detalhado
- **Correções aplicadas**: Gender fields, species fields, abilities generation_id

---

## 🐳 Configurações Docker Atualizadas

### Volume Mounts
```yaml
volumes:
  - ./database/schema/:/docker-entrypoint-initdb.d/01-schema/
  - ./database/seeds/:/docker-entrypoint-initdb.d/02-seeds/
```

### Ambiente Isolado
- **Arquivo**: `docker/docker-compose.db-only.yml`
- **Porta**: `localhost:5434`
- **Logs**: Detalhados para debugging

---

## 📚 Documentação Atualizada

| Arquivo | Propósito |
|---------|-----------|
| `README.md` | Guia completo de setup de desenvolvimento |
| `doc/ARCHITECTURE.md` | **🆕 Documentação completa Clean Architecture** |
| `data/README.md` | Documentação da estrutura de dados |
| `tools/README.md` | Documentação das ferramentas |
| `CONTEXT.md` | Este arquivo - contexto e histórico do projeto |

---

## 🎯 Benefícios da Refatoração

### 🏗️ **Arquiteturais**
- ✅ **Testabilidade**: Domain sem dependências externas
- ✅ **Flexibilidade**: Troca de tecnologias sem afetar domínio
- ✅ **Manutenibilidade**: Responsabilidades claras
- ✅ **Escalabilidade**: Estrutura preparada para crescimento

### 🧹 **Organizacionais**
- ✅ **Projeto Limpo**: Removidos arquivos desnecessários
- ✅ **Estrutura Consistente**: Nomenclatura e organização padronizadas
- ✅ **Separação Clara**: Domain entities vs JPA entities
- ✅ **Imports Corretos**: Todos os packages atualizados automaticamente

---

## 🚀 Próximos Passos

1. **Testes**: Implementar testes unitários para domain entities
2. **Use Cases**: Expandir use cases para operações CRUD completas
3. **Validation**: Adicionar validações de domínio nas entities
4. **Error Handling**: Implementar exceções específicas de domínio
5. **Documentation**: Manter docs alinhadas com evolução

---

> 💡 **Nota**: Esta refatoração estabelece uma base sólida para desenvolvimento futuro, seguindo as melhores práticas de Clean Architecture e facilitando manutenção e testes.

---

*Documento atualizado após refatoração Clean Architecture - 22/09/2025*

