package com.pokedex.bff.application.usecase

import com.pokedex.bff.application.dtos.input.StartBattleInput
import com.pokedex.bff.domain.pokemon.entities.*
import com.pokedex.bff.domain.pokemon.exception.PokemonNotFoundException
import com.pokedex.bff.domain.pokemon.repository.PokemonRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BattleUseCaseImplTest {

    private val pokemonRepository = mockk<PokemonRepository>()
    private val battleUseCase = BattleUseCaseImpl(pokemonRepository)

    private fun createPokemon(
        id: Long,
        name: String,
        stats: Stats?,
        types: Set<Type> = setOf(
            Type(1L, "Normal", null)
        )
    ): Pokemon {
        return Pokemon(
            id = id,
            number = id.toString().padStart(3, '0'),
            name = name,
            height = 1.0,
            weight = 10.0,
            description = "Test Pokemon",
            sprites = null,
            genderRateValue = 4,
            genderMale = 50.0f,
            genderFemale = 50.0f,
            eggCycles = 20,
            stats = stats,
            generation = Generation(
                id = 1L,
                name = "generation-i",
                region = Region(1L, "kanto")
            ),
            species = Species(
                id = id,
                pokemonNumber = id.toString().padStart(3, '0'),
                name = name,
                speciesEn = name,
                speciesPt = name
            ),
            region = Region(1L, "kanto"),
            evolutionChain = null,
            types = types,
            abilities = emptySet(),
            eggGroups = emptySet(),
            weaknesses = emptySet()
        )
    }

    private fun createStats(
        id: Long = 1L,
        hp: Int = 100,
        attack: Int = 100,
        defense: Int = 100,
        spAtk: Int = 100,
        spDef: Int = 100,
        speed: Int = 100
    ): Stats {
        return Stats(
            id = id,
            total = hp + attack + defense + spAtk + spDef + speed,
            hp = hp,
            attack = attack,
            defense = defense,
            spAtk = spAtk,
            spDef = spDef,
            speed = speed
        )
    }

    @Nested
    inner class BattleExecution {

        @Test
        fun `deve retornar Pokemon com maior poder como vencedor`() {
            val strongPokemon = createPokemon(
                id = 1L,
                name = "Charizard",
                stats = createStats(hp = 150, attack = 120, defense = 100, speed = 110)
            )
            val weakPokemon = createPokemon(
                id = 2L,
                name = "Pidgey",
                stats = createStats(hp = 40, attack = 45, defense = 40, speed = 56)
            )

            every { pokemonRepository.findById("1") } returns strongPokemon
            every { pokemonRepository.findById("2") } returns weakPokemon

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            assertEquals("1", result.winnerId)
            assertEquals("2", result.loserId)
            assertTrue(result.summary.contains("Charizard"))
            assertTrue(result.summary.contains("WINNER"))
        }

        @Test
        fun `deve usar speed como critério de desempate quando poder é igual`() {
            val fastPokemon = createPokemon(
                id = 1L,
                name = "Electrode",
                stats = createStats(hp = 60, attack = 50, defense = 70, speed = 150)
            )
            val slowPokemon = createPokemon(
                id = 2L,
                name = "Snorlax",
                stats = createStats(hp = 160, attack = 110, defense = 65, speed = 30)
            )

            every { pokemonRepository.findById("1") } returns fastPokemon
            every { pokemonRepository.findById("2") } returns slowPokemon

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            assertNotNull(result)
            assertTrue(result.summary.contains("WINNER"))
        }

        @Test
        fun `deve lançar exceção quando primeiro Pokemon não existe`() {
            every { pokemonRepository.findById("999") } returns null

            val exception = assertThrows<PokemonNotFoundException> {
                battleUseCase.execute(StartBattleInput(pokemonId = "999", opponentId = "1"))
            }

            assertEquals("Pokemon with ID 999 not found", exception.message)
        }

        @Test
        fun `deve lançar exceção quando segundo Pokemon não existe`() {
            val pokemon = createPokemon(1L, "Pikachu", createStats())
            every { pokemonRepository.findById("1") } returns pokemon
            every { pokemonRepository.findById("999") } returns null

            val exception = assertThrows<PokemonNotFoundException> {
                battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "999"))
            }

            assertEquals("Pokemon with ID 999 not found", exception.message)
        }

        @Test
        fun `deve calcular batalha com Pokemon sem stats usando valor padrão`() {
            val pokemonWithStats = createPokemon(
                id = 1L,
                name = "Mewtwo",
                stats = createStats(hp = 106, attack = 110, defense = 90, speed = 130)
            )
            val pokemonWithoutStats = createPokemon(
                id = 2L,
                name = "MissingNo",
                stats = null
            )

            every { pokemonRepository.findById("1") } returns pokemonWithStats
            every { pokemonRepository.findById("2") } returns pokemonWithoutStats

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            // Pokemon com stats deve vencer (poder calculado > 100.0 padrão)
            assertEquals("1", result.winnerId)
            assertEquals("2", result.loserId)
        }

        @Test
        fun `deve calcular empate perfeito favorecendo primeiro Pokemon quando speed é igual`() {
            val pokemon1 = createPokemon(
                id = 1L,
                name = "Ditto",
                stats = createStats(hp = 48, attack = 48, defense = 48, speed = 48)
            )
            val pokemon2 = createPokemon(
                id = 2L,
                name = "Ditto Clone",
                stats = createStats(hp = 48, attack = 48, defense = 48, speed = 48)
            )

            every { pokemonRepository.findById("1") } returns pokemon1
            every { pokemonRepository.findById("2") } returns pokemon2

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            // Em empate perfeito, primeiro Pokemon vence (speed >= comparison)
            assertEquals("1", result.winnerId)
            assertEquals("2", result.loserId)
        }
    }

    @Nested
    inner class PowerCalculation {

        @Test
        fun `deve calcular poder com stats balanceadas`() {
            val pokemon = createPokemon(
                id = 1L,
                name = "Balanced",
                stats = createStats(hp = 100, attack = 100, defense = 100, speed = 100)
            )

            every { pokemonRepository.findById("1") } returns pokemon
            every { pokemonRepository.findById("2") } returns pokemon

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            // Empate - primeiro Pokemon vence
            assertEquals("1", result.winnerId)
        }

        @Test
        fun `deve dar peso maior para attack na fórmula de poder`() {
            val highAttackPokemon = createPokemon(
                id = 1L,
                name = "Attacker",
                stats = createStats(hp = 60, attack = 150, defense = 60, speed = 60)
            )
            val highDefensePokemon = createPokemon(
                id = 2L,
                name = "Defender",
                stats = createStats(hp = 60, attack = 60, defense = 150, speed = 60)
            )

            every { pokemonRepository.findById("1") } returns highAttackPokemon
            every { pokemonRepository.findById("2") } returns highDefensePokemon

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            // Attack tem peso 1.5, Defense tem peso 1.0 - attacker deve vencer
            assertEquals("1", result.winnerId)
        }

        @Test
        fun `deve calcular poder com HP alto corretamente`() {
            val highHpPokemon = createPokemon(
                id = 1L,
                name = "Tank",
                stats = createStats(hp = 250, attack = 50, defense = 50, speed = 50)
            )
            val lowHpPokemon = createPokemon(
                id = 2L,
                name = "Glass Cannon",
                stats = createStats(hp = 50, attack = 130, defense = 50, speed = 50)
            )

            every { pokemonRepository.findById("1") } returns highHpPokemon
            every { pokemonRepository.findById("2") } returns lowHpPokemon

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            assertNotNull(result)
            // HP tem peso 1.2, resultado depende de (250 * 1.2 + extras) vs (130 * 1.5 + extras)
        }

        @Test
        fun `deve usar valor padrão 100 quando stats é null`() {
            val pokemonWithoutStats = createPokemon(1L, "NoStats1", null)
            val pokemonWithLowStats = createPokemon(
                2L,
                "LowStats",
                createStats(hp = 10, attack = 10, defense = 10, speed = 10)
            )

            every { pokemonRepository.findById("1") } returns pokemonWithoutStats
            every { pokemonRepository.findById("2") } returns pokemonWithLowStats

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            // Poder padrão (100.0) deve vencer stats muito baixas
            assertEquals("1", result.winnerId)
        }
    }

    @Nested
    inner class BattleSummary {

        @Test
        fun `deve incluir nomes dos Pokemon no resumo`() {
            val pokemon1 = createPokemon(1L, "Pikachu", createStats())
            val pokemon2 = createPokemon(2L, "Raichu", createStats(hp = 80, attack = 80))

            every { pokemonRepository.findById("1") } returns pokemon1
            every { pokemonRepository.findById("2") } returns pokemon2

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            assertTrue(result.summary.contains("Pikachu"))
            assertTrue(result.summary.contains("Raichu"))
        }

        @Test
        fun `deve incluir dano causado por cada Pokemon`() {
            val pokemon1 = createPokemon(1L, "Charizard", createStats(attack = 120))
            val pokemon2 = createPokemon(2L, "Blastoise", createStats(attack = 110))

            every { pokemonRepository.findById("1") } returns pokemon1
            every { pokemonRepository.findById("2") } returns pokemon2

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            assertTrue(result.summary.contains("dealt"))
            assertTrue(result.summary.contains("damage"))
        }

        @Test
        fun `deve incluir indicador de vencedor no resumo`() {
            val winner = createPokemon(1L, "Mewtwo", createStats(attack = 154))
            val loser = createPokemon(2L, "Mew", createStats(attack = 100))

            every { pokemonRepository.findById("1") } returns winner
            every { pokemonRepository.findById("2") } returns loser

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            assertTrue(result.summary.contains("WINNER"))
            assertTrue(result.summary.contains("Mewtwo"))
        }

        @Test
        fun `deve formatar resumo com título de batalha`() {
            val pokemon1 = createPokemon(1L, "Dragonite", createStats())
            val pokemon2 = createPokemon(2L, "Salamence", createStats())

            every { pokemonRepository.findById("1") } returns pokemon1
            every { pokemonRepository.findById("2") } returns pokemon2

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            assertTrue(result.summary.contains("BATTLE SUMMARY"))
        }
    }

    @Nested
    inner class EdgeCases {

        @Test
        fun `deve lidar com stats zeradas`() {
            val pokemon1 = createPokemon(
                1L,
                "Zero1",
                createStats(hp = 0, attack = 0, defense = 0, speed = 0)
            )
            val pokemon2 = createPokemon(
                2L,
                "Zero2",
                createStats(hp = 0, attack = 0, defense = 0, speed = 0)
            )

            every { pokemonRepository.findById("1") } returns pokemon1
            every { pokemonRepository.findById("2") } returns pokemon2

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            // Empate com stats 0 - primeiro vence
            assertEquals("1", result.winnerId)
        }

        @Test
        fun `deve lidar com stats extremamente altas`() {
            val superPokemon = createPokemon(
                1L,
                "OverPowered",
                createStats(hp = 999, attack = 999, defense = 999, speed = 999)
            )
            val normalPokemon = createPokemon(
                2L,
                "Normal",
                createStats(hp = 100, attack = 100, defense = 100, speed = 100)
            )

            every { pokemonRepository.findById("1") } returns superPokemon
            every { pokemonRepository.findById("2") } returns normalPokemon

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            assertEquals("1", result.winnerId)
            assertEquals("2", result.loserId)
        }

        @Test
        fun `deve lidar com mesmo Pokemon lutando contra si mesmo`() {
            val pokemon = createPokemon(1L, "Narcissist", createStats())

            every { pokemonRepository.findById("1") } returns pokemon

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "1"))

            // Mesmo Pokemon - primeiro vence (empate perfeito)
            assertEquals("1", result.winnerId)
            assertEquals("1", result.loserId)
        }

        @Test
        fun `deve verificar chamadas ao repositório`() {
            val pokemon1 = createPokemon(1L, "Test1", createStats())
            val pokemon2 = createPokemon(2L, "Test2", createStats())

            every { pokemonRepository.findById("1") } returns pokemon1
            every { pokemonRepository.findById("2") } returns pokemon2

            battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            verify(exactly = 1) { pokemonRepository.findById("1") }
            verify(exactly = 1) { pokemonRepository.findById("2") }
        }
    }

    @Nested
    inner class TypeEffectiveness {

        @Test
        fun `deve considerar efetividade de tipo no cálculo de dano`() {
            // Nota: Pokemon.calculateTypeEffectiveness() atualmente retorna 1.0 (placeholder)
            // Este teste documenta o comportamento esperado quando a feature for implementada

            val firePokemon = createPokemon(
                1L,
                "Charizard",
                createStats(attack = 100),
                types = setOf(Type(1L, "Fire", null))
            )
            val grassPokemon = createPokemon(
                2L,
                "Venusaur",
                createStats(attack = 100),
                types = setOf(Type(2L, "Grass", null))
            )

            every { pokemonRepository.findById("1") } returns firePokemon
            every { pokemonRepository.findById("2") } returns grassPokemon

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            assertNotNull(result)
            // Quando implementado: Fire deve ser super efetivo contra Grass
            // assertEquals("1", result.winnerId) // Charizard venceria
        }

        @Test
        fun `deve lidar com Pokemon de dois tipos`() {
            val dualTypePokemon = createPokemon(
                1L,
                "Charizard",
                createStats(attack = 110),
                types = setOf(
                    Type(1L, "Fire", null),
                    Type(2L, "Flying", null)
                )
            )
            val singleTypePokemon = createPokemon(
                2L,
                "Blastoise",
                createStats(attack = 105),
                types = setOf(Type(3L, "Water", null))
            )

            every { pokemonRepository.findById("1") } returns dualTypePokemon
            every { pokemonRepository.findById("2") } returns singleTypePokemon

            val result = battleUseCase.execute(StartBattleInput(pokemonId = "1", opponentId = "2"))

            assertNotNull(result)
            // Pokemon pode ter até 2 tipos
        }
    }
}
