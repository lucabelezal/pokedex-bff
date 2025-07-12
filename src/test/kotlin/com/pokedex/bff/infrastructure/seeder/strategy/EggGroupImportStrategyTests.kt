package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entity.EggGroup
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
class EggGroupImportStrategyTests {

    @InjectMockKs
    private lateinit var strategy: EggGroupImportStrategy

    @MockK
    private lateinit var pokemonRepository: PokemonRepository

    @MockK
    private lateinit var dataImporter: DataImporter

    @Test
    fun `should return correct entity type`() {
        assert(strategy.getEntityType() == EntityType.EGG_GROUPS)
    }

    @Test
    fun `should import egg groups`() {
        val pokemons = listOf(Pokemon(id = 1, name = "Charizard"))
        val eggGroups = listOf(EggGroup(id = 1, name = "Monster"))

        every { dataImporter.read(any(), any<Class<Array<EggGroup>>>()) } returns eggGroups.toTypedArray()
        every { pokemonRepository.findAll() } returns pokemons

        strategy.import(dataImporter)

        verify(exactly = 1) { dataImporter.read(any(), any<Class<Array<EggGroup>>>()) }
        verify(exactly = 1) { pokemonRepository.findAll() }
    }
}
