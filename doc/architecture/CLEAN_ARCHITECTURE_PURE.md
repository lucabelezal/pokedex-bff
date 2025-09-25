# CLEAN ARCHITECTURE - ARQUITETURA LIMPA PURA

## 🎯 **Visão Geral**

Este documento apresenta a **Clean Architecture pura** seguindo rigorosamente os princípios de **Robert C. Martin**, sem misturar com outros padrões arquiteturais. Uma arquitetura **simples**, **pragmática** e **eficaz**.

## 🧭 **Princípios Fundamentais**

### **1. Dependency Rule (Regra de Dependência)**

```
🌐 Web/UI Layer
    ↓ (depends on)
🚀 Application Layer  
    ↓ (depends on)
💎 Domain Layer
```

**REGRA DE OURO**: Camadas externas **dependem** de camadas internas. **NUNCA** o contrário.

### **2. Camadas da Clean Architecture**

```
┌─────────────────────────────────────────┐
│          🌐 WEB/UI LAYER                │  ← Controllers, DTOs
├─────────────────────────────────────────┤
│        🚀 APPLICATION LAYER             │  ← Use Cases, Interactors
├─────────────────────────────────────────┤
│          💎 DOMAIN LAYER                │  ← Entities, Business Rules
└─────────────────────────────────────────┘
│        🔧 INFRASTRUCTURE                │  ← DB, External APIs
└─────────────────────────────────────────┘
```

## 📁 **Estrutura Simplificada**

### **💎 Domain Layer** (Núcleo de Negócio)
```
domain/
├── entities/
│   ├── Pokemon.kt                    # Entidade central
│   └── Evolution.kt                  # Entidade evolução
├── valueobjects/
│   ├── PokemonId.kt                  # Identificador
│   ├── PokemonNumber.kt              # Número do Pokémon
│   └── PokemonType.kt                # Tipo (Fire, Water, etc.)
├── repositories/
│   └── PokemonRepository.kt          # Interface do repositório
└── exceptions/
    └── PokemonBusinessException.kt   # Exceções de negócio
```

### **🚀 Application Layer** (Casos de Uso)
```
application/
├── usecases/
│   ├── GetPokemonUseCase.kt          # Buscar Pokémon por ID
│   ├── ListPokemonsUseCase.kt        # Listar Pokémons paginado
│   └── SearchPokemonUseCase.kt       # Buscar por nome/tipo
├── dtos/
│   ├── PokemonRequest.kt             # DTOs de entrada
│   └── PokemonResponse.kt            # DTOs de saída
└── interactors/
    └── PokemonInteractor.kt          # Orquestração de Use Cases
```

### **🌐 Web/UI Layer** (Interface com usuário)
```
web/
├── controllers/
│   └── PokemonController.kt          # REST endpoints
├── dtos/
│   ├── PokemonWebRequest.kt          # DTOs específicos da web
│   └── PokemonWebResponse.kt         # Responses para API REST
└── mappers/
    └── PokemonWebMapper.kt           # Conversão Web ↔ Application
```

### **🔧 Infrastructure Layer** (Detalhes técnicos)
```
infrastructure/
├── persistence/
│   ├── entities/
│   │   └── PokemonJpaEntity.kt       # Entidade JPA
│   ├── repositories/
│   │   └── PokemonJpaRepository.kt   # Implementação do repositório
│   └── mappers/
│       └── PokemonDataMapper.kt      # Conversão JPA ↔ Domain
├── external/
│   ├── pokeapi/
│   │   ├── PokeApiClient.kt          # Cliente da API externa
│   │   └── PokeApiMapper.kt          # Conversão API ↔ Domain
│   └── cache/
│       └── RedisCacheAdapter.kt      # Cache implementation
└── config/
    ├── DatabaseConfig.kt             # Configuração do banco
    └── ExternalApiConfig.kt          # Configuração APIs externas
```

## 🔄 **Fluxo de Dados Simplificado**

