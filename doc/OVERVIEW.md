
# Visão Geral

Este repositório contém o código-fonte do **Pokedex BFF (Backend For Frontend)**, implementado com **DDD + Clean Architecture**. O serviço atua como camada intermediária entre fontes de dados externas sobre Pokémon e aplicações frontend, centralizando, transformando e expondo dados via API REST.

## 🎯 Objetivos
- Centralizar e transformar dados de múltiplas fontes, fornecendo uma API unificada
- Garantir alta coesão e baixo acoplamento entre camadas
## 🏗️ Arquitetura (2025)
    application/
# Visão Geral

Este repositório contém o código-fonte do **Pokedex BFF (Backend For Frontend)**, implementado com **DDD + Clean Architecture**. O serviço atua como camada intermediária entre fontes de dados externas sobre Pokémon e aplicações frontend, centralizando, transformando e expondo dados via API REST.

## 🎯 Objetivos
- Centralizar e transformar dados de múltiplas fontes, fornecendo uma API unificada
- Garantir alta coesão e baixo acoplamento entre camadas
- Domínio rico com regras de negócio explícitas (uso de agregados e value objects)
- Testabilidade e evolução facilitadas por separação de responsabilidades
- Isolamento de detalhes técnicos (banco, frameworks, Spring)

## Estrutura de Pastas e Camadas

```
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
```

### Camadas e Responsabilidades
- **Domain**: Núcleo puro, sem dependências técnicas/frameworks. Contém entidades, value objects, agregados e interfaces de repositório. Segue DDD para modelar o negócio.
- **Application**: Casos de uso (interfaces e implementações), orquestração de entidades/agregados, DTOs. Não depende de frameworks.
- **Adapters**: Controllers REST, mapeadores, adapters de persistência (implementam interfaces do domínio), integrações externas.
- **Infrastructure**: Configurações técnicas, beans, providers, migrações, segurança. Isola detalhes como banco de dados e Spring.

### Isolamento de Detalhes Técnicos
O domínio não conhece detalhes de banco, frameworks ou Spring. As interfaces de repositório ficam no domínio; as implementações (adapters) estão fora, garantindo baixo acoplamento e alta testabilidade. O Spring é usado apenas para compor e injetar dependências.

### DDD na Prática
O projeto adota DDD especialmente na modelagem de agregados (ex: Pokémon, Trainer), value objects e repositórios. Cada agregado tem seu próprio pacote, mantendo alta coesão e clareza de limites.

## Exemplos de Implementação

### Value Object
```kotlin
// domain/pokemon/valueobject/PokemonId.kt
@JvmInline
value class PokemonId(val value: String)
```

### Interface e Adapter de Repositório
```kotlin
// domain/pokemon/repository/PokemonRepository.kt
interface PokemonRepository {
    fun save(pokemon: Pokemon): Pokemon
    fun findById(id: String): Pokemon?
    fun findAll(page: Int, size: Int): Page<Pokemon>
}

// adapters/output/persistence/repository/PokemonRepositoryAdapter.kt
class PokemonRepositoryAdapter(...) : PokemonRepository {
    // Implementação usando Spring Data JPA
}
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
            // ...
        )
        pokemonRepository.save(pokemon)
        return PokemonOutput.fromDomain(pokemon)
    }

    override fun findAll(page: Int, size: Int): Page<Pokemon> {
        return pokemonRepository.findAll(page, size)
    }
}
```

## Referências

- Martin, R. C. (2019). Arquitetura Limpa: O Guia do Artesão para Estrutura e Design de Software. Starlin Alta Editora e Consultoria Eireli.
- Evans, E. (2004). Domain-Driven Design: Tackling Complexity in the Heart of Software. Addison-Wesley.

> Para detalhes de configuração e exemplos de uso, consulte o GETTING_STARTED.md e os arquivos em doc/architecture/.
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
