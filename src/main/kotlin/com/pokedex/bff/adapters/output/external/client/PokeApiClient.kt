package com.pokedex.bff.adapters.output.external.client

import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class PokeApiClient(
    private val restTemplate: RestTemplate
) {
    fun getPokemonById(id: String): String {
        // Exemplo fictício de chamada à API externa
        val url = "https://pokeapi.co/api/v2/pokemon/$id"
        return restTemplate.getForObject(url, String::class.java) ?: "{}"
    }
}
