# Arquitetura do Sistema — Pokédex BFF

## 1. Visão Geral

O **Pokédx BFF** é um backend que fornece dados estruturados de Pokémon através de APIs REST.
A arquitetura é baseada em **Spring Boot** e Kotlin, organizada seguindo rigorosamente os princípios do **Clean Architecture com Ports & Adapters (Hexagonal Architecture)**, garantindo separação total de responsabilidades, alta testabilidade e baixo acoplamento.

---

## 2. Objetivos Arquiteturais

* **Separação total de responsabilidades** entre as camadas: interfaces, aplicação, domínio e infraestrutura.
* **Domínio puro** sem dependências externas, contendo apenas regras de negócio.
* **Inversão de dependência completa** com interfaces bem definidas entre camadas.
* **Alta testabilidade** facilitada através de Use Cases isolados e Value Objects puros.
* **Baixo acoplamento** permitindo evolução independente das camadas.
* **Ports & Adapters** para entrada e saída, seguindo Hexagonal Architecture.
* **Domain-Driven Design** com entidades ricas e Value Objects.

---

## 3. Visão Geral da Arquitetura

A arquitetura segue rigorosamente os princípios do **Clean Architecture + Hexagonal Architecture**, com implementação de **Ports & Adapters** e separação total entre domínio e infraestrutura.

### ⭕ Diagrama de Dependências

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   INTERFACES    │───▶│   APPLICATION    │───▶│     DOMAIN      │
│                 │    │                  │    │                 │
│ Controllers     │    │ Use Cases        │    │ Entities        │
│ DTOs            │    │ Ports            │    │ Value Objects   │
│ Validators      │    │ Mappers          │    │ Repositories    │
│                 │    │                  │    │ (interfaces)    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         ↑                       ↑                       ↑
         │                       │                       │
         │              ┌──────────────────┐             │
         └──────────────│ INFRASTRUCTURE   │─────────────┘
                        │                  │
                        │ Adapters         │
                        │ JPA Entities     │
                        │ Repositories     │
                        │ Configurations   │
                        └──────────────────┘
```

### Camadas e Componentes Implementados

| Camada Principal   | Subcomponente/Diretório        | Responsabilidade                                                                                                                               |
| :----------------- | :----------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------- |
| **Domain**         | `entities/`                    | **Entidades puras de domínio** sem dependências externas. Implementados: `Pokemon.kt`, `Species.kt`, `Type.kt`                             |
|                    | `valueobjects/`                | **Value Objects ricos** com validações e comportamentos. Implementados: `PokemonId.kt`, `PokemonNumber.kt`                                  |
|                    | `repositories/`                | **Interfaces de repositório** definindo contratos de persistência. Implementado: `PokemonRepository.kt`                                     |
|                    | `services/`                    | Serviços de domínio para lógicas complexas de negócio (futuro)                                                                               |
|                    | `exceptions/`                  | Exceções específicas do domínio para violações de regras de negócio                                                                          |
| **Application**    | `ports/input/`                 | **Portas de entrada** (Use Case interfaces). Implementado: `PokedexUseCases.kt`                                                             |
|                    | `ports/output/`                | **Portas de saída** (Repository interfaces para infraestrutura)                                                                              |
|                    | `usecases/`                    | **Use Cases específicos** com responsabilidade única. Implementados: `FetchPokemonByIdUseCase.kt`, `GetPaginatedPokemonsUseCase.kt`        |
|                    | `dto/`                         | DTOs para comunicação entre camadas (request/response). Implementados: `PokemonDto.kt`, `SearchDto.kt`                                      |
|                    | `mappers/`                     | Mapeadores entre camadas (Domain ↔ Application)                                                                                              |
| **Infrastructure** | `adapters/`                    | **Adaptadores** que implementam portas. Implementado: `PokedexUseCasesAdapter.kt`                                                           |
|                    | `persistence/entities/`        | **Entities JPA** com anotações de persistência. Separadas do domínio: `PokemonEntity.kt`, `TypeEntity.kt`                                  |
|                    | `persistence/repositories/`    | Implementações concretas dos repositórios usando Spring Data JPA. Implementado: `JpaPokemonRepository.kt`                                   |
|                    | `persistence/mappers/`         | Mappers entre JPA entities e domain entities                                                                                                 |
|                    | `configurations/`              | Configurações do Spring Boot, CORS, OpenAPI. Implementados: `OpenApiConfiguration.kt`, `CorsConfiguration.kt`                              |
| **Interfaces**     | `controllers/`                 | **Controllers REST** que usam apenas interfaces (portas). Implementados: `PokedexController.kt`, `PokemonController.kt`                    |
|                    | `dto/request/`                 | DTOs específicos para requests da API REST                                                                                                   |
|                    | `dto/response/`                | DTOs específicos para responses da API REST                                                                                                  |
|                    | `validators/`                  | Validadores de entrada para requests HTTP                                                                                                    |
| **Shared**         | `exceptions/`                  | Exceções globais e handlers compartilhados entre camadas                                                                                     |
|                    | `utils/`                       | Utilitários compartilhados                                                                                                                   |
|                    | `constants/`                   | Constantes globais da aplicação                                                                                                              |

---

## 4. Implementação Concreta da Clean Architecture

### 4.1 Separação Total de Responsabilidades (Implementada)

#### **Domain (Núcleo de Negócio)**
```kotlin
// domain/valueobjects/PokemonId.kt - Puro, sem dependências
@JvmInline
value class PokemonId(val value: Long) {
    init {
        require(value > 0) { "Pokemon ID must be positive" }
    }
    
    fun isGeneration1(): Boolean = value in 1L..151L
    fun getGeneration(): Int = when(value) { /* regras */ }
}

