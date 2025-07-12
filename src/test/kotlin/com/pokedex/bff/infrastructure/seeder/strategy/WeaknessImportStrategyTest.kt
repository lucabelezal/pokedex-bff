package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.*
import com.pokedex.bff.domain.repositories.PokemonRepository
import com.pokedex.bff.domain.repositories.TypeRepository
import com.pokedex.bff.application.dto.seeder.WeaknessDto
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

class WeaknessImportStrategyTest {

    @MockK
    private lateinit var pokemonRepository: PokemonRepository

    @MockK
    private lateinit var typeRepository: TypeRepository

    @MockK
    private lateinit var jsonLoader: JsonLoader

    @MockK
    private lateinit var importResults: ImportResults

    @InjectMockKs
    private lateinit var weaknessImportStrategy: WeaknessImportStrategy

    private val dummyStats = mockk<StatsEntity>()
    private val dummyGeneration = mockk<GenerationEntity>()
    private val dummySpecies = mockk<SpeciesEntity>()
    private val dummyEvolutionChain = mockk<EvolutionChainEntity>()
    private val dummySprites = mockk<SpritesVO>()


    private val typeFire = TypeEntity(1, "Fire", "red")
    private val typeWater = TypeEntity(2, "Water", "blue")
    private val typeGrass = TypeEntity(3, "Grass", "green")

    private lateinit var pokemonPikachu: PokemonEntity
    private lateinit var pokemonCharmander: PokemonEntity

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { importResults.add(any(), any()) } just Runs

        pokemonPikachu = PokemonEntity(
            id = 25, name = "Pikachu", number = "025", description = "Electric mouse",
            height = 0.4, weight = 6.0, eggCycles = 10,
            stats = dummyStats, generation = dummyGeneration, species = dummySpecies, evolutionChain = dummyEvolutionChain,
            types = mutableSetOf(TypeEntity(4, "Electric", "yellow")),
            weaknesses = mutableSetOf(),
            sprites = dummySprites,
            region = mockk(),
            genderMale = 50.0f,
            genderFemale = 50.0f
        )
        pokemonCharmander = PokemonEntity(
            id = 4, name = "Charmander", number = "004", description = "Fire lizard",
            height = 0.6, weight = 8.5, eggCycles = 20,
            stats = dummyStats, generation = dummyGeneration, species = dummySpecies, evolutionChain = dummyEvolutionChain,
            types = mutableSetOf(typeFire),
            weaknesses = mutableSetOf(typeWater),
            sprites = dummySprites,
            region = mockk(),
            genderMale = 87.5f,
            genderFemale = 12.5f
        )

        every { typeRepository.findAll() } returns listOf(typeFire, typeWater, typeGrass, TypeEntity(4, "Electric", "yellow"))
        every { pokemonRepository.findAll() } returns listOf(pokemonPikachu, pokemonCharmander)
        every { pokemonRepository.save(any()) } answers { firstArg() }
    }

    @Test
    fun `getEntityName should return correct name`() {
        assertEquals("Fraquezas", weaknessImportStrategy.getEntityName())
    }

    @Test
    fun `import should add new weaknesses to a Pokemon`() {
        val weaknessDtos = listOf(WeaknessDto(pokemonId = 25, pokemonName = "Pikachu", weaknesses = listOf("Grass")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val counts = weaknessImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 1, errors = 0), counts)
        assertTrue(pokemonPikachu.weaknesses.contains(typeGrass))
        verify(exactly = 1) { pokemonRepository.save(pokemonPikachu) }
        verify(exactly = 1) { importResults.add("Fraquezas", counts) }
    }

    @Test
    fun `import should not save if weakness already exists`() {
        val weaknessDtos = listOf(WeaknessDto(pokemonId = 4, pokemonName = "Charmander", weaknesses = listOf("Water")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val initialWeaknessCount = pokemonCharmander.weaknesses.size
        val counts = weaknessImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 0, errors = 0), counts)
        assertEquals(initialWeaknessCount, pokemonCharmander.weaknesses.size)
        verify(exactly = 0) { pokemonRepository.save(pokemonCharmander) }
    }

    @Test
    fun `import should count error if pokemon in DTO not found`() {
        val weaknessDtos = listOf(WeaknessDto(pokemonId = 999, pokemonName = "Mewtwo", weaknesses = listOf("Bug")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val counts = weaknessImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 0, errors = 1), counts)
        verify(exactly = 0) { pokemonRepository.save(any()) }
    }

    @Test
    fun `import should count error if all specified weakness types in DTO are not found`() {
        val weaknessDtos = listOf(WeaknessDto(pokemonId = 25, pokemonName = "Pikachu", weaknesses = listOf("UnknownType", "AnotherUnknown")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val counts = weaknessImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 0, errors = 1), counts)
        verify(exactly = 0) { pokemonRepository.save(pokemonPikachu) }
    }

    @Test
    fun `import should save if some weakness types are new and others are not`() {
        val weaknessDtos = listOf(WeaknessDto(pokemonId = 4, pokemonName = "Charmander", weaknesses = listOf("Water", "Grass")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val counts = weaknessImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 1, errors = 0), counts)
        assertTrue(pokemonCharmander.weaknesses.contains(typeGrass))
        verify(exactly = 1) { pokemonRepository.save(pokemonCharmander) }
    }

    @Test
    fun `import should handle empty weakness list in DTO gracefully`() {
        val weaknessDtos = listOf(WeaknessDto(pokemonId = 25, pokemonName = "Pikachu", weaknesses = emptyList()))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val initialPikachuWeaknessCount = pokemonPikachu.weaknesses.size
        val counts = weaknessImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 0, errors = 0), counts)
        assertEquals(initialPikachuWeaknessCount, pokemonPikachu.weaknesses.size)
        verify(exactly = 0) { pokemonRepository.save(pokemonPikachu) }
    }

    @Test
    fun `import should handle exception during pokemon save`() {
        val weaknessDtos = listOf(WeaknessDto(pokemonId = 25, pokemonName = "Pikachu", weaknesses = listOf("Grass")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos
        every { pokemonRepository.save(pokemonPikachu) } throws RuntimeException("DB Error")

        val counts = weaknessImportStrategy.import(importResults)
        assertEquals(ImportCounts(success = 0, errors = 1), counts)
    }
}
