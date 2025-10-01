# Documentação do Esquema do Banco de Dados Pokémon

Este documento descreve o esquema de banco de dados relacional para armazenar informações sobre Pokémon, suas características, evoluções, tipos, habilidades, regiões e grupos de ovos. O banco de dados de destino é PostgreSQL e está implementado seguindo os princípios da **Clean Architecture**.


## Contexto Arquitetural (Atualizado - Setembro 2025)

O projeto utiliza **Clean Architecture** com separação total entre domínio e infraestrutura:

### 🎯 **Separação Total de Responsabilidades**

- **Domain Entities** (`src/main/kotlin/com/pokedex/bff/domain/entities/`): Entidades puras de negócio **sem dependências externas**
- **Value Objects** (`src/main/kotlin/com/pokedex/bff/domain/valueobjects/`): ✅ **Implementados** - `PokemonId`, `PokemonNumber` com validações
- **Domain Repositories** (`src/main/kotlin/com/pokedex/bff/domain/repositories/`): Interfaces que definem contratos de persistência
- **JPA Entities** (`src/main/kotlin/com/pokedex/bff/infrastructure/persistence/entities/`): Mapeamento de tabelas **separado do domínio**
- **Repository Adapters** (`src/main/kotlin/com/pokedex/bff/infrastructure/persistence/repositories/`): Implementações que conectam JPA ao domínio
- **Use Cases** (`src/main/kotlin/com/pokedex/bff/application/usecases/`): ✅ **Implementados** - Cases específicos com responsabilidade única
- **Ports & Adapters** (`src/main/kotlin/com/pokedex/bff/application/ports/` + `infrastructure/adapters/`): ✅ **Implementados**


### ✅ **Benefícios da Separação**

- **Domain Purity**: Zero dependências de frameworks no domínio
- **Testabilidade**: Value Objects e Use Cases testáveis unitariamente
- **Flexibilidade**: Troca de tecnologias sem afetar o domínio
- **Inversão de Dependência**: Infraestrutura depende do domínio, não o contrário

### 🔄 **Mapeamento Domínio ↔ Infraestrutura**

```kotlin
// Domain Entity (Pura)
data class Pokemon(
    val id: PokemonId,           // ← Value Object
    val number: PokemonNumber,   // ← Value Object  
    val name: String,
    // ... sem anotações JPA
)

// JPA Entity (Infraestrutura)
@Entity
@Table(name = "pokemons")
class PokemonJpaEntity(
    @Id val id: Long,
    @Column val number: String?,
    @Column val name: String,
    // ... anotações JPA/Hibernate
)

// Mapper (Infraestrutura → Domínio)
class PokemonJpaMapper {
    fun toDomain(jpa: PokemonJpaEntity): Pokemon {
        return Pokemon(
            id = PokemonId(jpa.id),
            number = PokemonNumber.fromString(jpa.number),
            name = jpa.name
        )
    }
}
```

## Visão Geral do Esquema

O esquema é composto por **13 tabelas** principais e de junção (a tabela `evolution_links` foi incorporada como um campo JSON em `evolution_chains`). Elas são projetadas para manter os dados normalizados, minimizando redundância e garantindo a integridade referencial. As tabelas se relacionam para permitir consultas complexas e eficientes sobre o universo Pokémon.

As 13 tabelas são:

1.  `regions`
2.  `types`
3.  `egg_groups`
4.  `species`
5.  `generations`
6.  `abilities`
7.  `pokemons`
8.  `stats`
9.  `pokemon_types` (Tabela de Junção)
10. `pokemon_abilities` (Tabela de Junção com atributo extra)
11. `pokemon_egg_groups` (Tabela de Junção)
12. `evolution_chains` (Contém detalhes da evolução em JSON)
13. `pokemon_weaknesses` (Tabela de Junção)

## Implementação Arquitetural (Clean Architecture)

### 🎯 **Separação Domain vs Infrastructure**

