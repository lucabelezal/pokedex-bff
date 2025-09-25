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

### 🏛️ **Princípios MVC Estruturado**

1. **Separation of Concerns**: `Controller → Service → Repository → Entity`
2. **Single Responsibility**: Cada service tem responsabilidade única
3. **Dependency Inversion**: Services dependem de interfaces de repository
4. **Thin Controllers**: Controllers apenas coordenam, não contêm lógica
5. **Rich Entities**: Entities com comportamentos e validações
6. **SOLID Principles**: Aplicação consistente dos princípios SOLID

### 🎯 **Fluxo de Dependências MVC**

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   CONTROLLER    │───▶│     SERVICE      │───▶│   REPOSITORY    │
│                 │    │                  │    │                 │
│ PokemonController│    │ PokemonService   │    │PokemonRepository│
│      ↓          │    │       ↓          │    │ (interface)     │
│ thin, apenas    │    │ business logic   │    │ data access     │
│ coordenação     │    │ validações       │    │ simples         │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                ↓                       ↑
                       ┌──────────────────┐              │
                       │     ENTITY       │              │
                       │                  │              │
                       │ Pokemon.kt       │              │
                       │ (rich entity)    │              │
                       │ comportamentos   │              │
                       └──────────────────┘              │
                                ↑                       │
                       ┌──────────────────┐              │
                       │ INFRASTRUCTURE   │──────────────┘
                       │                  │
                       │ JpaPokemon...    │
                       │ RepositoryImpl   │
                       │ (implementação)  │
                       └──────────────────┘
```

### ✅ **Testabilidade Simplificada**

#### **Testes de Service (Principais)**
```kotlin
@ExtendWith(MockitoExtension::class)
class PokemonServiceTest {
    @Mock
    private lateinit var pokemonRepository: PokemonRepository
    
    @InjectMocks
    private lateinit var pokemonService: PokemonService
    
    @Test
    fun `should return pokemon when found by id`() {
        // Given
        val pokemon = Pokemon(1L, "Pikachu", "025", 40, 60)
        `when`(pokemonRepository.findById(1L)).thenReturn(pokemon)
        
        // When
        val result = pokemonService.findById(1L)
        
        // Then
        assertThat(result.name).isEqualTo("Pikachu")
        verify(pokemonRepository).findById(1L)
    }
}
```

#### **Testes de Controller (Integração)**
```kotlin
@WebMvcTest(PokemonController::class)
class PokemonControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockBean
    private lateinit var pokemonService: PokemonService
    
    @Test
    fun `should return pokemon when valid id`() {
        // Given
        val response = PokemonResponse(1L, "Pikachu", "025")
        `when`(pokemonService.findById(1L)).thenReturn(response)
        
        // When & Then
        mockMvc.perform(get("/api/v1/pokemons/1"))
            .andExpected(status().isOk)
            .andExpected(jsonPath("$.name").value("Pikachu"))
    }
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

### 🔧 Comandos de Desenvolvimento

```bash
# Compilação e verificação
./gradlew compileKotlin      # Verifica estrutura MVC
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

# Testes da arquitetura MVC
./gradlew test --tests "*Service*"           # Testes de Services
./gradlew test --tests "*Controller*"        # Testes de Controllers
./gradlew test --tests "*Repository*"        # Testes de Repositories
```

### 🌐 Compatibilidade Multiplataforma

O projeto é **totalmente compatível** com:
- **Linux**: Debian, Ubuntu (testado)
- **macOS**: Intel e Apple Silicon (testado)  
- **Windows**: WSL2, Git Bash, PowerShell (suporte via instruções automáticas)

**Dependências verificadas automaticamente:**
- Python 3.7+, Docker 20.0+, Docker Compose 2.0+, Make 3.8+, psycopg2 2.8+

### 📊 Status da Validação

- ✅ **MVC Estruturado**: Arquitetura simplificada com 3 camadas principais
- ✅ **SOLID Principles**: Aplicação consistente dos princípios SOLID
- ✅ **Separation of Concerns**: Controllers thin, Services específicos, Repositories simples
- ✅ **Rich Entities**: Modelos com comportamentos e validações integradas
- ✅ **Testabilidade**: Testes diretos sem complexidade excessiva
- ✅ **Dependency Inversion**: Services dependem de interfaces de repository
- ✅ **Compilação**: Zero erros após simplificação arquitetural
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

