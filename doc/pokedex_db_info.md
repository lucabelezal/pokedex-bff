# Documentação do Esquema do Banco de Dados Pokémon

Este documento descreve o esquema de banco de dados relacional para armazenar informações sobre Pokémon, suas características, evoluções, tipos, habilidades, regiões e grupos de ovos. O banco de dados de destino é PostgreSQL.

## Visão Geral do Esquema

O esquema é composto por **13 tabelas** principais e de junção, projetadas para manter os dados normalizados, minimizando redundância e garantindo a integridade referencial. As tabelas se relacionam para permitir consultas complexas e eficientes sobre o universo Pokémon.

---

## Entidades (Tabelas) e Seus Relacionamentos

### 1. `region`

Armazena informações sobre as diferentes regiões do mundo Pokémon.

*   **Finalidade:** Categorizar gerações.
*   **Colunas:**
  *   `id` (SERIAL PRIMARY KEY): Identificador único da região. Gerado automaticamente.
  *   `name` (VARCHAR(255) NOT NULL UNIQUE): Nome da região (ex: 'Kanto', 'Johto').
*   **Relacionamentos:**
  *   `generation`: Uma região é referenciada por uma ou mais gerações (`generation.region_id`).

### 2. `generation`

Armazena informações sobre as diferentes gerações de Pokémon.

*   **Finalidade:** Agrupar Pokémon, habilidades e outras características por sua geração de introdução.
*   **Colunas:**
  *   `id` (SERIAL PRIMARY KEY): Identificador único da geração. Gerado automaticamente.
  *   `name` (VARCHAR(50) NOT NULL UNIQUE): Nome da geração (ex: 'Geração I', 'Geração II').
  *   `region_id` (INTEGER NOT NULL REFERENCES `region(id)` ON DELETE RESTRICT): Chave estrangeira para a região principal associada a esta geração.
*   **Relacionamentos:**
  *   `region`: Uma geração pertence a uma única região (`generation.region_id`).
  *   `pokemon`: Uma geração introduz múltiplos Pokémon (`pokemon.generation_id`).
  *   `ability`: Uma geração introduz múltiplas habilidades (`ability.introduced_generation_id`).

### 3. `type`

Armazena os diferentes tipos elementais de Pokémon.

*   **Finalidade:** Classificar Pokémon por seus tipos e definir as relações de efetividade entre eles.
*   **Colunas:**
  *   `id` (SERIAL PRIMARY KEY): Identificador único do tipo. Gerado automaticamente.
  *   `name` (VARCHAR(255) NOT NULL UNIQUE): Nome do tipo (ex: 'Planta', 'Fogo', 'Água').
  *   `color` (VARCHAR(50) NOT NULL): Cor associada ao tipo (ex: '#7AC74C' para Planta).
*   **Relacionamentos:**
  *   `pokemon_type`: Um tipo pode ser associado a múltiplos Pokémon (`pokemon_type.type_id`).
  *   `type_matchup`: Um tipo pode atuar como tipo atacante ou defensor em matchups de dano (`type_matchup.attacking_type_id`, `type_matchup.defending_type_id`).

### 4. `stats`

Armazena os valores base de estatísticas para um Pokémon.

*   **Finalidade:** Manter as estatísticas (HP, Attack, Defense, etc.) de forma separada e referenciável.
*   **Colunas:**
  *   `id` (SERIAL PRIMARY KEY): Identificador único do conjunto de estatísticas. Gerado automaticamente.
  *   `total` (INTEGER): Soma de todas as estatísticas.
  *   `hp` (INTEGER): Pontos de vida.
  *   `attack` (INTEGER): Estatística de Ataque.
  *   `defense` (INTEGER): Estatística de Defesa.
  *   `sp_atk` (INTEGER): Estatística de Ataque Especial.
  *   `sp_def` (INTEGER): Estatística de Defesa Especial.
  *   `speed` (INTEGER): Estatística de Velocidade.
