//package com.pokedex.bff.infrastructure.seeder.services
//
//import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
//import com.pokedex.bff.infrastructure.seeder.strategy.ImportStrategy
//import io.mockk.every
//import io.mockk.impl.annotations.InjectMockKs
//import io.mockk.impl.annotations.MockK
//import io.mockk.junit5.MockKExtension
//import io.mockk.verify
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//
//@ExtendWith(MockKExtension::class)
//class DatabaseSeederTests {
//
//    @InjectMockKs
//    private lateinit var seeder: DatabaseSeeder
//
//    @MockK
//    private lateinit var strategies: List<ImportStrategy>
//
//    @MockK
//    private lateinit var strategy: ImportStrategy
//
//    @Test
//    fun `should run seeder`() {
//        every { strategies.iterator() } returns mutableListOf(strategy).iterator()
//        every { strategy.import(any()) } returns ImportResults()
//
//        seeder.run {}
//
//        verify(exactly = 1) { strategy.import(any()) }
//    }
//}
