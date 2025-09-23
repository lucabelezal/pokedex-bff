package com.pokedex.bff.application.usecases.pokedex

import com.pokedex.bff.application.dto.response.PageInfoDto
import com.pokedex.bff.application.dto.response.PokedexListResponse
import com.pokedex.bff.application.dto.response.PokemonDto
import com.pokedex.bff.application.dto.response.PokemonImageDto
import com.pokedex.bff.application.dto.response.PokemonImageElementDto
import com.pokedex.bff.application.dto.response.PokemonTypeDto
import com.pokedex.bff.application.dto.response.SearchDto
import com.pokedex.bff.domain.repositories.PokemonRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Use case for retrieving paginated Pokemon list for Pokedex display
 * 
 * This use case orchestrates the business logic for fetching Pokemon data,
 * formatting it for display, and providing pagination information.
 */
@Component
class GetPaginatedPokemonsUseCase(
    private val pokemonRepository: PokemonRepository
) {

    /**
     * Executes the use case to get a paginated list of Pokemon
     * 
     * Business rules:
     * - Format Pokemon number as "Nº{number}" or "NºUNK" if unknown
     * - Use main type (first type) for image element styling
     * - Provide default colors if type color is missing
     * - Include pagination metadata
     * 
     * @param page the page number (zero-based)
     * @param size the number of Pokemon per page
     * @return formatted Pokemon list with pagination info
     */
    @Transactional(readOnly = true)
    fun execute(page: Int, size: Int): PokedexListResponse {
        validatePaginationParameters(page, size)
        
        val pageResult = pokemonRepository.findAll(page, size)

        val pokemons: List<PokemonDto> = pageResult.content.map { pokemon ->
            val formattedNumber = formatPokemonNumber(pokemon.number)
            val mainType = pokemon.types.firstOrNull()

            PokemonDto(
                number = formattedNumber,
                name = pokemon.name,
                image = PokemonImageDto(
                    url = pokemon.sprites?.other?.home ?: "",
                    element = PokemonImageElementDto(
                        color = mainType?.color ?: DEFAULT_TYPE_COLOR,
                        type = mainType?.name?.uppercase() ?: DEFAULT_TYPE_NAME
                    )
                ),
                types = pokemon.types.map { type ->
                    PokemonTypeDto(
                        name = type.name,
                        color = type.color ?: DEFAULT_TYPE_COLOR
                    )
                }.toList()
            )
        }

        return PokedexListResponse(
            pageInfo = createPageInfo(pageResult),
            search = SearchDto(placeholder = "Search Pokémon..."),
            filters = emptyList(), // TODO: Implement filtering logic in separate use case
            pokemons = pokemons
        )
    }

    private fun validatePaginationParameters(page: Int, size: Int) {
        require(page >= 0) { "Page number must be non-negative" }
        require(size > 0) { "Page size must be positive" }
        require(size <= MAX_PAGE_SIZE) { "Page size cannot exceed $MAX_PAGE_SIZE" }
    }

    private fun formatPokemonNumber(number: String?): String {
        return if (number.isNullOrBlank()) {
            "Nº$DEFAULT_NUMBER"
        } else {
            "Nº$number"
        }
    }

    private fun createPageInfo(pageResult: com.pokedex.bff.domain.common.Page<*>): PageInfoDto {
        return PageInfoDto(
            currentPage = pageResult.pageNumber,
            totalPages = pageResult.totalPages,
            totalElements = pageResult.totalElements,
            hasNext = pageResult.hasNext
        )
    }

    companion object {
        private const val DEFAULT_TYPE_COLOR = "#CCCCCC"
        private const val DEFAULT_TYPE_NAME = "UNKNOWN"
        private const val DEFAULT_NUMBER = "UNK"
        private const val MAX_PAGE_SIZE = 100
    }
}