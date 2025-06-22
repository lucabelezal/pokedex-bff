package com.pokedex.bff.application.dto.response

data class PageInfoDto(
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNext: Boolean
)