#### **Domain Entities (Puras)**
```kotlin
// domain/entities/Pokemon.kt - SEM dependências externas
data class Pokemon(
    val id: PokemonId,              // Value Object
    val number: PokemonNumber,      // Value Object
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<Type>,
    val stats: Stats,
    val species: Species
) {
    // Apenas lógica de negócio
    fun isValid(): Boolean = number.isValid() && name.isNotBlank()
    fun getMainType(): Type = types.first()
    fun hasType(type: Type): Boolean = types.contains(type)
}
```

#### **JPA Entities (Infraestrutura)**
```kotlin
// infrastructure/persistence/entities/PokemonJpaEntity.kt
@Entity
@Table(name = "pokemons")
data class PokemonJpaEntity(
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "number", length = 10)
    val number: String? = null,

    @Column(name = "name", nullable = false, length = 100)
    val name: String = "",

    @Column(name = "height")
    val height: Int? = null,

    @Column(name = "weight")
    val weight: Int? = null,

    // Relacionamentos JPA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    val species: SpeciesJpaEntity? = null,

    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL])
    val stats: List<StatJpaEntity> = emptyList(),

    // JSON para dados complexos
    @Type(JsonType::class)
    @Column(name = "sprites", columnDefinition = "jsonb")
    val sprites: JsonNode? = null
)
```

#### **Value Objects (Domain)**
```kotlin
// domain/valueobjects/PokemonId.kt - Rico em regras de negócio
@JvmInline
value class PokemonId(val value: Long) {
    init {
        require(value > 0) { "Pokemon ID must be positive" }
        require(value <= MAX_POKEMON_ID) { "Pokemon ID exceeds maximum" }
    }
    
    fun isGeneration1(): Boolean = value in 1L..151L
    fun getGeneration(): Int = when (value) {
        in 1L..151L -> 1
        in 152L..251L -> 2
        // ... outras gerações
        else -> 0
    }
    
    companion object {
        const val MAX_POKEMON_ID = 1010L
    }
}

// domain/valueobjects/PokemonNumber.kt - Formatação e validação
@JvmInline
value class PokemonNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Pokemon number cannot be blank" }
        require(isValidFormat(value)) { "Invalid Pokemon number format" }
    }
    
    fun formatForDisplay(): String = value.padStart(3, '0')
    fun toDisplayString(): String = "Nº${formatForDisplay()}"
    fun isGeneration1(): Boolean = toNumeric() in 1..151
    
    private fun isValidFormat(number: String): Boolean =
        number.matches(Regex("\\d{1,4}")) && number.toIntOrNull()?.let { it > 0 } == true
}
```

### 🔄 **Repository Pattern (Domain-First)**

#### **Domain Repository Interface**
```kotlin
// domain/repositories/PokemonDomainRepository.kt
interface PokemonDomainRepository {
    fun findById(id: PokemonId): Pokemon?
    fun findAll(page: Int, size: Int): List<Pokemon>
    fun save(pokemon: Pokemon): Pokemon
    fun existsById(id: PokemonId): Boolean
}
```

#### **Infrastructure Implementation**
```kotlin
// infrastructure/adapters/PokemonRepositoryAdapter.kt
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
}
```
- **Regras**: Responsáveis apenas pelo mapeamento objeto-relacional

#### DTOs de Interface (`interfaces/dto/`)
- **Propósito**: Serialização/deserialização para comunicação externa
- **Características**: Contêm anotações Jackson para JSON
- **Subpacotes**:
  - `sprites/`: DTOs para dados JSON complexos (ex: `SpritesDto.kt`, `OfficialArtworkSpritesDto.kt`)
- **Regras**: Isolam preocupações de serialização da lógica de domínio

### Campos JSON (sprites)
Os campos `sprites` (JSONB) são mapeados através de DTOs especializados na camada de interface. Isso garante que:
- A lógica de serialização não contamine as entidades de domínio
- A estrutura JSON seja bem definida e tipada
- Mudanças no formato de serialização não afetem a lógica de negócio

