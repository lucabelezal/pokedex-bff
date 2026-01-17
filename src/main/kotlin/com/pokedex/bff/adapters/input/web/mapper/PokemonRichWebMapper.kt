package com.pokedex.bff.adapters.input.web.mapper

import com.pokedex.bff.adapters.input.web.dto.response.*
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import org.springframework.stereotype.Component

@Component
class PokemonRichWebMapper {
    fun toRichWebResponse(pokemon: Pokemon): PokemonRichWebResponse {
        val types = pokemon.types.map {
            PokemonTypeWebResponse(
                name = it.name,
                color = it.color ?: "#000000"
            )
        }
        val mainType = pokemon.types.firstOrNull()
        val imageUrl = pokemon.sprites?.other?.home?.frontDefault 
            ?: pokemon.sprites?.other?.officialArtwork?.frontDefault
            ?: pokemon.sprites?.frontDefault 
            ?: ""
        return PokemonRichWebResponse(
            number = "Nº" + (pokemon.number ?: pokemon.id.toString().padStart(3, '0')),
            name = pokemon.name,
            image = PokemonImageWebResponse(
                url = imageUrl,
                element = PokemonImageElementWebResponse(
                    color = mainType?.color ?: "#000000",
                    type = mainType?.name?.uppercase() ?: ""
                )
            ),
            types = types
        )
    }

    fun toRichPageResponse(pokemons: List<Pokemon>, totalElements: Long, currentPage: Int, totalPages: Int, hasNext: Boolean): PokemonRichPageResponse {
        return PokemonRichPageResponse(
            search = SearchWebResponse(placeholder = "Buscar Pokémon..."),
            filters = emptyList(),
            pokemons = pokemons.map { toRichWebResponse(it) }
        )
    }
}