*   **Relacionamentos:**
  *   `pokemon`: Um conjunto de estatísticas pertence a um único Pokémon (`pokemon.stats_id`). É uma relação 1-para-1, garantida por `UNIQUE` na chave estrangeira.

### 5. `species`

Armazena informações sobre as "espécies" ou categorias dos Pokémon.

*   **Finalidade:** Categorizar Pokémon (ex: "Pokémon Semente", "Pokémon Lagarto").
*   **Colunas:**
  *   `id` (SERIAL PRIMARY KEY): Identificador único da espécie. Gerado automaticamente.
  *   `name_en` (VARCHAR(255) NOT NULL UNIQUE): Nome da categoria em inglês (ex: 'Seed Pokémon').
  *   `name_pt` (VARCHAR(255) NOT NULL UNIQUE): Nome da categoria em português (ex: 'Pokémon Semente').
  *   `description_en` (TEXT): Descrição da categoria em inglês (opcional).
  *   `description_pt` (TEXT): Descrição da categoria em português (opcional).
*   **Relacionamentos:**
  *   `pokemon`: Uma espécie pode ser atribuída a múltiplos Pokémon (`pokemon.species_id`).

### 6. `pokemon`

A entidade central, que armazena as informações principais de cada Pokémon.

*   **Finalidade:** Armazenar dados fundamentais sobre cada criatura e suas formas (se aplicável, considerando o `national_pokedex_number` único).
*   **Colunas:**
  *   `id` (SERIAL PRIMARY KEY): Identificador interno único do Pokémon. Gerado automaticamente.
  *   `national_pokedex_number` (VARCHAR(10) NOT NULL UNIQUE): O número oficial da Pokédex Nacional (ex: '0001' para Bulbasaur). Se formas diferentes usarem o mesmo número, esta coluna precisaria de ajuste ou uma coluna adicional para `form_identifier`.
  *   `name` (VARCHAR(255) NOT NULL): Nome do Pokémon (ex: 'Bulbasaur').
  *   `stats_id` (INTEGER UNIQUE REFERENCES `stats(id)` ON DELETE CASCADE): Chave estrangeira para o conjunto de estatísticas do Pokémon.
  *   `generation_id` (INTEGER NOT NULL REFERENCES `generation(id)` ON DELETE RESTRICT): Chave estrangeira para a geração em que o Pokémon foi introduzido.
  *   `species_id` (INTEGER REFERENCES `species(id)` ON DELETE RESTRICT): Chave estrangeira para a espécie/categoria do Pokémon.
  *   `height_m` (DECIMAL(5, 2)): Altura do Pokémon em metros.
  *   `weight_kg` (DECIMAL(6, 2)): Peso do Pokémon em quilogramas.
  *   `description` (TEXT): Uma descrição textual do Pokémon (geralmente da Pokédex).
  *   `sprites` (JSONB): Objeto JSON contendo URLs para os sprites do Pokémon. **Detalhes abaixo.**
  *   `gender_rate_value` (INTEGER): Valor numérico que representa a taxa de gênero.
    *   `-1`: Sem gênero
    *   `0`: 100% Macho
    *   `1`: 87.5% Macho, 12.5% Fêmea
    *   `2`: 75% Macho, 25% Fêmea
    *   `4`: 50% Macho, 50% Fêmea
    *   `6`: 25% Macho, 75% Fêmea
    *   `8`: 100% Fêmea
  *   `egg_cycles` (INTEGER): Número de ciclos de ovo necessários para chocar o Pokémon.
*   **Relacionamentos:**
  *   `stats`: Cada Pokémon tem um único conjunto de estatísticas (`pokemon.stats_id`).
  *   `generation`: Cada Pokémon pertence a uma única geração (`pokemon.generation_id`).
  *   `species`: Cada Pokémon pertence a uma única espécie/categoria (`pokemon.species_id`).
  *   `pokemon_type`: Um Pokémon pode ter um ou dois tipos (`pokemon_type.pokemon_id`).
  *   `pokemon_ability`: Um Pokémon pode ter múltiplas habilidades (`pokemon_ability.pokemon_id`).
  *   `pokemon_egg_group`: Um Pokémon pode pertencer a um ou mais grupos de ovos (`pokemon_egg_group.pokemon_id`).
  *   `evolution`: Um Pokémon pode ser uma pré-evolução ou pós-evolução (`evolution.pre_evolution_pokemon_id`, `evolution.post_evolution_pokemon_id`).

