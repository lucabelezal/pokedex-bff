package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults

interface ImportStrategy {
    fun import(results: ImportResults): ImportCounts
    fun getEntityName(): String
}
