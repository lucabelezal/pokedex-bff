package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entity.Pokemon
import com.pokedex.bff.domain.entity.Stats
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
class StatsImportStrategyTests {

    @InjectMockKs
    private lateinit var strategy: StatsImportStrategy

    @MockK
    private lateinit var pokemonRepository: PokemonRepository

    @MockK
    private lateinit var dataImporter: DataImporter

    @Test
    fun `should return correct entity type`() {
        assert(strategy.getEntityType() == EntityType.STATS)
    }

    @Test
    fun `should import stats`() {
        val pokemons = listOf(Pokemon(id = 1, name = "Charizard"))
        val stats = listOf(Stats(id = 1, pokemonId = 1, hp = 78, attack = 84, defense = 78, specialAttack = 109, specialDefense = 85, speed = 100))

        every { dataImporter.read(any(), any<Class<Array<Stats>>>()) } returns stats.toTypedArray()
        every { pokemonRepository.findAll() } returns pokemons

        strategy.import(dataImporter)

        verify(exactly = 1) { dataImporter.read(any(), any<Class<Array<Stats>>>()) }
        verify(exactly = 1) { pokemonRepository.findAll() }
    }
}