// domain/entities/Pokemon.kt - Entidade pura de domínio
data class Pokemon(
    val id: PokemonId,
    val number: PokemonNumber,
    val name: String,
    // ... sem anotações JPA
)
```

#### **Application (Casos de Uso)**
```kotlin
// application/ports/input/PokedexUseCases.kt - Porta de entrada
interface PokedexUseCases {
    fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse
}

// application/usecases/pokedex/GetPaginatedPokemonsUseCase.kt
@Component
class GetPaginatedPokemonsUseCase(
    private val pokemonRepository: PokemonRepository // ← Interface do domínio
) {
    fun execute(page: Int, size: Int): PokedexListResponse {
        validatePaginationParameters(page, size)
        // Lógica de negócio pura
    }
}
```

#### **Infrastructure (Detalhes Técnicos)**
```kotlin
// infrastructure/adapters/PokedexUseCasesAdapter.kt
@Service
class PokedexUseCasesAdapter(
    private val getPaginatedPokemonsUseCase: GetPaginatedPokemonsUseCase
) : PokedexUseCases {
    override fun getPaginatedPokemons(page: Int, size: Int) = 
        getPaginatedPokemonsUseCase.execute(page, size)
}

// infrastructure/persistence/entities/PokemonEntity.kt - JPA
@Entity
@Table(name = "pokemons")
class PokemonEntity(
    @Id val id: Long,
    // ... anotações JPA
)
```

#### **Interfaces (Controllers)**
```kotlin
// interfaces/controllers/PokedexController.kt
@RestController
class PokedexController(
    private val pokedexUseCases: PokedexUseCases // ← Interface, não implementação
) {
    @GetMapping("/pokemons")
    fun getPokemons(@RequestParam page: Int, @RequestParam size: Int) =
        ResponseEntity.ok(pokedexUseCases.getPaginatedPokemons(page, size))
}
```

### 4.2 Regra de Dependência (Rigorosamente Aplicada)
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   INTERFACES    │───▶│   APPLICATION    │───▶│     DOMAIN      │
│                 │    │                  │    │    (PURO)       │
│ Controllers     │    │ Use Cases        │    │ Entities        │
│ dependem de     │    │ dependem de      │    │ Value Objects   │
│ PokedexUseCases │    │ PokemonRepository│    │ Repositories    │
│ (interface)     │    │ (interface)      │    │ (interfaces)    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         ↑                       ↑                       ↑
         │                       │                       │
┌─────────────────┐              │              ┌─────────────────┐
│ INFRASTRUCTURE  │──────────────┘              │ INFRASTRUCTURE  │
│                 │                             │                 │
│PokedexUseCases  │                             │ JpaPokemon...   │
│   Adapter       │                             │ RepositoryImpl  │
│ (implementação) │                             │ (implementação) │
└─────────────────┘                             └─────────────────┘
```