### **1. Request Flow (Entrada)**
```
1. 🌐 Controller recebe HTTP request
   ↓
2. 🌐 Converte para Application DTO  
   ↓
3. 🚀 Use Case executa lógica de aplicação
   ↓
4. 💎 Entities aplicam regras de negócio
   ↓
5. 🔧 Repository persiste/busca dados
```

### **2. Response Flow (Saída)**
```
1. 🔧 Repository retorna Domain entities
   ↓
2. 💎 Entities com regras aplicadas
   ↓
3. 🚀 Use Case formata resposta
   ↓
4. 🌐 Controller converte para Web DTO
   ↓
5. 🌐 HTTP response enviado
```

## 💎 **Domain Layer Detalhado**

### **Entities (Entidades)**
```kotlin
// Entidade rica em comportamento, sem dependências
data class Pokemon(
    val id: PokemonId,
    val number: PokemonNumber,
    val name: String,
    val types: List<PokemonType>,
    val stats: PokemonStats
) {
    // Regras de negócio puras
    fun isLegendary(): Boolean = number.value > 144 && number.value <= 151
    
    fun canEvolve(): Boolean = evolutions.isNotEmpty()
    
    fun calculateTotalStats(): Int = stats.total()
    
    // Validações de negócio
    fun isValid(): Boolean {
        return name.isNotBlank() && 
               types.isNotEmpty() && 
               stats.isValid()
    }
}
```

### **Value Objects**
```kotlin
@JvmInline
value class PokemonId(val value: Long) {
    init {
        require(value > 0) { "Pokemon ID deve ser positivo" }
    }
}

@JvmInline
value class PokemonNumber(val value: String) {
    init {
        require(value.matches(Regex("^\\d{1,4}$"))) { 
            "Número deve ter 1-4 dígitos" 
        }
    }
    
    fun formatDisplay(): String = value.padStart(3, '0')
}
```

### **Repository Interface**
```kotlin
// Interface definida no Domain, implementada na Infrastructure
interface PokemonRepository {
    fun findById(id: PokemonId): Pokemon?
    fun findAll(page: Int, size: Int): List<Pokemon>
    fun findByName(name: String): List<Pokemon>
    fun findByType(type: PokemonType): List<Pokemon>
    fun save(pokemon: Pokemon): Pokemon
}
```

## 🚀 **Application Layer Detalhado**

### **Use Cases (Casos de Uso)**
```kotlin
// Use Case específico e focado
class GetPokemonUseCase(
    private val pokemonRepository: PokemonRepository // ← Dependency Inversion
) {
    fun execute(pokemonId: Long): PokemonResponse {
        // Validação de entrada
        val id = PokemonId(pokemonId)
        
        // Busca no repositório
        val pokemon = pokemonRepository.findById(id)
            ?: throw PokemonNotFoundException("Pokemon $pokemonId não encontrado")
        
        // Aplicar regras de negócio se necessário
        if (!pokemon.isValid()) {
            throw InvalidPokemonException("Pokemon inválido")
        }
        
        // Converter para DTO de saída
        return PokemonResponse.from(pokemon)
    }
}
```

### **DTOs de Application**
```kotlin
// DTOs simples para entrada e saída
data class PokemonRequest(
    val name: String? = null,
    val type: String? = null,
    val page: Int = 0,
    val size: Int = 10
)

data class PokemonResponse(
    val id: Long,
    val number: String,
    val name: String,
    val types: List<String>,
    val isLegendary: Boolean
) {
    companion object {
        fun from(pokemon: Pokemon): PokemonResponse {
            return PokemonResponse(
                id = pokemon.id.value,
                number = pokemon.number.formatDisplay(),
                name = pokemon.name,
                types = pokemon.types.map { it.name },
                isLegendary = pokemon.isLegendary()
            )
        }
    }
}
```

