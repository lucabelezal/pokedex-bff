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
open class DatabaseSeeder(
    private val regionRepository: RegionRepository,
    private val generationRepository: GenerationRepository,
    private val typeRepository: TypeRepository,
    private val eggGroupRepository: EggGroupRepository,
    private val abilityRepository: AbilityRepository,
    private val statsRepository: StatsRepository,
    private val speciesRepository: SpeciesRepository,
    private val evolutionChainRepository: EvolutionChainRepository,
    private val pokemonRepository: PokemonRepository,
    private val pokemonAbilityRepository: PokemonAbilityRepository,
    private val objectMapper: ObjectMapper
) {
    // --------------------------
    // Constants and Configuration
    // --------------------------
    companion object {
        private val logger = LoggerFactory.getLogger(DatabaseSeeder::class.java)
    }

    // --------------------------
    // Public API
    // --------------------------
    @Transactional
    fun importAll() {
        logStart()
        val results = ImportResults()
        importRegions(results)
        importTypes(results)
        importEggGroups(results)
        importGenerations(results)
        importAbilities(results)
        importSpecies(results)
        importStats(results)
        importEvolutionChains(results)
        importPokemons(results)
        importWeaknesses(results)
        logFinalResults(results)
    }

    // --------------------------
    // Resource Loading
    // --------------------------
    internal open fun createClassPathResource(filePath: String): ClassPathResource {
        return ClassPathResource(filePath)
    }

    private inline fun <reified T> loadJson(filePath: String): T {
        return loadJson(filePath, object : TypeReference<T>() {})
    }

    private fun <T> loadJson(filePath: String, typeReference: TypeReference<T>): T {
        try {
            val resource = createClassPathResource(filePath)
            return resource.inputStream.use { objectMapper.readValue(it, typeReference) }
        } catch (e: IOException) {
            logger.error("Failed to load JSON from $filePath: ${e.message}", e)
            throw DataImportException("Error loading data from $filePath", e)
        }
    }

    // --------------------------
    // Data Import Methods
    // --------------------------
    private fun importRegions(results: ImportResults) {
        executeImport<RegionDto, RegionEntity>(
            "Regiões",
            JsonFile.REGIONS.filePath,
            regionRepository,
            { dto -> RegionEntity(id = dto.id, name = dto.name) },
            results
        )
    }

    private fun importTypes(results: ImportResults) {
        executeImport<TypeDto, TypeEntity>(
            "Tipos",
            JsonFile.TYPES.filePath,
            typeRepository,
            { dto -> TypeEntity(id = dto.id, name = dto.name, color = dto.color) },
            results
        )
    }

    private fun importEggGroups(results: ImportResults) {
        executeImport<EggGroupDto, EggGroupEntity>(
            "Grupos de Ovos",
            JsonFile.EGG_GROUPS.filePath,
            eggGroupRepository,
            { dto -> EggGroupEntity(id = dto.id, name = dto.name) },
            results
        )
    }


    private fun importGenerations(results: ImportResults) {
        logger.info("Iniciando importação de Gerações...")
        val regionsMap = regionRepository.findAll().associateBy { it.id }
        val dtos = loadJson<List<GenerationDto>>(JsonFile.GENERATIONS.filePath)
        val counts = importDependentData<GenerationDto, GenerationEntity>(dtos, generationRepository) { dto ->
            val region = regionsMap[dto.regionId] ?: throw DataImportException("Region with ID ${dto.regionId} not found")
            GenerationEntity(id = dto.id, name = dto.name, region = region)
        }
        results.add("Gerações", counts)
    }

    private fun importAbilities(results: ImportResults) {
        logger.info("Iniciando importação de Habilidades...")
        val generationsMap = generationRepository.findAll().associateBy { it.id }
        val dtos = loadJson<List<AbilityDto>>(JsonFile.ABILITIES.filePath)
        val counts = importDependentData<AbilityDto, AbilityEntity>(dtos, abilityRepository) { dto ->
            val generation = generationsMap[dto.introducedGenerationId] ?: throw DataImportException("Generation with ID ${dto.introducedGenerationId} not found")
            AbilityEntity(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                introducedGeneration = generation
            )
        }
        results.add("Habilidades", counts)
    }


    private fun importSpecies(results: ImportResults) {
        executeImport<SpeciesDto, SpeciesEntity>(
            "Espécies",
            JsonFile.SPECIES.filePath,
            speciesRepository,
            { dto ->
                SpeciesEntity(
                    id = dto.id,
                    name = dto.name,
                    pokemon_number = dto.pokemonNumber,
                    speciesEn = dto.speciesEn,
                    speciesPt = dto.speciesPt
                )
            },
            results
        )
    }


    private fun importStats(results: ImportResults) {
        executeImport<StatsDto, StatsEntity>(
            "Estatísticas",
            JsonFile.STATS.filePath,
            statsRepository,
            { dto ->
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
            },
            results
        )
    }

    private fun importEvolutionChains(results: ImportResults) {
        executeImport<EvolutionChainDto, EvolutionChainEntity>(
            "Cadeias de Evolução",
            JsonFile.EVOLUTION_CHAINS.filePath,
            evolutionChainRepository,
            { dto ->
                val chainDataJsonString = objectMapper.writeValueAsString(dto.chainData)
                EvolutionChainEntity(id = dto.id, chainData = chainDataJsonString)
            },
            results
        )
    }
    private fun importPokemons(results: ImportResults) {
        logger.info("Iniciando importação de Pokémons...")
        val dtos = loadJson<List<PokemonDto>>(JsonFile.POKEMONS.filePath)
        val counts = ImportCounts()

        // Preload all required relations
        val relations = PokemonImportRelations(
            regions = regionRepository.findAll().associateBy { it.id },
            stats = statsRepository.findAll().associateBy { it.id },
            generations = generationRepository.findAll().associateBy { it.id },
            species = speciesRepository.findAll().associateBy { it.id },
            eggGroups = eggGroupRepository.findAll().associateBy { it.id },
            types = typeRepository.findAll().associateBy { it.id },
            abilities = abilityRepository.findAll().associateBy { it.id },
            evolutionChains = evolutionChainRepository.findAll().associateBy { it.id }
        )

        dtos.forEach { dto ->
            try {
                importPokemon(dto, relations)
                counts.success++
            } catch (e: Exception) {
                counts.errors++
                logger.error("Error importing Pokemon ${dto.name}: ${e.message}", e)
            }
        }

        results.add("Pokémons", counts)
    }

    private fun importWeaknesses(results: ImportResults) {
        logger.info("Iniciando importação de Fraquezas...")
        val dtos = loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath)
        val counts = ImportCounts()

        val pokemonMap = pokemonRepository.findAll().associateBy { it.name }
        val typeMap = typeRepository.findAll().associateBy { it.name }

        dtos.forEach { dto ->
            try {
                val pokemon = pokemonMap[dto.pokemonName]
                if (pokemon == null) {
                    logger.warn("Pokémon '${dto.pokemonName}' não encontrado para importar fraquezas")
                    counts.errors++
                    return@forEach
                }

                val weaknessTypes = dto.weaknesses.mapNotNull { typeName ->
                    typeMap[typeName]?.also {
                        if (it !in pokemon.types) {
                            pokemon.weaknesses.add(it)
                        }
                    } ?: run {
                        logger.warn("Tipo '$typeName' não encontrado para fraqueza do ${dto.pokemonName}")
                        null
                    }
                }

                if (weaknessTypes.isNotEmpty()) {
                    pokemonRepository.save(pokemon)
                    counts.success++
                } else {
                    counts.errors++
                }
            } catch (e: Exception) {
                counts.errors++
                logger.error("Erro importando fraquezas para ${dto.pokemonName}: ${e.message}", e)
            }
        }

        results.add("Fraquezas", counts)
    }

    // --------------------------
    // Helper Methods
    // --------------------------
    private inline fun <reified D, T : Any> executeImport(
        entityName: String,
        filePath: String,
        repository: JpaRepository<T, Long>,
        noinline transform: (D) -> T,
        results: ImportResults
    ) {
        logger.info("Iniciando importação de $entityName...")
        val dtos: List<D> = loadJson(filePath)
        val counts = importSimpleData(dtos, repository, transform)
        results.add(entityName, counts)
    }

    private fun <T : Any, D> importSimpleData(
        dtos: List<D>,
        repository: JpaRepository<T, Long>,
        transform: (D) -> T
    ): ImportCounts {
        val counts = ImportCounts()
        dtos.forEach { dto ->
            try {
                repository.save(transform(dto))
                counts.success++
            } catch (e: Exception) {
                counts.errors++
                logger.error("Error importing data with value $dto: ${e.message}", e)
            }
        }
        return counts
    }

    private fun <D, T : Any> importDependentData(
        dtos: List<D>,
        repository: JpaRepository<T, Long>,
        transform: (D) -> T
    ): ImportCounts {
        val counts = ImportCounts()
        dtos.forEach { dto ->
            try {
                repository.save(transform(dto))
                counts.success++
            } catch (e: DataImportException) {
                counts.errors++
                logger.error("Data dependency error: ${e.message}")
            } catch (e: Exception) {
                counts.errors++
                logger.error("Error importing data: ${e.message}", e)
            }
        }
        return counts
    }

    private fun importPokemon(dto: PokemonDto, relations: PokemonImportRelations) {
        val pokemon = PokemonEntity(
            id = dto.id,
            number = dto.number,
            name = dto.name,
            description = dto.description,
            height = dto.height,
            weight = dto.weight,
            genderRateValue = dto.genderRateValue,
            genderMale = dto.gender?.male,
            genderFemale = dto.gender?.female,
            eggCycles = dto.eggCycles,
            stats = relations.stats[dto.statsId] ?: throw DataImportException("Stats with ID ${dto.statsId} not found"),
            generation = relations.generations[dto.generationId] ?: throw DataImportException("Generation with ID ${dto.generationId} not found"),
            species = relations.species[dto.speciesId] ?: throw DataImportException("Species with ID ${dto.speciesId} not found"),
            evolutionChain = relations.evolutionChains[dto.evolutionChainId] ?: throw DataImportException("EvolutionChain with ID ${dto.evolutionChainId} not found"),
            region = dto.regionId?.let { relations.regions[it] },
            eggGroups = dto.eggGroupIds.mapNotNull { relations.eggGroups[it] }.toMutableSet(),
            types = dto.typeIds.mapNotNull { relations.types[it] }.toMutableSet(),
            sprites = dto.sprites.toVO()
        )

        val savedPokemon = pokemonRepository.save(pokemon)

        dto.abilities.forEach { abilityDto ->
            val ability = relations.abilities[abilityDto.abilityId] ?: throw DataImportException("Ability with ID ${abilityDto.abilityId} not found")
            pokemonAbilityRepository.save(
                PokemonAbilityEntity(
                    pokemon = savedPokemon,
                    ability = ability,
                    isHidden = abilityDto.isHidden
                )
            )
        }
    }

    // --------------------------
    // Logging Methods
    // --------------------------
    private fun logStart() {
        logger.info("====== 🚀 INICIANDO IMPORTAÇÃO DOS DADOS DA POKEDEX 🚀 ======")
    }

    private fun logFinalResults(results: ImportResults) {
        results.logFinalResults()
    }

    // --------------------------
    // Helper Classes
    // --------------------------
    private class ImportCounts(
        var success: Int = 0,
        var errors: Int = 0
    )

    private class ImportResults {
        private val results = mutableMapOf<String, ImportCounts>()
        var totalSuccess = 0
        var totalErrors = 0

        fun add(entityName: String, counts: ImportCounts) {
            results[entityName] = counts
            totalSuccess += counts.success
            totalErrors += counts.errors
            logImportResult(entityName, counts)
        }

        fun logFinalResults() {
            logger.info("====== ✅ IMPORTAÇÃO CONCLUÍDA ✅ ======")
            logger.info("✨ Resumo Geral: Sucessos: $totalSuccess, Falhas: $totalErrors")
            logger.info("--- Detalhes por Tabela ---")
            results.forEach { (name, counts) ->
                val status = if (counts.errors == 0) "✅ SUCESSO" else "❌ FALHAS"
                logger.info("  $name: $status (${counts.success}✓, ${counts.errors}✗)")
            }
        }

        private fun logImportResult(entityName: String, counts: ImportCounts) {
            val emoji = if (counts.errors == 0) "✅" else "❌"
            logger.info("  $emoji $entityName: ${counts.success}✓, ${counts.errors}✗")
        }
    }

    private data class PokemonImportRelations(
        val regions: Map<Long, RegionEntity>,
        val stats: Map<Long, StatsEntity>,
        val generations: Map<Long, GenerationEntity>,
        val species: Map<Long, SpeciesEntity>,
        val eggGroups: Map<Long, EggGroupEntity>,
        val types: Map<Long, TypeEntity>,
        val abilities: Map<Long, AbilityEntity>,
        val evolutionChains: Map<Long, EvolutionChainEntity>
    )

    private class DataImportException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
}