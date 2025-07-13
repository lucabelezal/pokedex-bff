package com.pokedex.bff.infrastructure.seeder.runners

import com.pokedex.bff.infrastructure.seeder.services.DatabaseSeeder
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@ExtendWith(MockKExtension::class)
class DataImportRunnerTests {

    @InjectMockKs
    private lateinit var sut: DataImportRunner

    @MockK
    private lateinit var seeder: DatabaseSeeder

    @Test
    fun `should run data import`() {
        every { seeder.importAll() } just Runs

        sut.run()

        verify(exactly = 1) { seeder.importAll() }
    }
}