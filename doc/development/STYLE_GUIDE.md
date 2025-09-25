# STYLE GUIDE - CODE REVIEW

## 🎯 **Visão Geral**

Este guia estabelece **padrões de código** e **critérios de code review** para o projeto Pokédex BFF, seguindo **MVC bem estruturado** com **SOLID principles**.

## ⚠️ **DECISÃO ARQUITETURAL**

Mudamos de **Clean Architecture** para **MVC estruturado** para maior simplicidade e produtividade.

📖 **Consulte**: [Comparação Arquitetural](../architecture/ARCHITECTURE_COMPARISON.md)

## 🏗️ **Princípios Arquiteturais**

### **1. MVC Bem Estruturado**

#### ✅ **OBRIGATÓRIO**
```kotlin
// ✅ Entity com comportamento
@Entity
data class Pokemon(
    @Id val id: Long,
    val name: String,
    val number: String
) {
    // Lógica de negócio na entity
    fun isLegendary(): Boolean = id in 144..151
    fun formatNumber(): String = number.padStart(3, '0')
}

// ✅ Service com lógica centralizada
@Service
class PokemonService(
    private val repository: PokemonRepository,
    private val validator: PokemonValidator
) {
    fun getPokemon(id: Long): PokemonResponse {
        validator.validateId(id)
        val pokemon = repository.findById(id)
            ?: throw PokemonNotFoundException()
        return PokemonResponse.from(pokemon)
    }
}

// ✅ Controller thin (apenas coordenação)
@RestController
class PokemonController(private val service: PokemonService) {
    @GetMapping("/{id}")
    fun getPokemon(@PathVariable id: Long) = service.getPokemon(id)
}
```

#### ❌ **PROIBIDO**
```kotlin
// ❌ Entity anêmica (sem comportamento)
@Entity
data class Pokemon(@Id val id: Long, val name: String)

// ❌ Controller gordo (com lógica)
@RestController
class PokemonController(private val repository: PokemonRepository) {
    @GetMapping("/{id}")
    fun getPokemon(@PathVariable id: Long): Pokemon {
        if (id <= 0) throw IllegalArgumentException() // ❌ Validação no controller
        return repository.findById(id) ?: throw RuntimeException() // ❌ Lógica no controller
    }
}

// ❌ Service genérico demais
@Service
class DataService {  // ❌ Muito genérico
    fun doEverything() { ... }  // ❌ Múltiplas responsabilidades
}
```

### **2. SOLID Principles**

#### ✅ **Single Responsibility**
```kotlin
// ✅ Uma responsabilidade por classe
@Service
class PokemonSearchService(private val repository: PokemonRepository) {
    fun searchByName(name: String): List<Pokemon> = repository.findByNameContaining(name)
}

@Service  
class PokemonValidationService {
    fun validatePokemon(pokemon: Pokemon): ValidationResult = ...
}
```

#### ✅ **Open/Closed**
```kotlin
// ✅ Extensível via estratégia
interface SearchStrategy {
    fun search(criteria: String): List<Pokemon>
}

@Component
class NameSearchStrategy : SearchStrategy { ... }

@Component  
class TypeSearchStrategy : SearchStrategy { ... }
```

#### ✅ **Dependency Inversion**
```kotlin
// ✅ Service depende de abstração
@Service
class PokemonService(
    private val repository: PokemonRepository  // Interface
) { ... }

// ✅ Implementação não importada no service
@Repository
class JpaPokemonRepository : PokemonRepository { ... }
```

### **3. Estrutura de Camadas MVC**

#### ✅ **Estrutura Simplificada**
```
src/main/kotlin/com/pokedex/bff/
├── controller/       # REST Controllers
├── service/          # Business Logic
├── repository/       # Data Access
├── entity/           # JPA Entities
├── dto/              # Data Transfer Objects
├── config/           # Configurations
└── exception/        # Exception Handling
```