## Entidades (Tabelas) e Seus Relacionamentos

### 1. `regions`

Armazena informações sobre as diferentes regiões do mundo Pokémon.

*   **Finalidade:** Categorizar Pokémon e Gerações por sua região de origem.
*   **Colunas:**
    *   `id` (BIGINT PRIMARY KEY): Identificador único da região.
    *   `name` (VARCHAR(255) NOT NULL UNIQUE): Nome da região (ex: 'Kanto', 'Johto').
*   **Relacionamentos:**
    *   Uma região pode estar associada a múltiplas `generations` (`generations.region_id` FK).
    *   Uma região pode estar associada a múltiplos `pokemons` (`pokemons.region_id` FK).

### 2. `types`

Armazena informações sobre os tipos elementais dos Pokémon.

*   **Finalidade:** Definir os tipos dos Pokémon, suas fraquezas e resistências.
*   **Colunas:**
    *   `id` (BIGINT PRIMARY KEY): Identificador único do tipo.
    *   `name` (VARCHAR(255) NOT NULL UNIQUE): Nome do tipo (ex: 'Fogo', 'Água').
    *   `color` (VARCHAR(7)): Código de cor hexadecimal associado ao tipo (ex: '#EE8130').
*   **Relacionamentos:**
    *   Associado a `pokemons` através da tabela de junção `pokemon_types`.
    *   Associado a `pokemons` (como fraqueza) através da tabela de junção `pokemon_weaknesses`.

### 3. `egg_groups`

Armazena informações sobre os grupos de ovos aos quais os Pokémon pertencem para fins de reprodução.

*   **Finalidade:** Agrupar Pokémon com características reprodutivas compatíveis.
*   **Colunas:**
    *   `id` (BIGINT PRIMARY KEY): Identificador único do grupo de ovos.
    *   `name` (VARCHAR(255) NOT NULL UNIQUE): Nome do grupo de ovos (ex: 'Amorfo', 'Dragão').
*   **Relacionamentos:**
    *   Associado a `pokemons` através da tabela de junção `pokemon_egg_groups`.

### 4. `species`

Armazena informações sobre as espécies gerais de Pokémon, que podem ter várias formas ou evoluções.

*   **Finalidade:** Categorizar Pokémon por sua espécie base e informações de Pokédex.
*   **Colunas:**
    *   `id` (BIGINT PRIMARY KEY): Identificador único da espécie.
    *   `pokemon_number` (VARCHAR(10)): Número da Pokédex nacional da espécie (ex: '0001'). Conforme esquema SQL/DBML, não possui constraint UNIQUE explícita.
    *   `name` (VARCHAR(255) NOT NULL): Nome da espécie (ex: 'Bulbasaur').
    *   `species_en` (VARCHAR(255)): Descrição da espécie em inglês (ex: 'Seed Pokémon').
    *   `species_pt` (VARCHAR(255)): Descrição da espécie em português (ex: 'Pokémon Semente').
*   **Relacionamentos:**
    *   Uma espécie é referenciada por um ou mais `pokemons` (`pokemons.species_id` FK), representando diferentes formas ou o Pokémon base.

### 5. `generations`

Armazena informações sobre as diferentes gerações de Pokémon.

*   **Finalidade:** Agrupar Pokémon, habilidades e outras características por sua geração de introdução.
*   **Colunas:**
    *   `id` (BIGINT PRIMARY KEY): Identificador único da geração.
    *   `name` (VARCHAR(255) NOT NULL UNIQUE): Nome da geração (ex: 'Geração I').
    *   `region_id` (BIGINT NOT NULL): Chave estrangeira referenciando `regions.id`.
