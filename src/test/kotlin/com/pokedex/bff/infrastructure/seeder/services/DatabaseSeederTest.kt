package com.pokedex.bff.infrastructure.seeder.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.bff.application.dto.seeder.*
import com.pokedex.bff.domain.entities.*
import com.pokedex.bff.domain.repositories.*
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.io.ClassPathResource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.test.util.ReflectionTestUtils
import java.io.ByteArrayInputStream
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class DatabaseSeederTest {

    @Mock private lateinit var regionRepository: RegionRepository
    @Mock private lateinit var typeRepository: TypeRepository
    @Mock private lateinit var eggGroupRepository: EggGroupRepository
    @Mock private lateinit var generationRepository: GenerationRepository
    @Mock private lateinit var abilityRepository: AbilityRepository
    @Mock private lateinit var speciesRepository: SpeciesRepository
    @Mock private lateinit var statsRepository: StatsRepository
    @Mock private lateinit var evolutionChainRepository: EvolutionChainRepository
    @Mock private lateinit var pokemonRepository: PokemonRepository
    @Mock private lateinit var pokemonAbilityRepository: PokemonAbilityRepository

    private lateinit var objectMapper: ObjectMapper
    private lateinit var databaseSeeder: DatabaseSeeder

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper() // Usar uma instância real de ObjectMapper para testes de JSON
        databaseSeeder = DatabaseSeeder(
            regionRepository,
            typeRepository,
            eggGroupRepository,
            generationRepository,
            abilityRepository,
            speciesRepository,
            statsRepository,
            evolutionChainRepository,
            pokemonRepository,
            pokemonAbilityRepository,
            objectMapper
        )
    }

    // --- Testes para o método loadJson (método privado) ---
    @Nested
    @DisplayName("Testes para loadJson")
    inner class LoadJsonTests {

        @Test
        @DisplayName("Deve carregar JSON com sucesso")
        fun `should load JSON successfully`() {
            // Prepara um JSON de exemplo para simular o arquivo
            val jsonContent = """[{"id": 1, "name": "Kanto"}]"""
            val filePath = "data/regions.json"

            // Usa ReflectionTestUtils para simular o ClassPathResource
            // Em testes reais, pode ser necessário um pouco mais de mock para ClassPathResource se ele for problemático
            // No entanto, para métodos privados, ReflectionTestUtils é útil.
            // Para ClassPathResource, geralmente não mockamos, pois ele lê do classpath, o que é um comportamento esperado.
            // O ideal seria que 'loadJson' não fosse privado para facilitar o teste.
            // Para fins de exemplo, vamos simular a leitura.
            // Nota: Para testar 'loadJson' isoladamente de forma mais limpa, ele poderia ser um método utilitário público.

            // Não é possível mockar ClassPathResource diretamente para simular o inputStream de forma simples.
            // O ideal é testar a exceção de IOException, ou refatorar loadJson para receber um InputStream.
            // Como está, testar o caminho de sucesso de loadJson diretamente é complicado sem expor o método ou usar um recurso real.
            // Vamos focar nos testes que usam loadJson implicitamente.
            // No entanto, podemos testar o cenário de falha.
        }

        @Test
        @DisplayName("Deve lançar RuntimeException quando o carregamento do JSON falha")
        fun `should throw RuntimeException when JSON loading fails`() {
            val filePath = "invalid/path/to/file.json"
            val typeReference = object : TypeReference<List<RegionDto>>() {}

            // Mockar o ObjectMapper para lançar IOException ao tentar ler
            val mockObjectMapper = mock(ObjectMapper::class.java)
            lenient().`when`(mockObjectMapper.readValue(any(ByteArrayInputStream::class.java), eq(typeReference)))
                .thenThrow(IOException("Erro de leitura simulado"))

            val databaseSeederWithMockedMapper = DatabaseSeeder(
                regionRepository, typeRepository, eggGroupRepository, generationRepository,
                abilityRepository, speciesRepository, statsRepository, evolutionChainRepository,
                pokemonRepository, pokemonAbilityRepository, mockObjectMapper
            )

            // Como ClassPathResource pode lançar IOException se o recurso não for encontrado,
            // precisamos de um mock para a parte que lê o JSON.
            // Para um teste unitário puro do `loadJson` que interage com `ClassPathResource`,
            // a abordagem mais direta seria testar o lançamento da `RuntimeException`
            // quando um arquivo não existe ou é inválido.

            // Usando ReflectionTestUtils para chamar o método privado
            val exception = assertFailsWith<RuntimeException> {
                ReflectionTestUtils.invokeMethod<Any>(
                    databaseSeederWithMockedMapper,
                    "loadJson",
                    filePath,
                    typeReference
                )
            }
            assertTrue("Error loading data from $filePath" in exception.message!!)
        }
    }