#### ✅ **Responsabilidades por Camada**
- **Controller**: Coordenação e mapeamento HTTP
- **Service**: Lógica de negócio e orquestração
- **Repository**: Acesso a dados
- **Entity**: Modelo de dados com comportamentos
- **DTO**: Transferência de dados entre camadas

## 🔧 **Padrões de Código**

### **1. Nomenclatura**

#### ✅ **Padrões Corretos**
```kotlin
// Services específicos
@Service
class PokemonSearchService
class PokemonValidationService
class PokedexManagementService

// Controllers organizados
@RestController
class PokemonController
class PokedexController
class TypeController

// Repositories focados
interface PokemonRepository
interface TypeRepository
```

#### ❌ **Nomenclatura Incorreta**
```kotlin
// ❌ Genérico demais
class PokemonService  // Muito genérico
class DataService    // O que faz?
class Helper        // Vago

// ❌ Não específico
class ProcessService
class HandleController
```

### **2. Value Objects (Opcionais)**

#### ✅ **Implementação com Validação**
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

#### ❌ **Primitive Obsession**
```kotlin
// ❌ Usar primitivos sem validação
fun searchPokemon(id: Long) { ... }  // Sem validação

// ❌ Validação espalhada
fun getPokemon(id: Long) {
    if (id <= 0) throw Exception()  // Repetido em todo lugar
}
```

### **3. Services Bem Definidos**

