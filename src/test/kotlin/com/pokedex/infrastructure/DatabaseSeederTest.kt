package com.pokedex.infrastructure

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pokedex.domain.dto.PokemonDTO
import com.pokedex.domain.dto.PokemonGenderDTO
import com.pokedex.domain.dto.PokemonStatDTO
import com.pokedex.domain.entity.Pokemon
import com.pokedex.domain.entity.PokemonType // Assuming PokemonType exists
import com.pokedex.domain.repository.PokemonRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import java.io.InputStream

class DatabaseSeederTest {

    private lateinit var pokemonRepository: PokemonRepository
    private lateinit var objectMapper: ObjectMapper // Will be part of spyk'd seeder
    private lateinit var databaseSeeder: DatabaseSeeder // This will be a spyk

    @BeforeEach
    fun setUp() {
        pokemonRepository = mockk(relaxed = true)
        // objectMapper is part of DatabaseSeeder, if we spyk DatabaseSeeder, its objectMapper will be used.
        // We can pass a mocked objectMapper to the spyk if needed, but for now, jacksonObjectMapper is fine if seeder uses it.
        objectMapper = jacksonObjectMapper()
        databaseSeeder = spyk(DatabaseSeeder(pokemonRepository, objectMapper), recordPrivateCalls = true)
    }

    @Test
    fun `run should seed pokemons when repository is empty`() {
        // Arrange
        every { pokemonRepository.count() } returns 0L

        val bulbasaurDTO = PokemonDTO(
            id = "1",
            name = "Bulbasaur",
            height = 7,
            weight = 69,
            types = listOf(PokemonType(name = "Grass"), PokemonType(name = "Poison")), // Assuming PokemonType constructor
            stats = listOf(PokemonStatDTO(name = "HP", value = 45)),
            gender = PokemonGenderDTO(male = 87.5f, female = 12.5f)
        )
        val charmanderDTO = PokemonDTO(
            id = "4",
            name = "Charmander",
            height = 6,
            weight = 85,
            types = listOf(PokemonType(name = "Fire")),
            stats = listOf(PokemonStatDTO(name = "HP", value = 39)),
            gender = PokemonGenderDTO(male = 87.5f, female = 12.5f)
        )
        val pokemonListDTO = listOf(bulbasaurDTO, charmanderDTO)

        // Mock the loadPokemons method directly using the spyk
        every { databaseSeeder.loadPokemons() } returns pokemonListDTO

        // Act
        databaseSeeder.run()

        // Assert
        verify(exactly = 1) { pokemonRepository.save(any<Pokemon>()) } // Verify save is called for Bulbasaur

        val expectedBulbasaurEntity = Pokemon(
            id = "1",
            name = "Bulbasaur",
            height = 7,
            weight = 69,
            genderMale = 87.5f,
            genderFemale = 12.5f
            // TODO: Add other fields once they are mapped in DatabaseSeeder
        )
        verify { pokemonRepository.save(match {
            it.id == expectedBulbasaurEntity.id &&
            it.name == expectedBulbasaurEntity.name &&
            it.height == expectedBulbasaurEntity.height &&
            it.weight == expectedBulbasaurEntity.weight &&
            it.genderMale == expectedBulbasaurEntity.genderMale &&
            it.genderFemale == expectedBulbasaurEntity.genderFemale
            // TODO: Add other field comparisons
        }) }

        // We can add a captor to verify all saved entities if needed more precisely
        // val pokemonCaptor = slot<Pokemon>()
        // verify(exactly = 2) { pokemonRepository.save(capture(pokemonCaptor)) }
        // assertEquals("Bulbasaur", pokemonCaptor.captured.name) // Example for one
    }

    @Test
    fun `run should not seed pokemons when repository is not empty`() {
        // Arrange
        every { pokemonRepository.count() } returns 1L // Repository is not empty

        // Act
        databaseSeeder.run()

        // Assert
        verify(exactly = 0) { pokemonRepository.save(any()) } // Save should not be called
    }

    @Test
    fun `run should handle genderless pokemon`() {
        every { pokemonRepository.count() } returns 0L
        val magnemiteDTO = PokemonDTO(
            id = "81",
            name = "Magnemite",
            height = 3,
            weight = 60,
            types = listOf(PokemonType(name = "Electric"), PokemonType(name = "Steel")),
            stats = listOf(PokemonStatDTO(name = "HP", value = 25)),
            gender = PokemonGenderDTO(male = 0.0f, female = 0.0f)
        )
        val pokemonListDTO = listOf(magnemiteDTO)
        every { databaseSeeder.loadPokemons() } returns pokemonListDTO

        databaseSeeder.run()

        val expectedMagnemiteEntity = Pokemon(
            id = "81",
            name = "Magnemite",
            height = 3,
            weight = 60,
            genderMale = 0.0f,
            genderFemale = 0.0f
        )
        verify { pokemonRepository.save(match {
            it.id == expectedMagnemiteEntity.id &&
            it.genderMale == expectedMagnemiteEntity.genderMale &&
            it.genderFemale == expectedMagnemiteEntity.genderFemale
        })}
    }
}

// Helper for mocking getResourceAsStream if needed directly
// For example, if TypeReference::class.java.getResourceAsStream is problematic to mock directly
// Might need to adjust how the InputStream is provided to the seeder for tests,
// e.g., by making loadPokemons method protected or package-private and overriding it in tests,
// or by injecting a ResourceLoader.

// Assuming PokemonType entity exists for simplicity in DTO
// package com.pokedex.domain.entity
// data class PokemonType(val name: String) // Simplified for test compilation
// If PokemonType is an enum or a more complex entity, adjust DTO and test data accordingly.