## 🎯 Benefícios da Simplificação Arquitetural

### 🏗️ **Arquiteturais (MVC Estruturado)**
- ✅ **Simplicidade**: 3 camadas claras e objetivas
- ✅ **Manutenibilidade**: Código direto sem abstrações desnecessárias
- ✅ **Compreensibilidade**: Qualquer desenvolvedor entende rapidamente
- ✅ **SOLID Principles**: Aplicação consistente sem complexidade excessiva
- ✅ **Rich Entities**: Modelos com comportamentos integrados
- ✅ **Dependency Inversion**: Services dependem de interfaces de repository
- ✅ **Flexibilidade**: Mudanças rápidas sem overhead arquitetural

### 🧹 **Organizacionais e Técnicas**
- ✅ **Controllers Thin**: Apenas coordenação e validação básica
- ✅ **Services Específicos**: Business logic concentrada e clara
- ✅ **Repositories Simples**: Acesso a dados sem complexity overhead
- ✅ **Estrutura Clara**: Nomenclatura e organização diretas
- ✅ **Testes Práticos**: Unit e Integration tests sem mock excessivo
- ✅ **Desenvolvimento Ágil**: Ciclo de feedback rápido

### 📈 **Métricas de Melhoria**

| Aspecto | Hexagonal (Complexo) | MVC (Simplificado) |
|---------|---------------------|-------------------|
| **Curva de Aprendizado** | Alta (abstrações) | Baixa (direta) |
| **Velocidade de Dev** | Lenta (muitas camadas) | Rápida (3 camadas) |
| **Manutenibilidade** | Difícil (abstrações) | Fácil (código direto) |
| **Testabilidade** | Complex (mock excessivo) | Prática (testes diretos) |
| **Simplicidade** | Baixa | Alta |

---

## � **CI/CD & GitHub Actions** (Janeiro 2025)

### 🎯 **Refatoração Completa dos Workflows**

Os workflows GitHub Actions foram **completamente refatorados** seguindo as especificações de:
- ✅ **Conventional Commits** obrigatório
- ✅ **CI apenas com PR aberto** para features  
- ✅ **Otimização de custos** (300 min/mês)
- ✅ **Reaproveitamento de código** com workflow compartilhado

### 🏗️ **Nova Estrutura dos Workflows**

```
.github/workflows/
├── shared-ci.yml          # ⚡ Workflow reutilizável (163 linhas)
├── 1-feature.yml          # 🔧 CI para PRs + naming validation (50 linhas)
├── 2-main.yml             # 🚀 CI/CD para main com deploy (40 linhas)
└── 3-sonar.yml            # 🔍 SonarQube otimizado (85 linhas)

doc/ci/
├── README.md              # 📖 Documentação completa dos workflows
├── MIGRATION.md           # 📋 Guia de migração detalhado
└── validate-workflows.sh  # 🔧 Script de validação executável
```

### ✅ **Conformidade com Restrições**

#### 1. **Numeração Mantida**: `1-feature.yml`, `2-main.yml`, `3-sonar.yml`

#### 2. **1-feature.yml - CI apenas com PR aberto**
```yaml
# ✅ Trigger: pull_request apenas para main
# ✅ Validação: Branch deve seguir conventional commits
# ✅ Tipos aceitos: feat/, fix/, doc/, refactor/, test/, ci/, chore/
```

#### 3. **2-main.yml - CI/CD apenas para main**
```yaml
# ✅ Trigger: push apenas para branches: [main]
# ✅ Deploy automático com criação de tags
```

### 📊 **Otimizações Implementadas**

#### ⚡ **Economia de Recursos (300 min/mês)**
```
📊 ESTIMATIVA MENSAL:
- Feature PRs: 20 × 10 min = 200 min
- Main pushes: 8 × 18 min = 144 min
- SonarQube: 4 × 15 min = 60 min
TOTAL: 280 min ✅ (dentro do limite)
```

#### 🔧 **Performance**
- **shared-ci.yml**: Workflow reutilizável com 3 jobs modulares
- **Cache inteligente**: Gradle + SonarQube + build artifacts
- **Paralelização**: `--parallel --daemon --no-scan`
- **33% mais rápido** em features (15→10 min)

