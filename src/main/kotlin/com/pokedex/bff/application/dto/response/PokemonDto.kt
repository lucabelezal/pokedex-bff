package com.pokedex.bff.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Pokemon data")
data class PokemonDto(
    @Schema(description = "Número do Pokemon", example = "025")
    val number: String,

    @Schema(description = "Nome do Pokemon", example = "Pikachu")
    val name: String,

    @Schema(description = "Imagens do Pokemon")
    val image: PokemonImageDto,

    @Schema(description = "Tipos do Pokemon")
    val types: List<PokemonTypeDto>
)
