package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.PokemonEntity
import com.pokedex.bff.domain.entities.TypeEntity
import com.pokedex.bff.domain.entities.StatsEntity
import com.pokedex.bff.domain.entities.GenerationEntity
import com.pokedex.bff.domain.entities.SpeciesEntity
import com.pokedex.bff.domain.entities.EvolutionChainEntity
import com.pokedex.bff.domain.entities.SpritesVO
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

        // Initialize Pokemon with mutable sets for types and weaknesses
        pokemonPikachu = PokemonEntity(
            id = 25, name = "Pikachu", number = 25, description = "Electric mouse",
            height = 4, weight = 60, genderRateValue = 1, eggCycles = 10,
            stats = dummyStats, generation = dummyGeneration, species = dummySpecies, evolutionChain = dummyEvolutionChain,
            types = mutableSetOf(TypeEntity(4, "Electric", "yellow")), // Pikachu is Electric
            weaknesses = mutableSetOf(), // Initially no weaknesses for testing addition
            sprites = dummySprites
        )
        pokemonCharmander = PokemonEntity(
            id = 4, name = "Charmander", number = 4, description = "Fire lizard",
            height = 6, weight = 85, genderRateValue = 1, eggCycles = 20,
            stats = dummyStats, generation = dummyGeneration, species = dummySpecies, evolutionChain = dummyEvolutionChain,
            types = mutableSetOf(typeFire), // Charmander is Fire
            weaknesses = mutableSetOf(typeWater), // Already weak to Water
            sprites = dummySprites
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
        // Pikachu (Electric) should become weak to Grass (hypothetically for this test)
        val weaknessDtos = listOf(WeaknessDto("Pikachu", listOf("Grass")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val counts = weaknessImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 1, errors = 0), counts)
        assertTrue(pokemonPikachu.weaknesses.contains(typeGrass))
        verify(exactly = 1) { pokemonRepository.save(pokemonPikachu) }
        verify(exactly = 1) { importResults.add("Fraquezas", counts) }
    }

    @Test
    fun `import should not save if weakness already exists`() {
        // Charmander is already weak to Water.
        val weaknessDtos = listOf(WeaknessDto("Charmander", listOf("Water")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val initialWeaknessCount = pokemonCharmander.weaknesses.size
        val counts = weaknessImportStrategy.import(importResults)

        // Not a success because save is not called. Not an error either.
        assertEquals(ImportCounts(success = 0, errors = 0), counts)
        assertEquals(initialWeaknessCount, pokemonCharmander.weaknesses.size)
        verify(exactly = 0) { pokemonRepository.save(pokemonCharmander) }
    }

    @Test
    fun `import should count error if pokemon in DTO not found`() {
        val weaknessDtos = listOf(WeaknessDto("Mewtwo", listOf("Bug")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val counts = weaknessImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 0, errors = 1), counts)
        verify(exactly = 0) { pokemonRepository.save(any()) }
    }

    @Test
    fun `import should count error if all specified weakness types in DTO are not found`() {
        val weaknessDtos = listOf(WeaknessDto("Pikachu", listOf("UnknownType", "AnotherUnknown")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val counts = weaknessImportStrategy.import(importResults)

        // The current logic will result in weaknessTypesFound being empty.
        // The pokemon itself is found, but no valid types to add.
        // The save is not called. It's counted as an error because specified types were not found.
        assertEquals(ImportCounts(success = 0, errors = 1), counts)
        verify(exactly = 0) { pokemonRepository.save(pokemonPikachu) }
    }

    @Test
    fun `import should save if some weakness types are new and others are not`() {
        // Charmander is already weak to Water. Let's add Water (existing) and Grass (new).
        val weaknessDtos = listOf(WeaknessDto("Charmander", listOf("Water", "Grass")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val counts = weaknessImportStrategy.import(importResults)

        // Counts as a success because the set was modified (Grass was added).
        assertEquals(ImportCounts(success = 1, errors = 0), counts)
        assertTrue(pokemonCharmander.weaknesses.contains(typeGrass))
        verify(exactly = 1) { pokemonRepository.save(pokemonCharmander) }
    }

    @Test
    fun `import should handle empty weakness list in DTO gracefully`() {
        val weaknessDtos = listOf(WeaknessDto("Pikachu", emptyList()))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos

        val initialPikachuWeaknessCount = pokemonPikachu.weaknesses.size
        val counts = weaknessImportStrategy.import(importResults)

        // No new weaknesses to add, no types not found. Not an error, not a success for this DTO.
        // The current logic: weaknessTypesFound will be empty, save not called.
        // It's not an error by the current definition.
        assertEquals(ImportCounts(success = 0, errors = 0), counts)
        assertEquals(initialPikachuWeaknessCount, pokemonPikachu.weaknesses.size)
        verify(exactly = 0) { pokemonRepository.save(pokemonPikachu) }
    }

    @Test
    fun `import should handle exception during pokemon save`() {
        val weaknessDtos = listOf(WeaknessDto("Pikachu", listOf("Grass")))
        every { jsonLoader.loadJson<List<WeaknessDto>>(JsonFile.WEAKNESSES.filePath) } returns weaknessDtos
        every { pokemonRepository.save(pokemonPikachu) } throws RuntimeException("DB Error")

        val counts = weaknessImportStrategy.import(importResults)
        assertEquals(ImportCounts(success = 0, errors = 1), counts)
    }
}
