package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.StatsEntity
import com.pokedex.bff.domain.repositories.StatsRepository
import com.pokedex.bff.application.dto.seeder.StatsDto
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class StatsImportStrategy(
    private val statsRepository: StatsRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(StatsImportStrategy::class.java)
        private const val ENTITY_NAME = "Estatísticas"
    }

    override fun getEntityName(): String = ENTITY_NAME

    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $ENTITY_NAME...")
        val dtos: List<StatsDto> = jsonLoader.loadJson(JsonFile.STATS.filePath)
        val counts = importSimpleData(dtos, statsRepository) { dto ->
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
