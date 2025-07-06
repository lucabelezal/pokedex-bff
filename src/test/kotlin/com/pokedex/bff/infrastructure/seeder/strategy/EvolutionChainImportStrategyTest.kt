package com.pokedex.bff.infrastructure.seeder.strategy

import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.bff.domain.entities.EvolutionChainEntity
import com.pokedex.bff.domain.repositories.EvolutionChainRepository
import com.pokedex.bff.application.dto.seeder.EvolutionChainDto
import com.pokedex.bff.application.dto.seeder.EvolutionLinkDto // Assuming structure for chainData
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

class EvolutionChainImportStrategyTest {

    @MockK
    private lateinit var evolutionChainRepository: EvolutionChainRepository

    @MockK
    private lateinit var jsonLoader: JsonLoader

    @MockK
    private lateinit var objectMapper: ObjectMapper

    @MockK
    private lateinit var importResults: ImportResults

    @InjectMockKs
    private lateinit var evolutionChainImportStrategy: EvolutionChainImportStrategy

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { importResults.add(any(), any()) } just Runs
    }

    @Test
    fun `getEntityName should return correct name`() {
        assertEquals("Cadeias de Evolução", evolutionChainImportStrategy.getEntityName())
    }

    @Test
    fun `import should process evolution chains successfully`() {
        val chainLink = EvolutionLinkDto("bulbasaur", emptyList()) // Simplified
        val evolutionChainDtos = listOf(
            EvolutionChainDto(1, chainLink),
            EvolutionChainDto(2, EvolutionLinkDto("charmander", emptyList()))
        )
        val jsonStringForChain1 = """{"species_name":"bulbasaur","evolves_to":[]}"""
        val jsonStringForChain2 = """{"species_name":"charmander","evolves_to":[]}"""

        val expectedEntities = listOf(
            EvolutionChainEntity(id = 1, chainData = jsonStringForChain1),
            EvolutionChainEntity(id = 2, chainData = jsonStringForChain2)
        )
        val expectedCounts = ImportCounts(success = 2, errors = 0)

        every { jsonLoader.loadJson<List<EvolutionChainDto>>(JsonFile.EVOLUTION_CHAINS.filePath) } returns evolutionChainDtos
        every { objectMapper.writeValueAsString(evolutionChainDtos[0].chainData) } returns jsonStringForChain1
        every { objectMapper.writeValueAsString(evolutionChainDtos[1].chainData) } returns jsonStringForChain2
        every { evolutionChainRepository.save(any()) } answers { firstArg() }

        val counts = evolutionChainImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { jsonLoader.loadJson<List<EvolutionChainDto>>(JsonFile.EVOLUTION_CHAINS.filePath) }
        verify(exactly = 2) { objectMapper.writeValueAsString(any()) }
        verify(exactly = 2) { evolutionChainRepository.save(any()) }
        verify { evolutionChainRepository.save(expectedEntities[0]) }
        verify { evolutionChainRepository.save(expectedEntities[1]) }
        verify(exactly = 1) { importResults.add("Cadeias de Evolução", expectedCounts) }
    }

    @Test
    fun `import should handle objectMapper throwing exception`() {
        val chainLink = EvolutionLinkDto("squirtle", emptyList())
        val evolutionChainDtos = listOf(EvolutionChainDto(1, chainLink))
        val expectedCounts = ImportCounts(success = 0, errors = 1)

        every { jsonLoader.loadJson<List<EvolutionChainDto>>(JsonFile.EVOLUTION_CHAINS.filePath) } returns evolutionChainDtos
        every { objectMapper.writeValueAsString(any()) } throws Exception("Serialization error")

        val counts = evolutionChainImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { objectMapper.writeValueAsString(chainLink) }
        verify(exactly = 0) { evolutionChainRepository.save(any()) }
        verify(exactly = 1) { importResults.add("Cadeias de Evolução", expectedCounts) }
    }
}