### 7. `pokemon_type`

Tabela de junção para a relação muitos-para-muitos entre Pokémon e Tipos.

*   **Finalidade:** Registrar quais tipos cada Pokémon possui.
*   **Colunas:**
  *   `pokemon_id` (INTEGER REFERENCES `pokemon(id)` ON DELETE CASCADE): Chave estrangeira para o Pokémon.
  *   `type_id` (INTEGER REFERENCES `type(id)` ON DELETE CASCADE): Chave estrangeira para o Tipo.
  *   `PRIMARY KEY (pokemon_id, type_id)`: Chave primária composta.
*   **Relacionamentos:**
  *   `pokemon`: Muitos `pokemon_type` podem pertencer a um `pokemon`.
  *   `type`: Muitos `pokemon_type` podem pertencer a um `type`.

### 8. `evolution`

Armazena as relações de evolução entre Pokémon.

*   **Finalidade:** Registrar como os Pokémon evoluem uns dos outros e sob quais condições.
*   **Colunas:**
  *   `id` (SERIAL PRIMARY KEY): Identificador único da relação de evolução.
  *   `pre_evolution_pokemon_id` (INTEGER NOT NULL REFERENCES `pokemon(id)` ON DELETE CASCADE): O Pokémon que evolui.
  *   `post_evolution_pokemon_id` (INTEGER NOT NULL REFERENCES `pokemon(id)` ON DELETE CASCADE): O Pokémon para o qual evolui.
  *   `condition_description` (VARCHAR(255) NOT NULL): Descrição textual da condição de evolução (ex: 'Level 16', 'Use Water Stone', 'Trade').
  *   `condition_level` (INTEGER): O nível específico para evolução, se aplicável.
  *   `UNIQUE (pre_evolution_pokemon_id, post_evolution_pokemon_id)`: Garante que uma rota de evolução específica seja única.
*   **Relacionamentos:**
  *   `pokemon` (como pré-evolução): Um `pokemon` pode ser `pre_evolution_pokemon_id` em múltiplas entradas (ex: Eevee).
  *   `pokemon` (como pós-evolução): Um `pokemon` pode ser `post_evolution_pokemon_id` em uma ou mais entradas (menos comum, mas possível se diferentes Pokémon evoluírem para o mesmo).

### 9. `ability`

Armazena informações sobre as habilidades dos Pokémon.

*   **Finalidade:** Registrar as habilidades que os Pokémon podem ter.
*   **Colunas:**
  *   `id` (SERIAL PRIMARY KEY): Identificador único da habilidade.
  *   `name` (VARCHAR(255) NOT NULL UNIQUE): Nome da habilidade (ex: 'Overgrow', 'Clorofila').
  *   `description` (TEXT): Descrição detalhada do efeito da habilidade.
  *   `introduced_generation_id` (INTEGER REFERENCES `generation(id)` ON DELETE RESTRICT): A geração em que a habilidade foi introduzida.
*   **Relacionamentos:**
  *   `generation`: Uma habilidade é introduzida em uma geração.
  *   `pokemon_ability`: Uma habilidade pode ser possuída por múltiplos Pokémon (`pokemon_ability.ability_id`).

### 10. `pokemon_ability`

Tabela de junção para a relação muitos-para-muitos entre Pokémon e Habilidades.