### 4.3 Inversão de Dependência Total
- **Domain** define contratos (`PokemonRepository`)
- **Infrastructure** implementa contratos (`JpaPokemonRepository`)
- **Application** usa abstrações, nunca implementações concretas
- **Interfaces** dependem de portas, nunca de adaptadores

### 4.4 Ports & Adapters (Hexagonal Architecture)

#### **Portas de Entrada (Input Ports)**
```kotlin
interface PokedexUseCases {
    fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse
}
```

#### **Portas de Saída (Output Ports)**
```kotlin
interface PokemonRepository {
    fun findAll(pageable: Pageable): Page<Pokemon>
}
```

#### **Adaptadores**
- **Entrada**: `PokedexUseCasesAdapter` implementa `PokedexUseCases`
- **Saída**: `JpaPokemonRepository` implementa `PokemonRepository`

---

## 5. Fluxo Principal (Clean Architecture)

### 5.1 Exemplo: Buscar Pokémon por ID

1. **Cliente Externo** faz requisição GET para `interfaces/controllers/PokemonController`
2. **Controller** valida parâmetros e chama `application/usecase/BuscarPokemonUseCase`
3. **Use Case** usa interface `domain/repositories/PokemonRepository` para buscar dados
4. **Repository Implementation** (`infrastructure/repository/JpaPokemonRepository`) executa consulta
5. **JPA Entity** (`infrastructure/persistence/entities/PokemonEntity`) é convertida para **Domain Entity** (`domain/entities/Pokemon`)
6. **Domain Entity** é convertida para **DTO** (`application/dto/response/PokemonDto`)
7. **Controller** retorna DTO como JSON para o cliente

### 5.2 Exemplo: Listar Pokédex

1. **Cliente** faz GET para `interfaces/controllers/PokedexController`
2. **Controller** chama `application/services/PokedexService`
3. **Service** orquestra múltiplas consultas e aplica lógica de apresentação
4. **Service** usa repositórios via interfaces do domínio
5. **Resposta** estruturada é retornada como `PokedexListResponse`

---

## 6. Tecnologias Utilizadas

* **Spring Boot:** Framework principal para APIs REST, injeção de dependências e configuração automática.
* **Kotlin:** Linguagem principal do projeto, rodando na JVM.
* **Spring Data JPA:** Para abstração e facilitação do acesso a dados relacionais (PostgreSQL) na camada de Infrastructure (implementação de repositórios).
* **PostgreSQL:** Banco de dados relacional robusto e extensível.
* **Jackson:** Para serialização/deserialização de JSON.
* **SLF4J + Logback:** Para logging estruturado e flexível.
* **Gradle:** Ferramenta de automação de build e gerenciamento de dependências.
* **OpenAPI (via Springdoc):** Para documentação automática de API (configurado em `Infrastructure/Configurations/OpenApiConfiguration.kt`).
* **(Potencialmente) Spring MediatR/Axon Framework:** Para implementações de Command/Query Bus e Domain Events.

---

## 7. Padrões e Boas Práticas Implementados

