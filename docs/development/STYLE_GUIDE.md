# STYLE GUIDE - CODE REVIEW

## 🎯 **Visão Geral**

Este guia estabelece **padrões de código** e **critérios de code review** para o projeto Pokédex BFF, seguindo **Clean Architecture**, **SOLID principles** e **Domain-Driven Design**.

## 🏗️ **Princípios Arquiteturais**

### **1. Clean Architecture + Hexagonal Architecture**

#### ✅ **OBRIGATÓRIO**
```kotlin
// ✅ Domain Entity (Pura)
data class Pokemon(
    val id: PokemonId,           // Value Object
    val number: PokemonNumber,   // Value Object
    val name: String
) {
    // Apenas lógica de negócio
    fun isValid(): Boolean = name.isNotBlank()
}

// ✅ Use Case específico
@Component
class SearchPokemonByTypeUseCase(
    private val pokemonRepository: PokemonRepository // Interface
) {
    fun execute(type: String): List<Pokemon> {
        require(type.isNotBlank()) { "Type cannot be blank" }
        return pokemonRepository.findByType(type)
    }
}
```

#### ❌ **PROIBIDO**
```kotlin
// ❌ Entity com anotações JPA no domain
@Entity
data class Pokemon(
    @Id val id: Long,  // ❌ Anotação JPA no domain
    val name: String
)

// ❌ Use Case genérico
@Service
class PokemonService {  // ❌ Muito genérico
    fun doEverything() { ... }  // ❌ Múltiplas responsabilidades
}
```

### **2. Separação de Camadas**

#### ✅ **Estrutura Correta**
```
domain/
├── entities/          # Entidades puras
├── valueobjects/     # Value Objects com validações
├── repositories/     # Interfaces de persistência
└── exceptions/       # Exceções de domínio

application/
├── ports/input/      # Contratos de entrada
├── usecases/         # Use Cases específicos
└── dto/              # DTOs de aplicação

infrastructure/
├── adapters/         # Implementam portas
├── persistence/      # JPA entities e repos
└── configurations/   # Configs Spring

interfaces/
├── controllers/      # REST controllers
└── dto/              # DTOs da API
```

## 🔧 **Padrões de Código**

### **1. Nomenclatura**

#### ✅ **Padrões Corretos**
```kotlin
// Value Objects
@JvmInline
value class PokemonId(val value: Long)
value class PokemonNumber(val value: String)

// Use Cases específicos
class FetchPokemonByIdUseCase
class SearchPokemonByTypeUseCase
class GetPaginatedPokemonsUseCase

// Ports (interfaces)
interface PokemonUseCases
interface PokedexUseCases

// Adapters
class PokemonUseCasesAdapter
class PokemonRepositoryAdapter
```

#### ❌ **Nomenclatura Incorreta**
```kotlin
// ❌ Genérico demais
class PokemonService
class DataService
class Helper

// ❌ Não específico
class ProcessUseCase
class HandleRequest
```

### **2. Value Objects**

#### ✅ **Implementação Correta**
```kotlin
@JvmInline
value class PokemonId(val value: Long) {
    init {
        require(value > 0) { "Pokemon ID must be positive" }
        require(value <= MAX_POKEMON_ID) { "ID exceeds maximum" }
    }
    
    fun isGeneration1(): Boolean = value in 1L..151L
    
    companion object {
        const val MAX_POKEMON_ID = 1010L
    }
}
```

#### ❌ **Implementação Incorreta**
```kotlin
// ❌ Sem validações
data class PokemonId(val value: Long)

// ❌ Primitive obsession
fun searchPokemon(id: Long) { ... }  // Deveria usar PokemonId
```

### **3. Use Cases**

#### ✅ **Use Case Bem Definido**
```kotlin
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
    
    private fun validatePaginationParameters(page: Int, size: Int) {
        require(page >= 0) { "Page must be non-negative" }
        require(size > 0) { "Size must be positive" }
        require(size <= 100) { "Size cannot exceed 100" }
    }
    
    private fun formatToResponse(pokemons: Page<Pokemon>): PokedexListResponse {
        // Lógica de formatação específica
    }
}
```

#### ❌ **Use Case Mal Definido**
```kotlin
// ❌ Múltiplas responsabilidades
@Service
class PokemonService {
    fun getPokemons() { ... }
    fun searchPokemons() { ... }
    fun createPokemon() { ... }  // ❌ CRUD genérico
    fun sendEmail() { ... }      // ❌ Responsabilidade não relacionada
}
```

