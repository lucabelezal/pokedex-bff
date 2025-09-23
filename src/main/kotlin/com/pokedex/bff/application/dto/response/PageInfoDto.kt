package com.pokedex.bff.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Page information in the response")
data class PageInfoDto(
    @Schema(description = "Current page", example = "0")
    val currentPage: Int,

    @Schema(description = "Total number of pages", example = "15")
    val totalPages: Int,

    @Schema(description = "Total number of elements", example = "151")
    val totalElements: Long,

    @Schema(description = "Whether there is a next page", example = "true")
    val hasNext: Boolean
)
