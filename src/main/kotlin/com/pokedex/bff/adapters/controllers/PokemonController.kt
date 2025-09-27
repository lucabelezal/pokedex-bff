package com.pokedex.bff.adapters.controllers

import com.pokedex.bff.application.ports.input.PokemonUseCases
import com.pokedex.bff.application.dto.response.PokedexListResponse
import com.pokedex.bff.application.dto.response.PokemonDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.validation.annotation.Validated

@RestController
@RequestMapping("/api/v1/pokemons")
@Tag(name = "Pokemon", description = "Pokemon related operations")
@Validated
class PokemonController(
    private val pokemonUseCases: PokemonUseCases
) {

    @Operation(
        summary = "List Pokemons with pagination",
        description = "Returns a paginated list of Pokemons for the Pokedex.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List returned successfully",
                content = [Content(schema = Schema(implementation = PokedexListResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid pagination parameters"
            )
        ]
    )
    @GetMapping
    fun getPokemons(
        @Parameter(
            description = "Page number (starting at 0)",
            example = "0",
            `in` = ParameterIn.QUERY
        )
    @RequestParam(defaultValue = "0")
        @Min(0, message = "Page number must be non-negative")
        page: Int,
        
        @Parameter(
            description = "Page size (max 100)",
            example = "10",
            `in` = ParameterIn.QUERY
        )
    @RequestParam(defaultValue = "10")
    @Min(1, message = "Page size must be positive")
    @Max(MAX_PAGE_SIZE, message = "Page size cannot exceed $MAX_PAGE_SIZE")
    size: Int
    ): ResponseEntity<PokedexListResponse> {
        return ResponseEntity.ok(pokemonUseCases.getPaginatedPokemons(page, size))
    }

    @Operation(
        summary = "Get Pokemon by ID",
        description = "Returns a single Pokemon by its ID.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Pokemon found",
                content = [Content(schema = Schema(implementation = PokemonDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Pokemon not found"
            )
        ]
    )
    @GetMapping("/{id}")
    fun getPokemon(
        @Parameter(description = "Pokemon ID", example = "1")
        @PathVariable id: Long
    ): ResponseEntity<PokemonDto> {
        return pokemonUseCases.findById(id)
            ?.let { ResponseEntity.ok(PokemonDto.from(it)) }
            ?: ResponseEntity.notFound().build()
    }
}

private const val MAX_PAGE_SIZE: Long = 100L



