package com.pokedex.bff.application.usecase

import com.pokedex.bff.application.port.input.CreatePokemonUseCase
import com.pokedex.bff.application.dtos.input.CreatePokemonInput
import com.pokedex.bff.application.dtos.output.PokemonOutput
import com.pokedex.bff.domain.pokemon.repository.PokemonRepository
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import com.pokedex.bff.domain.pokemon.entities.Type
import com.pokedex.bff.domain.pokemon.entities.Stats
import com.pokedex.bff.domain.pokemon.entities.Sprites
import com.pokedex.bff.domain.pokemon.exception.InvalidPokemonException
import org.slf4j.LoggerFactory
import kotlin.random.Random

class CreatePokemonUseCaseImpl(
    private val pokemonRepository: PokemonRepository
) : CreatePokemonUseCase {
    
    private val logger = LoggerFactory.getLogger(CreatePokemonUseCaseImpl::class.java)
    
    override fun execute(input: CreatePokemonInput): PokemonOutput {
        logger.info("Creating new Pokemon with name: {}", input.name)
        
        // Validação de entrada
        if (input.name.isBlank()) {
            throw InvalidPokemonException("Pokemon name cannot be blank")
        }
        
        // ID será gerado automaticamente pelo banco de dados (@GeneratedValue).
        // Usamos 0L como placeholder porque o JPA substitui pelo ID gerado no save.
        val newId = 0L
        
        // Criar Pokémon com dados básicos
        // TODO: Em uma implementação completa, os tipos viriam do input ou de uma API externa
        val defaultType = Type(
            id = 1L,
            name = "Normal",
            color = "#A8A878"
        )
        
        val pokemon = Pokemon(
            id = newId,
            number = newId.toString().padStart(4, '0'),
            name = input.name,
            height = Random.nextDouble(0.3, 2.5),
            weight = Random.nextDouble(5.0, 100.0),
            description = "A newly discovered Pokémon named ${input.name}",
            sprites = Sprites(
                frontDefault = null,
                backDefault = null,
                frontShiny = null,
                backShiny = null,
                frontFemale = null,
                backFemale = null,
                frontShinyFemale = null,
                backShinyFemale = null,
                other = null
            ),
            genderRateValue = 4,
            genderMale = 0.5f,
            genderFemale = 0.5f,
            eggCycles = 20,
            stats = Stats(
                id = newId,
                total = 300,
                hp = 50,
                attack = 50,
                defense = 50,
                spAtk = 50,
                spDef = 50,
                speed = 50
            ),
            generation = null,
            species = null,
            region = null,
            evolutionChain = null,
            types = setOf(defaultType),
            abilities = emptySet(),
            eggGroups = emptySet(),
            weaknesses = emptySet()
        )
        
        // Salvar no repositório
        val savedPokemon = pokemonRepository.save(pokemon)
        logger.info("Pokemon created successfully with ID: {}", savedPokemon.id)
        
        return PokemonOutput.fromDomain(savedPokemon)
    }

}
