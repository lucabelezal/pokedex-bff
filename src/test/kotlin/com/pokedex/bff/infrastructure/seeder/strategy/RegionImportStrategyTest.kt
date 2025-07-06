package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.RegionEntity
import com.pokedex.bff.domain.repositories.RegionRepository
import com.pokedex.bff.application.dto.seeder.RegionDto
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

class RegionImportStrategyTest {

    @MockK
    private lateinit var regionRepository: RegionRepository

    @MockK
    private lateinit var jsonLoader: JsonLoader

    @MockK
    private lateinit var importResults: ImportResults // Mocked to verify interaction

    @InjectMockKs
    private lateinit var regionImportStrategy: RegionImportStrategy

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { importResults.add(any(), any()) } just Runs // Ensure add doesn't throw
    }

    @Test
    fun `getEntityName should return correct name`() {
        assertEquals("Regiões", regionImportStrategy.getEntityName())
    }

    @Test
    fun `import should process regions successfully`() {
        val regionDtos = listOf(RegionDto(1, "Kanto"), RegionDto(2, "Johto"))
        val expectedEntities = listOf(
            RegionEntity(id = 1, name = "Kanto"),
            RegionEntity(id = 2, name = "Johto")
        )
        val expectedCounts = ImportCounts(success = 2, errors = 0)

        every { jsonLoader.loadJson<List<RegionDto>>(JsonFile.REGIONS.filePath) } returns regionDtos
        every { regionRepository.save(any()) } answers { firstArg() } // Return the saved entity

        val counts = regionImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { jsonLoader.loadJson<List<RegionDto>>(JsonFile.REGIONS.filePath) }
        verify(exactly = 2) { regionRepository.save(any()) }
        verify { regionRepository.save(expectedEntities[0]) }
        verify { regionRepository.save(expectedEntities[1]) }
        verify(exactly = 1) { importResults.add("Regiões", expectedCounts) }
    }

    @Test
    fun `import should handle empty list from jsonLoader`() {
        val expectedCounts = ImportCounts(success = 0, errors = 0)
        every { jsonLoader.loadJson<List<RegionDto>>(JsonFile.REGIONS.filePath) } returns emptyList()

        val counts = regionImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { jsonLoader.loadJson<List<RegionDto>>(JsonFile.REGIONS.filePath) }
        verify(exactly = 0) { regionRepository.save(any()) }
        verify(exactly = 1) { importResults.add("Regiões", expectedCounts) }
    }

    @Test
    fun `import should handle exceptions during save and count errors`() {
        val regionDtos = listOf(RegionDto(1, "Kanto"), RegionDto(2, "Johto"))
        val kantoEntity = RegionEntity(id = 1, name = "Kanto")
        val expectedCounts = ImportCounts(success = 1, errors = 1)

        every { jsonLoader.loadJson<List<RegionDto>>(JsonFile.REGIONS.filePath) } returns regionDtos
        every { regionRepository.save(kantoEntity) } returns kantoEntity
        every { regionRepository.save(RegionEntity(id = 2, name = "Johto")) } throws RuntimeException("DB error")

        val counts = regionImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { jsonLoader.loadJson<List<RegionDto>>(JsonFile.REGIONS.filePath) }
        verify(exactly = 2) { regionRepository.save(any()) }
        verify(exactly = 1) { importResults.add("Regiões", expectedCounts) }
    }

    @Test
    fun `import should handle exceptions from jsonLoader`() {
        val expectedCounts = ImportCounts(success = 0, errors = 0) // No DTOs processed
        every { jsonLoader.loadJson<List<RegionDto>>(JsonFile.REGIONS.filePath) } throws RuntimeException("JSON load error")

        // The exception from jsonLoader will propagate and not be caught by the strategy's importSimpleData's try-catch
        assertThrows(RuntimeException::class.java) {
            regionImportStrategy.import(importResults)
        }

        verify(exactly = 0) { regionRepository.save(any()) }
        // ImportResults.add will not be called if jsonLoader throws before importSimpleData
        verify(exactly = 0) { importResults.add(any(), any()) }
    }
}
