# REFATORAÇÃO CLEAN ARCHITECTURE - ESTRUTURA PROPOSTA

## 📁 Nova Estrutura de Pacotes

```
src/main/kotlin/com/pokedex/bff/
├── domain/                          # 🎯 DOMÍNIO PURO
│   ├── entities/                    # Entidades de domínio (sem anotações)
│   │   ├── Pokemon.kt
│   │   ├── Species.kt
│   │   └── ...
│   ├── valueobjects/               # Value Objects
│   │   ├── PokemonNumber.kt
│   │   ├── PokemonName.kt
│   │   └── ...
│   ├── repositories/               # Interfaces de repositório (contratos)
│   │   ├── PokemonDomainRepository.kt
│   │   ├── SpeciesDomainRepository.kt
│   │   └── ...
│   ├── services/                   # Serviços de domínio
│   │   ├── PokemonValidationService.kt
│   │   └── EvolutionCalculationService.kt
│   ├── specifications/             # Especificações de consulta
│   │   ├── PokemonByTypeSpecification.kt
│   │   └── ...
│   └── exceptions/                 # Exceções de domínio
│       ├── PokemonNotFoundException.kt
│       └── ...
│
├── application/                     # 🎯 CASOS DE USO
│   ├── usecases/                   # Use Cases específicos
│   │   ├── pokemon/
│   │   │   ├── FetchPokemonByIdUseCase.kt
│   │   │   ├── SearchPokemonsUseCase.kt
│   │   │   └── GetPokemonEvolutionUseCase.kt
│   │   └── pokedex/
│   │       ├── GetPaginatedPokemonsUseCase.kt
│   │       └── SearchPokedexUseCase.kt
│   ├── ports/                      # Portas (interfaces para infraestrutura)
│   │   ├── input/                 # Portas de entrada
│   │   │   ├── PokemonUseCases.kt
│   │   │   └── PokedexUseCases.kt
│   │   └── output/                # Portas de saída
│   │       ├── PokemonRepositoryPort.kt
│   │       └── ExternalApiPort.kt
│   ├── mappers/                   # Mapeadores de domínio
│   │   ├── PokemonMapper.kt
│   │   └── ...
│   └── dto/                       # DTOs internos da aplicação
│       ├── PokemonApplicationDto.kt
│       └── ...
│
├── infrastructure/                  # 🔧 DETALHES TÉCNICOS
│   ├── persistence/
│   │   ├── entities/              # Entidades JPA
│   │   │   ├── PokemonJpaEntity.kt
│   │   │   └── ...
│   │   ├── repositories/          # Implementações JPA
│   │   │   ├── JpaPokemonRepository.kt
│   │   │   └── ...
│   │   └── mappers/              # Mappers JPA ↔ Domain
│   │       ├── PokemonJpaMapper.kt
│   │       └── ...
│   ├── adapters/                 # Adaptadores de saída
│   │   ├── PokemonRepositoryAdapter.kt
│   │   └── ExternalApiAdapter.kt
│   ├── configurations/           # Configurações Spring
│   │   ├── BeanConfiguration.kt
│   │   ├── DatabaseConfiguration.kt
│   │   └── ...
│   └── seeder/                  # Data seeding
│       └── ...
│
├── interfaces/                     # 🌐 INTERFACE DO USUÁRIO
│   ├── web/                      # Controllers REST
│   │   ├── pokemon/
│   │   │   ├── PokemonController.kt
│   │   │   └── PokemonRestMapper.kt
│   │   └── pokedex/
│   │       ├── PokedexController.kt
│   │       └── PokedexRestMapper.kt
│   ├── dto/                      # DTOs de API
│   │   ├── request/
│   │   │   ├── SearchPokemonRequest.kt
│   │   │   └── ...
│   │   └── response/
│   │       ├── PokemonResponse.kt
│   │       ├── PokedexListResponse.kt
│   │       └── ...
│   └── validators/               # Validadores de entrada
│       ├── PokemonRequestValidator.kt
│       └── ...
│
└── shared/                         # 🤝 COMPARTILHADO
    ├── exceptions/               # Exceções globais
    ├── utils/                   # Utilitários
    └── constants/               # Constantes
```

## 🔄 Exemplo de Implementação: Buscar Pokémon por ID

