package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.application.valueobjects.DreamWorldSpritesVO

data class DreamWorldSpritesDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?
) {
    fun toVO(): DreamWorldSpritesVO {
        return DreamWorldSpritesVO(
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale
        )
    }
}