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
    fun setUp() {
        useCase = GetPaginatedPokemonsUseCase(pokemonRepository)
    }

    @Test
    fun `should return paginated pokemon list when valid parameters`() {
        // Given
        val page = 0
        val size = 5
        
        val mockPokemon = createMockPokemon(
            id = 1L,
            number = "001",
            name = "Bulbasaur",
            types = listOf(createMockType("grass", "#78C850"))
        )
        
        val mockPage = com.pokedex.bff.domain.common.Page(
            content = listOf(mockPokemon),
            pageNumber = page,
            pageSize = size,
            totalElements = 1,
            totalPages = 1,
            isFirst = true,
            isLast = true,
            hasNext = false,
            hasPrevious = false
        )
        every { pokemonRepository.findAll(page, size) } returns mockPage

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
        assertThat(pokemon.image.pokemon.id).isEqualTo(1L)
        assertThat(pokemon.image.pokemon.name).isEqualTo("bulbasaur")
        assertThat(pokemon.image.pokemon.url).isEqualTo("https://example.com/pokemon/1.png")

        verify(exactly = 1) { pokemonRepository.findAll(page, size) }
    }

    @Test
    fun `should format pokemon number correctly when number is null`() {
        // Given
        val page = 0
        val size = 10
        
        val mockPokemon = createMockPokemon(
            id = 1L,
            number = null, // null number
            name = "MissingNo",
            types = emptyList()
        )
        
        val mockPage = com.pokedex.bff.domain.common.Page(
            content = listOf(mockPokemon),
            pageNumber = page,
            pageSize = size,
            totalElements = 1,
            totalPages = 1,
            isFirst = true,
            isLast = true,
            hasNext = false,
            hasPrevious = false
        )
        every { pokemonRepository.findAll(page, size) } returns mockPage

        // When
        val result = useCase.execute(page, size)

        // Then
        val pokemon = result.pokemons.first()
        assertThat(pokemon.number).isEqualTo("Nº???")
        assertThat(pokemon.name).isEqualTo("MissingNo")
        assertThat(pokemon.image.element.type).isEqualTo("UNKNOWN")
        assertThat(pokemon.image.element.color).isEqualTo("#CCCCCC")
    }

    @Test
    fun `should use default values when pokemon has no types`() {
        // Given
        val page = 0
        val size = 10
        
        val mockPokemon = createMockPokemon(
            id = 1L,
            number = "025",
            name = "Pikachu",
            types = emptyList() // no types
        )
        
        val mockPage = com.pokedex.bff.domain.common.Page(
            content = listOf(mockPokemon),
            pageNumber = page,
            pageSize = size,
            totalElements = 1,
            totalPages = 1,
            isFirst = true,
            isLast = true,
            hasNext = false,
            hasPrevious = false
        )
        every { pokemonRepository.findAll(page, size) } returns mockPage

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
        // Given & When & Then
        val exception = assertThrows<IllegalArgumentException> {
            useCase.execute(-1, 10)
        }
        assertThat(exception.message).contains("Page number cannot be negative")
    }

    @Test
    fun `should throw exception when size is zero or negative`() {
        // Given & When & Then
        val exception = assertThrows<IllegalArgumentException> {
            useCase.execute(0, 0)
        }
        assertThat(exception.message).contains("Page size must be greater than zero")
    }

    @Test
    fun `should throw exception when size exceeds maximum`() {
        // Given & When & Then
        val exception = assertThrows<IllegalArgumentException> {
            useCase.execute(0, 101)
        }
        assertThat(exception.message).contains("Page size cannot exceed 100")
    }

    @Test
    fun `should handle empty result page`() {
        // Given
        val page = 0
        val size = 10
        
        val emptyPage = com.pokedex.bff.domain.common.Page<Pokemon>(
            content = emptyList(),
            pageNumber = page,
            pageSize = size,
            totalElements = 0,
            totalPages = 1,
            isFirst = true,
            isLast = true,
            hasNext = false,
            hasPrevious = false
        )
        every { pokemonRepository.findAll(page, size) } returns emptyPage

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
        every { this@mockk.types } returns types.toSet()
        every { sprites } returns mockk {
            every { other } returns mockk {
                every { home } returns mockk {
                    every { frontDefault } returns "https://example.com/pokemon/$id.png"
                }
            }
        }
    }

    private fun createMockType(name: String, color: String): Type = mockk {
        every { this@mockk.name } returns name
        every { this@mockk.color } returns color
    }
}