### 1. Domain Entity (Puro)
```kotlin
// domain/entities/Pokemon.kt
package com.pokedex.bff.domain.entities

data class Pokemon(
    val id: PokemonId,
    val number: PokemonNumber,
    val name: PokemonName,
    val height: Height,
    val weight: Weight,
    val types: List<Type>,
    val stats: Stats,
    val species: Species
) {
    fun isValid(): Boolean {
        return number.isValid() && name.isValid() && types.isNotEmpty()
    }
    
    fun getMainType(): Type = types.first()
    
    fun hasType(type: Type): Boolean = types.contains(type)
}
```

### 2. Value Objects
```kotlin
// domain/valueobjects/PokemonId.kt
package com.pokedex.bff.domain.valueobjects

@JvmInline
value class PokemonId(val value: Long) {
    init {
        require(value > 0) { "Pokemon ID must be positive" }
    }
}

// domain/valueobjects/PokemonNumber.kt
package com.pokedex.bff.domain.valueobjects

@JvmInline
value class PokemonNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Pokemon number cannot be blank" }
        require(value.matches(Regex("\\d{3,4}"))) { "Pokemon number must be 3-4 digits" }
    }
    
    fun isValid(): Boolean = value.isNotBlank() && value.matches(Regex("\\d{3,4}"))
}
```

### 3. Domain Repository Interface
```kotlin
// domain/repositories/PokemonDomainRepository.kt
package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.valueobjects.PokemonId

interface PokemonDomainRepository {
    fun findById(id: PokemonId): Pokemon?
    fun findAll(page: Int, size: Int): List<Pokemon>
    fun countAll(): Long
    fun save(pokemon: Pokemon): Pokemon
    fun existsById(id: PokemonId): Boolean
}
```

### 4. Use Case
```kotlin
// application/usecases/pokemon/FetchPokemonByIdUseCase.kt
package com.pokedex.bff.application.usecases.pokemon

import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.repositories.PokemonDomainRepository
import com.pokedex.bff.domain.valueobjects.PokemonId
import com.pokedex.bff.domain.exceptions.PokemonNotFoundException

class FetchPokemonByIdUseCase(
    private val pokemonRepository: PokemonDomainRepository
) {
    fun execute(id: Long): Pokemon {
        val pokemonId = PokemonId(id)
        return pokemonRepository.findById(pokemonId)
            ?: throw PokemonNotFoundException("Pokemon with ID $id not found")
    }
}
```

### 5. Port Interface
```kotlin
// application/ports/input/PokemonUseCases.kt
package com.pokedex.bff.application.ports.input

import com.pokedex.bff.domain.entities.Pokemon

interface PokemonUseCases {
    fun fetchById(id: Long): Pokemon
    fun searchByName(name: String): List<Pokemon>
    fun getByType(typeName: String): List<Pokemon>
}
```

### 6. Infrastructure Adapter
```kotlin
// infrastructure/adapters/PokemonRepositoryAdapter.kt
package com.pokedex.bff.infrastructure.adapters

import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.repositories.PokemonDomainRepository
import com.pokedex.bff.domain.valueobjects.PokemonId
import com.pokedex.bff.infrastructure.persistence.repositories.JpaPokemonRepository
import com.pokedex.bff.infrastructure.persistence.mappers.PokemonJpaMapper
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class PokemonRepositoryAdapter(
    private val jpaRepository: JpaPokemonRepository,
    private val mapper: PokemonJpaMapper
) : PokemonDomainRepository {

    override fun findById(id: PokemonId): Pokemon? {
        return jpaRepository.findById(id.value)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findAll(page: Int, size: Int): List<Pokemon> {
        val pageable = PageRequest.of(page, size)
        return jpaRepository.findAll(pageable)
            .content
            .map { mapper.toDomain(it) }
    }

    override fun countAll(): Long = jpaRepository.count()

    override fun save(pokemon: Pokemon): Pokemon {
        val entity = mapper.toJpa(pokemon)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun existsById(id: PokemonId): Boolean {
        return jpaRepository.existsById(id.value)
    }
}
```

