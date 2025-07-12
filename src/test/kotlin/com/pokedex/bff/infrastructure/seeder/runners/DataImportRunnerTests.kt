package com.pokedex.bff.infrastructure.seeder.runners

import com.pokedex.bff.infrastructure.seeder.services.DatabaseSeeder
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.ApplicationArguments

@ExtendWith(MockKExtension::class)
class DataImportRunnerTests {

    @InjectMockKs
    private lateinit var runner: DataImportRunner

    @MockK
    private lateinit var seeder: DatabaseSeeder

    @MockK
    private lateinit var args: ApplicationArguments

    @Test
    fun `should run data import`() {
        every { seeder.run() } returns Unit

        runner.run(args)

        verify(exactly = 1) { seeder.run() }
    }
}
