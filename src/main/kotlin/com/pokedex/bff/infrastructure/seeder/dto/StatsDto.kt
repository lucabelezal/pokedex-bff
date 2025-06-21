package com.pokedex.bff.infrastructure.seeder.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class StatsDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("total")
    val total: Int,
    @JsonProperty("hp")
    val hp: Int,
    @JsonProperty("attack")
    val attack: Int,
    @JsonProperty("defense")
    val defense: Int,
    @JsonProperty("sp_atk")
    val spAtk: Int,
    @JsonProperty("sp_def")
    val spDef: Int,
    @JsonProperty("speed")
    val speed: Int
)