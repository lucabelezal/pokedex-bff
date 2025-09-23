package com.pokedex.bff.interfaces.controllers

import com.pokedex.bff.application.ports.input.PokedexUseCases
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
@Tag(name = "Pokedex", description = "Pokedex related operations")
class PokedexController(
    private val pokedexUseCases: PokedexUseCases
) {

    @Operation(
        summary = "List Pokemons with pagination",
        description = "Returns a paginated list of Pokemons.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List returned successfully",
                content = [Content(schema = Schema(implementation = PokedexListResponse::class))]
            )
        ]
    )
    @GetMapping("/pokemons")
    fun getPokemons(
        @Parameter(description = "Page number (starting at 0)", example = "0", `in` = ParameterIn .QUERY)
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Page size", example = "10", `in` = ParameterIn.QUERY)
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PokedexListResponse> {
        return ResponseEntity.ok(pokedexUseCases.getPaginatedPokemons(page, size))
    }
}