*   **Relacionamentos:**
    *   Cada geração pertence a uma `regions` (`region_id` FK).
    *   Uma geração introduz vários `pokemons` (`pokemons.generation_id` FK).
    *   Uma geração pode introduzir várias `abilities` (`abilities.introduced_generation_id` FK).

### 6. `abilities`

Armazena informações sobre as habilidades especiais dos Pokémon.

*   **Finalidade:** Detalhar as habilidades que os Pokémon podem possuir.
*   **Colunas:**
    *   `id` (BIGINT PRIMARY KEY): Identificador único da habilidade.
    *   `name` (VARCHAR(255) NOT NULL UNIQUE): Nome da habilidade.
    *   `description` (TEXT): Descrição detalhada da habilidade.
    *   `introduced_generation_id` (BIGINT): Chave estrangeira referenciando `generations.id`, indicando em qual geração a habilidade foi introduzida.
*   **Relacionamentos:**
    *   Uma habilidade é introduzida em uma `generations` (`introduced_generation_id` FK).
    *   Associada a `pokemons` através da tabela de junção `pokemon_abilities`.

### 7. `pokemons`

Armazena as informações principais de cada Pokémon individual (incluindo suas formas).

*   **Finalidade:** Entidade central para todas as informações detalhadas sobre os Pokémon.
*   **Colunas:**
    *   `id` (BIGINT PRIMARY KEY): Identificador único do Pokémon (para formas específicas, etc.).
    *   `number` (VARCHAR(10)): Número da Pokédex nacional (ex: '0001', '0025-alola'). Conforme esquema SQL/DBML, não possui constraint UNIQUE ou NOT NULL explícita.
    *   `name` (VARCHAR(255) NOT NULL): Nome do Pokémon (ex: 'Bulbasaur', 'Charizard Mega Charizard Y', 'Pikachu Alola Form').
    *   `stats_id` (BIGINT UNIQUE): Chave estrangeira referenciando `stats.id` (relacionamento 1:1 obrigatório ou opcional dependendo da modelagem, aqui UNIQUE sugere 1:1).
    *   `generation_id` (BIGINT NOT NULL): Chave estrangeira referenciando `generations.id`.
    *   `species_id` (BIGINT NOT NULL): Chave estrangeira referenciando `species.id`.
    *   `region_id` (BIGINT, NULLABLE): Chave estrangeira referenciando `regions.id` (para formas regionais, por exemplo).
    *   `evolution_chain_id` (BIGINT, NULLABLE): Chave estrangeira referenciando `evolution_chains.id`.
    *   `height` (NUMERIC(5, 2)): Altura do Pokémon em metros.
    *   `weight` (NUMERIC(6, 2)): Peso do Pokémon em quilogramas.
    *   `description` (TEXT): Descrição de Pokédex do Pokémon.
    *   `sprites` (JSONB): Objeto JSON que armazena URLs para diferentes sprites do Pokémon. Mapeado através de DTOs especializados (`SpritesDto`, `OfficialArtworkSpritesDto`) na camada de interface para garantir tipagem forte e isolamento da lógica de serialização.
    *   `gender_rate_value` (INT): Taxa de gênero (informações sobre proporção de gênero, ex: 0-8, -1 para sem gênero).
    *   `egg_cycles` (INT): Número de ciclos de ovos para chocar.
*   **Relacionamentos:**
    *   Possui um conjunto de `stats` (`stats_id` FK para `stats.id`, relação 1:1).
    *   Pertence a uma `generations` (`generation_id` FK).
    *   É de uma `species` (`species_id` FK).
    *   Pode pertencer a uma `regions` (`region_id` FK).
    *   Pode pertencer a uma `evolution_chains` (`evolution_chain_id` FK).
    *   Muitos-para-Muitos com `types` via `pokemon_types`.
    *   Muitos-para-Muitos com `abilities` via `pokemon_abilities`.
    *   Muitos-para-Muitos com `egg_groups` via `pokemon_egg_groups`.
    *   Muitos-para-Muitos com `types` (como fraquezas) via `pokemon_weaknesses`.
    *   Pode ser origem ou destino em `evolution_links`.

