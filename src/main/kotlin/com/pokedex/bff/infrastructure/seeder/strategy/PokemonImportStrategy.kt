package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.PokemonAbilityEntity
import com.pokedex.bff.domain.entities.PokemonEntity
import com.pokedex.bff.domain.repositories.*
import com.pokedex.bff.application.dto.seeder.PokemonDto
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.dto.PokemonImportRelations
import com.pokedex.bff.infrastructure.seeder.exception.DataImportException
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PokemonImportStrategy(
    private val pokemonRepository: PokemonRepository,
    private val regionRepository: RegionRepository,
    private val statsRepository: StatsRepository,
    private val generationRepository: GenerationRepository,
    private val speciesRepository: SpeciesRepository,
    private val eggGroupRepository: EggGroupRepository,
    private val typeRepository: TypeRepository,
    private val abilityRepository: AbilityRepository,
    private val evolutionChainRepository: EvolutionChainRepository,
    private val pokemonAbilityRepository: PokemonAbilityRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(PokemonImportStrategy::class.java)
        private const val ENTITY_NAME = "Pokémons"
    }

    override fun getEntityName(): String = ENTITY_NAME

    @Transactional
    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $ENTITY_NAME...")
        val dtos: List<PokemonDto> = jsonLoader.loadJson(JsonFile.POKEMONS.filePath)
        val counts = ImportCounts()

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
                importSinglePokemon(dto, relations)
                counts.success++
            } catch (e: DataImportException) {
                counts.errors++
                logger.error("Data dependency error importing Pokemon ${dto.name}: ${e.message}")
            } catch (e: Exception) {
                counts.errors++
                logger.error("Error importing Pokemon ${dto.name}: ${e.message}", e)
            }
        }

        results.add(ENTITY_NAME, counts)
        return counts
    }

    private fun importSinglePokemon(dto: PokemonDto, relations: PokemonImportRelations) {
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
            stats = relations.stats[dto.statsId]
                ?: throw DataImportException("Stats with ID ${dto.statsId} not found for Pokemon ${dto.name}"),
            generation = relations.generations[dto.generationId]
                ?: throw DataImportException("Generation with ID ${dto.generationId} not found for Pokemon ${dto.name}"),
            species = relations.species[dto.speciesId]
                ?: throw DataImportException("Species with ID ${dto.speciesId} not found for Pokemon ${dto.name}"),
            evolutionChain = relations.evolutionChains[dto.evolutionChainId]
                ?: throw DataImportException("EvolutionChain with ID ${dto.evolutionChainId} not found for Pokemon ${dto.name}"),
            region = dto.regionId?.let { relations.regions[it] },
            eggGroups = dto.eggGroupIds.mapNotNull { relations.eggGroups[it] }.toMutableSet(),
            types = dto.typeIds.mapNotNull { relations.types[it] }.toMutableSet(),
            sprites = dto.sprites.toVO()
        )

        val savedPokemon = pokemonRepository.save(pokemon)

        dto.abilities.forEach { abilityDto ->
            val ability = relations.abilities[abilityDto.abilityId]
                ?: throw DataImportException("Ability with ID ${abilityDto.abilityId} not found for Pokemon ${dto.name}")
            pokemonAbilityRepository.save(
                PokemonAbilityEntity(
                    pokemon = savedPokemon,
                    ability = ability,
                    isHidden = abilityDto.isHidden
                )
            )
        }
    }
}