* **Clean Architecture**: Separação rigorosa de camadas com dependências apontando para o domínio
* **Inversão de Dependência (DIP)**: Interfaces definidas no domínio, implementadas na infraestrutura
* **Single Responsibility Principle**: Cada classe tem uma única responsabilidade bem definida
* **Separation of Concerns**: Entidades de domínio separadas de entities JPA
* **DTOs para Contratos**: DTOs específicos para comunicação entre camadas
* **Imutabilidade**: Value Objects e DTOs imutáveis sempre que possível
* **Repository Pattern**: Abstração da persistência através de interfaces

---

## 8. Implementações Recentes (Clean Architecture Avançada)

### 8.1 Refatoração de Separação de Responsabilidades

**ANTES (Violação Arquitetural):**
```kotlin
// ❌ Interface e implementação no mesmo arquivo
interface PokedexService {
    fun getPokemons(page: Int, size: Int): PokedexListResponse
}

@Service  
class PokedexServiceImpl(
    private val pokemonRepository: PokemonRepository
): PokedexService {
    // Implementação misturada
}
```

**DEPOIS (Clean Architecture):**
```kotlin
// ✅ Porta de entrada bem definida
interface PokedexUseCases {
    fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse
}

// ✅ Use Case específico com responsabilidade única
@Component
class GetPaginatedPokemonsUseCase(
    private val pokemonRepository: PokemonRepository
) {
    fun execute(page: Int, size: Int): PokedexListResponse {
        validatePaginationParameters(page, size)
        // Lógica de negócio pura
    }
}

// ✅ Adaptador que implementa a porta
@Service
class PokedexUseCasesAdapter(
    private val getPaginatedPokemonsUseCase: GetPaginatedPokemonsUseCase
) : PokedexUseCases
```

### 8.2 Value Objects Implementados

**PokemonId - Identificação com Validações:**
```kotlin
@JvmInline
value class PokemonId(val value: Long) {
    init {
        require(value > 0) { "Pokemon ID must be positive" }
        require(value <= MAX_POKEMON_ID) { "Pokemon ID cannot exceed $MAX_POKEMON_ID" }
    }
    
    fun isGeneration1(): Boolean = value in 1L..151L
    fun getGeneration(): Int = when (value) {
        in 1L..151L -> 1
        in 152L..251L -> 2
        // ... outras gerações
        else -> 0
    }
}
```

**PokemonNumber - Formatação e Validação:**
```kotlin
@JvmInline
value class PokemonNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Pokemon number cannot be blank" }
        require(isValidFormat(value)) { "Pokemon number must be 3-4 digits" }
    }
    
    fun formatForDisplay(): String = value.padStart(3, '0')
    fun toDisplayString(): String = "Nº${formatForDisplay()}"
    fun toNumeric(): Int = value.toIntOrNull() ?: throw IllegalStateException("Invalid format")
}
```

### 8.3 Testes Unitários Implementados

**Testes de Value Objects:**
```kotlin
class PokemonNumberTest {
    @Test
    fun `should format pokemon number correctly`() {
        val pokemonNumber = PokemonNumber("25")
        assertThat(pokemonNumber.formatForDisplay()).isEqualTo("025")
    }
    
    @Test
    fun `should throw exception for blank number`() {
        assertThrows<IllegalArgumentException> { PokemonNumber("") }
    }
}
```

**Testes de Use Cases:**
```kotlin
class GetPaginatedPokemonsUseCaseTest {
    @Mock private lateinit var pokemonRepository: PokemonRepository
    private lateinit var useCase: GetPaginatedPokemonsUseCase

    @Test
    fun `should return paginated pokemon list when valid parameters`() {
        // Given
        every { pokemonRepository.findAll(any()) } returns mockPage
        
        // When
        val result = useCase.execute(0, 10)
        
        // Then
        assertThat(result.pokemons).hasSize(1)
        verify(exactly = 1) { pokemonRepository.findAll(any()) }
    }
}
```

### 8.4 Benefícios Alcançados

| Aspecto | Antes | Depois |
|---------|-------|--------|
| **Acoplamento** | Alto (interface+impl juntos) | Baixo (separação total) |
| **Testabilidade** | Difícil (depende de Spring) | Fácil (mocks simples) |
| **Domínio** | Anêmico | Rico (Value Objects) |
| **Responsabilidades** | Misturadas | Single Responsibility |
| **Inversão de Dependência** | Parcial | Total |

