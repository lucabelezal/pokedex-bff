# CLEAN ARCHITECTURE

## 🎯 **Visão Geral**

Este documento detalha a **implementação de Clean Architecture** no projeto Pokédex BFF, seguindo os princípios de **Robert C. Martin** combinados com **Hexagonal Architecture** de **Alistair Cockburn**.

## 🏗️ **Princípios Fundamentais**

### **1. Dependency Rule (Regra de Dependência)**

```
Infrastructure → Application → Domain
     ↓              ↓          ↓
   Outer          Middle     Inner
```

**REGRA FUNDAMENTAL**: Código em camadas internas **NUNCA** deve depender de camadas externas.

### **2. Separation of Concerns**

- **Domain**: Regras de negócio fundamentais
- **Application**: Casos de uso específicos da aplicação
- **Infrastructure**: Detalhes técnicos e frameworks
- **Interfaces**: Pontos de entrada (REST, GraphQL, etc.)

## 📁 **Estrutura de Camadas**

### **Domain Layer** (Camada de Domínio)
```
src/main/kotlin/com/pokedex/bff/domain/
├── entities/
│   ├── Pokemon.kt                     # Core business entity
│   └── PokedexEntry.kt               # Aggregate root
├── valueobjects/
│   ├── PokemonId.kt                  # Identifier with validation
│   ├── PokemonNumber.kt              # Business number format
│   ├── PokemonType.kt                # Type enumeration
│   └── PokemonStats.kt               # Stats value object
├── repositories/
│   ├── PokemonRepository.kt          # Domain interface
│   └── PokedexRepository.kt          # Repository contract
├── services/
│   └── PokemonDomainService.kt       # Complex business rules
└── exceptions/
    ├── PokemonNotFoundException.kt    # Domain exceptions
    └── InvalidPokemonDataException.kt # Business rule violations
```

### **Application Layer** (Camada de Aplicação)
```
src/main/kotlin/com/pokedex/bff/application/
├── ports/
│   ├── input/
│   │   ├── PokedexUseCases.kt        # Input port interface
│   │   └── PokemonUseCases.kt        # Use case contracts
│   └── output/
│       ├── PokemonRepository.kt      # Output port (same as domain)
│       └── ExternalApiPort.kt        # External service interface
├── usecases/
│   ├── GetPaginatedPokemonsUseCase.kt    # Specific use case
│   ├── SearchPokemonByNameUseCase.kt     # Search functionality
│   ├── GetPokemonByIdUseCase.kt          # Retrieve by identifier
│   └── GetPokemonsByTypeUseCase.kt       # Filter by type
├── dto/
│   ├── PokedexListRequest.kt         # Input DTOs
│   ├── PokedexListResponse.kt        # Output DTOs
│   └── PokemonSearchRequest.kt       # Search parameters
└── adapters/
    ├── PokedexUseCasesAdapter.kt     # Implements input ports
    └── PokemonUseCasesAdapter.kt     # Use case orchestration
```

### **Infrastructure Layer** (Camada de Infraestrutura)
```
src/main/kotlin/com/pokedex/bff/infrastructure/
├── adapters/
│   ├── output/
│   │   ├── PokemonRepositoryAdapter.kt       # Repository implementation
│   │   ├── ExternalApiAdapter.kt             # External service client
│   │   └── CacheRepositoryAdapter.kt         # Caching implementation
│   └── input/
│       └── ScheduledTaskAdapter.kt           # Scheduled job adapter
├── persistence/
│   ├── entities/
│   │   ├── PokemonJpaEntity.kt               # JPA entity
│   │   └── PokedexEntryJpaEntity.kt          # Database representation
│   ├── repositories/
│   │   ├── PokemonJpaRepository.kt           # Spring Data repository
│   │   └── PokedexJpaRepository.kt           # JPA operations
│   └── mappers/
│       ├── PokemonMapper.kt                  # Domain ↔ JPA mapping
│       └── PokedexMapper.kt                  # Entity transformation
├── external/
│   ├── pokeapi/
│   │   ├── PokeApiClient.kt                  # External API client
│   │   ├── PokeApiDto.kt                     # API response DTOs
│   │   └── PokeApiMapper.kt                  # External ↔ Domain mapping
│   └── cache/
│       ├── RedisCacheAdapter.kt              # Cache implementation
│       └── CacheConfiguration.kt             # Cache setup
├── configurations/
│   ├── DatabaseConfiguration.kt              # DB config
│   ├── RestClientConfiguration.kt            # HTTP client setup
│   └── SecurityConfiguration.kt              # Security config
└── messaging/
    ├── events/
    │   ├── PokemonUpdatedEvent.kt            # Domain events
    │   └── PokedexSyncEvent.kt               # Sync events
    └── publishers/
        └── EventPublisher.kt                 # Event publishing
```

