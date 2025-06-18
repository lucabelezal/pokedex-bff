package com.pokedex.bff.controllers

import com.pokedex.bff.controllers.dtos.PokemonHomeResponse
import com.pokedex.bff.services.PokemonHomeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/home")
class PokemonHomeController(
    private val pokemonHomeService: PokemonHomeService
) {

    @GetMapping("/pokemons")
    fun getHomePagePokemons(): PokemonHomeResponse {
        return pokemonHomeService.getPokemonHomeData()
    }
}