*   **Finalidade:** Registrar quais habilidades cada Pokémon pode ter, indicando se é uma habilidade oculta.
*   **Colunas:**
  *   `pokemon_id` (INTEGER REFERENCES `pokemon(id)` ON DELETE CASCADE): Chave estrangeira para o Pokémon.
  *   `ability_id` (INTEGER REFERENCES `ability(id)` ON DELETE CASCADE): Chave estrangeira para a Habilidade.
  *   `is_hidden` (BOOLEAN DEFAULT FALSE): Verdadeiro se for a habilidade oculta do Pokémon.
  *   `PRIMARY KEY (pokemon_id, ability_id)`: Chave primária composta.
*   **Relacionamentos:**
  *   `pokemon`: Muitos `pokemon_ability` podem pertencer a um `pokemon`.
  *   `ability`: Muitos `pokemon_ability` podem pertencer a uma `ability`.

### 11. `egg_group`

Armazena as categorias de grupos de ovos.

*   **Finalidade:** Classificar Pokémon para fins de breeding.
*   **Colunas:**
  *   `id` (SERIAL PRIMARY KEY): Identificador único do grupo de ovos.
  *   `name` (VARCHAR(50) NOT NULL UNIQUE): Nome do grupo de ovos (ex: 'Monstro', 'Planta', 'Ditto').
*   **Relacionamentos:**
  *   `pokemon_egg_group`: Um grupo de ovos pode conter múltiplos Pokémon (`pokemon_egg_group.egg_group_id`).

### 12. `pokemon_egg_group`

Tabela de junção para a relação muitos-para-muitos entre Pokémon e Grupos de Ovos.

*   **Finalidade:** Registrar a quais grupos de ovos cada Pokémon pertence.
*   **Colunas:**
  *   `pokemon_id` (INTEGER REFERENCES `pokemon(id)` ON DELETE CASCADE): Chave estrangeira para o Pokémon.
  *   `egg_group_id` (INTEGER REFERENCES `egg_group(id)` ON DELETE CASCADE): Chave estrangeira para o Grupo de Ovos.
  *   `PRIMARY KEY (pokemon_id, egg_group_id)`: Chave primária composta.
*   **Relacionamentos:**
  *   `pokemon`: Muitos `pokemon_egg_group` podem pertencer a um `pokemon`.
  *   `egg_group`: Muitos `pokemon_egg_group` podem pertencer a um `egg_group`.

### 13. `type_matchup`

Armazena as relações de efetividade de dano entre tipos de Pokémon.

*   **Finalidade:** Definir fraquezas, resistências e imunidades com base na interação de tipos.
*   **Colunas:**
  *   `attacking_type_id` (INTEGER NOT NULL REFERENCES `type(id)` ON DELETE CASCADE): O ID do tipo que está causando o dano.
  *   `defending_type_id` (INTEGER NOT NULL REFERENCES `type(id)` ON DELETE CASCADE): O ID do tipo que está recebendo o dano.
  *   `damage_factor` (DECIMAL(3,2) NOT NULL): O multiplicador de dano (ex: `2.00` para super efetivo, `0.50` para não muito efetivo, `0.00` para imune).
  *   `PRIMARY KEY (attacking_type_id, defending_type_id)`: Chave primária composta.
*   **Relacionamentos:**
  *   `type` (como tipo atacante): Um `type` pode ser `attacking_type_id` em múltiplos matchups.
  *   `type` (como tipo defensor): Um `type` pode ser `defending_type_id` em múltiplos matchups.

---

## Detalhes da Coluna `sprites` (JSONB) na Tabela `pokemon`

A coluna `sprites` na tabela `pokemon` é do tipo **`JSONB`**.

