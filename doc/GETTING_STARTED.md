# Como Começar

Siga estas instruções para configurar e executar o Pokedex BFF em seu ambiente de desenvolvimento local.

## Arquitetura do Projeto

Este projeto implementa **DDD + Clean Architecture** com separação clara de responsabilidades, isolamento de detalhes técnicos e foco em testabilidade e evolução.

### Estrutura das Camadas

```
src/main/kotlin/com/pokedex/bff/
├── domain/           # Núcleo do negócio (entidades, value objects, agregados, repositórios)
│   ├── pokemon/      # Agregado Pokémon: entidades, value objects, repositório
│   ├── trainer/      # Agregado Trainer
│   └── shared/       # Tipos utilitários, value objects e exceções genéricas
├── application/      # Casos de uso (interfaces e implementações), DTOs
│   ├── usecase/      # Interfaces de casos de uso
│   ├── interactor/   # Implementações dos casos de uso
│   └── dtos/         # DTOs de entrada/saída
├── adapters/         # Entrada (REST/controllers) e saída (persistência, integrações externas)
│   ├── input/web/controller/           # Controllers REST
│   └── output/persistence/repository/  # Adapters de persistência (implementam repositórios do domínio)
├── infrastructure/   # Configurações técnicas, beans, migrações, segurança
│   ├── config/       # Beans, providers, DI
│   ├── migration/    # Scripts de migração
│   └── security/     # Configuração de segurança
└── test/             # Testes automatizados (unitários, integração, etc.)
```

- **Domain**: Núcleo puro, sem dependências técnicas/frameworks. Contém entidades, value objects, agregados e interfaces de repositório.
- **Application**: Casos de uso (interfaces e implementações), DTOs, orquestração de entidades/agregados.
- **Adapters**: Controllers REST, mapeadores, adapters de persistência (implementam interfaces do domínio), integrações externas.
- **Infrastructure**: Configurações técnicas, beans, providers, migrações, segurança. Isola detalhes como banco de dados e Spring.

## Configuração do Ambiente

### Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas:
* [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (inclui Docker e Docker Compose)
* **GNU Make** (ou `make` no seu sistema, geralmente pré-instalado em sistemas Unix/Linux/macOS).

## Comandos Principais

```bash
./gradlew bootRun           # Inicia aplicação
./gradlew test              # Executa testes
./gradlew build             # Build completo
./gradlew bootJar           # Gera JAR executável
```

Consulte o README.md e os arquivos em `doc/` para mais detalhes sobre arquitetura, exemplos e guias de cada camada.


## Estrutura de Testes

Os testes ficam em `src/test/kotlin/com/pokedex/bff/` e podem ser organizados em subpastas para diferentes tipos:

```
src/test/kotlin/com/pokedex/bff/
├── unit/           # Testes unitários (foco em classes isoladas)
├── integration/    # Testes de integração (foco em integração entre camadas, banco, etc.)
└── PokedexBffApplicationTests.kt  # Teste de contexto principal
```

**Boas práticas:**
- Separe testes unitários e de integração para facilitar manutenção e execução.
- Use nomes claros para as classes de teste e métodos.
- Utilize mocks/stubs em testes unitários e recursos reais em integração.

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

### Ambiente recomendado para desenvolvimento

Para rodar o BFF localmente com o banco de dados em container (fluxo mais produtivo para desenvolvimento, debug e hot reload):

```bash
make dev-up
```

Esse comando:
- Sobe o banco de dados PostgreSQL em container (porta 5434)
- Roda o BFF localmente (com recarga automática)
- Permite debugar e editar o código em tempo real

> **Recomendado:** Use sempre `make dev-up` para desenvolvimento local. O BFF roda na sua máquina, o banco roda isolado em container.

### Outros comandos úteis

Se o banco já estiver rodando e quiser apenas subir o BFF:
```bash
make run-bff
```

Para ver todos os comandos disponíveis:
```bash
make
```

```bach
===================================================================
                                 Comandos do Makefile para Pokedex BFF
===================================================================
    make help                   - Exibe esta mensagem de ajuda.
    make dev-up                 - Sobe banco em container e roda o BFF localmente (modo recomendado).
    make dev-down               - Para o ambiente de desenvolvimento (banco e BFF).
    make dev-status             - Mostra status dos serviços.
    make dev-logs               - Exibe logs em tempo real.
    make clean-all              - Para tudo, limpa DB, Gradle e contêineres.
    make run-bff                - Executa o BFF (banco já deve estar rodando).
    make clean-bff              - Executa './gradlew clean'.
    make db-only-up             - Sobe apenas o banco isolado.
    make db-only-down           - Para o banco isolado.
    make db-only-shell          - Abre shell psql no banco isolado.
    make validate-db            - Valida estrutura e dados do banco.
    make generate-sql-data      - Gera SQL a partir dos JSONs.
    make lint                   - Roda lint/análise estática.
    make lint-fix               - Corrige formatação do código.
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
