package com.pokedex.bff.interfaces.controllers

import com.pokedex.bff.application.usecase.BuscarPokemonUseCase
import com.pokedex.bff.interfaces.dto.PokemonDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pokemon")
class PokemonController(
    private val buscarPokemonUseCase: BuscarPokemonUseCase
) {
    @GetMapping("/{id}")
    fun getPokemon(@PathVariable id: Long): ResponseEntity<PokemonDto> =
        buscarPokemonUseCase.execute(id)
            ?.let { ResponseEntity.ok(PokemonDto.from(it)) }
            ?: ResponseEntity.notFound().build()
}
