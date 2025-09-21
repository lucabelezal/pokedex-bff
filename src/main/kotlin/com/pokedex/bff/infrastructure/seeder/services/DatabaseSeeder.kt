package com.pokedex.bff.infrastructure.seeder.services

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
open class DatabaseSeeder(
    private val jdbcTemplate: JdbcTemplate
) {

    companion object {
        private val logger = LoggerFactory.getLogger(DatabaseSeeder::class.java)
    }

    fun importAll() {
        logStart()
        executeSqlScripts()
        logEnd()
    }

    private fun executeSqlScripts() {
        val scripts = listOf(
            "classpath:sql/01_region.sql",
            "classpath:sql/02_type.sql",
            "classpath:sql/03_egg_group.sql",
            "classpath:sql/04_generation.sql",
            "classpath:sql/05_ability.sql",
            "classpath:sql/06_species.sql",
            "classpath:sql/07_stats.sql",
            "classpath:sql/08_evolution_chains.sql",
            "classpath:sql/09_weaknesses.sql",
            "classpath:sql/10_pokemon.sql"
        )

        scripts.forEach { script ->
            logger.info("Executando script: $script")
            jdbcTemplate.execute(script)
        }
    }

    private fun logStart() {
        logger.info("====== 🚀 INICIANDO IMPORTAÇÃO DOS DADOS DA POKEDEX 🚀 ======")
    }

    private fun logEnd() {
        logger.info("====== ✅ IMPORTAÇÃO DOS DADOS DA POKEDEX FINALIZADA ✅ ======")
    }

}