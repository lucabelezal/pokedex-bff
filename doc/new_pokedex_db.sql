CREATE TABLE region (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE generation (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    region_id INTEGER NOT NULL REFERENCES region(id) ON DELETE RESTRICT
);

CREATE TABLE type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    color VARCHAR(50) NOT NULL
);

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

CREATE TABLE pokemon (
    id SERIAL PRIMARY KEY,
    national_pokedex_number VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    stats_id INTEGER UNIQUE REFERENCES stats(id) ON DELETE CASCADE,
    generation_id INTEGER NOT NULL REFERENCES generation(id) ON DELETE RESTRICT,
    species VARCHAR(255),
    height_m DECIMAL(5, 2),
    weight_kg DECIMAL(6, 2),
    description TEXT,
    sprites JSONB,
    gender_rate_value INTEGER,
    egg_cycles INTEGER
);

CREATE TABLE pokemon_type (
    pokemon_id INTEGER REFERENCES pokemon(id) ON DELETE CASCADE,
    type_id INTEGER REFERENCES type(id) ON DELETE CASCADE,
    PRIMARY KEY (pokemon_id, type_id)
);

CREATE TABLE evolution (
    id SERIAL PRIMARY KEY,
    pre_evolution_pokemon_id INTEGER NOT NULL REFERENCES pokemon(id) ON DELETE CASCADE,
    post_evolution_pokemon_id INTEGER NOT NULL REFERENCES pokemon(id) ON DELETE CASCADE,
    condition_description VARCHAR(255) NOT NULL,
    condition_level INTEGER,
    UNIQUE (pre_evolution_pokemon_id, post_evolution_pokemon_id)
);

CREATE TABLE ability (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    introduced_generation_id INTEGER REFERENCES generation(id) ON DELETE RESTRICT
);

CREATE TABLE pokemon_ability (
    pokemon_id INTEGER REFERENCES pokemon(id) ON DELETE CASCADE,
    ability_id INTEGER REFERENCES ability(id) ON DELETE CASCADE,
    is_hidden BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (pokemon_id, ability_id)
);

CREATE TABLE egg_group (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE pokemon_egg_group (
    pokemon_id INTEGER REFERENCES pokemon(id) ON DELETE CASCADE,
    egg_group_id INTEGER REFERENCES egg_group(id) ON DELETE CASCADE,
    PRIMARY KEY (pokemon_id, egg_group_id)
);

CREATE TABLE type_matchup (
    attacking_type_id INTEGER NOT NULL REFERENCES type(id) ON DELETE CASCADE,
    defending_type_id INTEGER NOT NULL REFERENCES type(id) ON DELETE CASCADE,
    damage_factor DECIMAL(3,2) NOT NULL,
    PRIMARY KEY (attacking_type_id, defending_type_id)
);