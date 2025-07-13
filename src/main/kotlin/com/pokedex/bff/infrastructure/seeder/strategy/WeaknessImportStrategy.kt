package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.repositories.PokemonRepository
import com.pokedex.bff.domain.repositories.TypeRepository
import com.pokedex.bff.application.dto.seeder.WeaknessDto
import com.pokedex.bff.infrastructure.seeder.data.EntityType
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
        private val entityName = EntityType.WEAKNESS.entityName
    }

    override fun getEntityName(): String = entityName

    @Transactional
    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $entityName...")
        val dtos: List<WeaknessDto> = jsonLoader.loadJson(JsonFile.WEAKNESSES.filePath)
        val counts = ImportCounts()

        val pokemonMap = pokemonRepository.findAll().associateBy { it.name }
        val typeMap = typeRepository.findAll().associateBy { it.name }

        dtos.forEach { dto ->
            try {
                val pokemon = pokemonMap[dto.pokemonName]
                if (pokemon == null) {
                    logger.warn("Pokémon '${dto.pokemonName}' não encontrado para importar fraquezas. DTO ignorado.")
                    counts.errors++
                    return@forEach
                }

                val validWeaknessTypes = dto.weaknesses.mapNotNull { typeName ->
                    typeMap[typeName] ?: run {
                        logger.warn("Tipo de fraqueza '$typeName' não encontrado no banco de dados para o Pokémon ${dto.pokemonName}")
                        null
                    }
                }

                val wereNewWeaknessesAdded = pokemon.weaknesses.addAll(validWeaknessTypes)

                if (wereNewWeaknessesAdded) {
                    pokemonRepository.save(pokemon)
                    counts.success++
                } else {
                    if (validWeaknessTypes.isEmpty() && dto.weaknesses.isNotEmpty()) {
                        logger.error("Nenhum dos tipos de fraqueza especificados para ${dto.pokemonName} era válido: ${dto.weaknesses}")
                        counts.errors++
                    }
                }

            } catch (e: Exception) {
                counts.errors++
                logger.error("Erro inesperado ao importar fraquezas para ${dto.pokemonName}: ${e.message}", e)
            }
        }

        results.add(entityName, counts)
        return counts
    }
}
