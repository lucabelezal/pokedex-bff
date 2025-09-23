# CONTEXTO DO PROJETO POKÉDX BFF

**Última atualização:** 23 de setembro de 2025

---

## 🏗️ REFATORAÇÃO CLEAN ARCHITECTURE AVANÇADA (Setembro 2025)

### 🎯 **Clean Architecture com Ports & Adapters Implementada**

O projeto foi **completamente refatorado** seguindo rigorosamente os princípios do **Clean Architecture** com implementação de **Ports & Adapters**, separação total de responsabilidades entre domínio e infraestrutura, e alta testabilidade.

#### ✅ **NOVA ESTRUTURA (Clean Architecture + Ports & Adapters)**:
```
src/main/kotlin/com/pokedex/bff/
├── domain/                         # 🎯 DOMÍNIO PURO (Core Business)
│   ├── entities/                   # Entidades de domínio (sem anotações)
│   ├── valueobjects/              # ✅ Value Objects com regras de negócio
│   │   ├── PokemonId.kt           # IDs com validações e geração
│   │   └── PokemonNumber.kt       # Números com formatação/validação
│   ├── repositories/              # Interfaces de repositório (contratos)
│   ├── services/                  # Serviços de domínio (futuro)
│   └── exceptions/                # Exceções de domínio
│
├── application/                    # 🎯 CASOS DE USO (Orchestration)
│   ├── ports/                     # ✅ Portas (Hexagonal Architecture)
│   │   ├── input/                 # Portas de entrada (Use Case contracts)
│   │   │   └── PokedexUseCases.kt # Interface para casos de uso
│   │   └── output/                # Portas de saída (Repository contracts)
│   ├── usecases/                  # ✅ Use Cases específicos
│   │   ├── pokemon/               # Use cases de Pokemon
│   │   │   └── FetchPokemonByIdUseCase.kt
│   │   └── pokedex/               # Use cases de Pokedex
│   │       └── GetPaginatedPokemonsUseCase.kt
│   ├── dto/                       # DTOs de response/request
│   └── mappers/                   # Mapeadores aplicação ↔ domínio
│
├── infrastructure/                 # 🔧 DETALHES TÉCNICOS
│   ├── adapters/                  # ✅ Adaptadores (implementam portas)
│   │   └── PokedexUseCasesAdapter.kt # Implementa PokedexUseCases
│   ├── persistence/
│   │   ├── entities/              # Entidades JPA (com anotações)
│   │   ├── repositories/          # Implementações JPA dos repositórios
│   │   └── mappers/               # Mappers JPA ↔ Domain
│   ├── configurations/            # Configurações Spring Boot
│   └── config/                    # Configurações de beans/use cases
│
├── interfaces/                     # 🌐 INTERFACE DO USUÁRIO
│   ├── controllers/               # ✅ Controllers REST (usa apenas portas)
│   │   └── PokedexController.kt   # Refatorado para usar PokedexUseCases
│   ├── dto/                       # DTOs da API REST
│   └── validators/                # Validadores de entrada
│
└── shared/                        # 🤝 COMPARTILHADO
    ├── exceptions/                # Exceções globais
    ├── utils/                     # Utilitários
    └── constants/                 # Constantes
```

### 🔄 **Refatoração de Separação de Responsabilidades Implementada**

| Aspecto | Antes (22/09) | Depois (23/09) | Benefício |
|---------|---------------|----------------|-----------|
| **Interface/Implementação** | `PokedexService` + `PokedexServiceImpl` no mesmo arquivo | Separados: `PokedexUseCases` → `GetPaginatedPokemonsUseCase` → `PokedexUseCasesAdapter` | Inversão de dependência correta |
| **Use Cases** | Application Services genéricos | Use Cases específicos com responsabilidade única | Single Responsibility Principle |
| **Ports & Adapters** | Dependência direta de repositories | Portas de entrada/saída com adaptadores | Hexagonal Architecture |
| **Value Objects** | Entities anêmicas | Value Objects ricos (`PokemonId`, `PokemonNumber`) | Domain-Driven Design |
| **Testabilidade** | Testes dependem de Spring context | Testes unitários puros com mocks simples | Testabilidade isolada |
| **Domínio** | Misturado com infraestrutura | Completamente puro, sem dependências externas | Domain purity |

