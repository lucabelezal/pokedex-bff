package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.infra.entity.DreamWorldSprites

data class DreamWorldSpritesDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?
) {
    fun toModel(): DreamWorldSprites {
        return DreamWorldSprites(
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale
        )
    }
}