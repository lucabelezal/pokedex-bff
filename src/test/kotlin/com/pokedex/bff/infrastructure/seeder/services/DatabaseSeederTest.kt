package com.pokedex.bff.infrastructure.seeder.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pokedex.bff.application.dto.seeder.GenderRateSeedDTO
import com.pokedex.bff.application.dto.seeder.PokemonDto
import com.pokedex.bff.application.dto.seeder.SpritesDto // Supondo que exista e seja simples
import com.pokedex.bff.application.dto.seeder.PokemonAbilityDto // Supondo que exista
import com.pokedex.bff.domain.entities.PokemonEntity
import com.pokedex.bff.domain.entities.StatsEntity
import com.pokedex.bff.domain.entities.GenerationEntity
import com.pokedex.bff.domain.entities.SpeciesEntity
import com.pokedex.bff.domain.entities.EvolutionChainEntity
import com.pokedex.bff.domain.entities.RegionEntity
import com.pokedex.bff.domain.entities.AbilityEntity
import com.pokedex.bff.domain.entities.TypeEntity
import com.pokedex.bff.domain.entities.EggGroupEntity
import com.pokedex.bff.domain.repositories.*
import com.pokedex.bff.infrastructure.utils.JsonFile
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.springframework.core.io.ClassPathResource
import org.springframework.data.jpa.repository.JpaRepository

class DatabaseSeederTest {

    private lateinit var regionRepository: RegionRepository
    private lateinit var typeRepository: TypeRepository
    private lateinit var eggGroupRepository: EggGroupRepository
    private lateinit var generationRepository: GenerationRepository
    private lateinit var abilityRepository: AbilityRepository
    private lateinit var speciesRepository: SpeciesRepository
    private lateinit var statsRepository: StatsRepository
    private lateinit var evolutionChainRepository: EvolutionChainRepository
    private lateinit var pokemonRepository: PokemonRepository
    private lateinit var pokemonAbilityRepository: PokemonAbilityRepository
    private lateinit var objectMapper: ObjectMapper // Usaremos o real para o spy, mas podemos mockar o loadJson

    private lateinit var databaseSeeder: DatabaseSeeder

    // Mocks para as entidades relacionadas que o Pokémon precisa
    private val mockStats = mockk<StatsEntity>()
    private val mockGeneration = mockk<GenerationEntity>()
    private val mockSpecies = mockk<SpeciesEntity>()
    private val mockEvolutionChain = mockk<EvolutionChainEntity>()
    private val mockRegion = mockk<RegionEntity>()
    private val mockAbility = mockk<AbilityEntity>()
    private val mockType = mockk<TypeEntity>()
    private val mockEggGroup = mockk<EggGroupEntity>()


    @BeforeEach
    fun setUp() {
        regionRepository = mockk(relaxed = true)
        typeRepository = mockk(relaxed = true)
        eggGroupRepository = mockk(relaxed = true)
        generationRepository = mockk(relaxed = true)
        abilityRepository = mockk(relaxed = true)
        speciesRepository = mockk(relaxed = true)
        statsRepository = mockk(relaxed = true)
        evolutionChainRepository = mockk(relaxed = true)
        pokemonRepository = mockk(relaxed = true)
        pokemonAbilityRepository = mockk(relaxed = true)

        objectMapper = jacksonObjectMapper() // Real mapper

        databaseSeeder = spyk(
            DatabaseSeeder(
                regionRepository, typeRepository, eggGroupRepository, generationRepository,
                abilityRepository, speciesRepository, statsRepository, evolutionChainRepository,
                pokemonRepository, pokemonAbilityRepository, objectMapper
            ), recordPrivateCalls = true // Necessário para mockar o método privado loadJson
        )

        // Mockear o logger para evitar NullPointerException se ele for usado internamente por métodos não mockados
        val loggerMock = mockk<Logger>(relaxed = true)
        val seederClass = DatabaseSeeder::class.java
        val loggerField = seederClass.getDeclaredField("logger")
        loggerField.isAccessible = true
        loggerField.set(databaseSeeder, loggerMock)

        // Mock das chamadas a findAll() dos repositórios para retornar mapas vazios ou com mocks
        // para evitar NPEs quando o seeder tenta construir os mapas de lookup.
        every { regionRepository.findAll() } returns listOf(mockRegion)
        every { typeRepository.findAll() } returns listOf(mockType)
        every { eggGroupRepository.findAll() } returns listOf(mockEggGroup)
        every { generationRepository.findAll() } returns listOf(mockGeneration)
        every { abilityRepository.findAll() } returns listOf(mockAbility)
        every { speciesRepository.findAll() } returns listOf(mockSpecies)
        every { statsRepository.findAll() } returns listOf(mockStats)
        every { evolutionChainRepository.findAll() } returns listOf(mockEvolutionChain)
        // pokemonRepository.findAll() é usado para fraquezas, vamos mockar se necessário
        every { pokemonRepository.findAll() } returns emptyList()


        // Configurar mocks para as entidades relacionadas que são buscadas por ID
        every { mockStats.id } returns 1L
        every { mockGeneration.id } returns 1L
        every { mockSpecies.id } returns 1L
        every { mockEvolutionChain.id } returns 1L
        every { mockRegion.id } returns 1L
        every { mockAbility.id } returns 1L
        every { mockType.id } returns 1L
        every { mockEggGroup.id } returns 1L
    }

