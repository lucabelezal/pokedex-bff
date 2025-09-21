package com.pokedex.bff.infrastructure.seeder.runners

import com.pokedex.bff.infrastructure.seeder.services.DatabaseSeeder
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner

// Removido @Component para não ser executado automaticamente no ciclo do BFF
class DataImportRunner(private val seeder: DatabaseSeeder) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(DataImportRunner::class.java)

    override fun run(vararg args: String?) {
        logger.info("Executing Pokedex data import on application startup...")
        seeder.importAll()
    }
}