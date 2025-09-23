package com.pokedex.bff.interfaces.controllers

import com.pokedex.bff.application.usecase.FetchPokemonUseCase
import com.pokedex.bff.interfaces.dto.PokemonDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pokemon")
class PokemonController(
    private val fetchPokemonUseCase: FetchPokemonUseCase
) {
    @GetMapping("/{id}")
    fun getPokemon(@PathVariable id: Long): ResponseEntity<PokemonDto> =
        fetchPokemonUseCase.execute(id)
            ?.let { ResponseEntity.ok(PokemonDto.from(it)) }
            ?: ResponseEntity.notFound().build()
}