### **Interfaces Layer** (Camada de Interface)
```
src/main/kotlin/com/pokedex/bff/interfaces/
├── rest/
│   ├── controllers/
│   │   ├── PokedexController.kt              # REST endpoints
│   │   ├── PokemonController.kt              # Pokemon operations
│   │   └── HealthController.kt               # Health checks
│   ├── dto/
│   │   ├── request/
│   │   │   ├── PokemonSearchRequest.kt       # API request DTOs
│   │   │   └── PaginationRequest.kt          # Pagination parameters
│   │   └── response/
│   │       ├── PokemonResponse.kt            # API response DTOs
│   │       ├── PokedexResponse.kt            # List responses
│   │       └── ErrorResponse.kt              # Error handling
│   └── mappers/
│       ├── PokemonRestMapper.kt              # REST ↔ Application mapping
│       └── ErrorMapper.kt                    # Exception mapping
├── graphql/
│   ├── resolvers/
│   │   ├── PokemonResolver.kt                # GraphQL resolvers
│   │   └── PokedexResolver.kt                # Query resolvers
│   └── types/
│       ├── PokemonType.kt                    # GraphQL types
│       └── PokedexType.kt                    # Schema definitions
└── grpc/
    ├── services/
    │   └── PokemonGrpcService.kt             # gRPC service implementation
    └── mappers/
        └── PokemonGrpcMapper.kt              # gRPC ↔ Application mapping
```

## 🔄 **Fluxo de Dados**

### **Request Flow** (Fluxo de Requisição)
```
1. REST Controller (Interface)
   ↓
2. Input Port (Application)
   ↓
3. Use Case (Application)
   ↓
4. Domain Service (Domain)
   ↓
5. Output Port/Repository (Application)
   ↓
6. Repository Adapter (Infrastructure)
   ↓
7. Database/External API
```

### **Exemplo Prático**
```kotlin
// 1. Controller recebe requisição
@RestController
class PokedexController(
    private val pokedexUseCases: PokedexUseCases  // ← Input Port
) {
    @GetMapping("/pokemons")
    fun getPokemons(request: PaginationRequest): PokedexResponse {
        // 2. Converte para Application DTO
        val appRequest = request.toApplicationDto()
        
        // 3. Chama Use Case através do Input Port
        val result = pokedexUseCases.getPaginatedPokemons(
            appRequest.page, 
            appRequest.size
        )
        
        // 4. Converte response para REST DTO
        return result.toRestResponse()
    }
}

// 5. Use Case executa lógica de aplicação
@Component
class GetPaginatedPokemonsUseCase(
    private val pokemonRepository: PokemonRepository  // ← Output Port
) {
    fun execute(page: Int, size: Int): PokedexListResponse {
        // Validações de aplicação
        validatePaginationParameters(page, size)
        
        // 6. Chama repository através do Output Port
        val pageable = PageRequest.of(page, size)
        val pokemons = pokemonRepository.findAll(pageable)
        
        // Transforma em DTO de aplicação
        return formatToResponse(pokemons)
    }
}

// 7. Repository Adapter implementa Output Port
@Component
class PokemonRepositoryAdapter(
    private val jpaRepository: PokemonJpaRepository  // ← Infrastructure
) : PokemonRepository {
    override fun findAll(pageable: Pageable): Page<Pokemon> {
        // 8. Chama JPA repository
        val jpaEntities = jpaRepository.findAll(pageable)
        
        // 9. Converte JPA entities para Domain entities
        return jpaEntities.map { it.toDomain() }
    }
}
```

## 🧩 **Ports & Adapters (Hexagonal Architecture)**