### **Interactor (Orquestração)**
```kotlin
// Orquestra múltiplos Use Cases se necessário
@Service
class PokemonInteractor(
    private val getPokemonUseCase: GetPokemonUseCase,
    private val listPokemonsUseCase: ListPokemonsUseCase,
    private val searchPokemonUseCase: SearchPokemonUseCase
) {
    fun getPokemon(id: Long): PokemonResponse {
        return getPokemonUseCase.execute(id)
    }
    
    fun searchPokemons(request: PokemonRequest): List<PokemonResponse> {
        return when {
            request.name != null -> searchPokemonUseCase.byName(request.name)
            request.type != null -> searchPokemonUseCase.byType(request.type)
            else -> listPokemonsUseCase.execute(request.page, request.size)
        }
    }
}
```

## 🌐 **Web Layer Detalhado**

### **Controllers**
```kotlin
// Controller fino - apenas coordenação
@RestController
@RequestMapping("/api/v1/pokemons")
class PokemonController(
    private val pokemonInteractor: PokemonInteractor // ← Depends on Application
) {
    @GetMapping("/{id}")
    fun getPokemon(@PathVariable id: Long): ResponseEntity<PokemonWebResponse> {
        val pokemon = pokemonInteractor.getPokemon(id)
        val response = PokemonWebResponse.from(pokemon)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping
    fun searchPokemons(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) type: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<PokemonWebResponse>> {
        val request = PokemonRequest(name, type, page, size)
        val pokemons = pokemonInteractor.searchPokemons(request)
        val response = pokemons.map { PokemonWebResponse.from(it) }
        return ResponseEntity.ok(response)
    }
}
```

## 🔧 **Infrastructure Layer Detalhado**

### **Repository Implementation**
```kotlin
// Implementa interface do Domain
@Repository
class PokemonRepositoryImpl(
    private val jpaRepository: PokemonJpaRepository,
    private val mapper: PokemonDataMapper
) : PokemonRepository {
    
    override fun findById(id: PokemonId): Pokemon? {
        return jpaRepository.findById(id.value)
            ?.let { mapper.toDomain(it) }
    }
    
    override fun findAll(page: Int, size: Int): List<Pokemon> {
        val pageable = PageRequest.of(page, size)
        return jpaRepository.findAll(pageable)
            .map { mapper.toDomain(it) }
    }
    
    override fun save(pokemon: Pokemon): Pokemon {
        val jpaEntity = mapper.toJpaEntity(pokemon)
        val saved = jpaRepository.save(jpaEntity)
        return mapper.toDomain(saved)
    }
}
```

## 🧪 **Testing Strategy Simplificada**

### **Domain Tests**
```kotlin
class PokemonTest {
    @Test
    fun `should validate pokemon business rules`() {
        val pokemon = Pokemon(
            id = PokemonId(25),
            number = PokemonNumber("25"),
            name = "Pikachu",
            types = listOf(PokemonType.ELECTRIC),
            stats = PokemonStats(35, 55, 40, 50, 50, 90)
        )
        
        assertThat(pokemon.isValid()).isTrue()
        assertThat(pokemon.isLegendary()).isFalse()
        assertThat(pokemon.calculateTotalStats()).isEqualTo(320)
    }
}
```

### **Use Case Tests**
```kotlin
class GetPokemonUseCaseTest {
    @Mock
    private lateinit var repository: PokemonRepository
    
    private lateinit var useCase: GetPokemonUseCase
    
    @BeforeEach
    fun setup() {
        useCase = GetPokemonUseCase(repository)
    }
    
    @Test
    fun `should return pokemon when found`() {
        // Given
        val pokemonId = PokemonId(25)
        val pokemon = createMockPokemon(pokemonId)
        every { repository.findById(pokemonId) } returns pokemon
        
        // When
        val result = useCase.execute(25)
        
        // Then
        assertThat(result.id).isEqualTo(25)
        assertThat(result.name).isEqualTo("Pikachu")
        verify { repository.findById(pokemonId) }
    }
}
```

## ⚡ **Benefícios da Arquitetura Simplificada**

### **✅ Simplicidade**
- **Menos conceitos**: Só Clean Architecture, sem misturar padrões
- **Estrutura clara**: Cada camada tem responsabilidade bem definida
- **Fácil entendimento**: Novos devs conseguem entender rapidamente

### **✅ Testabilidade**
- **Isolamento**: Cada camada testável independentemente
- **Mocks simples**: Interfaces claras facilitam mocks
- **Fast tests**: Domain e Application não dependem de I/O

