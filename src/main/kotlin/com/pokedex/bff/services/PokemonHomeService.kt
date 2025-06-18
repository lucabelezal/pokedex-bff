package com.pokedex.bff.services

import com.pokedex.bff.controllers.dtos.Filter
import com.pokedex.bff.controllers.dtos.Pokemon
import com.pokedex.bff.controllers.dtos.PokemonHomeResponse
import com.pokedex.bff.controllers.dtos.PokemonImage
import com.pokedex.bff.controllers.dtos.PokemonImageElement
import com.pokedex.bff.controllers.dtos.PokemonType
import com.pokedex.bff.controllers.dtos.Search
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.sql.ResultSet

@Service
class PokemonHomeService(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getPokemonHomeData(): PokemonHomeResponse {
        val search = Search(placeholder = "Buscar Pokémon...")
        val filters = emptyList<Filter>()

        val pokemonsSql = """
            SELECT 
                p.id, 
                p.number, 
                p.name, 
                p.sprites->'other'->'home'->>'front_default' AS image_url 
            FROM pokemons p
            ORDER BY p.id
            LIMIT 10; 
        """.trimIndent()

        val pokemons = jdbcTemplate.query(pokemonsSql) { rs, _ ->
            val pokemonId = rs.getLong("id")
            val pokemonNumber = "Nº" + rs.getString("number")
            val pokemonName = rs.getString("name")
            val imageUrl = rs.getString("image_url") ?: ""

            val typesSql = """
                SELECT t.name, t.color 
                FROM types t 
                JOIN pokemon_types pt ON t.id = pt.type_id 
                WHERE pt.pokemon_id = ? 
                ORDER BY pt.type_id;
            """.trimIndent()

            val pokemonTypes = jdbcTemplate.query(typesSql, { typeRs: ResultSet, _: Int ->
                PokemonType(
                    name = typeRs.getString("name"),
                    color = typeRs.getString("color")
                )
            }, pokemonId)

            val primaryTypeForImageElement = pokemonTypes.firstOrNull()

            val imageElement = PokemonImageElement(
                color = primaryTypeForImageElement?.color ?: "#FFFFFF", // Default white if no type
                type = primaryTypeForImageElement?.name?.uppercase() ?: "UNKNOWN"
            )
            val pokemonImage = PokemonImage(url = imageUrl, element = imageElement)

            Pokemon(
                number = pokemonNumber,
                name = pokemonName,
                image = pokemonImage,
                types = pokemonTypes
            )
        }

        return PokemonHomeResponse(
            search = search,
            filters = filters,
            pokemons = pokemons
        )
    }

}
