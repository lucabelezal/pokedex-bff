package com.pokedex.bff.service

import com.pokedex.bff.models.*
import com.pokedex.bff.repositories.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.nio.charset.StandardCharsets


@Service
class CsvImportService(
    private val superContestEffectRepository: SuperContestEffectRepository,
    private val regionRepository: RegionRepository,
    private val generationRepository: GenerationRepository,
    private val damageClassRepository: DamageClassRepository,
    private val typeRepository: TypeRepository,
    private val statRepository: StatRepository,
    private val growthRateRepository: GrowthRateRepository,
    private val eggGroupRepository: EggGroupRepository,
    private val genderRepository: GenderRepository,
    private val characteristicRepository: CharacteristicRepository,
    private val flavorRepository: FlavorRepository,
    private val natureRepository: NatureRepository,
    private val moveTargetRepository: MoveTargetRepository,
    private val moveEffectRepository: MoveEffectRepository,
    private val languageRepository: LanguageRepository,
    private val moveEffectProseRepository: MoveEffectProseRepository,
    private val moveRepository: MoveRepository,
    private val pokemonColorRepository: PokemonColorRepository,
    private val pokemonShapeRepository: PokemonShapeRepository,
    private val pokemonHabitatRepository: PokemonHabitatRepository,
    private val evolutionChainRepository: EvolutionChainRepository,
    private val pokemonSpeciesRepository: PokemonSpeciesRepository,
    private val pokemonRepository: PokemonRepository,
    private val versionGroupRepository: VersionGroupRepository,
    private val pokemonFormRepository: PokemonFormRepository,
    private val pokemonStatRepository: PokemonStatRepository,
    private val pokemonTypeRepository: PokemonTypeRepository,
    private val abilityRepository: AbilityRepository,
    private val pokemonAbilityRepository: PokemonAbilityRepository,
    private val pokemonEggGroupRepository: PokemonEggGroupRepository,
    private val locationRepository: LocationRepository,
    private val pokemonLocationAreaRepository: PokemonLocationAreaRepository,
    private val contestTypeRepository: ContestTypeRepository,
    private val contestEffectRepository: ContestEffectRepository,
    private val itemCategoryRepository: ItemCategoryRepository,
    private val itemRepository: ItemRepository,
    private val abilityProseRepository: AbilityProseRepository,
    private val abilityFlavorTextRepository: AbilityFlavorTextRepository,
    private val pokemonMoveMethodRepository: PokemonMoveMethodRepository,
    private val pokemonMoveRepository: PokemonMoveRepository,
    private val berryRepository: BerryRepository,
    private val berryFlavorRepository: BerryFlavorRepository
) {

    private val logger = LoggerFactory.getLogger(CsvImportService::class.java)

    private val csvMappers: Map<String, Pair<CrudRepository<*, *>, (CSVRecord) -> Any>> = mapOf(
        "super_contest_effects" to (superContestEffectRepository to { record ->
            SuperContestEffect(
                id = record.get("id").toInt(),
                appeal = record.get("appeal").toInt()
            )
        }),
        "regions" to (regionRepository to { record ->
            Region(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "generations" to (generationRepository to { record ->
            Generation(
                id = record.get("id").toInt(),
                mainRegionId = record.get("main_region_id").toIntOrNull(),
                identifier = record.get("identifier")
            )
        }),
        "damage_classes" to (damageClassRepository to { record ->
            DamageClass(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "types" to (typeRepository to { record ->
            Type(
                id = record.get("id").toInt(),
                identifier = record.get("identifier"),
                generationId = record.get("generation_id").toInt(),
                damageClassId = record.get("damage_class_id").toIntOrNull()
            )
        }),
        "stats" to (statRepository to { record ->
            Stat(
                id = record.get("id").toInt(),
                damageClassId = record.get("damage_class_id").toIntOrNull(),
                identifier = record.get("identifier"),
                isBattleOnly = record.get("is_battle_only").toBooleanStrictOrNull() ?: false,
                gameIndex = record.get("game_index").toIntOrNull()
            )
        }),
        "growth_rates" to (growthRateRepository to { record ->
            GrowthRate(
                id = record.get("id").toInt(),
                identifier = record.get("identifier"),
                formula = record.get("formula")
            )
        }),
        "egg_groups" to (eggGroupRepository to { record ->
            EggGroup(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "genders" to (genderRepository to { record ->
            Gender(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "characteristics" to (characteristicRepository to { record ->
            Characteristic(
                id = record.get("id").toInt(),
                statId = record.get("stat_id").toInt(),
                geneMod5 = record.get("gene_mod_5").toInt()
            )
        }),
        "flavors" to (flavorRepository to { record ->
            Flavor(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "natures" to (natureRepository to { record ->
            Nature(
                id = record.get("id").toInt(),
                identifier = record.get("identifier"),
                decreasedStatId = record.get("decreased_stat_id").toInt(),
                increasedStatId = record.get("increased_stat_id").toInt(),
                hatesFlavorId = record.get("hates_flavor_id").toInt(),
                likesFlavorId = record.get("likes_flavor_id").toInt(),
                gameIndex = record.get("game_index").toIntOrNull()
            )
        }),
        "move_targets" to (moveTargetRepository to { record ->
            MoveTarget(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "move_effects" to (moveEffectRepository to { record ->
            MoveEffect(
                id = record.get("id").toInt()
            )
        }),
        "languages" to (languageRepository to { record ->
            Language(
                id = record.get("id").toInt(),
                iso639 = record.get("iso639"),
                iso3166 = record.get("iso3166"),
                identifier = record.get("identifier"),
                official = record.get("official").toBooleanStrictOrNull() ?: false,
                displayOrder = record.get("order").toIntOrNull()
            )
        }),
        "move_effect_prose" to (moveEffectProseRepository to { record ->
            MoveEffectProse(
                id = MoveEffectProseId(
                    moveEffectId = record.get("move_effect_id").toInt(),
                    localLanguageId = record.get("local_language_id").toInt()
                ),
                shortEffect = record.get("short_effect"),
                effect = record.get("effect")
            )
        }),
        "moves" to (moveRepository to { record ->
            Move(
                id = record.get("id").toInt(),
                identifier = record.get("identifier"),
                generationId = record.get("generation_id").toInt(),
                typeId = record.get("type_id").toInt(),
                power = record.get("power").toIntOrNull(),
                pp = record.get("pp").toIntOrNull(),
                accuracy = record.get("accuracy").toIntOrNull(),
                priority = record.get("priority").toInt(),
                targetId = record.get("target_id").toIntOrNull(),
                damageClassId = record.get("damage_class_id").toIntOrNull(),
                effectId = record.get("effect_id").toIntOrNull(),
                effectChance = record.get("effect_chance").toIntOrNull(),
                contestTypeId = record.get("contest_type_id").toIntOrNull(),
                contestEffectId = record.get("contest_effect_id").toIntOrNull(),
                superContestEffectId = record.get("super_contest_effect_id").toIntOrNull()
            )
        }),
        "pokemon_colors" to (pokemonColorRepository to { record ->
            PokemonColor(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "pokemon_shapes" to (pokemonShapeRepository to { record ->
            PokemonShape(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "pokemon_habitats" to (pokemonHabitatRepository to { record ->
            PokemonHabitat(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "evolution_chains" to (evolutionChainRepository to { record ->
            EvolutionChain(
                id = record.get("id").toInt(),
                babyTriggerItemId = record.get("baby_trigger_item_id").toIntOrNull()
            )
        }),
        "pokemon_species" to (pokemonSpeciesRepository to { record ->
            PokemonSpecies(
                id = record.get("id").toInt(),
                identifier = record.get("identifier"),
                generationId = record.get("generation_id").toInt(),
                evolvesFromSpeciesId = record.get("evolves_from_species_id").toIntOrNull(),
                evolutionChainId = record.get("evolution_chain_id").toInt(),
                colorId = record.get("color_id").toInt(),
                shapeId = record.get("shape_id").toInt(),
                habitatId = record.get("habitat_id").toInt(),
                genderRate = record.get("gender_rate").toInt(),
                captureRate = record.get("capture_rate").toInt(),
                baseHappiness = record.get("base_happiness").toInt(),
                isBaby = record.get("is_baby").toBooleanStrictOrNull() ?: false,
                hatchCounter = record.get("hatch_counter").toIntOrNull(),
                hasGenderDifferences = record.get("has_gender_differences").toBooleanStrictOrNull() ?: false,
                growthRateId = record.get("growth_rate_id").toIntOrNull(),
                formsSwitchable = record.get("forms_switchable").toIntOrNull(),
                isLegendary = record.get("is_legendary").toBooleanStrictOrNull() ?: false,
                isMythical = record.get("is_mythical").toBooleanStrictOrNull() ?: false,
                orderIndex = record.get("order").toIntOrNull(),
                conquestOrder = record.get("conquest_order").toIntOrNull()
            )
        }),
        "pokemon" to (pokemonRepository to { record ->
            Pokemon(
                id = record.get("id").toInt(),
                identifier = record.get("identifier"),
                speciesId = record.get("species_id").toInt(),
                height = record.get("height").toInt(),
                weight = record.get("weight").toInt(),
                baseExperience = record.get("base_experience").toInt(),
                orderIndex = record.get("order").toIntOrNull(),
                isDefault = record.get("is_default").toBooleanStrictOrNull() ?: false
            )
        }),
        "version_groups" to (versionGroupRepository to { record ->
            VersionGroup(
                id = record.get("id").toInt(),
                identifier = record.get("identifier"),
                generationId = record.get("generation_id").toInt(),
                groupOrder = record.get("order").toIntOrNull()
            )
        }),
        "pokemon_forms" to (pokemonFormRepository to { record ->
            PokemonForm(
                id = record.get("id").toInt(),
                identifier = record.get("identifier"),
                formIdentifier = record.get("form_identifier"),
                pokemonId = record.get("pokemon_id").toInt(),
                introducedInVersionGroupId = record.get("introduced_in_version_group_id").toIntOrNull(),
                isDefault = record.get("is_default").toBooleanStrictOrNull() ?: false,
                isBattleOnly = record.get("is_battle_only").toBooleanStrictOrNull() ?: false,
                isMega = record.get("is_mega").toBooleanStrictOrNull() ?: false,
                formOrder = record.get("form_order").toIntOrNull(),
                orderIndex = record.get("order_index").toIntOrNull()
            )
        }),
        "pokemon_stats" to (pokemonStatRepository to { record ->
            PokemonStat(
                id = PokemonStatId(
                    pokemonId = record.get("pokemon_id").toInt(),
                    statId = record.get("stat_id").toInt()
                ),
                baseStat = record.get("base_stat").toInt(),
                effort = record.get("effort").toInt()
            )
        }),
        "pokemon_types" to (pokemonTypeRepository to { record ->
            PokemonType(
                id = PokemonTypeId(
                    pokemonId = record.get("pokemon_id").toInt(),
                    typeId = record.get("type_id").toInt()
                ),
                slot = record.get("slot").toInt()
            )
        }),
        "abilities" to (abilityRepository to { record ->
            Ability(
                id = record.get("id").toInt(),
                identifier = record.get("identifier"),
                generationId = record.get("generation_id").toInt(),
                isMainSeries = record.get("is_main_series").toBooleanStrictOrNull()
            )
        }),
        "pokemon_abilities" to (pokemonAbilityRepository to { record ->
            PokemonAbility(
                id = PokemonAbilityId(
                    pokemonId = record.get("pokemon_id").toInt(),
                    abilityId = record.get("ability_id").toInt()
                ),
                isHidden = record.get("is_hidden").toBooleanStrictOrNull() ?: false,
                slot = record.get("slot").toInt()
            )
        }),
        "pokemon_egg_groups" to (pokemonEggGroupRepository to { record ->
            PokemonEggGroup(
                id = PokemonEggGroupId(
                    pokemonSpeciesId = record.get("pokemon_species_id").toInt(),
                    eggGroupId = record.get("egg_group_id").toInt()
                )
            )
        }),
        "locations" to (locationRepository to { record ->
            Location(
                id = record.get("id").toInt(),
                regionId = record.get("region_id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "pokemon_location_areas" to (pokemonLocationAreaRepository to { record ->
            LocationArea(
                id = record.get("id").toInt(),
                locationId = record.get("location_id").toInt(),
                gameIndex = record.get("game_index").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "contest_types" to (contestTypeRepository to { record ->
            ContestType(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "contest_effects" to (contestEffectRepository to { record ->
            ContestEffect(
                id = record.get("id").toInt(),
                appeal = record.get("appeal").toInt(),
                jam = record.get("jam").toInt()
            )
        }),
        "item_categories" to (itemCategoryRepository to { record ->
            ItemCategory(
                id = record.get("id").toInt(),
                pocketId = record.get("pocket_id").toIntOrNull(),
                identifier = record.get("identifier")
            )
        }),
        "items" to (itemRepository to { record ->
            Item(
                id = record.get("id").toInt(),
                identifier = record.get("identifier"),
                categoryId = record.get("category_id").toIntOrNull(),
                cost = record.get("cost").toInt(),
                flingPower = record.get("fling_power").toIntOrNull(),
                flingEffectId = record.get("fling_effect_id").toIntOrNull()
            )
        }),
        "ability_prose" to (abilityProseRepository to { record ->
            AbilityProse(
                id = AbilityProseId(
                    abilityId = record.get("ability_id").toInt(),
                    localLanguageId = record.get("local_language_id").toInt()
                ),
                shortEffect = record.get("short_effect"),
                effect = record.get("effect")
            )
        }),
        "ability_flavor_text" to (abilityFlavorTextRepository to { record ->
            AbilityFlavorText(
                id = AbilityFlavorTextId(
                    abilityId = record.get("ability_id").toInt(),
                    versionGroupId = record.get("version_group_id").toInt(),
                    languageId = record.get("language_id").toInt()
                ),
                flavorText = record.get("flavor_text")
            )
        }),
        "pokemon_move_methods" to (pokemonMoveMethodRepository to { record ->
            PokemonMoveMethod(
                id = record.get("id").toInt(),
                identifier = record.get("identifier")
            )
        }),
        "pokemon_moves" to (pokemonMoveRepository to { record ->
            PokemonMove(
                id = PokemonMoveId(
                    pokemonId = record.get("pokemon_id").toInt(),
                    versionGroupId = record.get("version_group_id").toInt(),
                    moveId = record.get("move_id").toInt(),
                    pokemonMoveMethodId = record.get("pokemon_move_method_id").toInt()
                ),
                level = record.get("level").toInt(),
                moveOrder = record.get("order").toIntOrNull(),
                mastery = record.get("mastery").toIntOrNull()
            )
        }),
        "berries" to (berryRepository to { record ->
            Berry(
                id = record.get("id").toInt(),
                growthTime = record.get("growth_time").toIntOrNull(),
                maxHarvest = record.get("max_harvest").toIntOrNull(),
                naturalGiftPower = record.get("natural_gift_power").toIntOrNull(),
                size = record.get("size").toIntOrNull(),
                smoothness = record.get("smoothness").toIntOrNull(),
                firmnessId = record.get("firmness_id").toIntOrNull(),
                itemId = record.get("item_id").toIntOrNull(),
                naturalGiftTypeId = record.get("natural_gift_type_id").toIntOrNull(),
                soilDryness = record.get("soil_dryness").toIntOrNull()
            )
        }),
        "berry_flavors" to (berryFlavorRepository to { record ->
            BerryFlavor(
                id = BerryFlavorId(
                    berryId = record.get("berry_id").toInt(),
                    contestTypeId = record.get("contest_type_id").toInt()
                ),
                flavorId = record.get("flavor_id").toInt()
            )
        })
    )

    @Transactional
    fun <T> loadCsvData(
        inputStream: InputStream,
        csvFileName: String,
        repository: CrudRepository<T, *>,
        mapper: (CSVRecord) -> T,
        batchSize: Int = 100
    ): Int {
        var insertedRows = 0
        try {
            val reader = inputStream.bufferedReader(StandardCharsets.UTF_8)
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())

            csvParser.use { parser ->
                val entitiesToSave = mutableListOf<T>()
                for (record in parser) {
                    try {
                        val entity = mapper(record)
                        entitiesToSave.add(entity)

                        if (entitiesToSave.size >= batchSize) {
                            repository.saveAll(entitiesToSave)
                            insertedRows += entitiesToSave.size
                            entitiesToSave.clear()
                        }
                    } catch (e: Exception) {
                        logger.error("Erro ao processar a linha do CSV '$csvFileName': $record", e)
                        throw e
                    }
                }
                if (entitiesToSave.isNotEmpty()) {
                    repository.saveAll(entitiesToSave)
                    insertedRows += entitiesToSave.size
                }
            }
            logger.info("Importação de '$csvFileName' concluída. Inseridas $insertedRows linhas.")
        } catch (e: Exception) {
            logger.error("Erro fatal ao importar o arquivo CSV '$csvFileName'", e)
            throw e
        }
        return insertedRows
    }

    @Transactional
    fun importCsvForTable(tableName: String, inputStream: InputStream): Int {
        val (repository, mapper) = csvMappers[tableName.lowercase()]
            ?: throw IllegalArgumentException("Mapeamento para a tabela '$tableName' não encontrado. Verifique se o nome da tabela está correto e se foi adicionado no CsvImportService.")

        @Suppress("UNCHECKED_CAST")
        return loadCsvData(
            inputStream = inputStream,
            csvFileName = "$tableName.csv",
            repository = repository as CrudRepository<Any, *>,
            mapper = mapper as (CSVRecord) -> Any
        )
    }
}