package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RegionDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String
)