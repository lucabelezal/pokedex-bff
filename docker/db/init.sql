-- IMPORTANT: The order of DROP TABLE is crucial to avoid dependency errors.
-- Start with tables that have FKs to others, and finish with tables that are referenced.

DROP TABLE IF EXISTS pokemon_moves CASCADE;
DROP TABLE IF EXISTS ability_flavor_text CASCADE;
DROP TABLE IF EXISTS ability_prose CASCADE;
DROP TABLE IF EXISTS pokemon_forms CASCADE;
DROP TABLE IF EXISTS location_areas CASCADE;
DROP TABLE IF EXISTS pokemon_stats CASCADE;
DROP TABLE IF EXISTS pokemon_types CASCADE;
DROP TABLE IF EXISTS pokemon_abilities CASCADE;
DROP TABLE IF EXISTS pokemon_egg_groups CASCADE;
DROP TABLE IF EXISTS moves CASCADE;
DROP TABLE IF EXISTS natures CASCADE;
DROP TABLE IF EXISTS characteristics CASCADE;
DROP TABLE IF EXISTS types CASCADE;
DROP TABLE IF EXISTS stats CASCADE;
DROP TABLE IF EXISTS pokemon CASCADE;
DROP TABLE IF EXISTS pokemon_species CASCADE;
DROP TABLE IF EXISTS evolution_chains CASCADE;
DROP TABLE IF EXISTS pokemon_colors CASCADE;
DROP TABLE IF EXISTS pokemon_shapes CASCADE;
DROP TABLE IF EXISTS pokemon_habitats CASCADE;
DROP TABLE IF EXISTS growth_rates CASCADE;
DROP TABLE IF EXISTS egg_groups CASCADE;
DROP TABLE IF EXISTS genders CASCADE;
DROP TABLE IF EXISTS move_effect_prose CASCADE;
DROP TABLE IF EXISTS move_effects CASCADE;
DROP TABLE IF EXISTS move_targets CASCADE;
DROP TABLE IF EXISTS contest_types CASCADE;
DROP TABLE IF EXISTS contest_effects CASCADE;
DROP TABLE IF EXISTS super_contest_effects CASCADE;
DROP TABLE IF EXISTS versions CASCADE;
DROP TABLE IF EXISTS version_groups CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS regions CASCADE;
DROP TABLE IF EXISTS berries CASCADE;
DROP TABLE IF EXISTS berry_flavors CASCADE;
DROP TABLE IF EXISTS flavors CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS item_categories CASCADE;
DROP TABLE IF EXISTS abilities CASCADE;
DROP TABLE IF EXISTS pokemon_move_methods CASCADE;
DROP TABLE IF EXISTS languages CASCADE;


-- Basic Tables
CREATE TABLE super_contest_effects (
  id INT PRIMARY KEY,
  appeal INT NOT NULL
);

CREATE TABLE regions (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL
);

CREATE TABLE generations (
  id INT PRIMARY KEY,
  main_region_id INT,
  identifier VARCHAR(255) NOT NULL,
  CONSTRAINT fk_generations_main_region FOREIGN KEY (main_region_id) REFERENCES regions(id)
);

CREATE TABLE damage_classes (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL
);

-- Types and Stats
CREATE TABLE types (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL,
  generation_id INT NOT NULL,
  damage_class_id INT,
  CONSTRAINT fk_types_generation FOREIGN KEY (generation_id) REFERENCES generations(id),
  CONSTRAINT fk_types_damage_class FOREIGN KEY (damage_class_id) REFERENCES damage_classes(id)
);

CREATE TABLE stats (
  id INT PRIMARY KEY,
  damage_class_id INT,
  identifier VARCHAR(255) NOT NULL,
  is_battle_only BOOLEAN NOT NULL,
  game_index INT,
  CONSTRAINT fk_stats_damage_class FOREIGN KEY (damage_class_id) REFERENCES damage_classes(id)
);

-- Growth and Egg Groups
CREATE TABLE growth_rates (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL,
  formula TEXT
);

CREATE TABLE egg_groups (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL
);

-- Genders and Characteristics
CREATE TABLE genders (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL
);

CREATE TABLE characteristics (
  id INT PRIMARY KEY,
  stat_id INT NOT NULL,
  gene_mod_5 INT NOT NULL,
  CONSTRAINT fk_characteristics_stat FOREIGN KEY (stat_id) REFERENCES stats(id)
);

-- Flavors and Natures
CREATE TABLE flavors (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL
);

