package com.pokedex.bff.service

import com.pokedex.bff.controller.dtos.*
import com.pokedex.bff.repository.PokemonRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PokemonService(
    private val pokemonRepository: PokemonRepository
) {

    @Transactional(readOnly = true)
    fun getPokemons(page: Int, size: Int): PokemonListResponse {
        val pageable = PageRequest.of(page, size)
        val pageResult = pokemonRepository.findAll(pageable)

        val pokemonDetails = pageResult.content.map { pokemon ->
            val formattedNumber = "Nº${pokemon.number ?: "UNK"}"
            val mainType = pokemon.types.firstOrNull()

            PokemonDetail(
                number = formattedNumber,
                name = pokemon.name,
                image = PokemonImage(
                    url = pokemon.sprites?.other?.home?.frontDefault ?: "",
                    element = PokemonImageElement(
                        color = mainType?.color ?: "#CCCCCC",
                        type = mainType?.name?.uppercase() ?: "UNKNOWN"
                    )
                ),
                types = pokemon.types.map { type ->
                    PokemonType(
                        name = type.name,
                        color = type.color ?: "#000000"
                    )
                }.toList()
            )
        }

        return PokemonListResponse(
            pageInfo = PageInfo(
                currentPage = pageResult.number,
                totalPages = pageResult.totalPages,
                totalElements = pageResult.totalElements,
                hasNext = pageResult.hasNext()
            ),
            search = Search(placeholder = "Buscar Pokémon..."),
            filters = emptyList(),
            pokemons = pokemonDetails
        )
    }
}