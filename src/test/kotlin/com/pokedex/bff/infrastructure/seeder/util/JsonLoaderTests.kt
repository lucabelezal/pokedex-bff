package com.pokedex.bff.infrastructure.seeder.util

import com.pokedex.bff.infrastructure.seeder.exception.DataImportException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JsonLoaderTests {

    @Test
    fun `should load json`() {
        val inputStream = JsonLoader.load("data/pokemons.json")
        assert(inputStream != null)
    }

    @Test
    fun `should throw exception when file not found`() {
        assertThrows<DataImportException> {
            JsonLoader.load("fileNotFound.json")
        }
    }
}
