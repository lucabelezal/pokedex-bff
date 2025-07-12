//package com.pokedex.bff.infrastructure.seeder.strategy
//
//import com.pokedex.bff.domian.PokemonMother
//import com.pokedex.bff.domian.SpeciesMother
//import com.pokedex.bff.domain.repositories.PokemonRepository
//import com.pokedex.bff.infrastructure.seeder.data.EntityType
//import com.pokedex.bff.infrastructure.seeder.util.DataImporter
//import io.mockk.every
//import io.mockk.impl.annotations.InjectMockKs
//import io.mockk.impl.annotations.MockK
//import io.mockk.junit5.MockKExtension
//import io.mockk.verify
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//
//@ExtendWith(MockKExtension::class)
//class SpeciesImportStrategyTests {
//
//    @InjectMockKs
//    private lateinit var strategy: SpeciesImportStrategy
//
//    @MockK
//    private lateinit var pokemonRepository: PokemonRepository
//
//    @MockK
//    private lateinit var dataImporter: DataImporter
//
//    @Test
//    fun `should return correct entity type`() {
//        assert(strategy.getEntityType() == EntityType.SPECIES)
//    }
//
//    @Test
//    fun `should import species`() {
//        val pokemons = listOf(PokemonMother.withCharizard())
//        val species = listOf(SpeciesMother.withCharmander())
//
//        every { dataImporter.read(any(), any<Class<Array<Any>>>()) } returns species.toTypedArray()
//        every { pokemonRepository.findAll() } returns pokemons
//
//        strategy.import(dataImporter)
//
//        verify(exactly = 1) { dataImporter.read(any(), any<Class<Array<Any>>>()) }
//        verify(exactly = 1) { pokemonRepository.findAll() }
//    }
//}