### 📁 **Implementações Concretas Criadas**

#### ✅ **Value Objects (Domain Rich)**
```kotlin
// PokemonId.kt - Validações de negócio
@JvmInline
value class PokemonId(val value: Long) {
    fun isGeneration1(): Boolean = value in 1L..151L
    fun getGeneration(): Int = when(value) { /* regras */ }
}

// PokemonNumber.kt - Formatação e validação
@JvmInline 
value class PokemonNumber(val value: String) {
    fun formatForDisplay(): String = value.padStart(3, '0')
    fun toDisplayString(): String = "Nº${formatForDisplay()}"
}
```

#### ✅ **Use Case Específico**
```kotlin
// GetPaginatedPokemonsUseCase.kt
@Component
class GetPaginatedPokemonsUseCase(
    private val pokemonRepository: PokemonRepository // Interface do domínio
) {
    fun execute(page: Int, size: Int): PokedexListResponse {
        validatePaginationParameters(page, size)
        // Lógica de negócio pura
        return formatPokemonList(...)
    }
}
```

#### ✅ **Ports & Adapters**
```kotlin
// PokedexUseCases.kt (Porta de Entrada)
interface PokedexUseCases {
    fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse
}

// PokedexUseCasesAdapter.kt (Adaptador)
@Service
class PokedexUseCasesAdapter(
    private val getPaginatedPokemonsUseCase: GetPaginatedPokemonsUseCase
) : PokedexUseCases
```

#### ✅ **Controller Refatorado**
```kotlin
// PokedexController.kt (usa apenas interfaces)
@RestController
class PokedexController(
    private val pokedexUseCases: PokedexUseCases // ← Interface, não implementação
)
```

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

### 🏛️ **Princípios Clean Architecture + Ports & Adapters**

1. **Regra de Dependência**: `Interfaces → Application → Domain ← Infrastructure`
2. **Domain Puro**: Zero dependências externas, apenas regras de negócio
3. **Ports & Adapters**: Interfaces para entrada/saída, implementadas por adaptadores
4. **Use Cases Específicos**: Cada caso de uso tem responsabilidade única
5. **Value Objects Ricos**: Encapsulam validações e comportamentos de domínio
6. **Inversão Total**: Controllers dependem de interfaces, não implementações

### 🎯 **Fluxo de Dependências (Implementado)**

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   INTERFACES    │───▶│   APPLICATION    │───▶│     DOMAIN      │
│                 │    │                  │    │                 │
│ PokedexController│    │ PokedexUseCases  │    │ PokemonRepository│
│      ↓          │    │       ↓          │    │ (interface)     │
│ usa interface   │    │ GetPaginated...  │    │ Value Objects   │
│ PokedexUseCases │    │    UseCase       │    │ Domain Entities │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         ↑                       ↑                       ↑
         │                       │                       │
┌─────────────────┐              │              ┌─────────────────┐
│ INFRASTRUCTURE  │──────────────┘              │ INFRASTRUCTURE  │
│                 │                             │                 │
│PokedexUseCases  │                             │ JpaPokemon...   │
│   Adapter       │                             │ RepositoryImpl  │
│ (implementação) │                             │ (implementação) │
└─────────────────┘                             └─────────────────┘
```

### ✅ **Testabilidade Implementada**

#### **Testes Unitários de Value Objects**
```kotlin
@Test
fun `should format pokemon number correctly`() {
    val pokemonNumber = PokemonNumber("25")
    assertThat(pokemonNumber.formatForDisplay()).isEqualTo("025")
}
```

#### **Testes Unitários de Use Cases (com Mocks)**
```kotlin
@Test
fun `should return paginated pokemon list when valid parameters`() {
    // Given
    every { pokemonRepository.findAll(any()) } returns mockPage
    
    // When
    val result = useCase.execute(0, 10)
    
    // Then
    assertThat(result.pokemons).hasSize(1)
    verify(exactly = 1) { pokemonRepository.findAll(any()) }
}
```

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

# Testes da nova arquitetura
./gradlew test --tests "*UseCase*"           # Testes de Use Cases
./gradlew test --tests "*ValueObject*"       # Testes de Value Objects  
./gradlew test --tests "*Adapter*"           # Testes de Adaptadores
```

