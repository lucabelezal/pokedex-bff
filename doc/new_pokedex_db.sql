-- Tabela para armazenar informações sobre regiões do mundo Pokémon
CREATE TABLE region (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Tabela para armazenar informações sobre gerações de Pokémon
CREATE TABLE generation (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    region_id INTEGER NOT NULL REFERENCES region(id) ON DELETE RESTRICT
);

-- Tabela para armazenar os tipos de Pokémon (e.g., Fire, Water, Grass)
CREATE TABLE type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    color VARCHAR(50) NOT NULL -- Ex: '#EE8130' para Fire, '#6390F0' para Water
);

-- Tabela para armazenar os atributos de estatísticas de um Pokémon
CREATE TABLE stats (
    id SERIAL PRIMARY KEY,
    total INTEGER,
    hp INTEGER,
    attack INTEGER,
    defense INTEGER,
    sp_atk INTEGER,
    sp_def INTEGER,
    speed INTEGER
);

-- Tabela para armazenar informações sobre as espécies de Pokémon
CREATE TABLE species (
    id SERIAL PRIMARY KEY,
    name_en VARCHAR(255) NOT NULL UNIQUE,
    name_pt VARCHAR(255) NOT NULL UNIQUE,
    description_en TEXT,
    description_pt TEXT
);

-- Tabela principal para armazenar informações sobre cada Pokémon individual
CREATE TABLE pokemon (
    id SERIAL PRIMARY KEY,
    national_pokedex_number VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    stats_id INTEGER UNIQUE REFERENCES stats(id) ON DELETE CASCADE,
    generation_id INTEGER NOT NULL REFERENCES generation(id) ON DELETE RESTRICT,
    species_id INTEGER REFERENCES species(id) ON DELETE RESTRICT,
    height_m DECIMAL(5, 2),
    weight_kg DECIMAL(6, 2),
    description TEXT,
    sprites JSONB,
    gender JSONB, -- ALTERAÇÃO AQUI: de gender_rate_value INTEGER para gender JSONB
    egg_cycles INTEGER
);

-- Tabela de ligação para associar Pokémon a seus tipos (um Pokémon pode ter um ou dois tipos)
CREATE TABLE pokemon_type (
    pokemon_id INTEGER REFERENCES pokemon(id) ON DELETE CASCADE,
    type_id INTEGER REFERENCES type(id) ON DELETE CASCADE,
    PRIMARY KEY (pokemon_id, type_id)
);

-- Tabela para armazenar as cadeias de evolução dos Pokémon
CREATE TABLE evolution (
    id SERIAL PRIMARY KEY,
    pre_evolution_pokemon_id INTEGER NOT NULL REFERENCES pokemon(id) ON DELETE CASCADE,
    post_evolution_pokemon_id INTEGER NOT NULL REFERENCES pokemon(id) ON DELETE CASCADE,
    condition_description VARCHAR(255) NOT NULL,
    condition_level INTEGER,
    UNIQUE (pre_evolution_pokemon_id, post_evolution_pokemon_id)
);

-- Tabela para armazenar as habilidades dos Pokémon
CREATE TABLE ability (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    introduced_generation_id INTEGER REFERENCES generation(id) ON DELETE RESTRICT
);

-- Tabela de ligação para associar Pokémon a suas habilidades (um Pokémon pode ter múltiplas habilidades)
CREATE TABLE pokemon_ability (
    pokemon_id INTEGER REFERENCES pokemon(id) ON DELETE CASCADE,
    ability_id INTEGER REFERENCES ability(id) ON DELETE CASCADE,
    is_hidden BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (pokemon_id, ability_id)
);

-- Tabela para armazenar os grupos de ovos dos Pokémon
CREATE TABLE egg_group (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Tabela de ligação para associar Pokémon a seus grupos de ovos (um Pokémon pode pertencer a múltiplos grupos de ovos)
CREATE TABLE pokemon_egg_group (
    pokemon_id INTEGER REFERENCES pokemon(id) ON DELETE CASCADE,
    egg_group_id INTEGER REFERENCES egg_group(id) ON DELETE CASCADE,
    PRIMARY KEY (pokemon_id, egg_group_id)
);

-- Tabela para armazenar as relações de dano entre tipos de Pokémon (tabela de efetividade de tipos)
CREATE TABLE type_matchup (
    attacking_type_id INTEGER NOT NULL REFERENCES type(id) ON DELETE CASCADE,
    defending_type_id INTEGER NOT NULL REFERENCES type(id) ON DELETE CASCADE,
    damage_factor DECIMAL(3,2) NOT NULL,
    PRIMARY KEY (attacking_type_id, defending_type_id)
);