*   **Finalidade:** Armazenar uma coleção rica e semi-estruturada de URLs de imagens (sprites) para cada Pokémon.
*   **Fonte Exemplo:** A estrutura é baseada na resposta da [PokéAPI (ex: /api/v2/pokemon/1)](https://pokeapi.co/api/v2/pokemon/1).
*   **Estrutura do JSONB (Exemplo para Bulbasaur - ID 1):**
    ```json
    {
      "back_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/1.png",
      "back_female": null,
      "back_shiny": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/shiny/1.png",
      "back_shiny_female": null,
      "front_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
      "front_female": null,
      "front_shiny": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/1.png",
      "front_shiny_female": null,
      "other": {
        "dream_world": {
          "front_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/1.svg",
          "front_female": null
        },
        "home": {
          "front_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/1.png",
          "front_female": null,
          "front_shiny": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/shiny/1.png",
          "front_shiny_female": null
        },
        "official-artwork": {
          "front_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
          "front_shiny": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/1.png"
        },
        "showdown": {
          "back_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/back/1.gif",
          "back_female": null,
          "back_shiny": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/back/shiny/1.gif",
          "back_shiny_female": null,
          "front_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/1.gif",
          "front_female": null,
          "front_shiny": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/shiny/1.gif",
          "front_shiny_female": null
        }
      }
    }
    ```
*   **Relacionamento:** É uma relação **1-para-1** direta com a entidade `pokemon`. Cada linha na tabela `pokemon` possui um único campo `sprites` que armazena este objeto JSON.
*   **Vantagens do JSONB:**
  *   **Flexibilidade:** Permite que a estrutura interna dos sprites evolua sem alterar o esquema SQL da tabela `pokemon`.
  *   **Eficiência:** O tipo `JSONB` no PostgreSQL é otimizado para armazenamento e consulta, incluindo indexação de campos dentro do JSON.
  *   **Atomicidade:** Todas as URLs de sprite para um Pokémon são armazenadas e recuperadas juntas.

---

## Diagrama de Entidade-Relacionamento (ERD)

A seguir, um diagrama ERD simplificado representando as tabelas e suas principais relações, utilizando a sintaxe Mermaid.

```mermaid
erDiagram
    region {
        int id PK
        varchar name
    }

    generation {
        int id PK
        varchar name
        int region_id FK
    }

    type {
        int id PK
        varchar name
        varchar color
    }

    stats {
        int id PK
        int total
        int hp
        int attack
        int defense
        int sp_atk
        int sp_def
        int speed
    }

    species {
        int id PK
        varchar name_en
        varchar name_pt
        text description_en
        text description_pt
    }

    pokemon {
        int id PK
        varchar national_pokedex_number
        varchar name
        int stats_id FK
        int generation_id FK
        int species_id FK
        decimal height_m
        decimal weight_kg
        text description
        jsonb sprites
        int gender_rate_value
        int egg_cycles
    }

    pokemon_type {
        int pokemon_id PK,FK
        int type_id PK,FK
    }

    evolution {
        int id PK
        int pre_evolution_pokemon_id FK
        int post_evolution_pokemon_id FK
        varchar condition_description
        int condition_level
    }

    ability {
        int id PK
        varchar name
        text description
        int introduced_generation_id FK
    }

    pokemon_ability {
        int pokemon_id PK,FK
        int ability_id PK,FK
        boolean is_hidden
    }

    egg_group {
        int id PK
        varchar name
    }

    pokemon_egg_group {
        int pokemon_id PK,FK
        int egg_group_id PK,FK
    }

    type_matchup {
        int attacking_type_id PK,FK
        int defending_type_id PK,FK
        decimal damage_factor
    }

    region ||--|{ generation : "associada_a"
    generation ||--|{ pokemon : "introduz"
    generation ||--o{ ability : "introduz"
    stats ||--|| pokemon : "descreve"
    species ||--|{ pokemon : "classifica"
    pokemon }|--|{ pokemon_type : "possui"
    type }|--|{ pokemon_type : "é_tipo_de"
    pokemon }|--|{ pokemon_ability : "possui"
    ability }|--|{ pokemon_ability : "é_habilidade_de"
    pokemon }|--|{ pokemon_egg_group : "pertence_a"
    egg_group }|--|{ pokemon_egg_group : "contém"
    type }|--|{ type_matchup : "atacante_em"
    type }|--|{ type_matchup : "defensor_em"
    pokemon ||--o{ evolution : "evolui_de"
    pokemon ||--o{ evolution : "evolui_para"