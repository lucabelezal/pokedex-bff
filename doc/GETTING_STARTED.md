# Como Começar

Siga estas instruções para configurar e executar o Pokedex BFF em seu ambiente de desenvolvimento local.

## Arquitetura do Projeto

Este projeto implementa **DDD + Clean Architecture** com separação total de responsabilidades e alta testabilidade:

### Estrutura das Camadas (Setembro 2025)

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

## Configuração do Ambiente

### Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas:
* [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (inclui Docker e Docker Compose)
* **GNU Make** (ou `make` no seu sistema, geralmente pré-instalado em sistemas Unix/Linux/macOS; para Windows, você pode usar WSL ou ferramentas como Chocolatey para instalar `make`).

## Comandos Principais

```bash
./gradlew bootRun           # Inicia aplicação
./gradlew test              # Executa testes
./gradlew build             # Build completo
./gradlew bootJar           # Gera JAR executável
```

Consulte o README.md e os arquivos em `doc/` para mais detalhes sobre arquitetura, exemplos e guias de cada camada.

## Trabalhando com as Entidades

### Separação de Entidades

O projeto utiliza duas representações distintas para as entidades:

#### Entidades de Domínio (`domain/entities/`)
```kotlin
// Exemplo: Pokemon.kt
data class Pokemon(
    val id: Long?,
    val name: String,
    val height: BigDecimal,
    val weight: BigDecimal,
    // ... outros campos sem anotações
)
```
- **Propósito**: Representar conceitos puros de negócio
- **Características**: Sem anotações JPA, Jackson ou outras dependências
- **Uso**: Lógica de negócio e casos de uso

#### Entidades JPA (`infrastructure/persistence/entities/`)
```kotlin
// Exemplo: PokemonJpaEntity.kt
@Entity
@Table(name = "pokemons")
data class PokemonJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "name", nullable = false)
    val name: String,
    
    @Convert(converter = SpritesConverter::class)
    val sprites: SpritesDto?
    // ... outros campos com anotações JPA
)
```
- **Propósito**: Mapeamento objeto-relacional
- **Características**: Anotações JPA/Hibernate para persistência
- **Uso**: Operações de banco de dados

### DTOs de Interface (`interfaces/dto/`)

#### DTOs de Sprites
```kotlin
// SpritesDto.kt
@JsonIgnoreProperties(ignoreUnknown = true)
data class SpritesDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    
    @JsonProperty("official-artwork")
    val officialArtwork: OfficialArtworkSpritesDto?
)
```
- **Propósito**: Serialização/deserialização de dados complexos
- **Uso**: Campos JSONB e comunicação com APIs externas
- **Benefício**: Isolamento da lógica de serialização

## Boas Práticas de Desenvolvimento

### Regras da Clean Architecture

1. **Dependências sempre apontam para dentro**: Camadas externas podem depender de internas, mas nunca o contrário
2. **Entidades de domínio são puras**: Sem anotações de frameworks ou bibliotecas externas
3. **Use casos orquestram**: A lógica de aplicação fica nos use cases, não nos controllers
4. **DTOs apenas na interface**: Serialização/deserialização isolada na camada de interface
5. **Mapeamento entre camadas**: Use mappers para converter entre entidades de domínio e JPA

### Fluxo de Desenvolvimento Recomendado

1. **Modele o domínio**: Comece com as entidades de domínio puras
2. **Defina casos de uso**: Implemente a lógica de aplicação
3. **Crie entidades JPA**: Mapeie para o banco de dados na infraestrutura
4. **Implemente controllers**: Exponha APIs na camada de interface
5. **Configure DTOs**: Para serialização de dados complexos

### Estrutura de Pastas por Feature

```
src/main/kotlin/com/pokedex/bff/
├── domain/entities/Pokemon.kt
├── application/usecase/GetPokemonUseCase.kt
├── infrastructure/persistence/
│   ├── entities/PokemonJpaEntity.kt
│   └── repositories/PokemonJpaRepository.kt
└── interfaces/
    ├── controllers/PokemonController.kt
    └── dto/PokemonResponseDto.kt
```

## Executando o Projeto

Se o banco de dados já estiver rodando e populado, você pode iniciar apenas a aplicação BFF:

```bash
make run-bff
```

### Caso precise ver as opções do comando make
Rode o comando abaixo:
```bash
make
```
```bach
===================================================================
                 Comandos do Makefile para Pokedex BFF
===================================================================
  make help                   - Exibe esta mensagem de ajuda.

  make dev-setup              - Configura e inicia o ambiente (Linux/macOS).
  make dev-setup-for-windows - Configura e inicia o ambiente (Git Bash/WSL no Windows).

  make start-db               - Inicia o banco PostgreSQL com Docker Compose.
  make stop-db                - Para o contêiner do banco.
  make clean-db               - Remove o banco e os volumes (apaga os dados!).
  make load-data              - Executa o BFF e carrega os dados JSON.
  make run-bff                - Executa o BFF sem importar dados.
  make clean-bff              - Executa './gradlew clean'.

  make clean-all              - Para tudo, limpa DB, Gradle e contêineres.
  make force-remove-db-container - Força a remoção do contêiner 'pokedex-db'.
  make deep-clean-gradle      - Limpa caches e artefatos do Gradle.
===================================================================
```

## Documentação Complementar

Para informações mais detalhadas sobre o projeto, consulte:

- **[ARCHITECTURE.md](./ARCHITECTURE.md)**: Documentação completa da Clean Architecture implementada
- **[SCHEMA.md](./SCHEMA.md)**: Esquema detalhado do banco de dados e mapeamento de entidades
- **[OVERVIEW.md](./OVERVIEW.md)**: Visão geral do projeto e suas funcionalidades
- **[TECHNOLOGIES.md](./TECHNOLOGIES.md)**: Stack tecnológico utilizado
- **[SWAGGER.md](./SWAGGER.md)**: Documentação da API REST

### Próximos Passos

1. **Explore a estrutura**: Familiarize-se com a organização das camadas
2. **Leia a documentação**: Consulte os documentos de arquitetura e schema
3. **Execute os testes**: Verifique se tudo está funcionando corretamente
4. **Implemente features**: Siga os padrões estabelecidos da Clean Architecture
