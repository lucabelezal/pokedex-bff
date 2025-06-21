package com.pokedex.bff.interfaces.controllers

import com.pokedex.bff.application.pokedex.services.PokedexService
import com.pokedex.bff.application.pokedex.dto.response.PokedexListResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/pokedex")
class PokedexController(
    private val pokemonService: PokedexService
) {

    @GetMapping("/pokemons")
    fun getPokemons(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PokedexListResponse> {
        return ResponseEntity.ok(pokemonService.getPokemons(page, size))
    }
}