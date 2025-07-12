package com.pokedex.bff.infrastructure.seeder.services

import com.pokedex.bff.infrastructure.seeder.strategy.ImportStrategy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
open class DatabaseSeeder(
    private val strategies: List<ImportStrategy>
) {

    companion object {
        private val logger = LoggerFactory.getLogger(DatabaseSeeder::class.java)
    }

    @Transactional
    fun importAll() {
        logStart()
        val results = com.pokedex.bff.infrastructure.seeder.dto.ImportResults()
        strategies.forEach { strategy ->
            logger.info("Executando strategy: ${strategy.getEntityName()}...")
            try {
                val counts = strategy.import(results)
            } catch (e: Exception) {
                logger.error("Erro executando strategy ${strategy.getEntityName()}: ${e.message}", e)
            }
        }
        logFinalResults(results)
    }

    private fun logStart() {
        logger.info("====== 🚀 INICIANDO IMPORTAÇÃO DOS DADOS DA POKEDEX 🚀 ======")
    }

    private fun logFinalResults(results: com.pokedex.bff.infrastructure.seeder.dto.ImportResults) {
        results.logFinalResults()
    }

}