#### ✅ **Service Focado**
```kotlin
@Service
class PokemonSearchService(
    private val pokemonRepository: PokemonRepository
) {
    fun searchByName(name: String): List<Pokemon> {
        validateSearchTerm(name)
        return pokemonRepository.findByNameContaining(name)
    }
    
    fun searchByType(type: String): List<Pokemon> {
        validateType(type)
        return pokemonRepository.findByType(type)
    }
    
    private fun validateSearchTerm(term: String) {
        require(term.isNotBlank()) { "Search term cannot be blank" }
        require(term.length >= 2) { "Search term too short" }
    }
}
#### ❌ **Service Mal Definido**
```kotlin
// ❌ Múltiplas responsabilidades
@Service
class PokemonService {
    fun getPokemons() { ... }
    fun searchPokemons() { ... }
    fun createPokemon() { ... }    // ❌ CRUD genérico
    fun sendEmail() { ... }        // ❌ Responsabilidade não relacionada
    fun processPayment() { ... }   // ❌ Não é responsabilidade do Pokemon
}
```

### **4. Controllers Thin**

#### ✅ **Controller Correto**
```kotlin
@RestController
@RequestMapping("/api/v1/pokemons")
class PokemonController(
    private val pokemonService: PokemonService  // Service Interface
) {
    @GetMapping("/{id}")
    fun getPokemon(@PathVariable id: Long): ResponseEntity<PokemonResponse> {
        val pokemon = pokemonService.findById(id)
        return ResponseEntity.ok(pokemon)
    }
    
    @GetMapping
    fun searchPokemons(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) type: String?
    ): ResponseEntity<List<PokemonResponse>> {
        val pokemons = when {
            name != null -> pokemonService.searchByName(name)
            type != null -> pokemonService.searchByType(type)
            else -> pokemonService.findAll()
        }
        return ResponseEntity.ok(pokemons)
    }
}
```

#### ❌ **Controller Incorreto**
```kotlin
// ❌ Controller gordo com lógica
@RestController
class PokemonController(
    private val repository: PokemonRepository  // ❌ Acesso direto ao repository
) {
    @GetMapping("/{id}")
    fun getPokemon(@PathVariable id: Long): Pokemon {
        // ❌ Validação no controller
        if (id <= 0) throw IllegalArgumentException("Invalid ID")
        
        // ❌ Lógica de negócio no controller
        val pokemon = repository.findById(id) ?: throw NotFoundException()
        
        // ❌ Formatação no controller
        if (pokemon.name.contains("legendary")) {
            pokemon.type = "legendary"
        }
        
        return pokemon
## 🧪 **Padrões de Teste**

### **1. Testes de Service**

#### ✅ **Testes com Mocks**
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
        val pokemonId = 1L
        val expectedPokemon = Pokemon(pokemonId, "Pikachu", "025")
        `when`(pokemonRepository.findById(pokemonId)).thenReturn(expectedPokemon)
        
        // When
        val result = pokemonService.findById(pokemonId)
        
        // Then
        assertThat(result).isEqualTo(expectedPokemon)
        verify(pokemonRepository).findById(pokemonId)
    }
    
    @Test
    fun `should throw exception when pokemon not found`() {
        // Given
        val pokemonId = 999L
        `when`(pokemonRepository.findById(pokemonId)).thenReturn(null)
        
        // When & Then
        assertThrows<PokemonNotFoundException> {
            pokemonService.findById(pokemonId)
        }
    }
}
```

### **2. Testes de Controller**

#### ✅ **Testes de Integração**
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
        val pokemon = Pokemon(1L, "Pikachu", "025")
        `when`(pokemonService.findById(1L)).thenReturn(pokemon)
        
        // When & Then
        mockMvc.perform(get("/api/v1/pokemons/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Pikachu"))
            .andExpect(jsonPath("$.number").value("025"))
    }
}
## 📋 **Checklist de Code Review**

### **🏗️ Arquitetura MVC**
- [ ] Controllers thin (apenas coordenação)?
- [ ] Services focados e específicos?
- [ ] Repositories simples (acesso a dados)?
- [ ] Entities com comportamentos?
- [ ] DTOs para transferência de dados?

### **💎 SOLID Principles**
- [ ] **S** - Single Responsibility: Uma responsabilidade por classe?
- [ ] **O** - Open/Closed: Extensível sem modificação?
- [ ] **L** - Liskov Substitution: Implementações substituíveis?
- [ ] **I** - Interface Segregation: Interfaces específicas?
- [ ] **D** - Dependency Inversion: Depende de abstrações?

### **🎯 Qualidade de Código**
- [ ] Value Objects para conceitos importantes? (opcional)
- [ ] Entities ricas em comportamento?
- [ ] Validações centralizadas nos services?
- [ ] Tratamento de exceções adequado?

### **🧪 Testabilidade**
- [ ] Testes unitários para services?
- [ ] Testes de integração para controllers?
- [ ] Mocks para dependências externas?
- [ ] Cobertura de casos extremos?

### **📝 Código Limpo**
- [ ] Nomenclatura clara e específica?
- [ ] Métodos pequenos e focados?
- [ ] Sem primitive obsession excessiva?
- [ ] Tratamento de erros adequado?

### **🔧 Padrões Kotlin/Spring**
- [ ] Usa data classes apropriadamente?
- [ ] Aproveita null safety do Kotlin?
- [ ] Anotações Spring corretas?
- [ ] Injeção de dependência adequada?

## ⚠️ **Red Flags**

### **❌ Violações Críticas**
- Controllers gordos com lógica de negócio
- Services genéricos demais ou com múltiplas responsabilidades
- Entities anêmicas (apenas getters/setters)
- Acesso direto a repositories nos controllers
- Ausência de testes unitários

### **⚠️ Code Smells**
- Classes com mais de 200 linhas
- Métodos com mais de 20 linhas
- Mais de 3 parâmetros em métodos
- Primitive obsession excessiva
- Comentários explicando código ruim

## 🚀 **Padrões de Excelência**

### **🏆 Código Exemplar**
- Services específicos e bem testados
- Controllers thin que apenas coordenam
- Entities com comportamentos relevantes
- Validações centralizadas e reutilizáveis
- Documentação pragmática e útil
- Nomenclatura expressiva

### **🌟 Bonus Points**
- Immutabilidade por padrão
- Fail-fast com validações
- Error handling consistente
- Performance considerations
- Logging estratégico
- Documentação atualizada