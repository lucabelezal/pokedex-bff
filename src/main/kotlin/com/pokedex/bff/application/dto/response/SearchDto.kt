package com.pokedex.bff.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Informações sobre a busca atual na Pokedex")
data class SearchDto(
    @Schema(
        description = "Texto de placeholder para campo de busca",
        example = "Search Pokemons by name or number"
    )
    val placeholder: String
)