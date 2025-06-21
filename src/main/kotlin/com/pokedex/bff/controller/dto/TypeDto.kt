package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TypeDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("color")
    val color: String?
)
