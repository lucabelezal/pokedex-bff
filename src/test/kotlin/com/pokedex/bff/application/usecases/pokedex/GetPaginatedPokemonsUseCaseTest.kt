package com.pokedex.bff.application.usecases.pokedex

import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.entities.Type
import com.pokedex.bff.domain.repositories.PokemonRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

/**
 * Unit tests for GetPaginatedPokemonsUseCase
 * 
 * Demonstrates testing at the application layer level, focusing on business logic
 * without infrastructure concerns. Uses mocks to isolate the use case under test.
 */
class GetPaginatedPokemonsUseCaseTest {

    private val pokemonRepository: PokemonRepository = mockk()
    private lateinit var useCase: GetPaginatedPokemonsUseCase

    @BeforeEach
    fun setup() {
        useCase = GetPaginatedPokemonsUseCase(pokemonRepository)
    }

    @Test
    fun `should return paginated pokemon list when valid parameters`() {
        // Given
        val page = 0
        val size = 10
        val expectedPageable = PageRequest.of(page, size)
        
        val mockPokemon = createMockPokemon(
            id = 1L,
            number = "001",
            name = "Bulbasaur",
            types = listOf(createMockType("grass", "#78C850"))
        )
        
        val mockPage = PageImpl(listOf(mockPokemon), expectedPageable, 1)
        every { pokemonRepository.findAll(expectedPageable) } returns mockPage

        // When
        val result = useCase.execute(page, size)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.pokemons).hasSize(1)
        assertThat(result.pageInfo.currentPage).isEqualTo(0)
        assertThat(result.pageInfo.totalPages).isEqualTo(1)
        assertThat(result.pageInfo.totalElements).isEqualTo(1)
        assertThat(result.pageInfo.hasNext).isFalse()
        
        val pokemon = result.pokemons.first()
        assertThat(pokemon.number).isEqualTo("Nº001")
        assertThat(pokemon.name).isEqualTo("Bulbasaur")
        assertThat(pokemon.types).hasSize(1)
        assertThat(pokemon.types.first().name).isEqualTo("grass")
        assertThat(pokemon.image.element.type).isEqualTo("GRASS")
        assertThat(pokemon.image.element.color).isEqualTo("#78C850")

        verify(exactly = 1) { pokemonRepository.findAll(expectedPageable) }
    }

    @Test
    fun `should format pokemon number correctly when number is null`() {
        // Given
        val page = 0
        val size = 10
        val expectedPageable = PageRequest.of(page, size)
        
        val mockPokemon = createMockPokemon(
            id = 1L,
            number = null, // null number
            name = "MissingNo",
            types = emptyList()
        )
        
        val mockPage = PageImpl(listOf(mockPokemon), expectedPageable, 1)
        every { pokemonRepository.findAll(expectedPageable) } returns mockPage

        // When
        val result = useCase.execute(page, size)

        // Then
        val pokemon = result.pokemons.first()
        assertThat(pokemon.number).isEqualTo("NºUNK")
        assertThat(pokemon.image.element.type).isEqualTo("UNKNOWN")
        assertThat(pokemon.image.element.color).isEqualTo("#CCCCCC")
    }

    @Test
    fun `should use default values when pokemon has no types`() {
        // Given
        val page = 0
        val size = 10
        val expectedPageable = PageRequest.of(page, size)
        
        val mockPokemon = createMockPokemon(
            id = 1L,
            number = "025",
            name = "Pikachu",
            types = emptyList() // no types
        )
        
        val mockPage = PageImpl(listOf(mockPokemon), expectedPageable, 1)
        every { pokemonRepository.findAll(expectedPageable) } returns mockPage

        // When
        val result = useCase.execute(page, size)

        // Then
        val pokemon = result.pokemons.first()
        assertThat(pokemon.types).isEmpty()
        assertThat(pokemon.image.element.type).isEqualTo("UNKNOWN")
        assertThat(pokemon.image.element.color).isEqualTo("#CCCCCC")
    }

    @Test
    fun `should throw exception when page is negative`() {
        // Given
        val invalidPage = -1
        val size = 10

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            useCase.execute(invalidPage, size)
        }
        
        assertThat(exception.message).isEqualTo("Page number must be non-negative")
    }

    @Test
    fun `should throw exception when size is zero or negative`() {
        // Given
        val page = 0
        val invalidSize = 0

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            useCase.execute(page, invalidSize)
        }
        
        assertThat(exception.message).isEqualTo("Page size must be positive")
    }

    @Test
    fun `should throw exception when size exceeds maximum`() {
        // Given
        val page = 0
        val excessiveSize = 101 // MAX_PAGE_SIZE is 100

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            useCase.execute(page, excessiveSize)
        }
        
        assertThat(exception.message).isEqualTo("Page size cannot exceed 100")
    }

    @Test
    fun `should handle empty result page`() {
        // Given
        val page = 0
        val size = 10
        val expectedPageable = PageRequest.of(page, size)
        
        val emptyPage = PageImpl<Pokemon>(emptyList(), expectedPageable, 0)
        every { pokemonRepository.findAll(expectedPageable) } returns emptyPage

        // When
        val result = useCase.execute(page, size)

        // Then
        assertThat(result.pokemons).isEmpty()
        assertThat(result.pageInfo.totalElements).isEqualTo(0)
        assertThat(result.pageInfo.totalPages).isEqualTo(1)
        assertThat(result.pageInfo.hasNext).isFalse()
    }

    private fun createMockPokemon(
        id: Long,
        number: String?,
        name: String,
        types: List<Type>
    ): Pokemon = mockk {
        every { this@mockk.id } returns id
        every { this@mockk.number } returns number
        every { this@mockk.name } returns name
        every { this@mockk.types } returns types
        every { sprites?.other?.home?.frontDefault } returns "https://example.com/pokemon/$id.png"
    }

    private fun createMockType(name: String, color: String): Type = mockk {
        every { this@mockk.name } returns name
        every { this@mockk.color } returns color
    }
}