### **Input Ports** (Portas de Entrada)
```kotlin
// Define contratos de entrada na aplicação
interface PokedexUseCases {
    fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse
    fun searchPokemonsByName(query: String): List<PokemonResponse>
    fun getPokemonById(id: PokemonId): PokemonResponse
}

interface PokemonUseCases {
    fun findByType(type: PokemonType): List<Pokemon>
    fun findByGeneration(generation: Int): List<Pokemon>
}
```

### **Output Ports** (Portas de Saída)
```kotlin
// Define contratos para dependências externas
interface PokemonRepository {
    fun findById(id: PokemonId): Pokemon?
    fun findAll(pageable: Pageable): Page<Pokemon>
    fun findByType(type: PokemonType): List<Pokemon>
    fun save(pokemon: Pokemon): Pokemon
}

interface ExternalApiPort {
    fun fetchPokemonData(pokemonId: PokemonId): ExternalPokemonData?
    fun fetchAllPokemons(): List<ExternalPokemonData>
}
```

### **Input Adapters** (Adaptadores de Entrada)
```kotlin
// Implementam Input Ports e orquestram Use Cases
@Component
class PokedexUseCasesAdapter(
    private val getPaginatedUseCase: GetPaginatedPokemonsUseCase,
    private val searchByNameUseCase: SearchPokemonByNameUseCase,
    private val getByIdUseCase: GetPokemonByIdUseCase
) : PokedexUseCases {
    
    override fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse {
        return getPaginatedUseCase.execute(page, size)
    }
    
    override fun searchPokemonsByName(query: String): List<PokemonResponse> {
        return searchByNameUseCase.execute(query)
    }
    
    override fun getPokemonById(id: PokemonId): PokemonResponse {
        return getByIdUseCase.execute(id)
    }
}
```

### **Output Adapters** (Adaptadores de Saída)
```kotlin
// Implementam Output Ports com detalhes técnicos
@Component
class PokemonRepositoryAdapter(
    private val jpaRepository: PokemonJpaRepository,
    private val pokemonMapper: PokemonMapper
) : PokemonRepository {
    
    override fun findById(id: PokemonId): Pokemon? {
        return jpaRepository.findById(id.value)
            ?.let { pokemonMapper.toDomain(it) }
    }
    
    override fun findAll(pageable: Pageable): Page<Pokemon> {
        return jpaRepository.findAll(pageable)
            .map { pokemonMapper.toDomain(it) }
    }
    
    override fun save(pokemon: Pokemon): Pokemon {
        val jpaEntity = pokemonMapper.toJpaEntity(pokemon)
        val savedEntity = jpaRepository.save(jpaEntity)
        return pokemonMapper.toDomain(savedEntity)
    }
}
```

## 💎 **Value Objects**

### **Implementação Robusta**
```kotlin
@JvmInline
value class PokemonId(val value: Long) {
    init {
        require(value > 0) { "Pokemon ID must be positive" }
        require(value <= MAX_POKEMON_ID) { "Pokemon ID cannot exceed $MAX_POKEMON_ID" }
    }
    
    fun isGeneration1(): Boolean = value in 1L..151L
    fun isGeneration2(): Boolean = value in 152L..251L
    
    companion object {
        const val MAX_POKEMON_ID = 1010L
        
        fun fromString(value: String): PokemonId {
            val longValue = value.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid Pokemon ID format: $value")
            return PokemonId(longValue)
        }
    }
}

@JvmInline
value class PokemonNumber(val value: String) {
    init {
        require(value.matches(Regex("^\\d{1,4}$"))) { 
            "Pokemon number must be 1-4 digits: $value" 
        }
    }
    
    fun formatForDisplay(): String = value.padStart(3, '0')
    fun toInt(): Int = value.toInt()
    
    companion object {
        fun fromInt(number: Int): PokemonNumber {
            require(number > 0) { "Pokemon number must be positive" }
            return PokemonNumber(number.toString())
        }
    }
}
```