### **4. Controllers**

#### ✅ **Controller Correto**
```kotlin
@RestController
@RequestMapping("/api/v1/pokedex")
class PokedexController(
    private val pokedexUseCases: PokedexUseCases  // ← Interface
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

#### ❌ **Controller Incorreto**
```kotlin
// ❌ Dependência de implementação
@RestController
class PokemonController(
    private val pokemonService: PokemonServiceImpl  // ❌ Implementação
) {
    @GetMapping
    fun getAll() {  // ❌ Sem validação, sem tipagem específica
        return pokemonService.doEverything()  // ❌ Método genérico
    }
}
```

## 🧪 **Padrões de Teste**

### **1. Testes de Value Objects**

#### ✅ **Testes Corretos**
```kotlin
class PokemonNumberTest {
    @Test
    fun `should format number correctly`() {
        // Given
        val pokemonNumber = PokemonNumber("25")
        
        // When
        val formatted = pokemonNumber.formatForDisplay()
        
        // Then
        assertThat(formatted).isEqualTo("025")
    }
    
    @Test
    fun `should throw exception for invalid number`() {
        // When & Then
        assertThrows<IllegalArgumentException> {
            PokemonNumber("")
        }
    }
}
```

### **2. Testes de Use Cases**

#### ✅ **Testes com Mocks**
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
    fun `should return paginated list when valid parameters`() {
        // Given
        val mockPage = PageImpl(listOf(createMockPokemon()))
        every { pokemonRepository.findAll(any<Pageable>()) } returns mockPage
        
        // When
        val result = useCase.execute(0, 10)
        
        // Then
        assertThat(result.pokemons).hasSize(1)
        verify { pokemonRepository.findAll(any<Pageable>()) }
    }
}
```

## 📋 **Checklist de Code Review**

### **🏗️ Arquitetura**
- [ ] Segue Clean Architecture rigorosamente?
- [ ] Mantém separação domain/infrastructure?
- [ ] Usa Ports & Adapters corretamente?
- [ ] Evita dependências circulares?

### **💎 SOLID Principles**
- [ ] **S** - Single Responsibility: Uma responsabilidade por classe?
- [ ] **O** - Open/Closed: Extensível sem modificação?
- [ ] **L** - Liskov Substitution: Implementações substituíveis?
- [ ] **I** - Interface Segregation: Interfaces específicas?
- [ ] **D** - Dependency Inversion: Depende de abstrações?

### **🎯 Domain-Driven Design**
- [ ] Value Objects para conceitos importantes?
- [ ] Entities ricas em comportamento?
- [ ] Linguagem ubíqua consistente?
- [ ] Validações no domínio?

### **🧪 Testabilidade**
- [ ] Testes unitários para Value Objects?
- [ ] Testes unitários para Use Cases?
- [ ] Mocks para dependências externas?
- [ ] Cobertura de casos extremos?

### **📝 Código Limpo**
- [ ] Nomenclatura clara e específica?
- [ ] Métodos pequenos e focados?
- [ ] Sem primitive obsession?
- [ ] Tratamento de erros adequado?

### **🔧 Padrões Kotlin/Spring**
- [ ] Usa data classes apropriadamente?
- [ ] Aproveita null safety do Kotlin?
- [ ] Anotações Spring corretas?
- [ ] Injeção de dependência adequada?

## ⚠️ **Red Flags**

### **❌ Violações Críticas**
- Anotações JPA em domain entities
- Use Cases genéricos ou com múltiplas responsabilidades
- Controllers dependendo de implementações
- Domain dependendo de infrastructure
- Absence de testes unitários

### **⚠️ Code Smells**
- Classes com mais de 200 linhas
- Métodos com mais de 20 linhas
- Mais de 3 parâmetros em métodos
- Primitive obsession (usar String ao invés de Value Object)
- Comentários explicando código ruim

## 🚀 **Padrões de Excelência**

### **🏆 Código Exemplar**
- Value Objects ricos com validações
- Use Cases específicos e testáveis
- Separação total domain/infrastructure
- Testes abrangentes e claros
- Documentação pragmática
- Nomenclatura expressiva

### **🌟 Bonus Points**
- Immutabilidade por padrão
- Fail-fast com validações
- Error handling consistente
- Performance considerations
- Logging estratégico
- Documentação atualizada