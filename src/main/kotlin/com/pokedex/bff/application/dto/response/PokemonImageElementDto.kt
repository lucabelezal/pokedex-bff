package com.pokedex.bff.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Detalhes visuais do elemento da imagem do Pokemon")
data class PokemonImageElementDto(
    @Schema(description = "Cor predominante no elemento", example = "#FFCB05")
    val color: String,

    @Schema(description = "Tipo do elemento de imagem", example = "background")
    val type: String
)