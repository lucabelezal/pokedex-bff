CREATE TABLE Region (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE Type (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    color VARCHAR(7)
);

CREATE TABLE Egg_Group (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE Species (
    id INT PRIMARY KEY,
    nationalPokedexNumber VARCHAR(4) NOT NULL,
    name VARCHAR(255) NOT NULL,
    species_en VARCHAR(255),
    species_pt VARCHAR(255)
);

CREATE TABLE Generation (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    regiaoId INT,
    FOREIGN KEY (regiaoId) REFERENCES Region(id)
);

CREATE TABLE Ability (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    introducedGenerationId INT,
    FOREIGN KEY (introducedGenerationId) REFERENCES Generation(id)
);

CREATE TABLE Stats (
    id INT PRIMARY KEY,
    pokemonNationalPokedexNumber VARCHAR(4) NOT NULL,
    pokemonName VARCHAR(255) NOT NULL,
    total INT,
    hp INT,
    attack INT,
    defense INT,
    spAtk INT,
    spDef INT,
    speed INT
);

CREATE TABLE Evolution_Chain (
    id INT PRIMARY KEY
);

CREATE TABLE Pokemon (
    id INT PRIMARY KEY,
    nationalPokedexNumber VARCHAR(4) NOT NULL,
    name VARCHAR(255) NOT NULL,
    statsId INT,
    generationId INT,
    speciesId INT,
    heightM DECIMAL(3, 1),
    weightKg DECIMAL(4, 1),
    description TEXT,
    gender_rate_value INT,
    eggCycles INT,
    evolutionChainId INT,
    FOREIGN KEY (statsId) REFERENCES Stats(id),
    FOREIGN KEY (generationId) REFERENCES Generation(id),
    FOREIGN KEY (speciesId) REFERENCES Species(id),
    FOREIGN KEY (evolutionChainId) REFERENCES Evolution_Chain(id)
);

CREATE TABLE Pokemon_Type (
    pokemon_id INT NOT NULL,
    type_id INT NOT NULL,
    PRIMARY KEY (pokemon_id, type_id),
    FOREIGN KEY (pokemon_id) REFERENCES Pokemon(id),
    FOREIGN KEY (type_id) REFERENCES Type(id)
);

CREATE TABLE Pokemon_Ability (
    pokemon_id INT NOT NULL,
    ability_id INT NOT NULL,
    isHidden BOOLEAN,
    PRIMARY KEY (pokemon_id, ability_id),
    FOREIGN KEY (pokemon_id) REFERENCES Pokemon(id),
    FOREIGN KEY (ability_id) REFERENCES Ability(id)
);

CREATE TABLE Pokemon_Egg_Group (
    pokemon_id INT NOT NULL,
    egg_group_id INT NOT NULL,
    PRIMARY KEY (pokemon_id, egg_group_id),
    FOREIGN KEY (pokemon_id) REFERENCES Pokemon(id),
    FOREIGN KEY (egg_group_id) REFERENCES Egg_Group(id)
);

CREATE TABLE Weakness (
    id INT PRIMARY KEY,
    pokemon_id INT NOT NULL,
    pokemon_name VARCHAR(255) NOT NULL,
    weakness_type VARCHAR(255) NOT NULL,
    FOREIGN KEY (pokemon_id) REFERENCES Pokemon(id)
);

CREATE TABLE Evolution_Detail (
    id SERIAL PRIMARY KEY,
    evolutionChainId INT NOT NULL,
    pokemonId INT NOT NULL,
    pokemonName VARCHAR(255) NOT NULL,
    targetPokemonId INT,
    targetPokemonName VARCHAR(255),
    condition_type VARCHAR(255),
    condition_value VARCHAR(255),
    FOREIGN KEY (evolutionChainId) REFERENCES Evolution_Chain(id),
    FOREIGN KEY (pokemonId) REFERENCES Pokemon(id),
    FOREIGN KEY (targetPokemonId) REFERENCES Pokemon(id)
);