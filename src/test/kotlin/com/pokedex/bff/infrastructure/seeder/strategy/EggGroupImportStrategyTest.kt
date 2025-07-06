package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.EggGroupEntity
import com.pokedex.bff.domain.repositories.EggGroupRepository
import com.pokedex.bff.application.dto.seeder.EggGroupDto
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

class EggGroupImportStrategyTest {

    @MockK
    private lateinit var eggGroupRepository: EggGroupRepository

    @MockK
    private lateinit var jsonLoader: JsonLoader

    @MockK
    private lateinit var importResults: ImportResults

    @InjectMockKs
    private lateinit var eggGroupImportStrategy: EggGroupImportStrategy

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { importResults.add(any(), any()) } just Runs
    }

    @Test
    fun `getEntityName should return correct name`() {
        assertEquals("Grupos de Ovos", eggGroupImportStrategy.getEntityName())
    }

    @Test
    fun `import should process egg groups successfully`() {
        val eggGroupDtos = listOf(
            EggGroupDto(1, "Monster"),
            EggGroupDto(2, "Water 1")
        )
        val expectedEntities = listOf(
            EggGroupEntity(id = 1, name = "Monster"),
            EggGroupEntity(id = 2, name = "Water 1")
        )
        val expectedCounts = ImportCounts(success = 2, errors = 0)

        every { jsonLoader.loadJson<List<EggGroupDto>>(JsonFile.EGG_GROUPS.filePath) } returns eggGroupDtos
        every { eggGroupRepository.save(any()) } answers { firstArg() }

        val counts = eggGroupImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { jsonLoader.loadJson<List<EggGroupDto>>(JsonFile.EGG_GROUPS.filePath) }
        verify(exactly = 2) { eggGroupRepository.save(any()) }
        verify { eggGroupRepository.save(expectedEntities[0]) }
        verify { eggGroupRepository.save(expectedEntities[1]) }
        verify(exactly = 1) { importResults.add("Grupos de Ovos", expectedCounts) }
    }

    @Test
    fun `import should handle empty list from jsonLoader`() {
        val expectedCounts = ImportCounts(success = 0, errors = 0)
        every { jsonLoader.loadJson<List<EggGroupDto>>(JsonFile.EGG_GROUPS.filePath) } returns emptyList()

        val counts = eggGroupImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 0) { eggGroupRepository.save(any()) }
        verify(exactly = 1) { importResults.add("Grupos de Ovos", expectedCounts) }
    }

    @Test
    fun `import should handle exceptions during save and count errors`() {
        val eggGroupDtos = listOf(EggGroupDto(1, "Fairy"))
        val expectedCounts = ImportCounts(success = 0, errors = 1)

        every { jsonLoader.loadJson<List<EggGroupDto>>(JsonFile.EGG_GROUPS.filePath) } returns eggGroupDtos
        every { eggGroupRepository.save(any()) } throws RuntimeException("DB error")

        val counts = eggGroupImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { eggGroupRepository.save(EggGroupEntity(id = 1, name = "Fairy")) }
        verify(exactly = 1) { importResults.add("Grupos de Ovos", expectedCounts) }
    }
}
