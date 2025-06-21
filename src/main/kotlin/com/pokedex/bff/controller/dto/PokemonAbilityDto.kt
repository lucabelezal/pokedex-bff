package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonAbilityDto(
    @JsonProperty("ability_id")
    val abilityId: Long,
    @JsonProperty("is_hidden")
    val isHidden: Boolean
)