CREATE TABLE natures (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL,
  decreased_stat_id INT NOT NULL,
  increased_stat_id INT NOT NULL,
  hates_flavor_id INT NOT NULL,
  likes_flavor_id INT NOT NULL,
  game_index INT,
  CONSTRAINT fk_natures_decreased_stat FOREIGN KEY (decreased_stat_id) REFERENCES stats(id),
  CONSTRAINT fk_natures_increased_stat FOREIGN KEY (increased_stat_id) REFERENCES stats(id),
  CONSTRAINT fk_natures_hates_flavor FOREIGN KEY (hates_flavor_id) REFERENCES flavors(id),
  CONSTRAINT fk_natures_likes_flavor FOREIGN KEY (likes_flavor_id) REFERENCES flavors(id)
);

-- Moves and Effects
CREATE TABLE move_targets (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL
);

CREATE TABLE move_effects (
  id INT PRIMARY KEY
);

CREATE TABLE languages (
  id INT PRIMARY KEY,
  iso639 VARCHAR(255) NOT NULL,
  iso3166 VARCHAR(255) NOT NULL,
  identifier VARCHAR(255) NOT NULL,
  official BOOLEAN NOT NULL,
  display_order INT
);

CREATE TABLE move_effect_prose (
  move_effect_id INT NOT NULL,
  local_language_id INT NOT NULL,
  short_effect VARCHAR(255) NOT NULL,
  effect TEXT,
  PRIMARY KEY (move_effect_id, local_language_id),
  CONSTRAINT fk_move_effect_prose_effect FOREIGN KEY (move_effect_id) REFERENCES move_effects(id),
  CONSTRAINT fk_move_effect_prose_language FOREIGN KEY (local_language_id) REFERENCES languages(id)
);

-- Contests (Moved up as referenced by moves)
CREATE TABLE contest_types (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL
);

CREATE TABLE contest_effects (
  id INT PRIMARY KEY,
  appeal INT NOT NULL,
  jam INT NOT NULL
);

CREATE TABLE moves (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL,
  generation_id INT NOT NULL,
  type_id INT NOT NULL,
  power INT,
  pp INT,
  accuracy INT,
  priority INT NOT NULL,
  target_id INT,
  damage_class_id INT,
  effect_id INT,
  effect_chance INT,
  contest_type_id INT,
  contest_effect_id INT,
  super_contest_effect_id INT,
  CONSTRAINT fk_moves_generation FOREIGN KEY (generation_id) REFERENCES generations(id),
  CONSTRAINT fk_moves_type FOREIGN KEY (type_id) REFERENCES types(id),
  CONSTRAINT fk_moves_target FOREIGN KEY (target_id) REFERENCES move_targets(id),
  CONSTRAINT fk_moves_damage_class FOREIGN KEY (damage_class_id) REFERENCES damage_classes(id),
  CONSTRAINT fk_moves_effect FOREIGN KEY (effect_id) REFERENCES move_effects(id),
  CONSTRAINT fk_moves_contest_type FOREIGN KEY (contest_type_id) REFERENCES contest_types(id),
  CONSTRAINT fk_moves_contest_effect FOREIGN KEY (contest_effect_id) REFERENCES contest_effects(id),
  CONSTRAINT fk_moves_super_contest_effect FOREIGN KEY (super_contest_effect_id) REFERENCES super_contest_effects(id)
);

-- Pokémon and Species
CREATE TABLE pokemon_colors (
    id INT PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL
);

CREATE TABLE pokemon_shapes (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL
);

CREATE TABLE pokemon_habitats (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL
);

CREATE TABLE evolution_chains (
  id INT PRIMARY KEY,
  baby_trigger_item_id INT
);

CREATE TABLE pokemon_species (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL,
  generation_id INT NOT NULL,
  evolves_from_species_id INT,
  evolution_chain_id INT NOT NULL,
  color_id INT NOT NULL,
  shape_id INT NOT NULL,
  habitat_id INT NOT NULL,
  gender_rate INT NOT NULL,
  capture_rate INT NOT NULL,
  base_happiness INT NOT NULL,
  is_baby BOOLEAN NOT NULL,
  hatch_counter INT,
  has_gender_differences BOOLEAN NOT NULL,
  growth_rate_id INT NULL,
  forms_switchable IN NULL,
  is_legendary BOOLEAN NOT NULL,
  is_mythical BOOLEAN NOT NULL,
  order_index INT,
  conquest_order INT,
  CONSTRAINT fk_pokemon_species_generation FOREIGN KEY (generation_id) REFERENCES generations(id),
  CONSTRAINT fk_pokemon_species_evolves_from FOREIGN KEY (evolves_from_species_id) REFERENCES pokemon_species(id),
  CONSTRAINT fk_pokemon_species_evolution_chain FOREIGN KEY (evolution_chain_id) REFERENCES evolution_chains(id),
  CONSTRAINT fk_pokemon_species_color FOREIGN KEY (color_id) REFERENCES pokemon_colors(id),
  CONSTRAINT fk_pokemon_species_shape FOREIGN KEY (shape_id) REFERENCES pokemon_shapes(id),
  CONSTRAINT fk_pokemon_species_habitat FOREIGN KEY (habitat_id) REFERENCES pokemon_habitats(id),
  CONSTRAINT fk_pokemon_species_growth_rate FOREIGN KEY (growth_rate_id) REFERENCES growth_rates(id)
);

