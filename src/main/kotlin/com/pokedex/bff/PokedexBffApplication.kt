package com.pokedex.bff

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PokedexBffApplication

fun main() {
    // Start the Spring Boot application without forwarding CLI args.
    // This avoids using the spread operator which Detekt flags.
    runApplication<PokedexBffApplication>()
}