### 8. `stats`

Armazena os atributos de batalha (HP, Attack, Defense, etc.) de cada Pokémon.

*   **Finalidade:** Fornecer os valores dos atributos base para cada Pokémon.
*   **Colunas:**
    *   `id` (BIGINT PRIMARY KEY): Identificador único do conjunto de atributos.
    *   `total` (INT): Soma total dos atributos base.
    *   `hp` (INT): Pontos de vida base.
    *   `attack` (INT): Atributo de ataque físico base.
    *   `defense` (INT): Atributo de defesa física base.
    *   `sp_atk` (INT): Atributo de ataque especial base.
    *   `sp_def` (INT): Atributo de defesa especial base.
    *   `speed` (INT): Atributo de velocidade base.
*   **Relacionamentos:**
    *   Cada conjunto de `stats` pertence a um único `pokemons` (`pokemons.stats_id` referencia `stats.id` em uma relação 1:1).

### 9. `pokemon_types`

Tabela de junção para o relacionamento Muitos-para-Muitos entre `pokemons` e `types`.

*   **Finalidade:** Registrar os tipos elementais de um Pokémon (um Pokémon pode ter 1 ou 2 tipos).
*   **Colunas:**
    *   `pokemon_id` (BIGINT NOT NULL): Chave estrangeira referenciando `pokemons.id`.
    *   `type_id` (BIGINT NOT NULL): Chave estrangeira referenciando `types.id`.
    *   `PRIMARY KEY (pokemon_id, type_id)`: Chave primária composta.
*   **Relacionamentos:**
    *   Conecta `pokemons` a seus `types`.

### 10. `pokemon_abilities`

Tabela de junção para o relacionamento Muitos-para-Muitos entre `pokemons` e `abilities`, com um atributo adicional.

*   **Finalidade:** Registrar quais habilidades um Pokémon pode ter e se são ocultas.
*   **Colunas:**
    *   `id` (BIGSERIAL PRIMARY KEY): Identificador único para a associação (permite múltiplas habilidades para o mesmo Pokémon, incluindo variações como oculta vs não oculta, se necessário, embora `is_hidden` já cubra isso).
    *   `pokemon_id` (BIGINT NOT NULL): Chave estrangeira referenciando `pokemons.id`.
    *   `ability_id` (BIGINT NOT NULL): Chave estrangeira referenciando `abilities.id`.
    *   `is_hidden` (BOOLEAN NOT NULL DEFAULT FALSE): Indica se a habilidade é uma habilidade oculta.
    *   `UNIQUE (pokemon_id, ability_id)`: Garante que um Pokémon não tenha a mesma habilidade listada duas vezes (a menos que a PK `id` seja para permitir múltiplas entradas da mesma habilidade com slots diferentes, o que `is_hidden` não cobre). Se a intenção é apenas uma entrada por (pokemon, ability), então `(pokemon_id, ability_id)` deveria ser a PK. A PK `id` aqui é comum se a tabela de junção tiver atributos próprios além de `is_hidden`.
*   **Relacionamentos:**
    *   Conecta `pokemons` a suas `abilities`.

### 11. `pokemon_egg_groups`

Tabela de junção para o relacionamento Muitos-para-Muitos entre `pokemons` e `egg_groups`.

*   **Finalidade:** Registrar a quais grupos de ovos um Pokémon pertence.
*   **Colunas:**
    *   `pokemon_id` (BIGINT NOT NULL): Chave estrangeira referenciando `pokemons.id`.
    *   `egg_group_id` (BIGINT NOT NULL): Chave estrangeira referenciando `egg_groups.id`.
    *   `PRIMARY KEY (pokemon_id, egg_group_id)`: Chave primária composta.
*   **Relacionamentos:**
    *   Conecta `pokemons` a seus `egg_groups`.

### 12. `evolution_chains`