### **✅ Manutenibilidade**
- **Single Responsibility**: Cada classe focada em uma responsabilidade
- **Dependency Inversion**: Fácil substituição de implementações
- **Loose Coupling**: Baixo acoplamento entre camadas

### **✅ Evolução**
- **Framework Independent**: Domain não conhece Spring/JPA
- **Database Independent**: Pode trocar PostgreSQL sem afetar lógica
- **API Independent**: Pode adicionar GraphQL mantendo Use Cases

## 🎯 **Comparação: Antes vs Depois**

### **❌ Antes (Confuso)**
```
Domain + Hexagonal + Ports + Adapters + Clean Architecture
= Muitos conceitos misturados
= Complexidade desnecessária
= Difícil de entender
```

### **✅ Depois (Simples)**
```
Clean Architecture Pura
= Conceitos claros e separados
= Estrutura pragmática
= Fácil de implementar e manter
```

## 🚀 **Implementação Prática**

### **1. Migration Path**
1. **Refatorar estrutura**: Reorganizar pastas seguindo Clean Architecture pura
2. **Remover Ports/Adapters**: Simplificar para interfaces simples
3. **Consolidar Use Cases**: Um Use Case = uma responsabilidade específica
4. **Limpar DTOs**: Separar DTOs por camada (Web, Application)

### **2. Next Steps**
1. **Implementar entidades ricas**: Pokemon com comportamentos
2. **Criar Use Cases específicos**: GetPokemonUseCase, SearchPokemonUseCase
3. **Simplificar Controllers**: Apenas coordenação, sem lógica
4. **Testes por camada**: Domain, Application, Web separadamente

---

## 💡 **Conclusão**

Esta **Clean Architecture pura** é:
- **🎯 Simples**: Sem misturar padrões desnecessários
- **📚 Pragmática**: Focada na realidade do projeto
- **🧪 Testável**: Cada camada testável independentemente
- **🔧 Manutenível**: Fácil de evoluir e modificar
- **👥 Acessível**: Qualquer dev consegue entender

**Resultado**: Arquitetura **limpa**, **simples** e **eficaz** que resolve os problemas reais do projeto sem complexidade desnecessária.

---

## 📦 **Value Objects vs DTOs na Clean Architecture**

### **🤔 Decisão Importante: Quando usar cada um?**

A Clean Architecture frequentemente usa **Value Objects** para conceitos de domínio, mas nem sempre isso é a melhor escolha. Para uma análise completa de **quando usar Value Objects vs DTOs**, consulte:

**📖 [Value Objects vs DTOs - Guia de Decisão](VALUE_OBJECTS_VS_DTOS.md)**

### **🎯 Resumo para Pokédx BFF:**

#### **📦 Value Objects (Clean Architecture)**
```kotlin
// ✅ Quando o domínio é COMPLEXO
value class Money(val amount: Double, val currency: String) {
    fun convertTo(newCurrency: String): Money { /* lógica complexa */ }
    fun applyTax(rate: Double): Money { /* cálculos específicos */ }
    fun formatForDisplay(): String { /* formatação rica */ }
}
```

#### **📄 DTOs (MVC Estruturado)**  
```kotlin
// ✅ Quando o domínio é SIMPLES (como Pokédx)
data class PokemonResponse(
    val number: String,  // "#025" - formatado no DTO
    val name: String,
    val types: List<String>
) {
    companion object {
        fun from(pokemon: Pokemon): PokemonResponse { /* conversão simples */ }
    }
}
```

### **🎯 Recomendação Final:**

Para o **Pokédx BFF**, que é um projeto de **domínio simples** focado em **APIs REST**:

- **❌ Value Objects**: Over-engineering para domínio simples
- **✅ DTOs**: Apropriados para transferência e formatação
- **✅ MVC Estruturado**: Mais alinhado com a realidade do projeto

**📖 Consulte o [guia completo](VALUE_OBJECTS_VS_DTOS.md) para análise detalhada com exemplos práticos.**