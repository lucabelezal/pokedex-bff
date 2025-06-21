package com.pokedex.bff.application.pokedex.dto.response

data class PageInfoDto(
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNext: Boolean
)

