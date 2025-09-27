package com.pokedex.bff.application.dto.response

import com.pokedex.bff.domain.entities.Pokemon
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Pokemon data")
data class PokemonDto(
    @Schema(description = "Número do Pokemon", example = "025")
    val number: String?,

    @Schema(description = "Nome do Pokemon", example = "Pikachu")
    val name: String,

    @Schema(description = "Imagens do Pokemon")
    val image: PokemonImageDto,

    @Schema(description = "Tipos do Pokemon")
    val types: List<PokemonTypeDto>
) {
    companion object {
        fun from(pokemon: Pokemon): PokemonDto {
            val mainType = pokemon.types.firstOrNull()
            val imageUrl = pokemon.sprites?.other?.home ?: pokemon.sprites?.frontDefault ?: ""

            return PokemonDto(
                number = pokemon.number,
                name = pokemon.name,
                image = PokemonImageDto(
                    url = imageUrl,
                    element = PokemonImageElementDto(
                        color = mainType?.color ?: "#CCCCCC",
                        type = mainType?.name?.uppercase() ?: "UNKNOWN"
                    )
                ),
                types = pokemon.types.map { type ->
                    PokemonTypeDto(
                        name = type.name,
                        color = type.color ?: "#CCCCCC"
                    )
                }
            )
        }
    }
}
