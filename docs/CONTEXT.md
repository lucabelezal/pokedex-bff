# CONTEXTO DO PROJETO POKÉDX BFF

**Última atualização:** 23 de setembro de 2025

---

## 🏗️ REFATORAÇÃO ARQUITETURAL - MVC ESTRUTURADO (Setembro 2025)

### 🎯 **Decisão: MVC Estruturado ao invés de Clean Architecture**

O projeto foi **analisado e simplificado** após identificar que a **Clean Architecture com Hexagonal** estava introduzindo **complexidade desnecessária** para um domínio relativamente simples como o Pokédx BFF.

#### ⚠️ **MUDANÇA ARQUITETURAL FUNDAMENTAL**

**❌ Removido**: Clean Architecture + Hexagonal Architecture (complexidade excessiva)  
**✅ Adotado**: **MVC Estruturado** com **Princípios SOLID**

### 📚 **Documentação Atualizada**

- 🆕 [**MVC Architecture**](architecture/ARCHITECTURE_COMPARISON.md) - Comparação Clean vs MVC
- 🆕 [**Style Guide**](development/STYLE_GUIDE.md) - Padrões MVC atualizados  
- 📄 [**Clean Architecture**](architecture/CLEAN_ARCHITECTURE.md) - Mantido para referência histórica

### 🎯 **Nova Estrutura (MVC Estruturado)**:

```
src/main/kotlin/com/pokedex/bff/
├── controller/                     # � REST Controllers (thin)
│   ├── PokemonController.kt        # Endpoints de Pokemon
│   ├── PokedexController.kt        # Endpoints de Pokedex
│   └── TypeController.kt           # Endpoints de Types
│
├── service/                        # 🎯 Business Logic (específicos)
│   ├── PokemonService.kt           # Lógica de Pokemon
│   ├── PokemonSearchService.kt     # Busca especializada
│   ├── PokedexService.kt           # Lógica de Pokedex
│   └── ValidationService.kt        # Validações centralizadas
│
├── repository/                     # 🗄️ Data Access (simples)
│   ├── PokemonRepository.kt        # Interface de Pokemon
│   ├── TypeRepository.kt           # Interface de Types
│   └── SpeciesRepository.kt        # Interface de Species
│
├── entity/                         # 📊 JPA Entities (com comportamentos)
│   ├── Pokemon.kt                  # Entity rica com métodos
│   ├── Type.kt                     # Entity com validações
│   └── Species.kt                  # Entity com comportamentos
│
├── dto/                           # � Data Transfer Objects
│   ├── request/                   # DTOs de entrada
│   └── response/                  # DTOs de saída
│
├── config/                        # ⚙️ Configurations
│   ├── DatabaseConfig.kt          # Configuração DB
│   └── WebConfig.kt               # Configuração Web
│
└── exception/                     # ❌ Exception Handling
    ├── PokemonNotFoundException.kt # Exceções específicas
    └── GlobalExceptionHandler.kt  # Handler global
```
### 🔄 **Refatoração Arquitetural Implementada**

| Aspecto | Clean Architecture (Removida) | MVC Estruturado (Atual) | Benefício |
|---------|--------------------------------|--------------------------|-----------|
| **Complexidade** | 4+ camadas + Ports & Adapters | 3 camadas principais | **Simplicidade** e facilidade de entendimento |
| **Services** | Use Cases específicos complexos | Services focados e específicos | **Pragmatismo** sem over-engineering |
| **Controllers** | Dependem de portas/interfaces | Dependem de services diretos | **Clareza** no fluxo de dados |
| **Entities** | Domain entities puras + JPA entities | Entities JPA ricas com comportamentos | **Consolidação** sem duplicação |
| **Testabilidade** | Testes puros com mocks complexos | Testes diretos com mocks simples | **Produtividade** nos testes |
| **Domínio** | Value Objects elaborados | Entities com validações e comportamentos | **Equilíbrio** entre simplicidade e riqueza |

### 📁 **Implementações MVC Estruturado**

#### ✅ **Controllers Thin (Apenas Coordenação)**
```kotlin
// PokemonController.kt - Coordenação simples
@RestController
@RequestMapping("/api/v1/pokemons")
class PokemonController(
    private val pokemonService: PokemonService
) {
    @GetMapping("/{id}")
    fun getPokemon(@PathVariable id: Long): ResponseEntity<PokemonResponse> {
        val pokemon = pokemonService.findById(id)
        return ResponseEntity.ok(pokemon)
    }
}
```

#### ✅ **Services Específicos (Business Logic)**
```kotlin
// PokemonService.kt - Lógica de negócio focada
@Service
class PokemonService(
    private val pokemonRepository: PokemonRepository,
    private val validationService: ValidationService
) {
    fun findById(id: Long): PokemonResponse {
        validationService.validatePokemonId(id)
        val pokemon = pokemonRepository.findById(id)
            ?: throw PokemonNotFoundException("Pokemon with ID $id not found")
        return PokemonResponse.from(pokemon)
    }
}
```

#### ✅ **Entities Rica (Domain Models)**
```kotlin
// Pokemon.kt - Entity com comportamentos
@Entity
@Table(name = "pokemons")
data class Pokemon(
    @Id val id: Long,
    val name: String,
    val number: String,
    val height: Int,
    val weight: Int
) {
    // Comportamentos de domínio
    fun isLegendary(): Boolean = id in 144..151
    fun formatNumber(): String = number.padStart(3, '0')
    fun getBMI(): Double = weight.toDouble() / (height.toDouble() / 100).pow(2)
    fun isGeneration1(): Boolean = id <= 151
}
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

