-- Create the Region table
CREATE TABLE Region (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create the Type table
CREATE TABLE Type (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    color VARCHAR(7) -- e.g., #RRGGBB
);

-- Create the Egg_Group table
CREATE TABLE Egg_Group (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create the Species table
CREATE TABLE Species (
    id INT PRIMARY KEY,
    national_pokedex_number VARCHAR(4) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    species_en VARCHAR(255),
    species_pt VARCHAR(255)
);

-- Create the Generation table
CREATE TABLE Generation (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    region_id INT NOT NULL,
    FOREIGN KEY (region_id) REFERENCES Region(id)
);

-- Create the Ability table (Adjusted PK to use 'id')
CREATE TABLE Ability (
    id INT PRIMARY KEY, -- Use ID as PK as per new data
    name VARCHAR(255) NOT NULL UNIQUE, -- Ensure name is also unique
    description TEXT,
    introduced_generation_id INT,
    FOREIGN KEY (introduced_generation_id) REFERENCES Generation(id)
);

-- Create the Pokemon table (Removed unique constraint on national_pokedex_number and stats_id reference)
CREATE TABLE Pokemon (
    id INT PRIMARY KEY, -- PK for each unique Pokemon form
    national_pokedex_number VARCHAR(4) NOT NULL, -- No longer unique here, as multiple forms share it
    name VARCHAR(255) NOT NULL,
    -- stats_id INT, -- Removed, stats will reference Pokemon.id directly
    generation_id INT NOT NULL,
    species_id INT NOT NULL,
    height_m NUMERIC(4, 2),
    weight_kg NUMERIC(6, 2),
    description TEXT,
    sprites JSONB, -- Store sprite URLs as JSONB
    gender_rate_value INT,
    egg_cycles INT,
    FOREIGN KEY (generation_id) REFERENCES Generation(id),
    FOREIGN KEY (species_id) REFERENCES Species(id)
    -- FOREIGN KEY (stats_id) REFERENCES Stats(id) -- Removed
);

-- Create the Stats table (Adjusted for 1:1 relation with Pokemon.id)
CREATE TABLE Stats (
    pokemon_id INT PRIMARY KEY, -- This column is both PK and FK to Pokemon
    total INT,
    hp INT,
    attack INT,
    defense INT,
    sp_atk INT,
    sp_def INT,
    speed INT,
    FOREIGN KEY (pokemon_id) REFERENCES Pokemon(id) -- FK linking to Pokemon
);

-- Removed the ALTER TABLE for stats_id as it's no longer needed with the new Stats structure
-- ALTER TABLE Pokemon
-- ADD COLUMN stats_id INT,
-- ADD CONSTRAINT fk_stats
-- FOREIGN KEY (stats_id) REFERENCES Stats(id);


-- Create the Pokemon_Type junction table for N:N relationship
CREATE TABLE Pokemon_Type (
    pokemon_id INT NOT NULL,
    type_id INT NOT NULL,
    PRIMARY KEY (pokemon_id, type_id),
    FOREIGN KEY (pokemon_id) REFERENCES Pokemon(id),
    FOREIGN KEY (type_id) REFERENCES Type(id)
);

-- Create the Pokemon_Ability junction table for N:N relationship (Adjusted to use ability_id)
CREATE TABLE Pokemon_Ability (
    pokemon_id INT NOT NULL,
    ability_id INT NOT NULL, -- Reference Ability by ID now
    is_hidden BOOLEAN,
    PRIMARY KEY (pokemon_id, ability_id),
    FOREIGN KEY (pokemon_id) REFERENCES Pokemon(id),
    FOREIGN KEY (ability_id) REFERENCES Ability(id) -- FK references Ability.id
);

-- Create the Pokemon_Egg_Group junction table for N:N relationship
CREATE TABLE Pokemon_Egg_Group (
    pokemon_id INT NOT NULL,
    egg_group_id INT NOT NULL,
    PRIMARY KEY (pokemon_id, egg_group_id),
    FOREIGN KEY (pokemon_id) REFERENCES Pokemon(id),
    FOREIGN KEY (egg_group_id) REFERENCES Egg_Group(id)
);

-- Create the Evolution_Chain table
CREATE TABLE Evolution_Chain (
    id INT PRIMARY KEY
);

-- Create the Evolution_Link table to store individual evolution steps (Condition stored as JSONB)
CREATE TABLE Evolution_Link (
    evolution_chain_id INT NOT NULL,
    pokemon_id INT NOT NULL, -- The Pokemon that evolves
    target_pokemon_id INT, -- The Pokemon it evolves into (Can be NULL if it's the last in the chain)
    condition JSONB, -- Store the evolution condition as a JSONB object
    PRIMARY KEY (evolution_chain_id, pokemon_id), -- Composite PK for unique links within a chain
    FOREIGN KEY (evolution_chain_id) REFERENCES Evolution_Chain(id),
    FOREIGN KEY (pokemon_id) REFERENCES Pokemon(id),
    FOREIGN KEY (target_pokemon_id) REFERENCES Pokemon(id)
);

-- Create the Pokemon_Weakness junction table for N:N relationship
CREATE TABLE Pokemon_Weakness (
    pokemon_id INT NOT NULL,
    weakness_type_id INT NOT NULL,
    PRIMARY KEY (pokemon_id, weakness_type_id),
    FOREIGN KEY (pokemon_id) REFERENCES Pokemon(id),
    FOREIGN KEY (weakness_type_id) REFERENCES Type(id)
);