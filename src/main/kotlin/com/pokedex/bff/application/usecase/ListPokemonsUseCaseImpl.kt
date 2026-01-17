package com.pokedex.bff.application.usecase

import com.pokedex.bff.application.port.input.ListPokemonsUseCase
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import com.pokedex.bff.domain.pokemon.repository.PokemonRepository
import com.pokedex.bff.domain.shared.Page
import org.slf4j.LoggerFactory

class ListPokemonsUseCaseImpl(
    private val pokemonRepository: PokemonRepository
) : ListPokemonsUseCase {

    private val logger = LoggerFactory.getLogger(ListPokemonsUseCaseImpl::class.java)

    override fun findAll(page: Int, size: Int): Page<Pokemon> {
        logger.debug("Fetching pokemons - page: {}, size: {}", page, size)
        return pokemonRepository.findAll(page, size)
    }
}
