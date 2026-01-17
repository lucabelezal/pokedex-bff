package com.pokedex.bff.application.interactor

import com.pokedex.bff.application.dtos.input.CreatePokemonInput
import com.pokedex.bff.application.dtos.output.PokemonOutput
import com.pokedex.bff.application.usecase.CreatePokemonUseCase
import com.pokedex.bff.domain.pokemon.repository.PokemonRepository
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import com.pokedex.bff.domain.shared.Page

@Deprecated(
    message = "Use CreatePokemonUseCaseImpl (application/usecase) com wiring em UseCaseFactory",
    level = DeprecationLevel.WARNING
)
class CreatePokemonInteractor(
    private val pokemonRepository: PokemonRepository
) : CreatePokemonUseCase {
    override fun execute(input: CreatePokemonInput): PokemonOutput {
        // TODO: Implementar lógica de criação de Pokémon
        val pokemon = Pokemon(
            id = 0L,
            number = "000",
            name = input.name,
            height = 1.0,
            weight = 1.0,
            description = "Placeholder description",
            sprites = null,
            genderRateValue = 0,
            genderMale = 0.5f,
            genderFemale = 0.5f,
            eggCycles = 10,
            stats = null,
            generation = null,
            species = null,
            region = null,
            evolutionChain = null,
            types = emptySet(),
            abilities = emptySet(),
            eggGroups = emptySet(),
            weaknesses = emptySet()
        )
        pokemonRepository.save(pokemon)
        return PokemonOutput.fromDomain(pokemon)
    }

    override fun findAll(page: Int, size: Int): Page<Pokemon> {
        return pokemonRepository.findAll(page, size)
    }
}
