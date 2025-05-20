package com.pokedex.bff.config

import com.pokedex.bff.models.Pokemon
import com.pokedex.bff.repositories.PokemonRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.io.File

@Component
@Profile("dev")
class DataLoader(
    private val pokemonRepository: PokemonRepository
) {

    @Value("\${data.csv.path}")
    private lateinit var csvPath: String

    @PostConstruct
    fun loadData() {
        val file = File(csvPath)
        if (!file.exists()) {
            println("CSV file not found: $csvPath")
            return
        }

        val lines = file.readLines().drop(1) // Remove header
        val pokemons = lines.mapNotNull { line ->
            val parts = line.split(",")
            try {
                Pokemon(
                    id = parts[0].toLong(),
                    identifier = parts[1],
                    speciesId = parts[2].toLongOrNull(),
                    height = parts[3].toIntOrNull(),
                    weight = parts[4].toIntOrNull(),
                    baseExperience = parts[5].toIntOrNull(),
                    order = parts[6].toIntOrNull(),
                    isDefault = parts[7] == "1"
                )
            } catch (e: Exception) {
                println("Error parsing line: $line — ${e.message}")
                null
            }
        }
        pokemonRepository.saveAll(pokemons)
    }
}
