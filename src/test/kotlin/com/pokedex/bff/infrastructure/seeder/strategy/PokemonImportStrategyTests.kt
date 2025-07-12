package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entity.Pokemon
import com.pokedex.bff.domain.repositories.PokemonRepository
import com.pokedex.bff.infrastructure.seeder.data.EntityType
import com.pokedex.bff.infrastructure.seeder.util.DataImporter
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PokemonImportStrategyTests {

    @InjectMockKs
    private lateinit var strategy: PokemonImportStrategy

    @MockK
    private lateinit var pokemonRepository: PokemonRepository

    @MockK
    private lateinit var dataImporter: DataImporter

    @Test
    fun `should return correct entity type`() {
        assert(strategy.getEntityType() == EntityType.POKEMONS)
    }

    @Test
    fun `should import pokemons`() {
        val pokemons = listOf(Pokemon(id = 1, name = "Charizard"))

        every { dataImporter.read(any(), any<Class<Array<Pokemon>>>()) } returns pokemons.toTypedArray()

        strategy.import(dataImporter)

        verify(exactly = 1) { dataImporter.read(any(), any<Class<Array<Pokemon>>>()) }
    }
}
