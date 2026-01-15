# STYLE GUIDE - CODE REVIEW

## 🎯 **Visão Geral**

Este guia estabelece **padrões de código** e **critérios de code review** para o projeto Pokédex BFF, seguindo **DDD + Clean Architecture** e princípios SOLID.

## 🏗️ **Princípios Arquiteturais**

### **1. DDD + Clean Architecture**

#### ✅ **OBRIGATÓRIO**
```kotlin
// ✅ Entity com comportamento de domínio
class Pokemon(
    val id: PokemonId,
    val name: String,
    val type: PokemonType,
    val level: Int
) {
    fun isLegendary(): Boolean = id.value in 144..151
    fun formatNumber(): String = id.value.toString().padStart(3, '0')
}

// ✅ Use Case isolado
class CreatePokemonInteractor(
    private val pokemonRepository: PokemonRepository
) : CreatePokemonUseCase {
    override fun execute(input: CreatePokemonInput): PokemonOutput {
        // ...lógica de negócio...
    }
}

// ✅ Controller thin (apenas coordenação)
@RestController
class PokemonController(private val createPokemonUseCase: CreatePokemonUseCase) {
    @PostMapping
    fun create(@RequestBody req: CreatePokemonWebRequest) =
        createPokemonUseCase.execute(req.toInput())
}
```

#### ❌ **PROIBIDO**
```kotlin
// ❌ Entity anêmica (sem comportamento)
class Pokemon(val id: String, val name: String)

// ❌ Controller gordo (com lógica de negócio)
@RestController
class PokemonController {
    @PostMapping
    fun create(@RequestBody req: CreatePokemonWebRequest): PokemonWebResponse {
        // ...lógica de negócio aqui... (ERRADO)
    }
}
```

## 🧩 **Padrões Gerais**
- Domínio nunca depende de frameworks
- Use Cases orquestram entidades, nunca expõem lógica técnica
- Controllers apenas coordenam, nunca implementam regras de negócio
- DTOs REST nunca expõem entidades do domínio diretamente
- Sempre use mappers explícitos entre camadas

Consulte os demais arquivos em `doc/` para exemplos e decisões detalhadas.
