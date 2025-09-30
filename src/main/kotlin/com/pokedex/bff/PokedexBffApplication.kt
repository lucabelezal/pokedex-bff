package com.pokedex.bff

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories("com.pokedex.bff.adapters.output.persistence.repository")
class PokedexBffApplication

fun main(args: Array<String>) {
    runApplication<PokedexBffApplication>(*args)
}
