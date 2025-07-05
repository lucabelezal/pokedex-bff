package com.pokedex.bff.infrastructure.seeder.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.bff.domain.repositories.*
import com.pokedex.bff.infrastructure.utils.JsonFile
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.io.ClassPathResource
import java.io.ByteArrayInputStream
import java.io.IOException

@ExtendWith(MockKExtension::class)
class DatabaseSeederTest {

    @MockK lateinit var regionRepository: RegionRepository
    @MockK lateinit var generationRepository: GenerationRepository
    @MockK lateinit var typeRepository: TypeRepository
    @MockK lateinit var eggGroupRepository: EggGroupRepository
    @MockK lateinit var abilityRepository: AbilityRepository
    @MockK lateinit var statsRepository: StatsRepository
    @MockK lateinit var speciesRepository: SpeciesRepository
    @MockK lateinit var evolutionChainRepository: EvolutionChainRepository
    @MockK lateinit var pokemonRepository: PokemonRepository
    @MockK lateinit var pokemonAbilityRepository: PokemonAbilityRepository
    @MockK(relaxed = true) lateinit var objectMapper: ObjectMapper

    lateinit var sut: DatabaseSeederUnderTest

    class DatabaseSeederUnderTest(
        regionRepository: RegionRepository,
        generationRepository: GenerationRepository,
        typeRepository: TypeRepository,
        eggGroupRepository: EggGroupRepository,
        abilityRepository: AbilityRepository,
        statsRepository: StatsRepository,
        speciesRepository: SpeciesRepository,
        evolutionChainRepository: EvolutionChainRepository,
        pokemonRepository: PokemonRepository,
        pokemonAbilityRepository: PokemonAbilityRepository,
        objectMapper: ObjectMapper
    ): DatabaseSeeder(
        regionRepository,
        generationRepository,
        typeRepository,
        eggGroupRepository,
        abilityRepository,
        statsRepository,
        speciesRepository,
        evolutionChainRepository,
        pokemonRepository,
        pokemonAbilityRepository,
        objectMapper
    ) {
        val mockJsonFiles = mutableMapOf<String, String>()

        // Tornando público para verificação de ordem no teste de importAll
        public override fun createClassPathResource(filePath: String): ClassPathResource {
            val content = mockJsonFiles[filePath]
            return if (content != null) {
                val inputStream = ByteArrayInputStream(content.toByteArray(Charsets.UTF_8))
                mockk<ClassPathResource>(relaxed = true).apply {
                    every { getInputStream() } returns inputStream
                    every { exists() } returns true
                    every { filename } returns filePath.substringAfterLast('/')
                    every { path } returns filePath
                }
            } else {
                mockk<ClassPathResource>(relaxed = true).apply {
                    every { inputStream } throws IOException("Mocked file not found: $filePath")
                    every { exists() } returns false
                    every { filename } returns filePath.substringAfterLast('/')
                    every { path } returns filePath
                }
            }
        }
    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        val actualSeederInstance = DatabaseSeederUnderTest(
            regionRepository,
            generationRepository,
            typeRepository,
            eggGroupRepository,
            abilityRepository,
            statsRepository,
            speciesRepository,
            evolutionChainRepository,
            pokemonRepository,
            pokemonAbilityRepository,
            objectMapper
        )
        sut = spyk(actualSeederInstance)
        sut.mockJsonFiles.clear()

        listOf(
            regionRepository,
            generationRepository,
            typeRepository,
            eggGroupRepository,
            abilityRepository,
            statsRepository,
            speciesRepository,
            evolutionChainRepository,
            pokemonRepository,
            pokemonAbilityRepository,
            objectMapper
        ).forEach {
            clearMocks(
                it,
                answers = false,
                recordedCalls = true,
                childMocks = true
            )
        }
    }