CREATE TABLE pokemon (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL,
  species_id INT NOT NULL,
  height INT NOT NULL,
  weight INT NOT NULL,
  base_experience INT NOT NULL,
  order_index INT,
  is_default BOOLEAN NOT NULL,
  CONSTRAINT fk_pokemon_species FOREIGN KEY (species_id) REFERENCES pokemon_species(id)
);

CREATE TABLE version_groups (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL,
  generation_id INT NOT NULL,
  group_order INT,
  CONSTRAINT fk_version_groups_generation FOREIGN KEY (generation_id) REFERENCES generations(id)
);

-- ADDED versions table
CREATE TABLE versions (
  id INT PRIMARY KEY,
  version_group_id INT NOT NULL,
  identifier VARCHAR(255) NOT NULL,
  CONSTRAINT fk_versions_version_group FOREIGN KEY (version_group_id) REFERENCES version_groups(id)
);

CREATE TABLE pokemon_forms (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL,
  form_identifier VARCHAR(255),
  pokemon_id INT NOT NULL,
  introduced_in_version_group_id INT,
  is_default BOOLEAN NOT NULL,
  is_battle_only BOOLEAN NOT NULL,
  is_mega BOOLEAN NOT NULL,
  form_order INT,
  order_index INT,
  CONSTRAINT fk_pokemon_forms_pokemon FOREIGN KEY (pokemon_id) REFERENCES pokemon(id),
  CONSTRAINT fk_pokemon_forms_version_group FOREIGN KEY (introduced_in_version_group_id) REFERENCES version_groups(id)
);

-- Many-to-Many Pokémon Relationships
CREATE TABLE pokemon_stats (
  pokemon_id INT NOT NULL,
  stat_id INT NOT NULL,
  base_stat INT NOT NULL,
  effort INT NOT NULL,
  PRIMARY KEY (pokemon_id, stat_id),
  CONSTRAINT fk_pokemon_stats_pokemon FOREIGN KEY (pokemon_id) REFERENCES pokemon(id),
  CONSTRAINT fk_pokemon_stats_stat FOREIGN KEY (stat_id) REFERENCES stats(id)
);

CREATE TABLE pokemon_types (
  pokemon_id INT NOT NULL,
  type_id INT NOT NULL,
  slot INT NOT NULL,
  PRIMARY KEY (pokemon_id, type_id),
  CONSTRAINT fk_pokemon_types_pokemon FOREIGN KEY (pokemon_id) REFERENCES pokemon(id),
  CONSTRAINT fk_pokemon_types_type FOREIGN KEY (type_id) REFERENCES types(id)
);

-- Moved abilities table definition here for correct FK referencing
CREATE TABLE abilities (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL,
  generation_id INT NOT NULL,
  is_main_series BOOLEAN,
  CONSTRAINT fk_abilities_generation FOREIGN KEY (generation_id) REFERENCES generations(id)
);

CREATE TABLE pokemon_abilities (
  pokemon_id INT NOT NULL,
  ability_id INT NOT NULL,
  PRIMARY KEY (pokemon_id, ability_id),
  CONSTRAINT fk_pokemon_abilities_pokemon FOREIGN KEY (pokemon_id) REFERENCES pokemon(id),
  CONSTRAINT fk_pokemon_abilities_ability FOREIGN KEY (ability_id) REFERENCES abilities(id)
);

CREATE TABLE pokemon_egg_groups (
  pokemon_species_id INT NOT NULL,
  egg_group_id INT NOT NULL,
  PRIMARY KEY (pokemon_species_id, egg_group_id),
  CONSTRAINT fk_pokemon_egg_groups_species FOREIGN KEY (pokemon_species_id) REFERENCES pokemon_species(id),
  CONSTRAINT fk_pokemon_egg_groups_egg_group FOREIGN KEY (egg_group_id) REFERENCES egg_groups(id)
);