### 🌐 Compatibilidade Multiplataforma

O projeto é **totalmente compatível** com:
- **Linux**: Debian, Ubuntu (testado)
- **macOS**: Intel e Apple Silicon (testado)  
- **Windows**: WSL2, Git Bash, PowerShell (suporte via instruções automáticas)

**Dependências verificadas automaticamente:**
- Python 3.7+, Docker 20.0+, Docker Compose 2.0+, Make 3.8+, psycopg2 2.8+

### 📊 Status da Validação

- ✅ **Clean Architecture Avançada**: Ports & Adapters implementados com separação total
- ✅ **Separação de Responsabilidades**: Interface/implementação completamente separadas
- ✅ **Value Objects**: Domínio rico com `PokemonId` e `PokemonNumber` 
- ✅ **Use Cases Específicos**: `GetPaginatedPokemonsUseCase` com responsabilidade única
- ✅ **Testabilidade**: Testes unitários puros sem dependências de infraestrutura
- ✅ **Inversão de Dependência**: Controllers usam apenas interfaces
- ✅ **Compilação**: Zero erros após refatoração avançada
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

## 🎯 Benefícios da Refatoração Avançada

### 🏗️ **Arquiteturais (Clean Architecture + Ports & Adapters)**
- ✅ **Testabilidade Total**: Use Cases testáveis unitariamente sem infraestrutura
- ✅ **Inversão de Dependência**: Controllers usam interfaces, não implementações
- ✅ **Single Responsibility**: Cada Use Case tem uma responsabilidade específica
- ✅ **Domain-Driven Design**: Value Objects ricos com comportamentos de negócio
- ✅ **Hexagonal Architecture**: Portas/adaptadores para entrada e saída
- ✅ **Baixo Acoplamento**: Camadas comunicam apenas via interfaces
- ✅ **Flexibilidade**: Fácil substituição de implementações

### 🧹 **Organizacionais e Técnicas**
- ✅ **Separação Total**: Interface/implementação em arquivos distintos
- ✅ **Domain Purity**: Zero dependências externas no domínio
- ✅ **Use Cases Específicos**: Lógica de negócio bem encapsulada
- ✅ **Estrutura Consistente**: Nomenclatura e organização padronizadas
- ✅ **Testes Abrangentes**: Cobertura de Value Objects e Use Cases
- ✅ **Manutenibilidade**: Código mais limpo e organizazdo

### 📈 **Métricas de Melhoria**

| Aspecto | Antes | Depois |
|---------|-------|--------|
| **Acoplamento** | Alto (interface+impl juntos) | Baixo (separação total) |
| **Testabilidade** | Difícil (depende de Spring) | Fácil (mocks simples) |
| **Domínio** | Anêmico | Rico (Value Objects) |
| **Responsabilidades** | Misturadas | Separação clara |
| **Inversão de Dependência** | Parcial | Total |

---

## 🚀 Próximos Passos

### 🎯 **Extensão da Arquitetura**
1. **More Use Cases**: Aplicar padrão para Species, Evolution, Search
2. **Domain Services**: Implementar serviços de domínio para lógicas complexas  
3. **Specifications**: Adicionar especificações para consultas avançadas
4. **More Value Objects**: `PokemonType`, `PokemonStats`, `Height`, `Weight`

### 🧪 **Testes Abrangentes**
1. **Integration Tests**: Testes de adaptadores com banco H2
2. **Contract Tests**: Validação de interfaces entre camadas
3. **Architecture Tests**: ArchUnit para validar regras arquiteturais
4. **Performance Tests**: Benchmarks de Use Cases

### 📚 **Documentação Técnica**
1. **ADRs**: Architectural Decision Records das escolhas feitas
2. **API Documentation**: Swagger com exemplos da nova estrutura
3. **Developer Guide**: Guia para adicionar novos Use Cases
4. **Testing Guide**: Estratégias de teste para cada camada

---

> 💡 **Nota**: A refatoração avançada estabelece uma **base sólida e profissional** para desenvolvimento futuro, seguindo rigorosamente os princípios de Clean Architecture, Hexagonal Architecture, e Domain-Driven Design. O código agora é altamente testável, manutenível e evolutivo.

---

*Documento atualizado após refatoração Clean Architecture avançada com Ports & Adapters - 23/09/2025*

