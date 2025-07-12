package com.pokedex.bff.infrastructure.seeder.services

import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.strategy.ImportStrategy
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class DatabaseSeederTest {

    @MockK
    private lateinit var strategy1: ImportStrategy

    @MockK
    private lateinit var strategy2: ImportStrategy

    private lateinit var strategies: List<ImportStrategy>

    private lateinit var databaseSeeder: DatabaseSeeder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        strategies = listOf(strategy1, strategy2)
        databaseSeeder = DatabaseSeeder(strategies)

        every { strategy1.getEntityName() } returns "Strategy1"
        every { strategy1.import(any()) } returns ImportCounts(1, 0)
        every { strategy2.getEntityName() } returns "Strategy2"
        every { strategy2.import(any()) } returns ImportCounts(2, 0)
    }

    @Test
    fun `importAll should call import on each strategy`() {
        val importResultsSlot = slot<ImportResults>()

        databaseSeeder.importAll()

        verify(exactly = 1) { strategy1.import(capture(importResultsSlot)) }
        verify(exactly = 1) { strategy2.import(capture(importResultsSlot)) }

        verify(atLeast = 1) { strategy1.getEntityName() }
        verify(atLeast = 1) { strategy2.getEntityName() }
    }

    @Test
    fun `importAll should proceed with other strategies if one fails`() {
        val failingStrategy = mockk<ImportStrategy>()
        val workingStrategy = mockk<ImportStrategy>()

        val customStrategies = listOf(failingStrategy, workingStrategy)
        val seederWithFailure = DatabaseSeeder(customStrategies)

        every { failingStrategy.getEntityName() } returns "FailingStrategy"
        every { failingStrategy.import(any()) } throws RuntimeException("Simulated strategy failure")

        every { workingStrategy.getEntityName() } returns "WorkingStrategy"
        every { workingStrategy.import(any()) } returns ImportCounts(1,0)

        seederWithFailure.importAll()

        verify(exactly = 1) { failingStrategy.import(any()) }
        verify(exactly = 1) { workingStrategy.import(any()) }
    }

    @Test
    fun `importAll should complete without throwing exceptions`() {
        assertDoesNotThrow { databaseSeeder.importAll() }
    }
}