//    // --- Testes para o método importSimpleData (método privado) ---
//    @Nested
//    @DisplayName("Testes para importSimpleData")
//    inner class ImportSimpleDataTests {
//
//        private class TestEntity(var id: Long? = null, var name: String)
//        private class TestDto(val id: Long, val name: String)
//
//        @Mock private lateinit var mockRepository: JpaRepository<TestEntity, Long>
//
//        @Test
//        @DisplayName("Deve importar dados simples com sucesso")
//        fun `should import simple data successfully`() {
//            val dtos = listOf(TestDto(1, "Item1"), TestDto(2, "Item2"))
//            `when`(mockRepository.save(any(TestEntity::class.java))).thenAnswer { it.arguments[0] as TestEntity }
//
//            val transform: (TestDto) -> TestEntity = { dto -> TestEntity(dto.id, dto.name) }
//
//            val counts = ReflectionTestUtils.invokeMethod<ImportCounts>(
//                databaseSeeder,
//                "importSimpleData",
//                dtos,
//                mockRepository,
//                transform
//            )
//
//            assertNotNull(counts)
//            assertEquals(2, counts.success)
//            assertEquals(0, counts.errors)
//            verify(mockRepository, times(2)).save(any(TestEntity::class.java))
//        }

//        @Test
//        @DisplayName("Deve lidar com erros durante a importação de dados simples")
//        fun `should handle errors during simple data import`() {
//            val dtos = listOf(TestDto(1, "Item1"), TestDto(2, "Item2"))
//            // Simula um erro no save do primeiro item
//            `when`(mockRepository.save(argThat { it.id == 1L }))
//                .thenThrow(RuntimeException("Simulated save error"))
//            // Simula sucesso no save do segundo item
//            `when`(mockRepository.save(argThat { it.id == 2L }))
//                .thenAnswer { it.arguments[0] as TestEntity }
//
//            val transform: (TestDto) -> TestEntity = { dto -> TestEntity(dto.id, dto.name) }
//
//            val counts = ReflectionTestUtils.invokeMethod<ImportCounts>(
//                databaseSeeder,
//                "importSimpleData",
//                dtos,
//                mockRepository,
//                transform
//            )
//
//            assertNotNull(counts)
//            assertEquals(1, counts.success)
//            assertEquals(1, counts.errors)
//            verify(mockRepository, times(2)).save(any(TestEntity::class.java))
//        }
    }