Armazena a identificação de uma cadeia de evolução, agrupando uma linha evolutiva.

*   **Finalidade:** Agrupar os Pokémon que fazem parte de uma mesma linha evolutiva e armazenar os detalhes dessa evolução em formato JSON.
*   **Colunas:**
    *   `id` (BIGINT PRIMARY KEY): Identificador único da cadeia de evolução.
    *   `chain_data` (JSONB NOT NULL): Objeto JSON contendo os detalhes da cadeia de evolução (ex: de qual Pokémon evolui para qual, condições de evolução, etc.), conforme definido nos arquivos de esquema (`.dbml`, `.sql`).
*   **Relacionamentos:**
    *   Um `pokemons` pode pertencer a uma `evolution_chains` (`pokemons.evolution_chain_id` FK).
    *   A estrutura detalhada dos links de evolução (de, para, condições) está contida no campo `chain_data`.


### 13. `pokemon_weaknesses`
*(Numeração ajustada após consolidação da lógica de evolução em `evolution_chains`)*

Tabela de junção para o relacionamento Muitos-para-Muitos entre `pokemons` e `types`, especificando as fraquezas.

*   **Finalidade:** Registrar contra quais tipos um Pokémon é fraco.
*   **Colunas:**
    *   `pokemon_id` (BIGINT NOT NULL): Chave estrangeira referenciando `pokemons.id`.
    *   `type_id` (BIGINT NOT NULL): Chave estrangeira referenciando `types.id` (o tipo que representa a fraqueza).
    *   `PRIMARY KEY (pokemon_id, type_id)`: Chave primária composta.
*   **Relacionamentos:**
    *   Conecta `pokemons` aos `types` contra os quais ele tem fraqueza.

