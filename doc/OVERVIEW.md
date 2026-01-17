# Visão Geral

Este repositório contém o código-fonte do **Pokedex BFF (Backend For Frontend)**, implementado com **arquitetura em camadas pragmática** usando Spring Boot. O serviço atua como camada intermediária entre fontes de dados e aplicações frontend, fornecendo uma API REST unificada.

## 🎯 Objetivos
- Centralizar e transformar dados de múltiplas fontes via API unificada
- Garantir separação de responsabilidades entre camadas
- Manter código testável e evolutivo
- Suportar JSONB para dados complexos (sprites, evolution chains)

## 🏗️ Arquitetura Atual

```
┌─────────────────────────────────────────────────────┐
│            Controllers REST (Web)                   │
│      adapters/input/web/controller/                 │
└──────────────────┬──────────────────────────────────┘
                   │ WebMapper (DTO → Domain)
┌──────────────────▼──────────────────────────────────┐
│           Use Cases (Application)                   │
│    application/port/input/, application/usecase/    │
└──────────────────┬──────────────────────────────────┘
                   │ Repository Interface
┌──────────────────▼──────────────────────────────────┐
│     JPA Repositories & Entities (Domain)            │
│    domain/repositories/, domain/entities/           │
└──────────────────┬──────────────────────────────────┘
                   │ JDBC/JPA
┌──────────────────▼──────────────────────────────────┐
│              PostgreSQL Database                    │
│         (JSONB para sprites, evolutions)            │
└─────────────────────────────────────────────────────┘
```

### Camadas e Responsabilidades

- **Adapters (Input)**: Controllers REST, DTOs Web, Mappers
- **Application**: Use Cases e portas de entrada
- **Domain**: Entidades JPA, Repositórios Spring Data, Exceções de domínio
- **Infrastructure**: Configurações, Exception Handlers, Seeders

### Estado da Migração

**Legado (Atual):**
- Entidades JPA em `domain/entities/` com anotações `@Entity`
- Repositórios Spring Data em `domain/repositories/`
- Mistura de responsabilidades (JPA no domínio)

**Novo (Em Progresso):**
- Entidades puras em `domain/pokemon/entities/` (sem anotações)
- Interfaces de repositório em `domain/pokemon/repository/` (contratos)
- Adaptadores em `adapters/output/persistence/` (implementações)
- Seguindo Hexagonal Architecture

## 📋 Funcionalidades Principais

### API REST

```kotlin
@RestController
@RequestMapping("/api/v1/pokemons")
class PokemonController(
    private val listPokemonsUseCase: ListPokemonsUseCase,
    private val richWebMapper: PokemonRichWebMapper
) {
    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): PokemonRichPageResponse {
        val pageResult = listPokemonsUseCase.findAll(page, size)
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

### Deserialização JSONB

Sprites e evolution chains são armazenados como JSONB no PostgreSQL:

```kotlin
@Converter
class SpritesJsonConverter : AttributeConverter<Sprites?, String?> {
    override fun convertToEntityAttribute(json: String?): Sprites? {
        return json?.let {
            try {
                objectMapper.readValue<SpritesDTO>(it).toDomain()
            } catch (e: Exception) {
                logger.error("Deserialization error", e)
                null // Fallback gracioso
            }
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class SpritesDTO(
        @JsonProperty("front_default") val frontDefault: String?,
        @JsonProperty("official-artwork") val officialArtwork: OfficialArtworkDTO?
    )
}
```

**Características:**
- Mapeamento snake_case → camelCase via `@JsonProperty`
- Tolerância a campos desconhecidos via `@JsonIgnoreProperties`
- Fallback gracioso em caso de erro
- Logging para debug

### Tratamento de Erros (Dev/Prod)

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {
    @Value("\${spring.profiles.active:prod}")
    private lateinit var activeProfile: String
    
    @ExceptionHandler(MismatchedInputException::class)
    fun handleDeserializationError(ex: MismatchedInputException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(400).body(ErrorResponse(
            code = "DESERIALIZATION_ERROR",
            message = if (activeProfile == "dev") {
                "Failed to deserialize: ${ex.originalMessage}"
            } else {
                "Invalid data format"
            },
            details = if (activeProfile == "dev") {
                mapOf(
                    "exception" to ex.javaClass.simpleName,
                    "path" to ex.path?.joinToString(".") { it.fieldName ?: "[${it.index}]" },
                    "stackTrace" to ex.stackTrace.take(5).map { it.toString() }
                )
            } else {
                mapOf("exception" to ex.javaClass.simpleName)
            }
        ))
    }
}
```

**Modo Dev:**
- Mensagens detalhadas
- Stack traces completos
- Path do erro no JSON
- Tipo esperado vs recebido

**Modo Prod:**
- Mensagens genéricas
- Sem exposição de internos
- Apenas código de erro

### Segurança e CORS (por ambiente)

- **Dev**: endpoints liberados e CORS amplo para facilitar desenvolvimento.
- **Prod**: autenticação básica obrigatória e CORS restrito via variáveis de ambiente.
- **Management**: `health` e `info` expostos em prod; demais apenas em dev.

## 🚀 Status do Projeto

- ✅ API REST funcional
- ✅ Persistência PostgreSQL com JSONB
- ✅ Deserialização JSONB corrigida (sprites, evolution chains)
- ✅ Error handling dev/prod implementado
- ✅ Seeder automático de dados
- 🔄 Migração para Hexagonal Architecture (em progresso)
- 🔄 Testes unitários e integração (em expansão)

## 📚 Referências

- [GETTING_STARTED.md](./GETTING_STARTED.md) - Como configurar e rodar
- [TECHNOLOGIES.md](./TECHNOLOGIES.md) - Stack tecnológico
- [database/SCHEMA.md](./database/SCHEMA.md) - Esquema do banco
- [api/SWAGGER.md](./api/SWAGGER.md) - Documentação da API

---

*Atualizado em 17/01/2026 - Segurança por perfil, CORS configurável e separação de use cases*