    private fun <T> mockLoadJson(jsonFile: JsonFile, data: List<T>) {
        every {
            databaseSeeder["loadJson"](jsonFile.filePath, any<TypeReference<List<T>>>())
        } returns data
    }

    private fun mockLoadJsonForTypeRef(filePath: String, typeRef: TypeReference<*>, data: Any) {
        every { databaseSeeder["loadJson"](filePath, typeRef) } returns data
    }


    @Test
    fun `importAll deve salvar PokemonEntity com dados de genero corretos`() {
        // Arrange
        // Mock loadJson para retornar listas vazias para todas as entidades, exceto Pokemons
        JsonFile.values().forEach { jsonFile ->
            if (jsonFile != JsonFile.POKEMONS && jsonFile != JsonFile.WEAKNESSES) { // Weaknesses é carregado depois
                 // Precisa ser mais específico com o TypeReference para o MockK funcionar corretamente com chamadas privadas
                when (jsonFile) {
                    JsonFile.REGIONS -> mockLoadJsonForTypeRef(jsonFile.filePath, object : TypeReference<List<RegionDto>>() {}, emptyList<RegionDto>())
                    JsonFile.TYPES -> mockLoadJsonForTypeRef(jsonFile.filePath, object : TypeReference<List<TypeDto>>() {}, emptyList<TypeDto>())
                    JsonFile.EGG_GROUPS -> mockLoadJsonForTypeRef(jsonFile.filePath, object : TypeReference<List<EggGroupDto>>() {}, emptyList<EggGroupDto>())
                    JsonFile.GENERATIONS -> mockLoadJsonForTypeRef(jsonFile.filePath, object : TypeReference<List<GenerationDto>>() {}, emptyList<GenerationDto>())
                    JsonFile.ABILITIES -> mockLoadJsonForTypeRef(jsonFile.filePath, object : TypeReference<List<AbilityDto>>() {}, emptyList<AbilityDto>())
                    JsonFile.SPECIES -> mockLoadJsonForTypeRef(jsonFile.filePath, object : TypeReference<List<SpeciesDto>>() {}, emptyList<SpeciesDto>())
                    JsonFile.STATS -> mockLoadJsonForTypeRef(jsonFile.filePath, object : TypeReference<List<StatsDto>>() {}, emptyList<StatsDto>())
                    JsonFile.EVOLUTION_CHAINS -> mockLoadJsonForTypeRef(jsonFile.filePath, object : TypeReference<List<EvolutionChainDto>>() {}, emptyList<EvolutionChainDto>())
                    else -> {} // POKEMONS e WEAKNESSES serão tratados separadamente
                }
            }
        }
         mockLoadJsonForTypeRef(JsonFile.WEAKNESSES.filePath, object : TypeReference<List<WeaknessDto>>() {}, emptyList<WeaknessDto>())


        val pokemonDtoComGenero = PokemonDto(
            id = 1, number = "001", name = "Bulbasaur", description = "Test",
            height = 0.7, weight = 6.9, statsId = 1L, generationId = 1L, speciesId = 1L, regionId = 1L, evolutionChainId = 1L,
            genderRateValue = 1, eggCycles = 20, eggGroupIds = listOf(1L), typeIds = listOf(1L),
            abilities = listOf(PokemonAbilityDto(abilityId = 1L, isHidden = false)),
            sprites = SpritesDto(null, null, null, null, null, null, null, null, null), // Simplificado
            gender = GenderRateSeedDTO(male = 87.5f, female = 12.5f)
        )
        val pokemonDtoSemGenero = PokemonDto(
            id = 81, number = "081", name = "Magnemite", description = "Test",
            height = 0.3, weight = 6.0, statsId = 1L, generationId = 1L, speciesId = 1L, regionId = 1L, evolutionChainId = 1L,
            genderRateValue = -1, eggCycles = 20, eggGroupIds = listOf(1L), typeIds = listOf(1L),
            abilities = listOf(PokemonAbilityDto(abilityId = 1L, isHidden = false)),
            sprites = SpritesDto(null, null, null, null, null, null, null, null, null), // Simplificado
            gender = null // Pokémon sem gênero
        )

        mockLoadJsonForTypeRef(JsonFile.POKEMONS.filePath, object: TypeReference<List<PokemonDto>>() {}, listOf(pokemonDtoComGenero, pokemonDtoSemGenero))

        // Act
        databaseSeeder.importAll()

        // Assert
        val pokemonCaptor = slot<PokemonEntity>()
        verify(exactly = 2) { pokemonRepository.save(capture(pokemonCaptor)) }

        val bulbasaurSalvo = pokemonCaptor.allValues.first { it.name == "Bulbasaur" }
        kotlin.test.assertEquals(87.5f, bulbasaurSalvo.genderMale)
        kotlin.test.assertEquals(12.5f, bulbasaurSalvo.genderFemale)

        val magnemiteSalvo = pokemonCaptor.allValues.first { it.name == "Magnemite" }
        kotlin.test.assertNull(magnemiteSalvo.genderMale)
        kotlin.test.assertNull(magnemiteSalvo.genderFemale)

        verify { pokemonAbilityRepository.save(any()) } // Verifica se as habilidades também foram processadas
    }
}
