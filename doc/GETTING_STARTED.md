# Como Começar

Siga estas instruções para configurar e executar o Pokedex BFF em seu ambiente de desenvolvimento local.

## Arquitetura do Projeto

Este projeto adota uma **arquitetura em camadas pragmática** com Spring Boot, focando em separação de responsabilidades, manutenibilidade e testabilidade.

### Estrutura das Camadas

```
src/main/kotlin/com/pokedex/bff/
├── domain/                    # Camada de domínio
│   ├── entities/              # Entidades JPA (anotadas com @Entity, @Table)
│   ├── repositories/          # Interfaces Spring Data JPA (extends JpaRepository)
│   └── pokemon/               # Agregados e entidades de domínio puro (novos use cases)
│       ├── entities/          # Entidades de domínio (sem anotações JPA)
│       ├── exception/         # Exceções de domínio
│       └── repository/        # Interfaces de repositório (contratos)
├── application/               # Camada de aplicação
│   ├── services/              # Serviços de aplicação (@Service)
│   ├── dto/                   # DTOs de entrada/saída
│   ├── valueobjects/          # Value Objects (ex: SpritesVO)
│   ├── port/input/            # Interfaces de use cases (Hexagonal)
│   └── usecase/               # Implementações de use cases
├── adapters/                  # Camada de adaptadores
│   ├── input/web/             # Adaptadores de entrada (REST)
│   │   ├── controller/        # @RestController
│   │   ├── dto/               # Request/Response DTOs Web
│   │   └── mapper/            # Conversores DTO Web ↔ Domain/Entity
│   └── output/persistence/    # Adaptadores de saída (Persistência)
│       ├── converter/         # Conversores JSONB (AttributeConverter)
│       ├── entity/            # Entidades JPA adicionais
│       ├── mapper/            # Mapeadores JPA Entity ↔ Domain
│       └── repository/        # Implementações de repositórios customizados
└── infrastructure/            # Infraestrutura e configurações
    ├── config/                # Configurações Spring (@Configuration)
    ├── exception/             # GlobalExceptionHandler, ErrorResponse
    ├── seeder/                # Populador de dados inicial
    └── utils/                 # Utilitários gerais
```

### Características da Arquitetura

**Estado Atual (Migração em Andamento):**
- **Domain (Legado)**: Entidades JPA em `domain/entities/` com anotações Spring Data
- **Domain (Novo)**: Entidades puras em `domain/pokemon/entities/` seguindo DDD
- **Application**: Serviços, DTOs, value objects e use cases (interfaces + implementações)
- **Adapters**: Controllers REST (entrada) e conversores/mappers (saída)
- **Infrastructure**: Configurações técnicas, exception handlers, seeders

## Configuração do Ambiente

### Pré-requisitos

* [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)
* **GNU Make**

### Comandos Principais

```bash
./gradlew bootRun           # Inicia aplicação
./gradlew test              # Executa testes
./gradlew build             # Build completo
make dev-up                 # Sobe banco + BFF local
```

## Trabalhando com Entidades e Persistência

### Conversores JSONB (`adapters/output/persistence/converter/`)

Para campos JSONB complexos use `AttributeConverter` com DTOs internos:

```kotlin
@Converter
class SpritesJsonConverter : AttributeConverter<Sprites?, String?> {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    
    override fun convertToEntityAttribute(json: String?): Sprites? {
        return json?.let {
            try {
                objectMapper.readValue<SpritesDTO>(it).toDomain()
            } catch (e: Exception) {
                logger.error("Deserialization error: ${e.message}", e)
                null // Fallback gracioso
            }
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class SpritesDTO(
        @JsonProperty("front_default") val frontDefault: String?,
        @JsonProperty("front_shiny") val frontShiny: String?
    ) {
        fun toDomain() = Sprites(frontDefault, frontShiny)
    }
}
```

**Boas práticas:**
- Use `@JsonProperty` para snake_case → camelCase
- Adicione `@JsonIgnoreProperties(ignoreUnknown = true)`
- Implemente fallback gracioso em caso de erro
- Log erros para debug

### Tratamento de Erros (Dev/Prod)

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {
    @Value("\${spring.profiles.active:prod}")
    private lateinit var activeProfile: String
    
    private fun isDevelopmentMode() = activeProfile == "dev"
    
    @ExceptionHandler(MismatchedInputException::class)
    fun handleError(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(500).body(ErrorResponse(
            code = "ERROR",
            message = if (isDevelopmentMode()) ex.message else "Internal error",
            details = if (isDevelopmentMode()) {
                mapOf("stackTrace" to ex.stackTrace.take(5).map { it.toString() })
            } else null
        ))
    }
}
```

## Executando o Projeto

```bash
make dev-up      # Banco em container + BFF local (recomendado)
make run-bff     # Apenas BFF (banco já rodando)
make help        # Ver todos comandos
```

## Documentação Complementar

- **[OVERVIEW.md](./OVERVIEW.md)**: Visão geral e arquitetura
- **[TECHNOLOGIES.md](./TECHNOLOGIES.md)**: Stack tecnológico
- **[database/SCHEMA.md](./database/SCHEMA.md)**: Esquema do banco
- **[api/SWAGGER.md](./api/SWAGGER.md)**: API REST

---

*Atualizado em 07/12/2025 - Correções JSONB e error handling*
