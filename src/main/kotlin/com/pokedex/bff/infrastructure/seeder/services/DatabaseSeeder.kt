package com.pokedex.bff.infrastructure.seeder.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.bff.domain.repositories.*
import com.pokedex.bff.infrastructure.seeder.strategy.ImportStrategy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
open class DatabaseSeeder(
    private val strategies: List<ImportStrategy>,
    // ObjectMapper and specific repositories might still be needed if JsonLoader or strategies don't encapsulate everything.
    // For now, assuming strategies handle their own repository needs via injection.
    // private val objectMapper: ObjectMapper
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
        val results = com.pokedex.bff.infrastructure.seeder.dto.ImportResults()
        strategies.forEach { strategy ->
            logger.info("Executando strategy: ${strategy.getEntityName()}...")
            try {
                val counts = strategy.import(results) // strategy.import now adds to results directly or returns counts to be added
                // If strategy.import returns counts, uncomment next line and adjust strategy.import
                // results.add(strategy.getEntityName(), counts)
            } catch (e: Exception) {
                logger.error("Erro executando strategy ${strategy.getEntityName()}: ${e.message}", e)
                // Optionally, add to results as a global error for this strategy if not handled within
            }
        }
        logFinalResults(results)
    }

    // --------------------------
    // Data Import Methods (All moved to strategy classes)
    // --------------------------

    // --------------------------
    // Helper Methods (All moved or adapted into strategy classes)
    // --------------------------

    // --------------------------
    // Logging Methods
    // --------------------------
    private fun logStart() {
        logger.info("====== 🚀 INICIANDO IMPORTAÇÃO DOS DADOS DA POKEDEX 🚀 ======")
    }

    private fun logFinalResults(results: com.pokedex.bff.infrastructure.seeder.dto.ImportResults) {
        results.logFinalResults()
    }

    // --------------------------
    // Helper Classes (These have been moved to dto package)
    // --------------------------
    // private class ImportCounts(...) - MOVED
    // private class ImportResults {...} - MOVED
    // private data class PokemonImportRelations(...) - MOVED
    // private class DataImportException(...) - MOVED
}