package com.pokedex.bff.adapters.input.web.controller

import com.pokedex.bff.adapters.input.web.dto.request.CreatePokemonWebRequest
import com.pokedex.bff.adapters.input.web.dto.response.PokemonWebResponse
import com.pokedex.bff.adapters.input.web.dto.response.PokemonPageResponse
import com.pokedex.bff.adapters.input.web.mapper.PokemonWebMapper
import com.pokedex.bff.application.usecase.CreatePokemonUseCase
import com.pokedex.bff.application.usecase.EvolvePokemonUseCase
import com.pokedex.bff.application.usecase.BattleUseCase
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/api/v1/pokemons")
class PokemonController(
    private val createPokemonUseCase: CreatePokemonUseCase,
    private val evolvePokemonUseCase: EvolvePokemonUseCase,
    private val battleUseCase: BattleUseCase,
    private val webMapper: PokemonWebMapper
) {
    @PostMapping
    fun create(@RequestBody request: CreatePokemonWebRequest): PokemonWebResponse {
        val input = webMapper.toCreatePokemonInput(request)
        val output = createPokemonUseCase.execute(input)
        return webMapper.toWebResponse(output)
    }

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): PokemonPageResponse {
        val pageSize = size.coerceAtMost(100)
        val pageResult = createPokemonUseCase.findAll(page, pageSize)
        return webMapper.toPageResponse(pageResult)
    }

    // Outros endpoints podem ser adicionados aqui conforme necessário
}
