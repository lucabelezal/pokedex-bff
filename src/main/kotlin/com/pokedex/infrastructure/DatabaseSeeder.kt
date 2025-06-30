package com.pokedex.infrastructure

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.domain.dto.PokemonDTO
import com.pokedex.domain.entity.Pokemon
import com.pokedex.domain.repository.PokemonRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class DatabaseSeeder(
    private val pokemonRepository: PokemonRepository,
    private val objectMapper: ObjectMapper
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (pokemonRepository.count() == 0L) {
            val pokemonsDTO: List<PokemonDTO> = loadPokemons()
            pokemonsDTO.forEach { dto ->
                val pokemon = Pokemon(
                    id = dto.id,
                    name = dto.name,
                    height = dto.height,
                    weight = dto.weight,
                    genderMale = dto.gender.male,
                    genderFemale = dto.gender.female
                    // TODO: Map other fields like types, stats from DTO to entity
                )
                pokemonRepository.save(pokemon)
            }
        }
    }

    internal fun loadPokemons(): List<PokemonDTO> { // Changed to internal for testability
        val inputStream: InputStream = javaClass.getResourceAsStream("/data/10_pokemon.json")
            ?: throw RuntimeException("Cannot find 10_pokemon.json. Make sure the file exists in src/main/resources/data/")
        return objectMapper.readValue(inputStream, object : TypeReference<List<PokemonDTO>>() {})
    }
}
