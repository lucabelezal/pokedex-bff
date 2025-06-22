package com.pokedex.bff.interfaces.controllers

import com.pokedex.bff.application.services.PokedexService
import com.pokedex.bff.application.dto.response.PokedexListResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/pokedex")
@Tag(name = "Pokedex", description = "Operações relacionadas à Pokedex")
class PokedexController(
    private val pokemonService: PokedexService
) {

    @Operation(
        summary = "Lista Pokemons com paginação",
        description = "Retorna uma lista paginada de Pokemons.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Lista retornada com sucesso",
                content = [Content(schema = Schema(implementation = PokedexListResponse::class))]
            )
        ]
    )
    @GetMapping("/pokemons")
    fun getPokemons(
        @Parameter(description = "Número da página (começando em 0)", example = "0", `in` = ParameterIn .QUERY)
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Tamanho da página", example = "10", `in` = ParameterIn.QUERY)
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PokedexListResponse> {
        return ResponseEntity.ok(pokemonService.getPokemons(page, size))
    }
}