package com.pokedex.bff.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Tipo de Pokemon com nome e cor associada")
data class PokemonTypeDto(
    @Schema(description = "Nome do tipo do Pokemon", example = "Electric")
    val name: String,

    @Schema(description = "Cor representativa do tipo (hexadecimal ou nome)", example = "#F8D030")
    val color: String
)