---

## 9. Estrutura de Pastas Implementada

### 9.1 Domain (Núcleo Puro)

*   **`controllers/`**:
    *   Responsabilidade: Lidar com requisições HTTP, desserializar payloads, chamar a camada de Aplicação (Comandos/Consultas), serializar respostas.
    *   Exemplo: `PokemonController.kt` (Observado).
*   **`views/`**:
    *   Responsabilidade: (Se aplicável) Renderização de Server-Side Templates. Menos comum para BFFs puros que servem JSON.
*   **`mappers/`**:
    *   Responsabilidade: Transformar DTOs da camada de Aplicação em modelos de view ou formatos de resposta HTTP específicos, ou vice-versa para dados de entrada.
*   **`validators/`**:
    *   Responsabilidade: Validação de dados de entrada no nível da interface (ex: usando Bean Validation com anotações em DTOs de entrada).

### 7.2 Application (`com.pokedex.bff.application`)

*   **`services/`**:
    *   Responsabilidade: Orquestrar casos de uso mais complexos que podem envolver múltiplos comandos ou consultas, ou lógica de aplicação que não se encaixa em um Command/Query Handler simples. (Observado: `PokedexService.kt` pode ser refatorado para usar Comandos/Consultas).
*   **`commands/`**:
    *   Responsabilidade: Contém definições de Comandos (objetos imutáveis representando uma intenção de mudar o estado do sistema) e seus respectivos Handlers (classes que processam um tipo de Comando).
    *   Exemplo: `CreatePokemonCommand.kt`, `CreatePokemonCommandHandler.kt`.
*   **`queries/`**:
    *   Responsabilidade: Contém definições de Consultas (objetos imutáveis representando uma solicitação de dados) e seus Handlers (classes que processam uma Consulta e retornam DTOs).
    *   Exemplo: `GetPokemonByIdQuery.kt`, `GetPokemonByIdQueryHandler.kt`, retornando `PokemonDetailsDTO.kt`.
*   **`dto/`**:
    *   Responsabilidade: Objetos de Transferência de Dados para comunicação entre camadas. Usados como parâmetros para Comandos, entrada para Consultas e como dados de retorno de Consultas/Serviços. (Observado)

### 7.3 Domain (`com.pokedex.bff.domain`)

*   **`aggregates/`**:
    *   Responsabilidade: Raízes de Agregação e suas entidades internas. Um Agregado é uma unidade transacional e de consistência que encapsula lógica de negócio complexa.
    *   Exemplo: `PokemonAggregate.kt` (poderia conter `PokemonEntity` e `StatsEntity` como parte do mesmo agregado).
*   **`entities/`**:
    *   Responsabilidade: Objetos de domínio com identidade que não são raízes de agregado, mas fazem parte de um. Se não usar o conceito de Agregado explicitamente, esta pasta contém as entidades principais. (Observado: `PokemonEntity.kt`, `TypeEntity.kt`).
*   **`valueobjects/`**:
    *   Responsabilidade: Objetos imutáveis que descrevem características de entidades ou agregados, sem identidade própria (ex: `Money.kt`, `Address.kt`, `SpriteDetailsVO.kt`). (Observado em `application/`, conceitualmente melhor aqui).
*   **`events/`**:
    *   Responsabilidade: Eventos de Domínio que representam algo significativo que aconteceu no domínio.
    *   Exemplo: `PokemonRegisteredEvent.kt`, `PokemonStatsUpdatedEvent.kt`.
*   **`exceptions/`**:
    *   Responsabilidade: Exceções específicas do domínio que representam violações de regras de negócio.
    *   Exemplo: `InvalidPokemonStatsException.kt`.
