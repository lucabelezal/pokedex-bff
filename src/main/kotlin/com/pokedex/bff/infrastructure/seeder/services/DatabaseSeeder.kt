package com.pokedex.bff.infrastructure.seeder.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.bff.domain.entities.*
import com.pokedex.bff.domain.repositories.*
import com.pokedex.bff.application.dto.seeder.*
import com.pokedex.bff.infrastructure.utils.*
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException

@Service
class DatabaseSeeder(
    private val regionRepository: RegionRepository,
    private val typeRepository: TypeRepository,
    private val eggGroupRepository: EggGroupRepository,
    private val generationRepository: GenerationRepository,
    private val abilityRepository: AbilityRepository,
    private val speciesRepository: SpeciesRepository,
    private val statsRepository: StatsRepository,
    private val evolutionChainRepository: EvolutionChainRepository,
    private val pokemonRepository: PokemonRepository,
    private val pokemonAbilityRepository: PokemonAbilityRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(DatabaseSeeder::class.java)

    private fun <T> loadJson(filePath: String, typeReference: TypeReference<T>): T {
        try {
            val resource = ClassPathResource(filePath)
            return resource.inputStream.use { objectMapper.readValue(it, typeReference) }
        } catch (e: IOException) {
            logger.error("Failed to load JSON from $filePath: ${e.message}", e)
            throw RuntimeException("Error loading data from $filePath", e)
        }
    }

    private fun <T : Any, D> importSimpleData(
        dtos: List<D>,
        repository: JpaRepository<T, Long>,
        transform: (D) -> T
    ): ImportCounts {
        val counts = ImportCounts()
        dtos.forEach { dto ->
            try {
                val entity = transform(dto)
                repository.save(entity)
                counts.success++
            } catch (e: Exception) {
                counts.errors++
                logger.error("Error importing data with value $dto: ${e.message}", e)
            }
        }
        return counts
    }

    private fun logImportResult(entityName: String, counts: ImportCounts) {
        val statusEmoji = if (counts.errors == 0) "✅" else "❌"
        logger.info("  $statusEmoji Importação de $entityName concluída. Sucessos: ${counts.success}, Falhas: ${counts.errors}")
    }

    @Transactional
    fun importAll() {
        logger.info("====== 🚀 INICIANDO IMPORTAÇÃO DOS DADOS DA POKEDEX 🚀 ======")

        val importResults = mutableMapOf<String, ImportCounts>()
        var totalSuccess = 0
        var totalErrors = 0

        logger.info("Iniciando importação de Regiões...")
        val regionsCounts = importSimpleData(loadJson(JsonFile.REGIONS.filePath, object : TypeReference<List<RegionDto>>() {}), regionRepository) { dto ->
            RegionEntity(
                id = dto.id,
                name = dto.name
            )
        }
        importResults["Regiões"] = regionsCounts
        logImportResult("Regiões", regionsCounts)
        totalSuccess += regionsCounts.success
        totalErrors += regionsCounts.errors

        logger.info("Iniciando importação de Tipos...")
        val typesCounts = importSimpleData(loadJson(JsonFile.TYPES.filePath, object : TypeReference<List<TypeDto>>() {}), typeRepository) { dto ->
            TypeEntity(
                id = dto.id,
                name = dto.name,
                color = dto.color
            )
        }
        importResults["Tipos"] = typesCounts
        logImportResult("Tipos", typesCounts)
        totalSuccess += typesCounts.success
        totalErrors += typesCounts.errors

        logger.info("Iniciando importação de Grupos de Ovos...")
        val eggGroupsCounts = importSimpleData(loadJson(JsonFile.EGG_GROUPS.filePath, object : TypeReference<List<EggGroupDto>>() {}), eggGroupRepository) { dto ->
            EggGroupEntity(
                id = dto.id,
                name = dto.name
            )
        }
        importResults["Grupos de Ovos"] = eggGroupsCounts
        logImportResult("Grupos de Ovos", eggGroupsCounts)
        totalSuccess += eggGroupsCounts.success
        totalErrors += eggGroupsCounts.errors

        logger.info("Iniciando importação de Gerações...")
        val allRegionsMapForGenerations = regionRepository.findAll().associateBy { it.id }
        val generationsCounts = importSimpleData(loadJson(JsonFile.GENERATIONS.filePath, object : TypeReference<List<GenerationDto>>() {}), generationRepository) { dto ->
            val region = allRegionsMapForGenerations[dto.regionId]
                ?: throw IllegalArgumentException("Region with ID ${dto.regionId} not found for Generation ${dto.name}")
            GenerationEntity(id = dto.id, name = dto.name, region = region)
        }
        importResults["Gerações"] = generationsCounts
        logImportResult("Gerações", generationsCounts)
        totalSuccess += generationsCounts.success
        totalErrors += generationsCounts.errors

        logger.info("Iniciando importação de Habilidades...")
        val allGenerationsMapForAbilities = generationRepository.findAll().associateBy { it.id }
        val abilitiesCounts = importSimpleData(loadJson(JsonFile.ABILITIES.filePath, object : TypeReference<List<AbilityDto>>() {}), abilityRepository) { dto ->
            val generation = allGenerationsMapForAbilities[dto.introducedGenerationId]
                ?: throw IllegalArgumentException("Generation with ID ${dto.introducedGenerationId} not found for Ability ${dto.name}")
            AbilityEntity(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                introducedGeneration = generation
            )
        }
        importResults["Habilidades"] = abilitiesCounts
        logImportResult("Habilidades", abilitiesCounts)
        totalSuccess += abilitiesCounts.success
        totalErrors += abilitiesCounts.errors

        logger.info("Iniciando importação de Espécies...")
        val speciesCounts = importSimpleData(loadJson(JsonFile.SPECIES.filePath, object : TypeReference<List<SpeciesDto>>() {}), speciesRepository) { dto ->
            SpeciesEntity(
                id = dto.id,
                name = dto.name,
                pokemon_number = dto.pokemonNumber,
                speciesEn = dto.speciesEn,
                speciesPt = dto.speciesPt
            )
        }
        importResults["Espécies"] = speciesCounts
        logImportResult("Espécies", speciesCounts)
        totalSuccess += speciesCounts.success
        totalErrors += speciesCounts.errors

        logger.info("Iniciando importação de Estatísticas...")
        val statsCounts = importSimpleData(loadJson(JsonFile.STATS.filePath, object : TypeReference<List<StatsDto>>() {}), statsRepository) { dto ->
            StatsEntity(
                id = dto.id,
                total = dto.total,
                hp = dto.hp,
                attack = dto.attack,
                defense = dto.defense,
                spAtk = dto.spAtk,
                spDef = dto.spDef,
                speed = dto.speed
            )
        }
        importResults["Estatísticas"] = statsCounts
        logImportResult("Estatísticas", statsCounts)
        totalSuccess += statsCounts.success
        totalErrors += statsCounts.errors

        logger.info("Iniciando importação de Cadeias de Evolução...")
        val evolutionChainsCounts = importSimpleData(loadJson(JsonFile.EVOLUTION_CHAINS.filePath, object : TypeReference<List<EvolutionChainDto>>() {}), evolutionChainRepository) { dto ->
            val chainDataJsonString = objectMapper.writeValueAsString(dto.chainData)
            EvolutionChainEntity(id = dto.id, chainData = chainDataJsonString)
        }
        importResults["Cadeias de Evolução"] = evolutionChainsCounts
        logImportResult("Cadeias de Evolução", evolutionChainsCounts)
        totalSuccess += evolutionChainsCounts.success
        totalErrors += evolutionChainsCounts.errors

        logger.info("Iniciando importação de Pokémons...")
        val pokemonDtos = loadJson(JsonFile.POKEMONS.filePath, object : TypeReference<List<PokemonDto>>() {})
        val pokemonsCounts = ImportCounts()
        val regionsMapForPokemon = regionRepository.findAll().associateBy { it.id }
        val statsMapForPokemon = statsRepository.findAll().associateBy { it.id }
        val generationsMapForPokemon = generationRepository.findAll().associateBy { it.id }
        val speciesMapForPokemon = speciesRepository.findAll().associateBy { it.id }
        val eggGroupsMapForPokemon = eggGroupRepository.findAll().associateBy { it.id }
        val typesMapForPokemon = typeRepository.findAll().associateBy { it.id }
        val abilitiesMapForPokemon = abilityRepository.findAll().associateBy { it.id }
        val evolutionChainsMapForPokemon = evolutionChainRepository.findAll().associateBy { it.id }

        for (dto in pokemonDtos) {
            try {
                val region = dto.regionId?.let { regionsMapForPokemon[it] }
                val stats = statsMapForPokemon[dto.statsId] ?: throw IllegalArgumentException("Stats with ID ${dto.statsId} not found for Pokemon ${dto.name}")
                val generation = generationsMapForPokemon[dto.generationId] ?: throw IllegalArgumentException("Generation with ID ${dto.generationId} not found for Pokemon ${dto.name}")
                val species = speciesMapForPokemon[dto.speciesId] ?: throw IllegalArgumentException("Species with ID ${dto.speciesId} not found for Pokemon ${dto.name}")
                val evolutionChain = evolutionChainsMapForPokemon[dto.evolutionChainId]
                    ?: throw IllegalArgumentException("EvolutionChain with ID ${dto.evolutionChainId} not found for Pokemon ${dto.name}. Ensure EvolutionChains are imported first.")

                val eggGroups = dto.eggGroupIds.mapNotNull { eggGroupsMapForPokemon[it] }.toMutableSet()
                val types = dto.typeIds.mapNotNull { typesMapForPokemon[it] }.toMutableSet()

                val pokemon = PokemonEntity(
                    id = dto.id,
                    number = dto.number,
                    name = dto.name,
                    description = dto.description,
                    height = dto.height,
                    weight = dto.weight,
                    genderRateValue = dto.genderRateValue,
                    genderMale = dto.gender?.male, // Mapeando o novo campo
                    genderFemale = dto.gender?.female, // Mapeando o novo campo
                    eggCycles = dto.eggCycles,
                    stats = stats,
                    generation = generation,
                    species = species,
                    evolutionChain = evolutionChain,
                    region = region,
                    eggGroups = eggGroups,
                    types = types,
                    sprites = dto.sprites.toVO()
                )
                pokemonRepository.save(pokemon)

                dto.abilities.forEach { abilityDto ->
                    val ability = abilitiesMapForPokemon[abilityDto.abilityId]
                        ?: throw IllegalArgumentException("Ability with ID ${abilityDto.abilityId} not found for Pokemon ${dto.name}")
                    pokemonAbilityRepository.save(
                        PokemonAbilityEntity(
                            pokemon = pokemon,
                            ability = ability,
                            isHidden = abilityDto.isHidden
                        )
                    )
                }
                pokemonsCounts.success++
            } catch (e: Exception) {
                pokemonsCounts.errors++
                logger.error("Error importing Pokemon ${dto.name}: ${e.message}", e)
            }
        }
        importResults["Pokémons"] = pokemonsCounts
        logImportResult("Pokémons", pokemonsCounts)
        totalSuccess += pokemonsCounts.success
        totalErrors += pokemonsCounts.errors

        logger.info("Iniciando importação de Fraquezas...")
        val weaknessesDtos = loadJson(JsonFile.WEAKNESSES.filePath, object : TypeReference<List<WeaknessDto>>() {})
        val weaknessesCounts = ImportCounts()
        val pokemonMapByName = pokemonRepository.findAll().associateBy { it.name }
        val typeMapByName = typeRepository.findAll().associateBy { it.name }

        weaknessesDtos.forEach { weaknessDto ->
            try {
                val pokemon = pokemonMapByName[weaknessDto.pokemonName]
                if (pokemon == null) {
                    logger.warn("Pokemon with name '${weaknessDto.pokemonName}' not found for weakness import. Skipping.")
                    weaknessesCounts.errors++
                    return@forEach
                }

                val weaknessTypes = weaknessDto.weaknesses.mapNotNull { weaknessTypeName ->
                    val type = typeMapByName[weaknessTypeName]
                    if (type == null) {
                        logger.warn("Weakness type '${weaknessTypeName}' not found for Pokemon '${pokemon.name}'. Skipping this type.")
                        null
                    } else {
                        type
                    }
                }.toMutableSet()

                pokemon.weaknesses.addAll(weaknessTypes)
                pokemonRepository.save(pokemon)
                weaknessesCounts.success++
            } catch (e: Exception) {
                weaknessesCounts.errors++
                logger.error("Error importing weaknesses for ${weaknessDto.pokemonName}: ${e.message}", e)
            }
        }
        importResults["Fraquezas"] = weaknessesCounts
        logImportResult("Fraquezas", weaknessesCounts)
        totalSuccess += weaknessesCounts.success
        totalErrors += weaknessesCounts.errors

        logger.info("====== ✅ IMPORTAÇÃO DOS DADOS DA POKEDEX CONCLUÍDA ✅ ======")
        logger.info("✨ Resumo Geral da Importação:")
        logger.info("   * Sucessos Totais: $totalSuccess ")
        logger.info("   * Falhas Totais: $totalErrors")

        logger.info("--- Detalhes da Importação por Tabela ---")
        importResults.forEach { (entityName, counts) ->
            val detailStatusEmoji = if (counts.errors == 0) "✅ SUCESSO COMPLETO" else "❌ FALHAS"
            logger.info("  $entityName: $detailStatusEmoji (Sucessos: ${counts.success}, Falhas: ${counts.errors})")
        }
        logger.info("-----------------------------------------")
    }
}