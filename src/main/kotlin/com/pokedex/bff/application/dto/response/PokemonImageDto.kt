package com.pokedex.bff.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Informações da imagem do Pokemon")
data class PokemonImageDto(
    @Schema(
        description = "URL da imagem do Pokemon",
        example = "https://assets.pokemon.com/assets/cms2/img/pokedex/full/025.png"
    )
    val url: String,

    @Schema(description = "Detalhes do elemento visual da imagem")
    val element: PokemonImageElementDto
)