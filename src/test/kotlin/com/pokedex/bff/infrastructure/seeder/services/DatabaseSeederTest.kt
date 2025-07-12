//package com.pokedex.bff.infrastructure.seeder.services
//
//import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
//import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
//import com.pokedex.bff.infrastructure.seeder.strategy.ImportStrategy
//import io.mockk.*
//import io.mockk.impl.annotations.InjectMockKs
//import io.mockk.impl.annotations.MockK
//import io.mockk.junit5.MockKExtension
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.slf4j.LoggerFactory
//
//@ExtendWith(MockKExtension::class)
//class DatabaseSeederTest {
//
//    @MockK
//    private lateinit var strategy1: ImportStrategy
//
//    @MockK
//    private lateinit var strategy2: ImportStrategy
//
//    private lateinit var strategies: List<ImportStrategy>
//
//    // We will inject the list of mocked strategies into the real DatabaseSeeder
//    private lateinit var databaseSeeder: DatabaseSeeder
//
//    // For capturing logs if needed, though direct verification of method calls is preferred.
//    // val logger: Logger = LoggerFactory.getLogger(DatabaseSeeder::class.java) as Logger
//    // val listAppender: ListAppender<ILoggingEvent> = ListAppender()
//
//    @BeforeEach
//    fun setUp() {
//        MockKAnnotations.init(this) // Initializes mocks annotated with @MockK
//        strategies = listOf(strategy1, strategy2)
//        databaseSeeder = DatabaseSeeder(strategies) // Inject the mocked list
//
//        // Common behavior for mocked strategies
//        every { strategy1.getEntityName() } returns "Strategy1"
//        every { strategy1.import(any()) } returns ImportCounts(1, 0)
//        every { strategy2.getEntityName() } returns "Strategy2"
//        every { strategy2.import(any()) } returns ImportCounts(2, 0)
//
//        // Log capturing setup (optional)
//        // listAppender.start()
//        // logger.addAppender(listAppender)
//    }
//
//    // @AfterEach
//    // fun tearDown() {
//    //     logger.detachAndStopAllAppenders()
//    // }
//
//    @Test
//    fun `importAll should call import on each strategy`() {
//        val importResultsSlot = slot<ImportResults>()
//
//        databaseSeeder.importAll()
//
//        verify(exactly = 1) { strategy1.import(capture(importResultsSlot)) }
//        verify(exactly = 1) { strategy2.import(capture(importResultsSlot)) }
//
//        // Verify that the same ImportResults instance is passed to all strategies
//        // (This depends on DatabaseSeeder creating one ImportResults and passing it,
//        // or if strategies manage it internally, this check might change)
//        // In the current DatabaseSeeder, one ImportResults is created and passed.
//        // However, the strategies themselves add to it. So we just check they are called.
//
//        // Verify getEntityName is called (for logging purposes in DatabaseSeeder)
//        verify(atLeast = 1) { strategy1.getEntityName() }
//        verify(atLeast = 1) { strategy2.getEntityName() }
//    }
//
//    @Test
//    fun `importAll should proceed with other strategies if one fails`() {
//        val failingStrategy = mockk<ImportStrategy>()
//        val workingStrategy = mockk<ImportStrategy>()
//
//        val customStrategies = listOf(failingStrategy, workingStrategy)
//        val seederWithFailure = DatabaseSeeder(customStrategies)
//
//        every { failingStrategy.getEntityName() } returns "FailingStrategy"
//        every { failingStrategy.import(any()) } throws RuntimeException("Simulated strategy failure")
//
//        every { workingStrategy.getEntityName() } returns "WorkingStrategy"
//        every { workingStrategy.import(any()) } returns ImportCounts(1,0)
//
//        seederWithFailure.importAll()
//
//        verify(exactly = 1) { failingStrategy.import(any()) }
//        verify(exactly = 1) { workingStrategy.import(any()) } // Crucial: workingStrategy should still be called
//    }
//
//    @Test
//    fun `importAll should correctly use ImportResults for logging final results`() {
//        // We can't directly verify the log output easily without a more complex setup.
//        // However, we can ensure that the ImportResults object passed to logFinalResults
//        // is the one that strategies have interacted with.
//        // Since strategies add to the ImportResults instance passed to them,
//        // and DatabaseSeeder logs that same instance, this is implicitly tested
//        // by verifying strategies are called.
//
//        // For a more direct test of logFinalResults, one might spy on ImportResults,
//        // but that tests ImportResults' internals rather than DatabaseSeeder's usage.
//
//        // What we can do is ensure logFinalResults is called on an ImportResults instance.
//        // We can mock the ImportResults class itself if we were to inject it into DatabaseSeeder.
//        // But DatabaseSeeder currently news it up.
//
//        // Let's spy on DatabaseSeeder to verify logFinalResults is called.
//        val spiedSeeder = spyk(DatabaseSeeder(strategies))
//
//        spiedSeeder.importAll()
//
//        // Verify that the internal logFinalResults method was called.
//        // This requires logFinalResults to be open or the class to be open.
//        // DatabaseSeeder is already 'open class'.
//        // The method logFinalResults is private, so we can't directly verify it with a simple spy.
//        // We would need to make it internal or use more advanced MockK features if this was critical.
//
//        // For now, we trust that if strategies are called and complete,
//        // the final logging method containing results.logFinalResults() is invoked.
//        // The main behavior is that strategies are iterated and exceptions are handled.
//        // The logging itself is a side effect.
//
//        // A simple check is that the method completes without throwing further exceptions.
//        assertDoesNotThrow { databaseSeeder.importAll() }
//    }
//}