*   **`factories/`**:
    *   Responsabilidade: Encapsular a lógica de criação de Agregados ou Entidades complexas, garantindo que sejam criados em um estado válido.
    *   Exemplo: `PokemonFactory.kt`.
*   **`repositories/`**:
    *   Responsabilidade: Interfaces (contratos) para persistência de Agregados/Entidades. Abstraem a tecnologia de persistência. (Observado: `PokemonRepository.kt` como interface).
*   **`specifications/`**:
    *   Responsabilidade: Encapsular lógica de consulta de domínio de forma reutilizável e combinável, expressando critérios de seleção.
    *   Exemplo: `HighAttackPokemonSpecification.kt`.

### 7.4 Infrastructure (`com.pokedex.bff.infrastructure`)

*   **`repositories/`**:
    *   Responsabilidade: Implementações concretas das interfaces de `Domain/Repositories/`, geralmente usando um ORM como Spring Data JPA.
    *   Exemplo: `PostgresPokemonRepositoryImpl.kt` (nome hipotético).
*   **`persistence/`**:
    *   Responsabilidade: Configurações de persistência (ex: EntityManager, DataSources), scripts de migração de banco de dados (ex: Flyway, Liquibase), e lógica de seed de dados. (Observado: `seeder/` se encaixa aqui).
*   **`events/`**:
    *   Responsabilidade: Implementação da infraestrutura para publicação e consumo de eventos (ex: configuração de um Message Broker como Kafka/RabbitMQ, ou um EventBus síncrono/assíncrono local).
*   **`services/`**:
    *   Responsabilidade: Implementações de clientes para serviços externos (ex: cliente HTTP para uma API de terceiros, serviço de envio de email) ou outros serviços de infraestrutura.
*   **`configurations/`**:
    *   Responsabilidade: Configurações do Spring Boot, segurança, CORS, serialização JSON, OpenAPI, etc. (Observado).
*   **`utils/`**:
    *   Responsabilidade: Utilitários específicos para a camada de infraestrutura (ex: `JsonFile.kt` usado pelo seeder). (Observado).

### 7.5 Shared (`com.pokedex.bff.shared`)

*   **`utils/`**:
    *   Responsabilidade: Utilitários verdadeiramente genéricos, sem dependências de outras camadas, que podem ser usados em qualquer lugar.
*   **`constants/`**:
    *   Responsabilidade: Constantes globais da aplicação (ex: nomes de filas, chaves de configuração).
*   **`events/`**:
    *   Responsabilidade: (Opcional) Definições de eventos de integração que cruzam contextos delimitados, ou tipos de eventos base.
*   **`exceptions/`**:
    *   Responsabilidade: Classes base para exceções, handlers globais de exceção. (Observado: `GlobalExceptionHandler.kt`).

### 7.6 Tests (`src/test/kotlin/com.pokedex.bff`)

A estrutura de testes deve espelhar a estrutura de `src/main/kotlin/` para clareza, organizada por tipo de teste:
*   **`unit/`**: Testes para classes individuais, focando em sua lógica interna. Mocks/Stubs para dependências externas.
    *   Ex: `unit/domain/entities/PokemonEntityTest.kt`, `unit/application/commands/CreatePokemonCommandHandlerTest.kt`.
*   **`integration/`**: Testes que verificam a colaboração entre múltiplas classes/componentes. Podem envolver um banco de dados em memória ou Testcontainers.
    *   Ex: `integration/application/PokemonAppServiceIntegrationTest.kt` (testando um serviço de aplicação com seu repositório real ou mockado).
    *   Ex: `integration/infrastructure/repositories/PokemonRepositoryIntegrationTest.kt` (testando a implementação do repositório com o banco).
*   **`acceptance/` (ou `e2e/`)**: Testes de ponta a ponta que simulam o comportamento do usuário/cliente, geralmente fazendo requisições HTTP aos controllers e verificando as respostas.
    *   Ex: `acceptance/interfaces/controllers/PokemonControllerAcceptanceTest.kt`.
