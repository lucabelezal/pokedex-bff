package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entity.EvolutionChain
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
class EvolutionChainImportStrategyTests {

    @InjectMockKs
    private lateinit var strategy: EvolutionChainImportStrategy

    @MockK
    private lateinit var pokemonRepository: PokemonRepository

    @MockK
    private lateinit var dataImporter: DataImporter

    @Test
    fun `should return correct entity type`() {
        assert(strategy.getEntityType() == EntityType.EVOLUTION_CHAINS)
    }

    @Test
    fun `should import evolution chains`() {
        val pokemons = listOf(Pokemon(id = 1, name = "Charizard"))
        val evolutionChains = listOf(EvolutionChain(id = 1, pokemonId = 1, evolutionTo = 2))

        every { dataImporter.read(any(), any<Class<Array<EvolutionChain>>>()) } returns evolutionChains.toTypedArray()
        every { pokemonRepository.findAll() } returns pokemons

        strategy.import(dataImporter)

        verify(exactly = 1) { dataImporter.read(any(), any<Class<Array<EvolutionChain>>>()) }
        verify(exactly = 1) { pokemonRepository.findAll() }
    }
}
