package com.pokedex.bff.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Informações da página na resposta")
data class PageInfoDto(
    @Schema(description = "Página atual", example = "0")
    val currentPage: Int,

    @Schema(description = "Número total de páginas", example = "15")
    val totalPages: Int,

    @Schema(description = "Número total de elementos", example = "151")
    val totalElements: Long,

    @Schema(description = "Se existe próxima página", example = "true")
    val hasNext: Boolean
)
