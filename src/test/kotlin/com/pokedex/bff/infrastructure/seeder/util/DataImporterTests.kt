package com.pokedex.bff.infrastructure.seeder.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.bff.domian.PokemonMother
import com.pokedex.bff.infrastructure.seeder.exception.DataImportException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.io.ByteArrayInputStream

@ExtendWith(MockKExtension::class)
class DataImporterTests {

    @InjectMockKs
    private lateinit var dataImporter: DataImporter

    @MockK
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should read data`() {
        val pokemons = listOf(PokemonMother.withCharizard())
        val json = """[{"name": "Charizard"}]"""
        val inputStream = ByteArrayInputStream(json.toByteArray())

        every { objectMapper.readValue(any<ByteArrayInputStream>(), any<Class<Array<Any>>>()) } returns pokemons.toTypedArray()

        val result = dataImporter.read("pokemons.json", Any::class.java)

        assert(result.isNotEmpty())
    }

    @Test
    fun `should throw exception when file not found`() {
        assertThrows<DataImportException> {
            dataImporter.read("fileNotFound.json", Any::class.java)
        }
    }
}
