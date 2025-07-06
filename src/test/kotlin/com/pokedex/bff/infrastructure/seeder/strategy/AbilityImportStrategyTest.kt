package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.AbilityEntity
import com.pokedex.bff.domain.entities.GenerationEntity
import com.pokedex.bff.domain.entities.RegionEntity
import com.pokedex.bff.domain.repositories.AbilityRepository
import com.pokedex.bff.domain.repositories.GenerationRepository
import com.pokedex.bff.application.dto.seeder.AbilityDto
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

class AbilityImportStrategyTest {

    @MockK
    private lateinit var abilityRepository: AbilityRepository

    @MockK
    private lateinit var generationRepository: GenerationRepository // Dependency

    @MockK
    private lateinit var jsonLoader: JsonLoader

    @MockK
    private lateinit var importResults: ImportResults

    @InjectMockKs
    private lateinit var abilityImportStrategy: AbilityImportStrategy

    private val gen1 = GenerationEntity(1, "Gen I", RegionEntity(1, "Kanto"))
    private val gen2 = GenerationEntity(2, "Gen II", RegionEntity(2, "Johto"))

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { importResults.add(any(), any()) } just Runs
        every { generationRepository.findAll() } returns listOf(gen1, gen2)
    }

    @Test
    fun `getEntityName should return correct name`() {
        assertEquals("Habilidades", abilityImportStrategy.getEntityName())
    }

    @Test
    fun `import should process abilities successfully`() {
        val abilityDtos = listOf(
            AbilityDto(1, "Adaptability", "Powers up moves of the same type.", 1),
            AbilityDto(2, "Aerilate", "Normal-type moves become Flying-type moves.", 2)
        )
        val expectedEntities = listOf(
            AbilityEntity(id = 1, name = "Adaptability", description = "Powers up moves of the same type.", introducedGeneration = gen1),
            AbilityEntity(id = 2, name = "Aerilate", description = "Normal-type moves become Flying-type moves.", introducedGeneration = gen2)
        )
        val expectedCounts = ImportCounts(success = 2, errors = 0)

        every { jsonLoader.loadJson<List<AbilityDto>>(JsonFile.ABILITIES.filePath) } returns abilityDtos
        every { abilityRepository.save(any()) } answers { firstArg() }

        val counts = abilityImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { jsonLoader.loadJson<List<AbilityDto>>(JsonFile.ABILITIES.filePath) }
        verify(exactly = 1) { generationRepository.findAll() }
        verify(exactly = 2) { abilityRepository.save(any()) }
        verify { abilityRepository.save(expectedEntities[0]) }
        verify { abilityRepository.save(expectedEntities[1]) }
        verify(exactly = 1) { importResults.add("Habilidades", expectedCounts) }
    }

    @Test
    fun `import should count error if generation not found`() {
        val abilityDtos = listOf(AbilityDto(1, "Invalid Ability", "Desc", 99)) // Generation 99 does not exist
        val expectedCounts = ImportCounts(success = 0, errors = 1)

        every { jsonLoader.loadJson<List<AbilityDto>>(JsonFile.ABILITIES.filePath) } returns abilityDtos

        val counts = abilityImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 0) { abilityRepository.save(any()) } // Save is not called
        verify(exactly = 1) { importResults.add("Habilidades", expectedCounts) }
    }

    @Test
    fun `import should handle general exception during save`() {
        val abilityDtos = listOf(AbilityDto(1, "Adaptability", "Powers up moves of the same type.", 1))
        val expectedCounts = ImportCounts(success = 0, errors = 1)
        val entityToSave = AbilityEntity(id = 1, name = "Adaptability", description = "Powers up moves of the same type.", introducedGeneration = gen1)


        every { jsonLoader.loadJson<List<AbilityDto>>(JsonFile.ABILITIES.filePath) } returns abilityDtos
        every { abilityRepository.save(entityToSave) } throws RuntimeException("DB Error during save")

        val counts = abilityImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { abilityRepository.save(entityToSave) }
        verify(exactly = 1) { importResults.add("Habilidades", expectedCounts) }
    }
}