```mermaid
erDiagram
    regions {
        bigint id PK "Identificador único da região"
        varchar_255 name UK "Nome da região (ex: Kanto)"
    }

    types {
        bigint id PK "Identificador único do tipo"
        varchar_255 name UK "Nome do tipo (ex: Fogo)"
        varchar_7 color "Cor hexadecimal do tipo"
    }

    egg_groups {
        bigint id PK "Identificador único do grupo de ovo"
        varchar_255 name UK "Nome do grupo de ovo (ex: Amorfo)"
    }

    species {
        bigint id PK "Identificador único da espécie"
        varchar_10 pokemon_number "Número da Pokédex (ex: 0001)"
        varchar_255 name "Nome da espécie (ex: Bulbasaur)"
        varchar_255 species_en "Descrição da espécie (EN)"
        varchar_255 species_pt "Descrição da espécie (PT)"
    }

    generations {
        bigint id PK "Identificador único da geração"
        varchar_255 name UK "Nome da geração (ex: Geração I)"
        bigint region_id FK "ID da região (regions.id)"
    }

    abilities {
        bigint id PK "Identificador único da habilidade"
        varchar_255 name UK "Nome da habilidade"
        text description "Descrição da habilidade"
        bigint introduced_generation_id FK "ID da geração de introdução (generations.id)"
    }

    pokemons {
        bigint id PK "ID único do Pokémon/forma"
        varchar_10 number "Número Pokédex (ex: 0001, 0025-alola)"
        varchar_255 name "Nome do Pokémon (ex: Pikachu Alola Form)"
        numeric_5_2 height "Altura (m)"
        numeric_6_2 weight "Peso (kg)"
        text description "Descrição Pokédex"
        jsonb sprites "URLs dos sprites (via DTOs especializados)"
        integer gender_rate_value "Taxa de gênero"
        integer egg_cycles "Ciclos de ovo"
        bigint stats_id UK FK "ID dos stats (stats.id)"
        bigint generation_id FK "ID da geração (generations.id)"
        bigint species_id FK "ID da espécie (species.id)"
        bigint region_id FK "ID da região (regions.id), opcional"
        bigint evolution_chain_id FK "ID da cadeia de evolução (evolution_chains.id), opcional"
    }

    stats {
        bigint id PK "ID único do conjunto de stats"
        integer total "Total dos atributos base"
        integer hp "HP base"
        integer attack "Ataque base"
        integer defense "Defesa base"
        integer sp_atk "Ataque Especial base"
        integer sp_def "Defesa Especial base"
        integer speed "Velocidade base"
    }

    pokemon_types {
        bigint pokemon_id PK FK "ID do Pokémon (pokemons.id)"
        bigint type_id PK FK "ID do tipo (types.id)"
    }

    pokemon_abilities {
        bigserial id PK "ID único da associação"
        bigint pokemon_id FK "ID do Pokémon (pokemons.id)"
        bigint ability_id FK "ID da habilidade (abilities.id)"
        boolean is_hidden "É habilidade oculta?"
        %% UNIQUE (pokemon_id, ability_id)
    }

    pokemon_egg_groups {
        bigint pokemon_id PK FK "ID do Pokémon (pokemons.id)"
        bigint egg_group_id PK FK "ID do grupo de ovo (egg_groups.id)"
    }

    evolution_chains {
        bigint id PK "ID único da cadeia de evolução"
        jsonb chain_data NOT NULL "Detalhes da cadeia de evolução em JSON"
    }

    pokemon_weaknesses {
        bigint pokemon_id PK FK "ID do Pokémon (pokemons.id)"
        bigint type_id PK FK "ID do tipo de fraqueza (types.id)"
    }

    %% -------------------------
    %% RELACIONAMENTOS
    %% -------------------------

    regions ||--o{ generations : "tem"
    regions ||--o{ pokemons : "pode ser de"

    generations ||--o{ pokemons : "introduz"
    generations ||--o{ abilities : "pode introduzir"

    species ||--o{ pokemons : "é espécie de"

    pokemons ||--|| stats : "possui (1:1)"
    pokemons }o--o{ types : "tem tipo(s) (via pokemon_types)"
    pokemons }o--o{ abilities : "tem habilidade(s) (via pokemon_abilities)"
    pokemons }o--o{ egg_groups : "pertence a grupo(s) de ovo (via pokemon_egg_groups)"
    pokemons }o--o{ types : "é fraco contra tipo(s) (via pokemon_weaknesses)"

    pokemons ||--o{ evolution_chains : "pertence a"

    pokemon_types ||--|{ pokemons : "associa"
    pokemon_types ||--|{ types : "associa"

    pokemon_abilities ||--|{ pokemons : "associa"
    pokemon_abilities ||--|{ abilities : "associa"

    pokemon_egg_groups ||--|{ pokemons : "associa"
    pokemon_egg_groups ||--|{ egg_groups : "associa"

    pokemon_weaknesses ||--|{ pokemons : "associa fraqueza"
    pokemon_weaknesses ||--|{ types : "associa fraqueza"
```

## Considerações de Implementação

### Campos JSON (JSONB)
Os campos `sprites` e `chain_data` utilizam o tipo JSONB do PostgreSQL para armazenar dados estruturados complexos. A implementação segue o padrão da Clean Architecture:

- **Na camada de infraestrutura**: Entidades JPA mapeiam diretamente os campos JSONB
- **Na camada de interface**: DTOs especializados (`SpritesDto`, `OfficialArtworkSpritesDto`) gerenciam a serialização/deserialização
- **Na camada de domínio**: Entidades puras não dependem de detalhes de persistência ou serialização

### Mapeamento Objeto-Relacional
O projeto mantém separação clara entre:
- **Conceitos de negócio**: Representados nas entidades de domínio
- **Persistência**: Implementada nas entidades JPA da infraestrutura  
- **Comunicação externa**: Gerenciada pelos DTOs de interface

Esta separação garante que mudanças na estrutura do banco de dados ou formatos de serialização não afetem a lógica de negócio central da aplicação.