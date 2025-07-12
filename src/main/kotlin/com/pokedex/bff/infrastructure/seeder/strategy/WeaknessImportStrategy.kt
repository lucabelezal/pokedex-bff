package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.repositories.PokemonRepository
import com.pokedex.bff.domain.repositories.TypeRepository
import com.pokedex.bff.application.dto.seeder.WeaknessDto
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Order(10)
class WeaknessImportStrategy(
    private val pokemonRepository: PokemonRepository,
    private val typeRepository: TypeRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(WeaknessImportStrategy::class.java)
        private const val ENTITY_NAME = "Fraquezas"
    }

    override fun getEntityName(): String = ENTITY_NAME

    @Transactional // Each pokemon's weaknesses update should be transactional
    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $ENTITY_NAME...")
        val dtos: List<WeaknessDto> = jsonLoader.loadJson(JsonFile.WEAKNESSES.filePath)
        val counts = ImportCounts()

        // Preload data
        val pokemonMap = pokemonRepository.findAll().associateBy { it.name }
        val typeMap = typeRepository.findAll().associateBy { it.name }

        dtos.forEach { dto ->
            try {
                val pokemon = pokemonMap[dto.pokemonName]
                if (pokemon == null) {
                    logger.warn("Pokémon '${dto.pokemonName}' não encontrado para importar fraquezas. DTO ignorado.")
                    counts.errors++
                    return@forEach // continue to next dto
                }

                // 1. Find all valid TypeEntity objects for the weaknesses listed in the DTO.
                val validWeaknessTypes = dto.weaknesses.mapNotNull { typeName ->
                    typeMap[typeName] ?: run {
                        logger.warn("Tipo de fraqueza '$typeName' não encontrado no banco de dados para o Pokémon ${dto.pokemonName}")
                        null
                    }
                }

                // 2. Add all valid types to the Pokemon's weaknesses set.
                // The addAll method returns true if the set was modified.
                val wereNewWeaknessesAdded = pokemon.weaknesses.addAll(validWeaknessTypes)

                // 3. Save only if new weaknesses were actually added.
                if (wereNewWeaknessesAdded) {
                    pokemonRepository.save(pokemon)
                    counts.success++
                } else {
                    // If the DTO had weakness names, but none were valid, it's an error.
                    if (validWeaknessTypes.isEmpty() && dto.weaknesses.isNotEmpty()) {
                        logger.error("Nenhum dos tipos de fraqueza especificados para ${dto.pokemonName} era válido: ${dto.weaknesses}")
                        counts.errors++
                    }
                    // If no new weaknesses were added because they were already present,
                    // or if the DTO weakness list was empty, we do nothing. It's not a success, but not an error either.
                }

            } catch (e: Exception) {
                counts.errors++
                logger.error("Erro inesperado ao importar fraquezas para ${dto.pokemonName}: ${e.message}", e)
            }
        }

        results.add(ENTITY_NAME, counts)
        return counts
    }
}
