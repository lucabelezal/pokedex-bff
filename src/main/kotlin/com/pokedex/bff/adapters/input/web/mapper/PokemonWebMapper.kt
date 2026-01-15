package com.pokedex.bff.adapters.input.web.mapper

import com.pokedex.bff.adapters.input.web.dto.request.CreatePokemonWebRequest
import com.pokedex.bff.adapters.input.web.dto.response.PokemonPageResponse
import com.pokedex.bff.adapters.input.web.dto.response.PokemonWebResponse
import com.pokedex.bff.application.dtos.input.CreatePokemonInput
import com.pokedex.bff.application.dtos.output.PokemonOutput
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import com.pokedex.bff.domain.shared.Page
import org.springframework.stereotype.Component

@Component
class PokemonWebMapper {
    fun toCreatePokemonInput(request: CreatePokemonWebRequest): CreatePokemonInput =
        CreatePokemonInput(
            name = request.name
        )

    fun toWebResponse(output: PokemonOutput): PokemonWebResponse =
        PokemonWebResponse(
            id = output.id,
            name = output.name,
            types = output.types
        )

    fun toPageResponse(page: Page<Pokemon>): PokemonPageResponse =
        PokemonPageResponse(
            pokemons = page.content.map { toWebResponse(PokemonOutput.fromDomain(it)) },
            totalElements = page.totalElements,
            currentPage = page.pageNumber,
            totalPages = page.totalPages,
            hasNext = page.hasNext
        )
}
