
# Visão Geral

Este repositório contém o código-fonte do **Pokedex BFF (Backend For Frontend)**, implementado com **DDD + Clean Architecture**. O serviço atua como camada intermediária entre fontes de dados externas sobre Pokémon e aplicações frontend, centralizando, transformando e expondo dados via API REST.

## 🎯 Objetivos
- Centralizar e transformar dados de múltiplas fontes, fornecendo uma API unificada
- Garantir alta coesão e baixo acoplamento entre camadas
- Domínio rico com regras de negócio explícitas
- Testabilidade e evolução facilitadas por separação de responsabilidades

src/main/kotlin/com/pokedex/bff/
├── domain/           # Núcleo do negócio (entidades, value objects, serviços, eventos, repositórios)
├── application/      # Casos de uso, orquestração, DTOs
├── adapters/         # Entrada (REST/controllers) e saída (persistência, integrações externas)
├── infrastructure/   # Configurações técnicas, segurança, migrações
└── tests/            # Testes automatizados

## 🏗️ Arquitetura (2025)

```
src/main/kotlin/com/pokedex/bff/
    domain/
        pokemon/
            entities/         # Entidades de domínio (ex: Pokemon.kt, Ability.kt)
            valueobject/      # Value Objects (ex: PokemonNumber.kt, Experience.kt)
            repository/       # Interfaces de repositório (ex: PokemonRepository.kt)
        shared/             # Tipos utilitários, exceptions e value objects genéricos
    application/
        interactor/         # Implementações dos casos de uso (ex: CreatePokemonInteractor.kt)
        usecase/            # Interfaces de casos de uso (ex: CreatePokemonUseCase.kt)
        dtos/
            input/            # DTOs de entrada (ex: CreatePokemonInput.kt)
            output/           # DTOs de saída (ex: PokemonOutput.kt)
    adapters/
        input/web/controller/ # Controllers REST (ex: PokemonController.kt)
        output/persistence/entity/ # Entidades JPA (ex: PokemonJpaEntity.kt)
        output/persistence/mapper/ # Mapeadores JPA <-> domínio
    infrastructure/
        config/             # Beans, providers, configuração de DI
        migration/          # Scripts de migração
        security/           # Configuração de segurança
```

- **Domain**: Núcleo puro, sem dependências técnicas/frameworks
- **Application**: Casos de uso, coordenação de entidades
- **Adapters**: Controllers, mappers, persistência, integrações
- **Infrastructure**: Configurações, segurança, migrações


## Exemplos de Implementação

### Value Object
```kotlin
// domain/pokemon/valueobject/PokemonId.kt
@JvmInline
value class PokemonId(val value: String)
```

### Use Case & Interactor
```kotlin
// application/usecase/CreatePokemonUseCase.kt
interface CreatePokemonUseCase {
    fun execute(input: CreatePokemonInput): PokemonOutput
    fun findAll(page: Int, size: Int): Page<Pokemon>
}

// application/interactor/CreatePokemonInteractor.kt
class CreatePokemonInteractor(
    private val pokemonRepository: PokemonRepository
) : CreatePokemonUseCase {
    override fun execute(input: CreatePokemonInput): PokemonOutput {
        val pokemon = Pokemon(
            id = 0L,
            number = "000",
            name = input.name,
            height = 1.0,
            weight = 1.0,
            description = "Placeholder description",
            sprites = null,
            genderRateValue = 0,
            genderMale = 0.5f,
            genderFemale = 0.5f,
            eggCycles = 10,
            stats = null,
            generation = null,
            species = null,
            region = null,
            evolutionChain = null,
            types = emptySet(),
            abilities = emptySet(),
            eggGroups = emptySet(),
            weaknesses = emptySet()
        )
        pokemonRepository.save(pokemon)
        return PokemonOutput.fromDomain(pokemon)
    }

    override fun findAll(page: Int, size: Int): Page<Pokemon> {
        return pokemonRepository.findAll(page, size)
    }
}
```

### Adapter (Controller)
```kotlin
// adapters/input/web/controller/PokemonController.kt
@RestController
@RequestMapping("/api/v1/pokemons")
class PokemonController(
    private val createPokemonUseCase: CreatePokemonUseCase,
    private val evolvePokemonUseCase: EvolvePokemonUseCase,
    private val battleUseCase: BattleUseCase,
    private val richWebMapper: PokemonRichWebMapper,
    private val webMapper: PokemonWebMapper
) {
    @PostMapping
    fun create(@RequestBody request: CreatePokemonWebRequest) {
        val input = webMapper.toCreatePokemonInput(request)
        createPokemonUseCase.execute(input)
    }

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): PokemonRichPageResponse {
        val pageSize = size.coerceAtMost(100)
        val pageResult = createPokemonUseCase.findAll(page, pageSize)
        return richWebMapper.toRichPageResponse(
            pokemons = pageResult.content,
            totalElements = pageResult.totalElements,
            currentPage = pageResult.pageNumber,
            totalPages = pageResult.totalPages,
            hasNext = pageResult.hasNext
        )
    }
}
```


## 🚀 Status
- Estrutura Clean Architecture consolidada
- Separação total entre domínio, application, adapters e infraestrutura
- Testes unitários e integração em progresso


Consulte os demais arquivos em `doc/` para detalhes, exemplos e guias de cada camada.

---


*Documento atualizado após refatoração para Clean Architecture - 01/10/2025*
