package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.SpeciesEntity
import com.pokedex.bff.domain.repositories.SpeciesRepository
import com.pokedex.bff.application.dto.seeder.SpeciesDto
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SpeciesImportStrategyTest {

    @MockK
    private lateinit var speciesRepository: SpeciesRepository

    @MockK
    private lateinit var jsonLoader: JsonLoader

    @MockK
    private lateinit var importResults: ImportResults

    @InjectMockKs
    private lateinit var speciesImportStrategy: SpeciesImportStrategy

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { importResults.add(any(), any()) } just Runs
    }

    @Test
    fun `getEntityName should return correct name`() {
        assertEquals("Espécies", speciesImportStrategy.getEntityName())
    }

    @Test
    fun `import should process species successfully`() {
        val speciesDtos = listOf(
            SpeciesDto(1, "Bulbasaur", 1, "Seed Pokémon", "Pokémon Semente"),
            SpeciesDto(2, "Ivysaur", 2, "Seed Pokémon", "Pokémon Semente")
        )
        val expectedEntities = listOf(
            SpeciesEntity(id = 1, name = "Bulbasaur", pokemon_number = 1, speciesEn = "Seed Pokémon", speciesPt = "Pokémon Semente"),
            SpeciesEntity(id = 2, name = "Ivysaur", pokemon_number = 2, speciesEn = "Seed Pokémon", speciesPt = "Pokémon Semente")
        )
        val expectedCounts = ImportCounts(success = 2, errors = 0)

        every { jsonLoader.loadJson<List<SpeciesDto>>(JsonFile.SPECIES.filePath) } returns speciesDtos
        every { speciesRepository.save(any()) } answers { firstArg() }

        val counts = speciesImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { jsonLoader.loadJson<List<SpeciesDto>>(JsonFile.SPECIES.filePath) }
        verify(exactly = 2) { speciesRepository.save(any()) }
        verify { speciesRepository.save(expectedEntities[0]) }
        verify { speciesRepository.save(expectedEntities[1]) }
        verify(exactly = 1) { importResults.add("Espécies", expectedCounts) }
    }

    @Test
    fun `import should handle empty list from jsonLoader`() {
        val expectedCounts = ImportCounts(success = 0, errors = 0)
        every { jsonLoader.loadJson<List<SpeciesDto>>(JsonFile.SPECIES.filePath) } returns emptyList()

        val counts = speciesImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 0) { speciesRepository.save(any()) }
        verify(exactly = 1) { importResults.add("Espécies", expectedCounts) }
    }

    @Test
    fun `import should handle exceptions during save and count errors`() {
        val speciesDtos = listOf(SpeciesDto(1, "Charmander", 4, "Lizard Pokémon", "Pokémon Lagarto"))
        val expectedCounts = ImportCounts(success = 0, errors = 1)
        val entityToSave = SpeciesEntity(id = 1, name = "Charmander", pokemon_number = 4, speciesEn = "Lizard Pokémon", speciesPt = "Pokémon Lagarto")

        every { jsonLoader.loadJson<List<SpeciesDto>>(JsonFile.SPECIES.filePath) } returns speciesDtos
        every { speciesRepository.save(entityToSave) } throws RuntimeException("DB error")

        val counts = speciesImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { speciesRepository.save(entityToSave) }
        verify(exactly = 1) { importResults.add("Espécies", expectedCounts) }
    }
}
