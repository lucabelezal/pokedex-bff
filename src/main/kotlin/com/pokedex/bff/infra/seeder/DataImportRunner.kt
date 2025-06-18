package com.pokedex.bff.infra.seeder

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataImportRunner(private val seeder: DatabaseSeeder) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(DataImportRunner::class.java)

    override fun run(vararg args: String?) {
        logger.info("Executing Pokedex data import on application startup...")
        seeder.importAll()
    }
}