*   **`mocks/`**: (Opcional) Utilitários para criar mocks, stubs ou classes de teste fakes que podem ser reutilizadas.

*Nota: A implementação real pode começar com uma estrutura mais simples e evoluir para esta forma mais detalhada conforme a complexidade do projeto aumenta.*

---

## 8. Estrutura de Pastas (Idealizada e Detalhada)

```plaintext
com.pokedex.bff
├── application
│   ├── dto           // DTOs para request/response da aplicação
│   │   ├── request   // DTOs de entrada
│   │   └── response  // DTOs de saída (PokemonDto.kt, SearchDto.kt, etc.)
│   ├── services      // Services de aplicação (PokedexService.kt)
│   └── usecase       // Use Cases específicos (BuscarPokemonUseCase.kt)
│
├── domain
│   ├── entities      // Entidades puras de domínio (Pokemon.kt, Type.kt, Species.kt, etc.)
│   ├── exceptions    // Exceções específicas do domínio
│   ├── repository    // Interfaces de repositório (PokemonRepository.kt)
│   ├── repositories  // Interfaces adicionais de repositório
│   └── valueobjects  // Value Objects (SpritesVO.kt, OfficialArtworkSpritesVO.kt, etc.)
│
├── infrastructure
│   ├── config        // Configurações de beans (UseCaseConfig.kt)
│   ├── configurations// Configurações do Spring (OpenApiConfiguration.kt, CorsConfiguration.kt)
│   ├── migration     // Scripts de migração (mantido para futuras migrações)
│   ├── persistence
│   │   └── entities  // Entities JPA (PokemonEntity.kt, TypeEntity.kt, etc.)
│   └── repository    // Implementações de repositório (JpaPokemonRepository.kt)
│
├── interfaces
│   ├── controllers   // Controllers REST (PokedexController.kt, PokemonController.kt)
│   └── dto          // DTOs específicos da interface REST (PokemonDto.kt)
│
├── shared
│   └── exceptions   // Exceções globais e handlers compartilhados
│
└── PokedexBffApplication.kt // Ponto de entrada do Spring Boot
```

---

## 9. Benefícios da Refatoração Realizada

### 9.1 Antes da Refatoração (Problemas)
- ❌ Pastas duplicadas: `interface/` e `interfaces/`
- ❌ Entities JPA na camada de domínio
- ❌ Value Objects na camada application  
- ❌ Arquivos `.keep` desnecessários poluindo o projeto
- ❌ Utilitários de seeder não utilizados na infrastructure
- ❌ Confusão entre entities e models no domínio

### 9.2 Depois da Refatoração (Soluções)
- ✅ **Estrutura unificada**: Uma única pasta `interfaces/`
- ✅ **Separação clara**: Domain entities puros, JPA entities na infrastructure
- ✅ **Clean Architecture**: Value Objects no domínio onde pertencem
- ✅ **Projeto limpo**: Removidos arquivos desnecessários
- ✅ **Infrastructure focada**: Apenas código realmente usado
- ✅ **Nomenclatura consistente**: Entities no domínio, sem duplicações

### 9.3 Vantagens Arquiteturais
- 🎯 **Testabilidade**: Domain sem dependências externas é facilmente testável
- 🔄 **Flexibilidade**: Troca de tecnologias de persistência sem afetar domínio
- 📦 **Manutenibilidade**: Responsabilidades claras facilitam manutenção
- 🚀 **Escalabilidade**: Estrutura preparada para crescimento do projeto
- 🛡️ **Robustez**: Regras de negócio protegidas de mudanças tecnológicas

---

## 10. Próximos Passos Recomendados

1. **Testes**: Implementar testes unitários para entities de domínio
2. **Use Cases**: Expandir use cases para operações CRUD completas
3. **Validation**: Adicionar validações de domínio nas entities
4. **Error Handling**: Implementar exceções específicas de domínio
5. **Documentation**: Manter documentação alinhada com evolução do código
---

*Documento atualizado após refatoração para Clean Architecture em 22/09/2025*