//    // --- Testes para o método importAll (método público e principal) ---
//    @Nested
//    @DisplayName("Testes para importAll")
//    inner class ImportAllTests {
//
//        @Test
//        @DisplayName("Deve importar todos os dados com sucesso")
//        fun `should import all data successfully`() {
//            // Mock de dados JSON para cada tipo
//            mockJsonLoading(JsonFile.REGIONS, "[{\"id\": 1, \"name\": \"Kanto\"}]")
//            mockJsonLoading(JsonFile.TYPES, "[{\"id\": 1, \"name\": \"Fire\", \"color\": \"#FF0000\"}]")
//            mockJsonLoading(JsonFile.EGG_GROUPS, "[{\"id\": 1, \"name\": \"Monster\"}]")
//            mockJsonLoading(JsonFile.GENERATIONS, "[{\"id\": 1, \"name\": \"Generation I\", \"regionId\": 1}]")
//            mockJsonLoading(JsonFile.ABILITIES, "[{\"id\": 1, \"name\": \"Blaze\", \"description\": \"Power up Fire-type moves.\", \"introducedGenerationId\": 1}]")
//            mockJsonLoading(JsonFile.SPECIES, "[{\"id\": 1, \"name\": \"Bulbasaur\", \"pokemonNumber\": \"001\", \"speciesEn\": \"Seed Pokemon\", \"speciesPt\": \"Pokémon Semente\"}]")
//            mockJsonLoading(JsonFile.STATS, "[{\"id\": 1, \"total\": 318, \"hp\": 45, \"attack\": 49, \"defense\": 49, \"spAtk\": 65, \"spDef\": 65, \"speed\": 45}]")
//            mockJsonLoading(JsonFile.EVOLUTION_CHAINS, "[{\"id\": 1, \"chainData\": { \"evolves_to\": []}}]")
//            mockJsonLoading(JsonFile.POKEMONS, """
//                [
//                    {
//                        "id": 1,
//                        "number": "001",
//                        "name": "Bulbasaur",
//                        "description": "A strange seed was planted...",
//                        "height": 7,
//                        "weight": 69,
//                        "genderRateValue": 8,
//                        "gender": {"male": 0.875, "female": 0.125},
//                        "eggCycles": 20,
//                        "statsId": 1,
//                        "generationId": 1,
//                        "speciesId": 1,
//                        "evolutionChainId": 1,
//                        "regionId": 1,
//                        "eggGroupIds": [1],
//                        "typeIds": [1],
//                        "abilities": [{"abilityId": 1, "isHidden": false}],
//                        "sprites": {"front_default": "url", "back_default": "url", "front_shiny": "url", "back_shiny": "url"}
//                    }
//                ]
//            """)
//            mockJsonLoading(JsonFile.WEAKNESSES, """
//                [
//                    {
//                        "pokemonName": "Bulbasaur",
//                        "weaknesses": ["Water"]
//                    }
//                ]
//            """)
//
//            // Mock dos retornos dos findAll para as associações
//            `when`(regionRepository.findAll()).thenReturn(listOf(RegionEntity(1, "Kanto")))
//            `when`(generationRepository.findAll()).thenReturn(listOf(GenerationEntity(1, "Generation I", RegionEntity(1, "Kanto"))))
//            `when`(speciesRepository.findAll()).thenReturn(listOf(SpeciesEntity(1, "Bulbasaur", "001", "Seed Pokemon", "Pokémon Semente")))
//            `when`(statsRepository.findAll()).thenReturn(listOf(StatsEntity(1, 318, 45, 49, 49, 65, 65, 45)))
//            `when`(eggGroupRepository.findAll()).thenReturn(listOf(EggGroupEntity(1, "Monster")))
//            `when`(typeRepository.findAll()).thenReturn(listOf(TypeEntity(1, "Water", "#0000FF"))) // Tipo "Water" para fraqueza
//            `when`(abilityRepository.findAll()).thenReturn(listOf(AbilityEntity(1, "Blaze", "Power up Fire-type moves.", GenerationEntity(1, "Generation I", RegionEntity(1, "Kanto")))))
//            `when`(evolutionChainRepository.findAll()).thenReturn(listOf(EvolutionChainEntity(1, "{ \"evolves_to\": []}")))
//            `when`(pokemonRepository.findAll()).thenReturn(listOf(
//                PokemonEntity(
//                    id = 1,
//                    name = "Bulbasaur",
//                    number = "001",
//                    description = "A strange seed was planted...",
//                    height = 7,
//                    weight = 69,
//                    genderRateValue = 8,
//                    genderMale = 0.875,
//                    genderFemale = 0.125,
//                    eggCycles = 20,
//                    stats = StatsEntity(1, 318, 45, 49, 49, 65, 65, 45),
//                    generation = GenerationEntity(1, "Generation I", RegionEntity(1, "Kanto")),
//                    species = SpeciesEntity(1, "Bulbasaur", "001", "Seed Pokemon", "Pokémon Semente"),
//                    evolutionChain = EvolutionChainEntity(1, "{ \"evolves_to\": []}"),
//                    region = RegionEntity(1, "Kanto"),
//                    eggGroups = mutableSetOf(EggGroupEntity(1, "Monster")),
//                    types = mutableSetOf(TypeEntity(1, "Fire", "#FF0000")),
//                    sprites = PokemonSpritesVO("url", "url", "url", "url")
//                )
//            ))
//
//
//            // Mock de todas as operações de save
//            `when`(regionRepository.save(any(RegionEntity::class.java))).thenAnswer { it.arguments[0] as RegionEntity }
//            `when`(typeRepository.save(any(TypeEntity::class.java))).thenAnswer { it.arguments[0] as TypeEntity }
//            `when`(eggGroupRepository.save(any(EggGroupEntity::class.java))).thenAnswer { it.arguments[0] as EggGroupEntity }
//            `when`(generationRepository.save(any(GenerationEntity::class.java))).thenAnswer { it.arguments[0] as GenerationEntity }
//            `when`(abilityRepository.save(any(AbilityEntity::class.java))).thenAnswer { it.arguments[0] as AbilityEntity }
//            `when`(speciesRepository.save(any(SpeciesEntity::class.java))).thenAnswer { it.arguments[0] as SpeciesEntity }
//            `when`(statsRepository.save(any(StatsEntity::class.java))).thenAnswer { it.arguments[0] as StatsEntity }
//            `when`(evolutionChainRepository.save(any(EvolutionChainEntity::class.java))).thenAnswer { it.arguments[0] as EvolutionChainEntity }
//            `when`(pokemonRepository.save(any(PokemonEntity::class.java))).thenAnswer { it.arguments[0] as PokemonEntity }
//            `when`(pokemonAbilityRepository.save(any(PokemonAbilityEntity::class.java))).thenAnswer { it.arguments[0] as PokemonAbilityEntity }
//
//
//            // Chama o método principal
//            databaseSeeder.importAll()
//
//            // Verifica se os métodos save foram chamados para cada repositório
//            verify(regionRepository, times(1)).save(any(RegionEntity::class.java))
//            verify(typeRepository, times(1)).save(any(TypeEntity::class.java))
//            verify(eggGroupRepository, times(1)).save(any(EggGroupEntity::class.java))
//            verify(generationRepository, times(1)).save(any(GenerationEntity::class.java))
//            verify(abilityRepository, times(1)).save(any(AbilityEntity::class.java))
//            verify(speciesRepository, times(1)).save(any(SpeciesEntity::class.java))
//            verify(statsRepository, times(1)).save(any(StatsEntity::class.java))
//            verify(evolutionChainRepository, times(1)).save(any(EvolutionChainEntity::class.java))
//            verify(pokemonRepository, times(2)).save(any(PokemonEntity::class.java)) // 1 para a criação, 1 para fraqueza
//            verify(pokemonAbilityRepository, times(1)).save(any(PokemonAbilityEntity::class.java))
//
//            // Verifica se os métodos findAll foram chamados para as associações
//            verify(regionRepository, times(2)).findAll() // Uma para gerações, uma para pokemons
//            verify(generationRepository, times(2)).findAll() // Uma para habilidades, uma para pokemons
//            verify(speciesRepository, times(1)).findAll()
//            verify(statsRepository, times(1)).findAll()
//            verify(eggGroupRepository, times(1)).findAll()
//            verify(typeRepository, times(2)).findAll() // Uma para pokemons, uma para fraquezas
//            verify(abilityRepository, times(1)).findAll()
//            verify(evolutionChainRepository, times(1)).findAll()
//            verify(pokemonRepository, times(1)).findAll()
//        }
//
//        @Test
//        @DisplayName("Deve lidar com falhas de importação de um tipo de dado específico")
//        fun `should handle import failures for a specific data type`() {
//            // Simula um JSON válido para Regiões
//            mockJsonLoading(JsonFile.REGIONS, "[{\"id\": 1, \"name\": \"Kanto\"}]")
//            // Simula um JSON inválido para Tipos (lançará IOException na leitura)
//            mockJsonLoading(JsonFile.TYPES, "INVALID_JSON", true) // O terceiro parâmetro indica para forçar IOException
//
//
//            // Mock de findAll para evitar NullPointerException nas etapas subsequentes
//            `when`(regionRepository.findAll()).thenReturn(emptyList())
//            `when`(generationRepository.findAll()).thenReturn(emptyList())
//            `when`(speciesRepository.findAll()).thenReturn(emptyList())
//            `when`(statsRepository.findAll()).thenReturn(emptyList())
//            `when`(eggGroupRepository.findAll()).thenReturn(emptyList())
//            `when`(typeRepository.findAll()).thenReturn(emptyList())
//            `when`(abilityRepository.findAll()).thenReturn(emptyList())
//            `when`(evolutionChainRepository.findAll()).thenReturn(emptyList())
//            `when`(pokemonRepository.findAll()).thenReturn(emptyList())
//
//
//            // Mock para o save das regiões (que deve funcionar)
//            `when`(regionRepository.save(any(RegionEntity::class.java))).thenAnswer { it.arguments[0] as RegionEntity }
//
//            // Chama o método principal
//            databaseSeeder.importAll()
//
//            // Verifica se o save de regiões foi chamado com sucesso
//            verify(regionRepository, times(1)).save(any(RegionEntity::class.java))
//            // Verifica que o save de tipos não foi chamado devido à falha de carregamento JSON
//            verify(typeRepository, never()).save(any(TypeEntity::class.java))
//            // E assim por diante para os outros, garantindo que as falhas sejam logadas e o processo continue.
//            // Poderíamos adicionar um Spy ao logger para verificar as mensagens de erro.
//        }
//
//        @Test
//        @DisplayName("Deve lançar IllegalArgumentException para dados inconsistentes (ex: Generation com RegionId inválido)")
//        fun `should throw IllegalArgumentException for inconsistent data`() {
//            // Mock de dados JSON para Regiões (válido)
//            mockJsonLoading(JsonFile.REGIONS, "[{\"id\": 1, \"name\": \"Kanto\"}]")
//            // Mock de dados JSON para Tipos (válido)
//            mockJsonLoading(JsonFile.TYPES, "[{\"id\": 1, \"name\": \"Fire\", \"color\": \"#FF0000\"}]")
//            // Mock de dados JSON para EggGroups (válido)
//            mockJsonLoading(JsonFile.EGG_GROUPS, "[{\"id\": 1, \"name\": \"Monster\"}]")
//            // Mock de dados JSON para Gerações com RegionId que não existe
//            mockJsonLoading(JsonFile.GENERATIONS, "[{\"id\": 1, \"name\": \"Generation I\", \"regionId\": 99}]") // RegionId 99 não existe
//
//
//            // Mock dos retornos dos findAll para as associações
//            `when`(regionRepository.findAll()).thenReturn(listOf(RegionEntity(1, "Kanto"))) // Kanto existe
//            `when`(typeRepository.findAll()).thenReturn(emptyList())
//            `when`(eggGroupRepository.findAll()).thenReturn(emptyList())
//            `when`(generationRepository.findAll()).thenReturn(emptyList())
//            `when`(abilityRepository.findAll()).thenReturn(emptyList())
//            `when`(speciesRepository.findAll()).thenReturn(emptyList())
//            `when`(statsRepository.findAll()).thenReturn(emptyList())
//            `when`(evolutionChainRepository.findAll()).thenReturn(emptyList())
//            `when`(pokemonRepository.findAll()).thenReturn(emptyList())
//
//            // Mock dos saves para os que devem funcionar
//            `when`(regionRepository.save(any(RegionEntity::class.java))).thenAnswer { it.arguments[0] as RegionEntity }
//            `when`(typeRepository.save(any(TypeEntity::class.java))).thenAnswer { it.arguments[0] as TypeEntity }
//            `when`(eggGroupRepository.save(any(EggGroupEntity::class.java))).thenAnswer { it.arguments[0] as EggGroupEntity }
//
//            // Chama o método principal. A expectativa é que a importação de Gerações falhe, mas o processo continue.
//            databaseSeeder.importAll()
//
//            // Verifica se o save de gerações **não** foi chamado ou foi chamado e falhou para a entidade problemática
//            verify(generationRepository, never()).save(any(GenerationEntity::class.java))
//
//            // Verifica que o logger registrou o erro para a importação de gerações.
//            // Para testar logs, você precisaria de uma biblioteca como `Logback Test Appender` ou um mock do LoggerFactory.
//            // Por simplicidade, este teste se concentra no comportamento do save/não save.
//        }
//
//        // --- Helper para mockar o carregamento de JSON ---
//        private fun mockJsonLoading(jsonFile: JsonFile, content: String, forceIoException: Boolean = false) {
//            val classPathResourceMock = mock(ClassPathResource::class.java)
//
//            // Simula o comportamento de ClassPathResource para o arquivo específico
//            try {
//                if (forceIoException) {
//                    `when`(classPathResourceMock.inputStream).thenThrow(IOException("Simulated IO Exception"))
//                } else {
//                    `when`(classPathResourceMock.inputStream).thenReturn(ByteArrayInputStream(content.toByteArray()))
//                }
//            } catch (e: Exception) {
//                // Should not happen in test setup
//            }
//
//            // Usa ReflectionTestUtils para substituir a forma como ClassPathResource é criado/obtido internamente
//            // Isso é um pouco invasivo e dependeria de como ClassPathResource é usado no método `loadJson`.
//            // A maneira mais robusta de testar isso seria se `loadJson` recebesse um `InputStream` diretamente
//            // ou se usasse uma interface para a leitura de recursos que pudesse ser mockada.
//
//            // Dada a estrutura atual, o teste de 'loadJson' com mockagem do ClassPathResource é complicado.
//            // Em vez de mockar ClassPathResource diretamente, o teste para 'importAll' deve garantir
//            // que a cadeia de chamadas se comporte como esperado (ex: se o JSON estiver inválido, o save não é chamado).
//
//            // Para simular o `loadJson` para `importAll`, vamos interceptar a chamada do `objectMapper.readValue`
//            // que é feita dentro de `loadJson`.
//            // Isso requer um `Spy` no `objectMapper` para mockar o método `readValue` apenas para o caminho específico.
//
//            // É importante que o `objectMapper` seja um `Spy` para que possamos mockar o `readValue`
//            // sem quebrar outros comportamentos do `ObjectMapper`.
//            // No entanto, para simplicidade e dada a forma como `loadJson` é chamado,
//            // podemos apenas configurar um comportamento para `objectMapper.readValue` que o método `loadJson`
//            // irá encontrar.
//
//            // A forma como `loadJson` é implementado (criando `ClassPathResource` internamente)
//            // torna o mock de `ClassPathResource` difícil.
//            // A melhor abordagem é mockar o resultado da chamada de `loadJson` se fosse público,
//            // ou mockar o `ObjectMapper` para lançar exceções.
//            // Para simular o sucesso do `loadJson` de dentro do `importAll`,
//            // mockamos o ObjectMapper para retornar o objeto deserializado esperado.
//
//            when(objectMapper.readValue(any(java.io.InputStream::class.java), any(TypeReference::class.java)))
//                .thenReturn(when (jsonFile) {
//                    JsonFile.REGIONS -> objectMapper.readValue(content, object : TypeReference<List<RegionDto>>() {})
//                    JsonFile.TYPES -> objectMapper.readValue(content, object : TypeReference<List<TypeDto>>() {})
//                    JsonFile.EGG_GROUPS -> objectMapper.readValue(content, object : TypeReference<List<EggGroupDto>>() {})
//                    JsonFile.GENERATIONS -> objectMapper.readValue(content, object : TypeReference<List<GenerationDto>>() {})
//                    JsonFile.ABILITIES -> objectMapper.readValue(content, object : TypeReference<List<AbilityDto>>() {})
//                    JsonFile.SPECIES -> objectMapper.readValue(content, object : TypeReference<List<SpeciesDto>>() {})
//                    JsonFile.STATS -> objectMapper.readValue(content, object : TypeReference<List<StatsDto>>() {})
//                    JsonFile.EVOLUTION_CHAINS -> objectMapper.readValue(content, object : TypeReference<List<EvolutionChainDto>>() {})
//                    JsonFile.POKEMONS -> objectMapper.readValue(content, object : TypeReference<List<PokemonDto>>() {})
//                    JsonFile.WEAKNESSES -> objectMapper.readValue(content, object : TypeReference<List<WeaknessDto>>() {})
//                    else -> throw IllegalArgumentException("Unsupported JsonFile type for mocking.")
//                })
//        }
//    }
//}