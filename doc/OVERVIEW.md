# Visão Geral

Este repositório contém o código-fonte para o **Pokedex BFF (Backend For Frontend)**, agora implementado com **DDD + Clean Architecture**. O serviço atua como uma camada intermediária otimizada entre fontes de dados externas sobre Pokémon e aplicações frontend.

## 🎯 Objetivos
- **Centralizar e transformar dados** de múltiplas fontes, fornecendo uma API unificada
- **Alta coesão e baixo acoplamento** entre camadas
- **Domínio rico** com regras de negócio explícitas
- **Testabilidade e evolutibilidade** garantidas por separação de responsabilidades

## 🏗️ Arquitetura (Setembro 2025)

### **DDD + Clean Architecture**

```
src/main/kotlin/com/pokedex/bff/
├── domain/           # Núcleo do negócio (entidades, value objects, serviços, eventos, repositórios)
├── application/      # Casos de uso, orquestração, DTOs
├── adapters/         # Entrada (REST/controllers) e saída (persistência, integrações externas)
├── infrastructure/   # Configurações técnicas, segurança, migrações
└── tests/            # Testes automatizados
```

- **Domain**: Núcleo puro, sem dependências técnicas
- **Application**: Casos de uso, coordenação de entidades
- **Adapters**: Controllers, mappers, persistência, integrações
- **Infrastructure**: Configurações, segurança, migrações

## Exemplos de Implementação

### Value Object
```kotlin
@JvmInline
value class PokemonId(val value: String)
```

### Use Case
```kotlin
class CreatePokemonInteractor(
    private val pokemonRepository: PokemonRepository
) : CreatePokemonUseCase {
    override fun execute(input: CreatePokemonInput): PokemonOutput {
        val pokemon = Pokemon(/* ... */)
        pokemonRepository.save(pokemon)
        return PokemonOutput.fromDomain(pokemon)
    }
}
```

### Adapter (Controller)
```kotlin
@RestController
@RequestMapping("/api/v1/pokemons")
class PokemonController(
    private val createPokemonUseCase: CreatePokemonUseCase,
    private val webMapper: PokemonWebMapper
) {
    @PostMapping
    fun create(@RequestBody request: CreatePokemonWebRequest): PokemonWebResponse {
        val input = webMapper.toCreatePokemonInput(request)
        val output = createPokemonUseCase.execute(input)
        return webMapper.toWebResponse(output)
    }
}
```

## 🚀 Status
- Estrutura DDD + Clean Architecture implementada
- Separação total entre domínio, aplicação, adapters e infraestrutura
- Testes unitários e integração em progresso

Consulte os demais arquivos em `doc/` para detalhes, exemplos e guias de cada camada.

---

*Documento atualizado após refatoração para DDD + Clean Architecture - 23/09/2025*
