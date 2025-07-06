package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.TypeEntity
import com.pokedex.bff.domain.repositories.TypeRepository
import com.pokedex.bff.application.dto.seeder.TypeDto
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

class TypeImportStrategyTest {

    @MockK
    private lateinit var typeRepository: TypeRepository

    @MockK
    private lateinit var jsonLoader: JsonLoader

    @MockK
    private lateinit var importResults: ImportResults

    @InjectMockKs
    private lateinit var typeImportStrategy: TypeImportStrategy

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { importResults.add(any(), any()) } just Runs
    }

    @Test
    fun `getEntityName should return correct name`() {
        assertEquals("Tipos", typeImportStrategy.getEntityName())
    }

    @Test
    fun `import should process types successfully`() {
        val typeDtos = listOf(
            TypeDto(1, "Grass", "green"),
            TypeDto(2, "Fire", "red")
        )
        val expectedEntities = listOf(
            TypeEntity(id = 1, name = "Grass", color = "green"),
            TypeEntity(id = 2, name = "Fire", color = "red")
        )
        val expectedCounts = ImportCounts(success = 2, errors = 0)

        every { jsonLoader.loadJson<List<TypeDto>>(JsonFile.TYPES.filePath) } returns typeDtos
        every { typeRepository.save(any()) } answers { firstArg() }

        val counts = typeImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { jsonLoader.loadJson<List<TypeDto>>(JsonFile.TYPES.filePath) }
        verify(exactly = 2) { typeRepository.save(any()) }
        verify { typeRepository.save(expectedEntities[0]) }
        verify { typeRepository.save(expectedEntities[1]) }
        verify(exactly = 1) { importResults.add("Tipos", expectedCounts) }
    }

    @Test
    fun `import should handle empty list from jsonLoader`() {
        val expectedCounts = ImportCounts(success = 0, errors = 0)
        every { jsonLoader.loadJson<List<TypeDto>>(JsonFile.TYPES.filePath) } returns emptyList()

        val counts = typeImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 0) { typeRepository.save(any()) }
        verify(exactly = 1) { importResults.add("Tipos", expectedCounts) }
    }

    @Test
    fun `import should handle exceptions during save and count errors`() {
        val typeDtos = listOf(TypeDto(1, "Water", "blue"))
        val expectedCounts = ImportCounts(success = 0, errors = 1)

        every { jsonLoader.loadJson<List<TypeDto>>(JsonFile.TYPES.filePath) } returns typeDtos
        every { typeRepository.save(any()) } throws RuntimeException("DB error")

        val counts = typeImportStrategy.import(importResults)

        assertEquals(expectedCounts, counts)
        verify(exactly = 1) { typeRepository.save(TypeEntity(id = 1, name = "Water", color = "blue")) }
        verify(exactly = 1) { importResults.add("Tipos", expectedCounts) }
    }
}
