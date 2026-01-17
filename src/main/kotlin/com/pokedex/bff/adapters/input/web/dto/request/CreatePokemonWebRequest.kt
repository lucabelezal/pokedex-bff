package com.pokedex.bff.adapters.input.web.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreatePokemonWebRequest(
    @field:NotBlank(message = "Pokemon name is required")
    @field:Size(min = 1, max = 50, message = "Pokemon name must be between 1 and 50 characters")
    val name: String,
    
    @field:NotBlank(message = "Pokemon type is required")
    val type: String
)
