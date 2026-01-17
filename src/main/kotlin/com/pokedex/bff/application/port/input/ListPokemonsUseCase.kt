package com.pokedex.bff.application.port.input

import com.pokedex.bff.domain.pokemon.entities.Pokemon
import com.pokedex.bff.domain.shared.Page

/**
 * Port (interface) para consulta de Pokémons com paginação.
 */
interface ListPokemonsUseCase {
    fun findAll(page: Int, size: Int): Page<Pokemon>
}
