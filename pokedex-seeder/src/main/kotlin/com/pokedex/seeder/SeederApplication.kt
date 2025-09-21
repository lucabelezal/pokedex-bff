package com.pokedex.seeder

import com.pokedex.bff.infrastructure.seeder.services.DatabaseSeeder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(scanBasePackages = ["com.pokedex.bff.infrastructure.seeder", "com.pokedex.bff.infrastructure", "com.pokedex.bff.domain"])
class SeederApplication {
    @Bean
    fun runSeeder(seeder: DatabaseSeeder) = org.springframework.boot.CommandLineRunner {
        println("Executando seeder...")
        seeder.importAll()
    }
}

fun main(args: Array<String>) {
    runApplication<SeederApplication>(*args)
}

