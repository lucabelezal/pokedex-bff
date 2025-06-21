package com.pokedex.bff.controller

import com.pokedex.bff.domain.model.PokedexListResponse
import com.pokedex.bff.service.PokemonService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/pokemons")
class PokemonController(
    private val pokemonService: PokemonService
) {

    @GetMapping
    fun getPokemons(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PokedexListResponse> {
        return ResponseEntity.ok(pokemonService.getPokemons(page, size))
    }
}