### **Value Objects Complexos**
```kotlin
data class PokemonStats(
    val hp: StatValue,
    val attack: StatValue,
    val defense: StatValue,
    val specialAttack: StatValue,
    val specialDefense: StatValue,
    val speed: StatValue
) {
    val total: Int = hp.value + attack.value + defense.value + 
                    specialAttack.value + specialDefense.value + speed.value
    
    fun isValid(): Boolean = total in 180..780  // Pokemon stat ranges
    
    companion object {
        fun create(
            hp: Int, attack: Int, defense: Int, 
            specialAttack: Int, specialDefense: Int, speed: Int
        ): PokemonStats {
            return PokemonStats(
                hp = StatValue(hp),
                attack = StatValue(attack),
                defense = StatValue(defense),
                specialAttack = StatValue(specialAttack),
                specialDefense = StatValue(specialDefense),
                speed = StatValue(speed)
            )
        }
    }
}

@JvmInline
value class StatValue(val value: Int) {
    init {
        require(value in 1..255) { "Stat value must be between 1 and 255: $value" }
    }
}
```

## 🔄 **Domain Events**

### **Event-Driven Architecture**
```kotlin
// Domain Event
interface DomainEvent {
    val occurredOn: Instant
    val aggregateId: String
    val eventType: String
}

data class PokemonUpdatedEvent(
    val pokemonId: PokemonId,
    val changes: Map<String, Any>,
    override val occurredOn: Instant = Instant.now(),
    override val aggregateId: String = pokemonId.value.toString(),
    override val eventType: String = "PokemonUpdated"
) : DomainEvent

// Domain Service with Events
@DomainService
class PokemonDomainService(
    private val eventPublisher: DomainEventPublisher
) {
    fun updatePokemonStats(pokemon: Pokemon, newStats: PokemonStats): Pokemon {
        val updatedPokemon = pokemon.copy(stats = newStats)
        
        // Publish domain event
        val event = PokemonUpdatedEvent(
            pokemonId = pokemon.id,
            changes = mapOf("stats" to newStats)
        )
        eventPublisher.publish(event)
        
        return updatedPokemon
    }
}
```

## 🧪 **Testing Strategy**

### **Unit Tests por Camada**

#### **Domain Tests**
```kotlin
class PokemonTest {
    @Test
    fun `should create valid pokemon with required fields`() {
        // Given
        val id = PokemonId(25)
        val number = PokemonNumber("25")
        val name = "Pikachu"
        
        // When
        val pokemon = Pokemon(id, number, name)
        
        // Then
        assertThat(pokemon.isValid()).isTrue()
        assertThat(pokemon.displayName()).isEqualTo("025 - Pikachu")
    }
}

class PokemonIdTest {
    @Test
    fun `should validate pokemon id range`() {
        assertThrows<IllegalArgumentException> {
            PokemonId(0)
        }
        
        assertThrows<IllegalArgumentException> {
            PokemonId(1011)
        }
    }
}
```

#### **Application Tests**
```kotlin
class GetPaginatedPokemonsUseCaseTest {
    @Mock
    private lateinit var pokemonRepository: PokemonRepository
    
    private lateinit var useCase: GetPaginatedPokemonsUseCase
    
    @BeforeEach
    fun setup() {
        useCase = GetPaginatedPokemonsUseCase(pokemonRepository)
    }
    
    @Test
    fun `should return paginated pokemons when valid parameters`() {
        // Given
        val mockPokemons = listOf(createMockPokemon())
        val mockPage = PageImpl(mockPokemons)
        every { pokemonRepository.findAll(any<Pageable>()) } returns mockPage
        
        // When
        val result = useCase.execute(0, 10)
        
        // Then
        assertThat(result.pokemons).hasSize(1)
        assertThat(result.totalElements).isEqualTo(1)
        verify { pokemonRepository.findAll(any<Pageable>()) }
    }
    
    @Test
    fun `should throw exception when invalid page size`() {
        assertThrows<IllegalArgumentException> {
            useCase.execute(0, 0)
        }
    }
}
```

#### **Integration Tests**
```kotlin
@SpringBootTest
@Testcontainers
class PokemonRepositoryAdapterTest {
    
    @Container
    companion object {
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:15")
            .withDatabaseName("pokedex_test")
            .withUsername("test")
            .withPassword("test")
    }
    
    @Autowired
    private lateinit var pokemonRepositoryAdapter: PokemonRepositoryAdapter
    
    @Test
    fun `should save and retrieve pokemon`() {
        // Given
        val pokemon = createTestPokemon()
        
        // When
        val savedPokemon = pokemonRepositoryAdapter.save(pokemon)
        val foundPokemon = pokemonRepositoryAdapter.findById(pokemon.id)
        
        // Then
        assertThat(foundPokemon).isNotNull
        assertThat(foundPokemon?.name).isEqualTo(pokemon.name)
    }
}
```

