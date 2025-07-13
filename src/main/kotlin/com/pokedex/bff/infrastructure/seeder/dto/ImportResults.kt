package com.pokedex.bff.infrastructure.seeder.dto

import org.slf4j.LoggerFactory

class ImportResults {
    private val results = mutableMapOf<String, ImportCounts>()
    var totalSuccess = 0
    var totalErrors = 0

    companion object {
        private val logger = LoggerFactory.getLogger(ImportResults::class.java)
    }

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
