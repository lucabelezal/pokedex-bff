package com.pokedex.bff.infrastructure.seeder.strategy

import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.bff.domain.entities.EvolutionChainEntity
import com.pokedex.bff.domain.repositories.EvolutionChainRepository
import com.pokedex.bff.application.dto.seeder.EvolutionChainDto
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
@Order(8)
class EvolutionChainImportStrategy(
    private val evolutionChainRepository: EvolutionChainRepository,
    private val jsonLoader: JsonLoader,
    private val objectMapper: ObjectMapper // Needed for converting chainData to JSON string
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(EvolutionChainImportStrategy::class.java)
        private const val ENTITY_NAME = "Cadeias de Evolução"
    }

    override fun getEntityName(): String = ENTITY_NAME

    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $ENTITY_NAME...")
        val dtos: List<EvolutionChainDto> = jsonLoader.loadJson(JsonFile.EVOLUTION_CHAINS.filePath)
        val counts = importSimpleData(dtos, evolutionChainRepository) { dto ->
            val chainDataJsonString = objectMapper.writeValueAsString(dto.chainData)
            EvolutionChainEntity(id = dto.id, chainData = chainDataJsonString)
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