    private fun mockOtherImportsToRun(exceptFile: JsonFile? = null) {
        val allFiles = JsonFile.entries.toTypedArray()
        for (file in allFiles) {
            if (file != exceptFile) {
                sut.mockJsonFiles[file.filePath] = "[]"
                every {
                    objectMapper.readValue(
                        match<java.io.InputStream> { stream ->
                            val expectedContent = sut.mockJsonFiles[file.filePath]
                            val actualContent = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                            actualContent == expectedContent
                        },
                        any<TypeReference<List<*>>>()
                    )
                } returns emptyList<Any>()
            }
        }
    }

    // Testes individuais omitidos para brevidade
    @Nested inner class ImportRegionsTest { /* ... */ }
    @Nested inner class ImportTypesTest { /* ... */ }
    @Nested inner class ImportEggGroupsTest { /* ... */ }
    @Nested inner class ImportSpeciesTest { /* ... */ }
    @Nested inner class ImportStatsTest { /* ... */ }
    @Nested inner class ImportEvolutionChainsTest { /* ... */ }
    @Nested inner class ImportGenerationsTest { /* ... */ }
    @Nested inner class ImportAbilitiesTest { /* ... */ }
    @Nested inner class ImportPokemonsTest { /* ... */ }
    @Nested inner class ImportWeaknessesTest { /* ... */ }

    @Test
    fun `importAll should attempt to load all JSON files in the correct order`() {
        // 1. Configurar todos os JSONs para serem vazios "[]"
        JsonFile.entries.forEach { file ->
            sut.mockJsonFiles[file.filePath] = "[]"
            every { objectMapper.readValue(any<ByteArrayInputStream>(), any<TypeReference<List<Any>>>()) } returns emptyList()
        }

        // 2. Configurar mocks para chamadas findAll() que ocorrem em alguns importadores
        every { regionRepository.findAll() } returns emptyList()
        every { generationRepository.findAll() } returns emptyList()
        every { abilityRepository.findAll() } returns emptyList()
        every { statsRepository.findAll() } returns emptyList()
        every { speciesRepository.findAll() } returns emptyList()
        every { eggGroupRepository.findAll() } returns emptyList()
        every { typeRepository.findAll() } returns emptyList()
        every { evolutionChainRepository.findAll() } returns emptyList()
        every { pokemonRepository.findAll() } returns emptyList()

        // 3. Chamar importAll
        sut.importAll()

        // 4. Verificar a ordem de chamada de createClassPathResource
        // A ordem é definida em DatabaseSeeder.importAll()
        verifyOrder {
            sut.createClassPathResource(JsonFile.REGIONS.filePath)
            sut.createClassPathResource(JsonFile.TYPES.filePath)
            sut.createClassPathResource(JsonFile.EGG_GROUPS.filePath)
            sut.createClassPathResource(JsonFile.GENERATIONS.filePath)
            sut.createClassPathResource(JsonFile.ABILITIES.filePath)
            sut.createClassPathResource(JsonFile.SPECIES.filePath)
            sut.createClassPathResource(JsonFile.STATS.filePath)
            sut.createClassPathResource(JsonFile.EVOLUTION_CHAINS.filePath)
            sut.createClassPathResource(JsonFile.POKEMONS.filePath)
            sut.createClassPathResource(JsonFile.WEAKNESSES.filePath)
        }

        verify(exactly = 0) { regionRepository.save(any()) }
        verify(exactly = 0) { typeRepository.save(any()) }
        verify(exactly = 0) { eggGroupRepository.save(any()) }
        verify(exactly = 0) { generationRepository.save(any()) }
        verify(exactly = 0) { abilityRepository.save(any()) }
        verify(exactly = 0) { speciesRepository.save(any()) }
        verify(exactly = 0) { statsRepository.save(any()) }
        verify(exactly = 0) { evolutionChainRepository.save(any()) }
        verify(exactly = 0) { pokemonRepository.save(any()) }
        verify(exactly = 0) { pokemonAbilityRepository.save(any()) }
    }
}
