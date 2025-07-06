package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.GenerationEntity
import com.pokedex.bff.domain.entities.RegionEntity
import com.pokedex.bff.domain.repositories.GenerationRepository
import com.pokedex.bff.domain.repositories.RegionRepository
import com.pokedex.bff.application.dto.seeder.GenerationDto
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.exception.DataImportException
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GenerationImportStrategyTest {

    @MockK
    private lateinit var generationRepository: GenerationRepository

    @MockK
    private lateinit var regionRepository: RegionRepository

    @MockK
    private lateinit var jsonLoader: JsonLoader

    @MockK
    private lateinit var importResults: ImportResults

    @InjectMockKs
    private lateinit var generationImportStrategy: GenerationImportStrategy

    private val kantoRegion = RegionEntity(1, "Kanto")
    private val johtoRegion = RegionEntity(2, "Johto")

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { importResults.add(any(), any()) } just Runs
        every { regionRepository.findAll() } returns listOf(kantoRegion, johtoRegion)
    }

    @Test
    fun `getEntityName should return correct name`() {
        assertEquals("Gerações", generationImportStrategy.getEntityName())
    }

    @Test
    fun `import should process generations successfully`() {
        val generationDtos = listOf(
            GenerationDto(1, "Generation I", 1),
            GenerationDto(2, "Generation II", 2)
        )
        val expectedEntities = listOf(
            GenerationEntity(id = 1, name = "Generation I", region = kantoRegion),
            GenerationEntity(id = 2, name = "Generation II", region = johtoRegion)
        )
        val expectedCounts = ImportCounts(success = 2, errors = 0)

        every { jsonLoader.loadJson<List<GenerationDto>>(JsonFile.GENERATIONS.filePath) } returns generationDtos
        every { generationRepository.save(any()) } answers { firstArg() }

        val counts = generationImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { jsonLoader.loadJson<List<GenerationDto>>(JsonFile.GENERATIONS.filePath) }
        verify(exactly = 1) { regionRepository.findAll() }
        verify(exactly = 2) { generationRepository.save(any()) }
        verify { generationRepository.save(expectedEntities[0]) }
        verify { generationRepository.save(expectedEntities[1]) }
        verify(exactly = 1) { importResults.add("Gerações", expectedCounts) }
    }

    @Test
    fun `import should count error if region not found`() {
        val generationDtos = listOf(GenerationDto(1, "Generation Invalid", 99)) // Region 99 does not exist
        val expectedCounts = ImportCounts(success = 0, errors = 1)

        every { jsonLoader.loadJson<List<GenerationDto>>(JsonFile.GENERATIONS.filePath) } returns generationDtos

        val counts = generationImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 0) { generationRepository.save(any()) } // Save is not called due to DataImportException
        verify(exactly = 1) { importResults.add("Gerações", expectedCounts) }
    }

    @Test
    fun `import should handle general exception during save`() {
        val generationDtos = listOf(GenerationDto(1, "Generation I", 1))
        val expectedCounts = ImportCounts(success = 0, errors = 1)

        every { jsonLoader.loadJson<List<GenerationDto>>(JsonFile.GENERATIONS.filePath) } returns generationDtos
        every { generationRepository.save(any()) } throws RuntimeException("DB Error during save")

        val counts = generationImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { generationRepository.save(GenerationEntity(id = 1, name="Generation I", region=kantoRegion)) }
        verify(exactly = 1) { importResults.add("Gerações", expectedCounts) }
    }
}
