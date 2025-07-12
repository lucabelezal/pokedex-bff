package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.application.dto.seeder.*
import com.pokedex.bff.application.valueobjects.*
import com.pokedex.bff.domain.entities.*
import com.pokedex.bff.domain.repositories.*
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PokemonImportStrategyTest {

    @MockK private lateinit var pokemonRepository: PokemonRepository
    @MockK private lateinit var regionRepository: RegionRepository
    @MockK private lateinit var statsRepository: StatsRepository
    @MockK private lateinit var generationRepository: GenerationRepository
    @MockK private lateinit var speciesRepository: SpeciesRepository
    @MockK private lateinit var eggGroupRepository: EggGroupRepository
    @MockK private lateinit var typeRepository: TypeRepository
    @MockK private lateinit var abilityRepository: AbilityRepository
    @MockK private lateinit var evolutionChainRepository: EvolutionChainRepository
    @MockK private lateinit var pokemonAbilityRepository: PokemonAbilityRepository
    @MockK private lateinit var jsonLoader: JsonLoader
    @MockK private lateinit var importResults: ImportResults

    @InjectMockKs
    private lateinit var pokemonImportStrategy: PokemonImportStrategy

    private val region1 = RegionEntity(1, "Kanto")
    private val stats1 = StatsEntity(1, 318, 45, 49, 49, 65, 65, 45)
    private val gen1 = GenerationEntity(1, "Gen I", region1)
    private val species1 = SpeciesEntity(1, "Bulbasaur", "Seed", "Semente")
    private val eggGroup1 = EggGroupEntity(1, "Monster")
    private val type1 = TypeEntity(1, "Grass", "green")
    private val type2 = TypeEntity(2, "Poison", "purple")
    private val ability1 = AbilityEntity(1, "Overgrow", "Desc", gen1)
    private val evolutionChain1 = EvolutionChainEntity(1, "{}")

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { importResults.add(any(), any()) } just Runs

        every { regionRepository.findAll() } returns listOf(region1)
        every { statsRepository.findAll() } returns listOf(stats1)
        every { generationRepository.findAll() } returns listOf(gen1)
        every { speciesRepository.findAll() } returns listOf(species1)
        every { eggGroupRepository.findAll() } returns listOf(eggGroup1)
        every { typeRepository.findAll() } returns listOf(type1, type2)
        every { abilityRepository.findAll() } returns listOf(ability1)
        every { evolutionChainRepository.findAll() } returns listOf(evolutionChain1)

        every { pokemonRepository.save(any()) } answers { firstArg<PokemonEntity>().apply { id = firstArg<PokemonEntity>().id ?: 0L } }
        every { pokemonAbilityRepository.save(any()) } answers { firstArg() }
    }

    @Test
    fun `getEntityName should return correct name`() {
        assertEquals("Pokémons", pokemonImportStrategy.getEntityName())
    }

    @Test
    fun `import should process pokemons successfully`() {
        val pokemonDtos = listOf(
            PokemonDto(
                id = 1, number = "001", name = "Bulbasaur", description = "Desc", height = 0.7, weight = 6.9,
                gender = GenderDto(87.5f, 12.5f), eggCycles = 20, genderRateValue = 1,
                statsId = 1, generationId = 1, speciesId = 1, evolutionChainId = 1, regionId = 1,
                eggGroupIds = listOf(1L), typeIds = listOf(1L, 2L),
                abilities = listOf(PokemonAbilityInDto(1, false)),
                sprites = SpritesDto("front", "back", null, null, null, null, null, null, OtherDto(DreamWorldDto("",""), HomeDto("","","",""), OfficialArtworkDto("","")))
            )
        )
        val expectedPokemonEntity = PokemonEntity(
            id = 1, number = "001", name = "Bulbasaur", description = "Desc", height = 0.7, weight = 6.9,
            genderMale = 87.5f, genderFemale = 12.5f, eggCycles = 20, genderRateValue = 1,
            stats = stats1, generation = gen1, species = species1, evolutionChain = evolutionChain1, region = region1,
            eggGroups = mutableSetOf(eggGroup1), types = mutableSetOf(type1, type2),
            sprites = SpritesVO("front", "back", null, null, null, null, null, null, OtherVO(DreamWorldVO("",""), HomeVO("","","",""), OfficialArtworkVO("","")))
        )
        every { jsonLoader.loadJson<List<PokemonDto>>(JsonFile.POKEMONS.filePath) } returns pokemonDtos

        val counts = pokemonImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 1, errors = 0), counts)
        verify(exactly = 1) { pokemonRepository.save(match { it.name == "Bulbasaur" }) }
        verify(exactly = 1) { pokemonAbilityRepository.save(match { it.ability.name == "Overgrow" && !it.isHidden }) }
        verify(exactly = 1) { importResults.add("Pokémons", counts) }
    }

    @Test
    fun `import should count error if a required relation is missing`() {
        val pokemonDtos = listOf(PokemonDto(id = 1, name = "MissingStat", statsId = 99,
            number = "999", description = "", height = 0.0, weight = 0.0,
            generationId = 1, speciesId = 1, evolutionChainId = 1, regionId = 1,
            sprites = SpritesDto("","","","","","","","", OtherDto(DreamWorldDto("",""), HomeDto("","","",""), OfficialArtworkDto("","")))))

        every { jsonLoader.loadJson<List<PokemonDto>>(JsonFile.POKEMONS.filePath) } returns pokemonDtos

        val counts = pokemonImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 0, errors = 1), counts)
        verify(exactly = 0) { pokemonRepository.save(any()) }
        verify(exactly = 0) { pokemonAbilityRepository.save(any()) }
    }

    @Test
    fun `import should count error if an ability for a pokemon is missing`() {
        val pokemonDtos = listOf(
            PokemonDto(
                id = 1, number = "001", name = "Bulbasaur", description = "Desc", height = 0.7, weight = 6.9,
                gender = GenderDto(87.5f, 12.5f), eggCycles = 20, genderRateValue = 1,
                statsId = 1, generationId = 1, speciesId = 1, evolutionChainId = 1, regionId = 1,
                eggGroupIds = listOf(1L), typeIds = listOf(1L, 2L),
                abilities = listOf(PokemonAbilityInDto(99, false)), // Missing ability ID
                sprites = SpritesDto("front", "back", null, null, null, null, null, null, OtherDto(DreamWorldDto("",""), HomeDto("","","",""), OfficialArtworkDto("","")))
            )
        )
        every { jsonLoader.loadJson<List<PokemonDto>>(JsonFile.POKEMONS.filePath) } returns pokemonDtos

        val counts = pokemonImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 0, errors = 1), counts)
        verify(exactly = 0) { pokemonRepository.save(any()) }
        verify(exactly = 0) { pokemonAbilityRepository.save(any()) }
    }

    @Test
    fun `import should handle general exception during pokemon save`() {
        val pokemonDtos = listOf(
             PokemonDto(
                id = 1, number = "001", name = "Bulbasaur", description = "Desc", height = 0.7, weight = 6.9,
                gender = GenderDto(87.5f, 12.5f), eggCycles = 20, genderRateValue = 1,
                statsId = 1, generationId = 1, speciesId = 1, evolutionChainId = 1, regionId = 1,
                eggGroupIds = listOf(1L), typeIds = listOf(1L, 2L),
                abilities = listOf(PokemonAbilityInDto(1, false)),
                 sprites = SpritesDto("front", "back", null, null, null, null, null, null, OtherDto(DreamWorldDto("",""), HomeDto("","","",""), OfficialArtworkDto("","")))
            )
        )
        every { jsonLoader.loadJson<List<PokemonDto>>(JsonFile.POKEMONS.filePath) } returns pokemonDtos
        every { pokemonRepository.save(any()) } throws RuntimeException("DB Error")

        val counts = pokemonImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 0, errors = 1), counts)
        verify(exactly = 1) { pokemonRepository.save(any()) }
        verify(exactly = 0) { pokemonAbilityRepository.save(any()) }
    }

    @Test
    fun `import should handle general exception during pokemon ability save`() {
        val pokemonDtos = listOf(
             PokemonDto(
                id = 1, number = "001", name = "Bulbasaur", description = "Desc", height = 0.7, weight = 6.9,
                 gender = GenderDto(87.5f, 12.5f), eggCycles = 20, genderRateValue = 1,
                statsId = 1, generationId = 1, speciesId = 1, evolutionChainId = 1, regionId = 1,
                eggGroupIds = listOf(1L), typeIds = listOf(1L, 2L),
                abilities = listOf(PokemonAbilityInDto(1, false)),
                 sprites = SpritesDto("front", "back", null, null, null, null, null, null, OtherDto(DreamWorldDto("",""), HomeDto("","","",""), OfficialArtworkDto("","")))
            )
        )
        val savedPokemon = PokemonEntity(id = 1, name="Bulbasaur", stats = stats1, generation = gen1, species = species1, evolutionChain = evolutionChain1, number="1", description = "", height=0.0, weight=0.0, eggCycles=0, region = region1, sprites = SpritesVO("","","","","","","","", OtherVO(DreamWorldVO("",""), HomeVO("","","",""), OfficialArtworkVO("",""))), genderRateValue = 1)

        every { jsonLoader.loadJson<List<PokemonDto>>(JsonFile.POKEMONS.filePath) } returns pokemonDtos
        every { pokemonRepository.save(any()) } returns savedPokemon
        every { pokemonAbilityRepository.save(any()) } throws RuntimeException("DB Error on ability save")


        val counts = pokemonImportStrategy.import(importResults)

        assertEquals(ImportCounts(success = 0, errors = 1), counts)
        verify(exactly = 1) { pokemonRepository.save(any()) }
        verify(exactly = 1) { pokemonAbilityRepository.save(any()) }
    }
}
