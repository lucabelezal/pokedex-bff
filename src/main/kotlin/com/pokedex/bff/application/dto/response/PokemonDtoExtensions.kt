package com.pokedex.bff.application.dto.response

import com.pokedex.bff.domain.entities.Pokemon

fun Pokemon.toDto(): PokemonDto {
    val mainType = this.types.firstOrNull()
    val imageUrl = this.sprites?.other?.home ?: this.sprites?.frontDefault ?: ""
    val numberValue = this.number ?: "UNK"
    return PokemonDto(
        number = numberValue,
        name = this.name,
        image = PokemonImageDto(
            url = imageUrl,
            element = PokemonImageElementDto(
                color = mainType?.color ?: "#CCCCCC",
                type = mainType?.name?.uppercase() ?: "UNKNOWN"
            )
        ),
        types = this.types.map { type ->
            PokemonTypeDto(
                name = type.name,
                color = type.color ?: "#CCCCCC"
            )
        }
    )
}