### 🎯 **Branch Naming Convention**
```bash
# ✅ Aceitos (conventional commits):
feat/add-pokemon-search        # Nova funcionalidade
fix/authentication-bug         # Correção de bug
doc/update-readme            # Documentação
refactor/clean-architecture   # Refatoração
test/add-integration-tests    # Testes
ci/optimize-workflows         # CI/CD
chore/update-dependencies     # Manutenção

# ❌ Rejeitados:
pokemon-search               # Sem prefixo conventional
bug-fix                     # Formato incorreto
random-branch-name          # Não segue padrão
```

### 🔒 **Configuração de Secrets**
```yaml
# GitHub Repository Settings → Secrets:
CODECOV_TOKEN=xxx           # Token do Codecov
SONAR_TOKEN=xxx            # Token do SonarCloud
SONAR_PROJECT_KEY=pokedex-bff        # Chave do projeto
SONAR_ORGANIZATION=lucabelezal       # Organização
```

### 📖 **Documentação Completa**
- **[Guia Completo CI/CD](ci/README.md)**: Documentação detalhada dos workflows
- **[Guia de Migração](ci/MIGRATION.md)**: Processo de migração dos workflows antigos
- **[Script de Validação](ci/validate-workflows.sh)**: Ferramenta para testar workflows localmente

### 🔧 **Validação Local**
```bash
# Validar workflows antes do commit
./doc/ci/validate-workflows.sh

# Resultado esperado:
✅ Todos os workflows validados com sucesso!
✅ Estimativa dentro do limite de 300 min/mês
```

### 🎯 **Principais Benefícios**
- ✅ **Conformidade**: Conventional commits obrigatório
- ✅ **Economia**: 33% menos tempo + 80% menos SonarQube
- ✅ **Qualidade**: Cache inteligente + paralelização
- ✅ **Manutenibilidade**: Código reutilizável + documentação completa
- ✅ **Flexibilidade**: SonarQube manual/semanal/crítico

---

## �🚀 Próximos Passos

### 🎯 **Extensão da Arquitetura**
1. **More Use Cases**: Aplicar padrão para Species, Evolution, Search
2. **Domain Services**: Implementar serviços de domínio para lógicas complexas  
3. **Specifications**: Adicionar especificações para consultas avançadas
4. **More Value Objects**: `PokemonType`, `PokemonStats`, `Height`, `Weight`

### 🧪 **Testes Simplificados**
1. **Unit Tests**: Testes diretos de Services sem mock excessivo
2. **Integration Tests**: Testes completos Controller → Repository
3. **Repository Tests**: Validação de queries e persistência
4. **API Tests**: Testes de endpoints com MockMvc

### 📚 **Documentação Técnica**
1. **MVC Guide**: Guia prático de implementação MVC
2. **API Documentation**: Swagger com exemplos da estrutura simplificada
3. **Service Patterns**: Padrões para Services de domínio
4. **Testing Strategy**: Estratégias de teste pragmáticas

---

## 🚀 Próximos Passos

### 🔄 **Evolução Arquitetural**
1. **Code Refactoring**: Implementar MVC estruturado no código atual
2. **Service Layer**: Consolidar business logic em Services específicos
3. **Repository Pattern**: Simplificar acesso a dados com interfaces claras
4. **Rich Entities**: Adicionar comportamentos aos modelos Pokemon

### 📚 **Documentação de Suporte**
1. **Implementation Guide**: Guia para implementar MVC estruturado
2. **Service Patterns**: Padrões para Services de domínio
3. **Testing Strategy**: Estratégias de teste simplificadas
4. **Code Examples**: Exemplos práticos de cada camada MVC

---

> 💡 **Nota**: A simplificação arquitetural estabelece uma **base prática e eficiente** para desenvolvimento futuro, seguindo princípios MVC com SOLID aplicado de forma pragmática. O código agora prioriza simplicidade, velocidade de desenvolvimento e manutenibilidade. A refatoração dos workflows CI/CD garante desenvolvimento colaborativo eficiente com conventional commits e otimização de recursos.

---

*Documento atualizado após simplificação arquitetural de Clean Architecture + Hexagonal para MVC Estruturado e refatoração completa do CI/CD - Janeiro 2025*