-- Locations and Areas
CREATE TABLE locations (
  id INT PRIMARY KEY,
  region_id INT NOT NULL,
  identifier VARCHAR(255) NOT NULL,
  CONSTRAINT fk_locations_region FOREIGN KEY (region_id) REFERENCES regions(id)
);

CREATE TABLE location_areas (
  id INT PRIMARY KEY,
  location_id INT NOT NULL,
  game_index INT NOT NULL,
  identifier VARCHAR(255),
  CONSTRAINT fk_location_areas_location FOREIGN KEY (location_id) REFERENCES locations(id)
);

-- Items and Item Categories (Moved item_categories before items)
CREATE TABLE item_categories (
  id INT PRIMARY KEY,
  pocket_id INT,
  identifier VARCHAR(255) NOT NULL
);

CREATE TABLE items (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL,
  category_id INT,
  cost INT NOT NULL,
  fling_power INT,
  fling_effect_id INT,
  CONSTRAINT fk_items_category FOREIGN KEY (category_id) REFERENCES item_categories(id)
);

-- Abilities and their Texts
CREATE TABLE ability_prose (
  ability_id INT NOT NULL,
  local_language_id INT NOT NULL,
  short_effect TEXT,
  effect TEXT,
  PRIMARY KEY (ability_id, local_language_id),
  CONSTRAINT fk_ability_prose_ability FOREIGN KEY (ability_id) REFERENCES abilities(id),
  CONSTRAINT fk_ability_prose_language FOREIGN KEY (local_language_id) REFERENCES languages(id)
);

CREATE TABLE ability_flavor_text (
  ability_id INT NOT NULL,
  version_group_id INT NOT NULL,
  language_id INT NOT NULL,
  flavor_text TEXT,
  PRIMARY KEY (ability_id, version_group_id, language_id),
  CONSTRAINT fk_ability_flavor_text_ability FOREIGN KEY (ability_id) REFERENCES abilities(id),
  CONSTRAINT fk_ability_flavor_text_version_group FOREIGN KEY (version_group_id) REFERENCES version_groups(id),
  CONSTRAINT fk_ability_flavor_text_language FOREIGN KEY (language_id) REFERENCES languages(id)
);

-- Moves Learned by Pokémon
CREATE TABLE pokemon_move_methods (
  id INT PRIMARY KEY,
  identifier VARCHAR(255) NOT NULL
);

CREATE TABLE pokemon_moves (
  pokemon_id INT NOT NULL,
  version_group_id INT NOT NULL,
  move_id INT NOT NULL,
  pokemon_move_method_id INT NOT NULL,
  level INT NOT NULL,
  move_order INT,
  mastery INT,
  PRIMARY KEY (pokemon_id, version_group_id, move_id, pokemon_move_method_id),
  CONSTRAINT fk_pokemon_moves_pokemon FOREIGN KEY (pokemon_id) REFERENCES pokemon(id),
  CONSTRAINT fk_pokemon_moves_version_group FOREIGN KEY (version_group_id) REFERENCES version_groups(id),
  CONSTRAINT fk_pokemon_moves_move FOREIGN KEY (move_id) REFERENCES moves(id),
  CONSTRAINT fk_pokemon_moves_method FOREIGN KEY (pokemon_move_method_id) REFERENCES pokemon_move_methods(id)
);

-- Berries and their Flavors
CREATE TABLE berries (
  id INT PRIMARY KEY,
  growth_time INT,
  max_harvest INT,
  natural_gift_power INT,
  size INT,
  smoothness INT,
  item_id INT,
  firmness_id INT,
  natural_gift_type_id INT,
  soil_dryness INT,
  CONSTRAINT fk_berries_item FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE berry_flavors (
  berry_id INT NOT NULL,
  contest_type_id INT NOT NULL,
  flavor_id INT NOT NULL,
  PRIMARY KEY (berry_id, contest_type_id),
  CONSTRAINT fk_berry_flavors_berry FOREIGN KEY (berry_id) REFERENCES berries(id),
  CONSTRAINT fk_berry_flavors_contest_type FOREIGN KEY (contest_type_id) REFERENCES contest_types(id),
  CONSTRAINT fk_berry_flavors_flavor FOREIGN KEY (flavor_id) REFERENCES flavors(id)
);

-- FK Adjustment for evolution_chains (now that items exist)
ALTER TABLE evolution_chains
ADD CONSTRAINT fk_evolution_chains_baby_trigger_item FOREIGN KEY (baby_trigger_item_id) REFERENCES items(id);