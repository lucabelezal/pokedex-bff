package com.pokedex.bff.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Paginated Pokedex list response")
data class PokedexListResponse(
    @Schema(description = "Current page information")
    val pageInfo: PageInfoDto,

    @Schema(description = "Current search data")
    val search: SearchDto,

    @Schema(description = "Applied filters (generic list)", example = "[]")
    val filters: List<Any>,

    @Schema(description = "List of Pokemons")
    val pokemons: List<PokemonDto>
)