## 📊 **Benefícios da Arquitetura**

### **🎯 Testabilidade**
- **Isolamento**: Cada camada pode ser testada independentemente
- **Mocks**: Interfaces facilitam criação de mocks e stubs
- **Fast Tests**: Domain e Application layers não dependem de I/O

### **🔧 Manutenibilidade**
- **Separation of Concerns**: Responsabilidades bem definidas
- **Single Responsibility**: Classes focadas em uma responsabilidade
- **Dependency Inversion**: Fácil substituição de implementações

### **🚀 Flexibilidade**
- **Framework Independence**: Domain não depende de Spring/JPA
- **Database Independence**: Pode trocar PostgreSQL por MongoDB
- **API Independence**: Pode adicionar GraphQL sem afetar lógica

### **📈 Escalabilidade**
- **Horizontal Scaling**: Use Cases podem ser distribuídos
- **Vertical Scaling**: Camadas podem ser otimizadas independentemente
- **Team Scaling**: Times podem trabalhar em camadas diferentes

## ⚠️ **Armadilhas Comuns**

### **❌ Violações Frequentes**

#### **1. Domain Contamination**
```kotlin
// ❌ ERRADO - Domain dependendo de Infrastructure
import javax.persistence.Entity

@Entity  // ❌ Anotação JPA no Domain
data class Pokemon(val id: Long, val name: String)
```

#### **2. Use Case Genérico**
```kotlin
// ❌ ERRADO - Use Case muito genérico
@Service
class PokemonService {
    fun doEverything() { ... }  // ❌ Múltiplas responsabilidades
}
```

#### **3. Controller Fat**
```kotlin
// ❌ ERRADO - Lógica de negócio no Controller
@RestController
class PokemonController {
    fun getPokemons() {
        // ❌ Validações e transformações no controller
        if (page < 0) throw IllegalArgumentException()
        val result = repository.findAll()
        return result.map { transform(it) }
    }
}
```

### **✅ Soluções Corretas**

#### **1. Domain Puro**
```kotlin
// ✅ CORRETO - Domain sem dependências externas
data class Pokemon(
    val id: PokemonId,
    val number: PokemonNumber,
    val name: String
) {
    fun isValid(): Boolean = name.isNotBlank()
    fun displayName(): String = "${number.formatForDisplay()} - $name"
}
```

#### **2. Use Cases Específicos**
```kotlin
// ✅ CORRETO - Use Case focado e específico
@Component
class GetPaginatedPokemonsUseCase(
    private val pokemonRepository: PokemonRepository
) {
    fun execute(page: Int, size: Int): PokedexListResponse {
        validatePaginationParameters(page, size)
        val pageable = PageRequest.of(page, size)
        val pokemons = pokemonRepository.findAll(pageable)
        return formatToResponse(pokemons)
    }
}
```

#### **3. Thin Controllers**
```kotlin
// ✅ CORRETO - Controller apenas coordenando
@RestController
class PokedexController(
    private val pokedexUseCases: PokedexUseCases
) {
    @GetMapping("/pokemons")
    fun getPokemons(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PokedexListResponse> {
        val result = pokedexUseCases.getPaginatedPokemons(page, size)
        return ResponseEntity.ok(result)
    }
}
```

## 🎯 **Conclusão**

A **Clean Architecture** combinada com **Hexagonal Architecture** proporciona:

1. **🔒 Proteção do Domain**: Regras de negócio isoladas e protegidas
2. **🧪 Testabilidade**: Testes rápidos e confiáveis em todas as camadas
3. **🔧 Flexibilidade**: Fácil troca de tecnologias e frameworks
4. **📈 Escalabilidade**: Arquitetura que cresce com o projeto
5. **👥 Colaboração**: Times podem trabalhar independentemente

**Resultado**: Código mais **limpo**, **testável**, **manutenível** e **evoluível**.