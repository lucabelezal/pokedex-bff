package com.pokedex.bff.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Resposta da lista paginada da Pokedex")
data class PokedexListResponse(
    @Schema(description = "Informações da página atual")
    val pageInfo: PageInfoDto,

    @Schema(description = "Dados da busca atual")
    val search: SearchDto,

    @Schema(description = "Filtros aplicados (lista genérica)", example = "[]")
    val filters: List<Any>,

    @Schema(description = "Lista de Pokemons")
    val pokemons: List<PokemonDto>
)