package com.pokedex.bff.infrastructure.seeder.strategy

import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.bff.domain.entities.EvolutionChainEntity
import com.pokedex.bff.domain.repositories.EvolutionChainRepository
import com.pokedex.bff.application.dto.seeder.EvolutionChainDto
import com.pokedex.bff.infrastructure.seeder.data.EntityType
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.seeder.util.importData
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
@Order(8)
class EvolutionChainImportStrategy(
    private val evolutionChainRepository: EvolutionChainRepository,
    private val jsonLoader: JsonLoader,
    private val objectMapper: ObjectMapper
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(EvolutionChainImportStrategy::class.java)
        private val entityName = EntityType.EVOLUTION_CHAIN.entityName
    }

    override fun getEntityName(): String = entityName

    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $entityName...")
        val dtos: List<EvolutionChainDto> = jsonLoader.loadJson(JsonFile.EVOLUTION_CHAINS.filePath)
        val counts = importData(dtos, evolutionChainRepository, { dto ->
            val chainDataJsonString = objectMapper.writeValueAsString(dto.chainData)
            EvolutionChainEntity(id = dto.id, chainData = chainDataJsonString)
        }, logger, entityName)
        results.add(entityName, counts)
        return counts
    }
}
