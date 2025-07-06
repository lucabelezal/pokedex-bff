package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.StatsEntity
import com.pokedex.bff.domain.repositories.StatsRepository
import com.pokedex.bff.application.dto.seeder.StatsDto
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

class StatsImportStrategyTest {

    @MockK
    private lateinit var statsRepository: StatsRepository

    @MockK
    private lateinit var jsonLoader: JsonLoader

    @MockK
    private lateinit var importResults: ImportResults

    @InjectMockKs
    private lateinit var statsImportStrategy: StatsImportStrategy

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { importResults.add(any(), any()) } just Runs
    }

    @Test
    fun `getEntityName should return correct name`() {
        assertEquals("Estatísticas", statsImportStrategy.getEntityName())
    }

    @Test
    fun `import should process stats successfully`() {
        val statsDtos = listOf(
            StatsDto(1, 318, 45, 49, 49, 65, 65, 45),
            StatsDto(2, 405, 60, 62, 63, 80, 80, 60)
        )
        val expectedEntities = listOf(
            StatsEntity(1, 318, 45, 49, 49, 65, 65, 45),
            StatsEntity(2, 405, 60, 62, 63, 80, 80, 60)
        )
        val expectedCounts = ImportCounts(success = 2, errors = 0)

        every { jsonLoader.loadJson<List<StatsDto>>(JsonFile.STATS.filePath) } returns statsDtos
        every { statsRepository.save(any()) } answers { firstArg() }

        val counts = statsImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { jsonLoader.loadJson<List<StatsDto>>(JsonFile.STATS.filePath) }
        verify(exactly = 2) { statsRepository.save(any()) }
        verify { statsRepository.save(expectedEntities[0]) }
        verify { statsRepository.save(expectedEntities[1]) }
        verify(exactly = 1) { importResults.add("Estatísticas", expectedCounts) }
    }

    @Test
    fun `import should handle empty list from jsonLoader`() {
        val expectedCounts = ImportCounts(success = 0, errors = 0)
        every { jsonLoader.loadJson<List<StatsDto>>(JsonFile.STATS.filePath) } returns emptyList()

        val counts = statsImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 0) { statsRepository.save(any()) }
        verify(exactly = 1) { importResults.add("Estatísticas", expectedCounts) }
    }

    @Test
    fun `import should handle exceptions during save and count errors`() {
        val statsDtos = listOf(StatsDto(1, 318, 45, 49, 49, 65, 65, 45))
        val expectedCounts = ImportCounts(success = 0, errors = 1)
        val entityToSave = StatsEntity(1, 318, 45, 49, 49, 65, 65, 45)

        every { jsonLoader.loadJson<List<StatsDto>>(JsonFile.STATS.filePath) } returns statsDtos
        every { statsRepository.save(entityToSave) } throws RuntimeException("DB error")

        val counts = statsImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { statsRepository.save(entityToSave) }
        verify(exactly = 1) { importResults.add("Estatísticas", expectedCounts) }
    }
}
