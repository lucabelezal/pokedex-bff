package com.pokedex.bff.adapters.input.web.controller

import com.pokedex.bff.adapters.input.web.dto.response.PokemonRichPageResponse
import com.pokedex.bff.adapters.input.web.mapper.PokemonRichWebMapper
import com.pokedex.bff.application.port.input.CreatePokemonUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/pokemons-rich")
class PokemonRichController(
    private val createPokemonUseCase: CreatePokemonUseCase,
    private val richWebMapper: PokemonRichWebMapper
) {
    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): PokemonRichPageResponse {
        val pageSize = size.coerceAtMost(100)
        val pageResult = createPokemonUseCase.findAll(page, pageSize)
        return richWebMapper.toRichPageResponse(
            pokemons = pageResult.content,
            totalElements = pageResult.totalElements,
            currentPage = pageResult.pageNumber,
            totalPages = pageResult.totalPages,
            hasNext = pageResult.hasNext
        )
    }
}
