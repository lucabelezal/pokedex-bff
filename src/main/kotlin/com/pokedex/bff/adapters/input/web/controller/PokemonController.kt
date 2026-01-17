package com.pokedex.bff.adapters.input.web.controller

import com.pokedex.bff.adapters.input.web.dto.request.CreatePokemonWebRequest
import com.pokedex.bff.adapters.input.web.dto.response.PokemonRichPageResponse
import com.pokedex.bff.adapters.input.web.mapper.PokemonRichWebMapper
import com.pokedex.bff.adapters.input.web.mapper.PokemonWebMapper
import com.pokedex.bff.application.port.input.CreatePokemonUseCase
import com.pokedex.bff.application.port.input.EvolvePokemonUseCase
import com.pokedex.bff.application.port.input.BattleUseCase
import com.pokedex.bff.application.port.input.ListPokemonsUseCase
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/pokemons")
class PokemonController(
    private val createPokemonUseCase: CreatePokemonUseCase,
    private val listPokemonsUseCase: ListPokemonsUseCase,
    private val evolvePokemonUseCase: EvolvePokemonUseCase,
    private val battleUseCase: BattleUseCase,
    private val richWebMapper: PokemonRichWebMapper,
    private val webMapper: PokemonWebMapper
) {
    @PostMapping
    fun create(@Valid @RequestBody request: CreatePokemonWebRequest): ResponseEntity<Void> {
        val input = webMapper.toCreatePokemonInput(request)
        createPokemonUseCase.execute(input)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) size: Int
    ): PokemonRichPageResponse {
        val pageResult = listPokemonsUseCase.findAll(page, size)
        return richWebMapper.toRichPageResponse(
            pokemons = pageResult.content,
            totalElements = pageResult.totalElements,
            currentPage = pageResult.pageNumber,
            totalPages = pageResult.totalPages,
            hasNext = pageResult.hasNext
        )
    }

    // Outros endpoints podem ser adicionados aqui conforme necessário
}