### 7. Controller
```kotlin
// interfaces/web/pokemon/PokemonController.kt
package com.pokedex.bff.interfaces.web.pokemon

import com.pokedex.bff.application.ports.input.PokemonUseCases
import com.pokedex.bff.interfaces.dto.response.PokemonResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/pokemon")
class PokemonController(
    private val pokemonUseCases: PokemonUseCases,
    private val mapper: PokemonRestMapper
) {

    @Operation(summary = "Fetch Pokemon by ID")
    @GetMapping("/{id}")
    fun getPokemon(@PathVariable id: Long): ResponseEntity<PokemonResponse> {
        val pokemon = pokemonUseCases.fetchById(id)
        val response = mapper.toResponse(pokemon)
        return ResponseEntity.ok(response)
    }
}
```

### 8. Configuration
```kotlin
// infrastructure/configurations/BeanConfiguration.kt
package com.pokedex.bff.infrastructure.configurations

import com.pokedex.bff.application.ports.input.PokemonUseCases
import com.pokedex.bff.application.usecases.pokemon.FetchPokemonByIdUseCase
import com.pokedex.bff.domain.repositories.PokemonDomainRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {

    @Bean
    fun fetchPokemonByIdUseCase(
        pokemonRepository: PokemonDomainRepository
    ): FetchPokemonByIdUseCase {
        return FetchPokemonByIdUseCase(pokemonRepository)
    }

    @Bean
    fun pokemonUseCases(
        fetchPokemonByIdUseCase: FetchPokemonByIdUseCase
    ): PokemonUseCases {
        return object : PokemonUseCases {
            override fun fetchById(id: Long) = fetchPokemonByIdUseCase.execute(id)
            // ... outras implementações
        }
    }
}
```

## 🧪 Estrutura de Testes Proposta

```
src/test/kotlin/com/pokedex/bff/
├── domain/
│   ├── entities/
│   │   └── PokemonTest.kt                    # Testes unitários de entidades
│   ├── valueobjects/
│   │   └── PokemonNumberTest.kt              # Testes de Value Objects
│   └── services/
│       └── PokemonValidationServiceTest.kt   # Testes de serviços de domínio
├── application/
│   ├── usecases/
│   │   └── FetchPokemonByIdUseCaseTest.kt    # Testes de use cases
│   └── mappers/
│       └── PokemonMapperTest.kt              # Testes de mapeadores
├── infrastructure/
│   ├── adapters/
│   │   └── PokemonRepositoryAdapterTest.kt   # Testes de adaptadores
│   └── persistence/
│       └── JpaPokemonRepositoryIT.kt         # Testes de integração JPA
└── interfaces/
    └── web/
        └── PokemonControllerIT.kt             # Testes de integração REST
```

### Exemplo de Teste Unitário de Use Case
```kotlin
// application/usecases/FetchPokemonByIdUseCaseTest.kt
class FetchPokemonByIdUseCaseTest {

    @Mock
    private lateinit var pokemonRepository: PokemonDomainRepository
    
    private lateinit var useCase: FetchPokemonByIdUseCase

    @BeforeEach
    fun setup() {
        useCase = FetchPokemonByIdUseCase(pokemonRepository)
    }

    @Test
    fun `should return pokemon when found`() {
        // Given
        val pokemonId = PokemonId(1L)
        val expectedPokemon = createPokemon(pokemonId)
        `when`(pokemonRepository.findById(pokemonId)).thenReturn(expectedPokemon)

        // When
        val result = useCase.execute(1L)

        // Then
        assertThat(result).isEqualTo(expectedPokemon)
        verify(pokemonRepository).findById(pokemonId)
    }

    @Test
    fun `should throw exception when pokemon not found`() {
        // Given
        val pokemonId = PokemonId(999L)
        `when`(pokemonRepository.findById(pokemonId)).thenReturn(null)

        // When & Then
        assertThrows<PokemonNotFoundException> {
            useCase.execute(999L)
        }
    }
}
```

## 📊 Benefícios da Refatoração

### ✅ Separação de Responsabilidades
- **Domínio**: Apenas regras de negócio
- **Aplicação**: Orquestração de casos de uso
- **Infraestrutura**: Detalhes técnicos isolados
- **Interface**: Apenas apresentação

### ✅ Baixo Acoplamento
- Interfaces bem definidas entre camadas
- Inversão de dependência aplicada corretamente
- Fácil substituição de implementações

### ✅ Alta Testabilidade
- Use cases testáveis unitariamente
- Mocks das dependências externos
- Testes de integração isolados

### ✅ Manutenibilidade
- Código organizado por responsabilidade
- Fácil localização de funcionalidades
- Evolução independente das camadas