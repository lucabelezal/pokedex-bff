package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.SpeciesEntity
import com.pokedex.bff.domain.repositories.SpeciesRepository
import com.pokedex.bff.application.dto.seeder.SpeciesDto
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class SpeciesImportStrategy(
    private val speciesRepository: SpeciesRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(SpeciesImportStrategy::class.java)
        private const val ENTITY_NAME = "Espécies"
    }

    override fun getEntityName(): String = ENTITY_NAME

    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $ENTITY_NAME...")
        val dtos: List<SpeciesDto> = jsonLoader.loadJson(JsonFile.SPECIES.filePath)
        val counts = importSimpleData(dtos, speciesRepository) { dto ->
            SpeciesEntity(
                id = dto.id,
                name = dto.name,
                pokemon_number = dto.pokemonNumber,
                speciesEn = dto.speciesEn,
                speciesPt = dto.speciesPt
            )
        }
        results.add(ENTITY_NAME, counts)
        return counts
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
                logger.error("Error importing data for $ENTITY_NAME with value $dto: ${e.message}", e)
            }
        }
        return counts
    }
}
