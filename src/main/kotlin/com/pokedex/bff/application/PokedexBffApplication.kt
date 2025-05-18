package com.pokedex.bff.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PokedexBffApplication

fun main(args: Array<String>) {
	runApplication<PokedexBffApplication>(*args)
}
