package com.pokedex.bff.application.pokedex.services

import com.pokedex.bff.application.pokedex.dto.response.*
import com.pokedex.bff.domain.pokedex.repositories.PokemonRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PokedexService(
    private val pokemonRepository: PokemonRepository
) {

    @Transactional(readOnly = true)
    fun getPokemons(page: Int, size: Int): PokedexListResponse {
        val pageable = PageRequest.of(page, size)
        val pageResult = pokemonRepository.findAll(pageable)

        val pokemons: List<PokemonDto> = pageResult.content.map { pokemon ->
            val formattedNumber = "Nº${pokemon.number ?: "UNK"}"
            val mainType = pokemon.types.firstOrNull()

            PokemonDto(
                number = formattedNumber,
                name = pokemon.name,
                image = PokemonImageDto(
                    url = pokemon.sprites?.other?.home?.frontDefault ?: "",
                    element = PokemonImageElementDto(
                        color = mainType?.color ?: "#CCCCCC",
                        type = mainType?.name?.uppercase() ?: "UNKNOWN"
                    )
                ),
                types = pokemon.types.map { type ->
                    PokemonTypeDto(
                        name = type.name,
                        color = type.color ?: "#000000"
                    )
                }.toList()
            )
        }

        return PokedexListResponse(
            pageInfo = PageInfoDto(
                currentPage = pageResult.number,
                totalPages = pageResult.totalPages,
                totalElements = pageResult.totalElements,
                hasNext = pageResult.hasNext()
            ),
            search = SearchDto(placeholder = "Buscar Pokémon..."),
            filters = emptyList(),
            pokemons = pokemons
        )
    }
}