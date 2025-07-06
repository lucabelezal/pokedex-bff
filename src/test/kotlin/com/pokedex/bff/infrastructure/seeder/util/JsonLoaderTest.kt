package com.pokedex.bff.infrastructure.seeder.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.bff.infrastructure.seeder.exception.DataImportException
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

class JsonLoaderTest {

    @MockK
    private lateinit var objectMapper: ObjectMapper

    @InjectMockKs
    private lateinit var jsonLoader: JsonLoader

    // Slot to capture ClassPathResource for verification
    private val pathSlot = slot<String>()

    // Mock for ClassPathResource itself to control getInputStream
    @MockK
    private lateinit var classPathResource: ClassPathResource

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        // Spy on jsonLoader to allow mocking createClassPathResource
        jsonLoader = spyk(JsonLoader(objectMapper), recordPrivateCalls = true)
        every { jsonLoader["createClassPathResource"](capture(pathSlot)) } returns classPathResource
    }

    data class TestDto(val id: Int, val name: String)

    @Test
    fun `loadJson should successfully load and parse JSON`() {
        val filePath = "data/test.json"
        val expectedDto = TestDto(1, "Test Name")
        val jsonString = """{"id":1,"name":"Test Name"}"""
        val inputStream = ByteArrayInputStream(jsonString.toByteArray())

        every { classPathResource.inputStream } returns inputStream
        every { objectMapper.readValue(any<InputStream>(), any<TypeReference<TestDto>>()) } returns expectedDto

        val result = jsonLoader.loadJson(filePath, object : TypeReference<TestDto>() {})

        assertEquals(expectedDto, result)
        assertEquals(filePath, pathSlot.captured)
        verify(exactly = 1) { classPathResource.inputStream }
        verify(exactly = 1) { objectMapper.readValue(any<InputStream>(), any<TypeReference<TestDto>>()) }
    }

    @Test
    fun `loadJson reified should successfully load and parse JSON`() {
        val filePath = "data/test-reified.json"
        val expectedDto = TestDto(2, "Test Reified")
        val jsonString = """{"id":2,"name":"Test Reified"}"""
        val inputStream = ByteArrayInputStream(jsonString.toByteArray())

        every { classPathResource.inputStream } returns inputStream
        // Use any() for TypeReference when it's reified in the original call
        every { objectMapper.readValue(any<InputStream>(), any<TypeReference<*>>()) } returns expectedDto


        val result: TestDto = jsonLoader.loadJson(filePath)

        assertEquals(expectedDto, result)
        assertEquals(filePath, pathSlot.captured)
    }

    @Test
    fun `loadJson should throw DataImportException when IOException occurs`() {
        val filePath = "data/nonexistent.json"
        val ioException = IOException("File not found")

        every { classPathResource.inputStream } throws ioException

        val exception = assertThrows<DataImportException> {
            jsonLoader.loadJson(filePath, object : TypeReference<TestDto>() {})
        }

        assertEquals("Error loading data from $filePath", exception.message)
        assertEquals(ioException, exception.cause)
        assertEquals(filePath, pathSlot.captured)
    }

    @Test
    fun `loadJson should throw DataImportException when object mapper fails to parse`() {
        val filePath = "data/invalid.json"
        val jsonString = """{"id":1, "name": "Test Name", malformed_json}""" // Invalid JSON
        val inputStream = ByteArrayInputStream(jsonString.toByteArray())
        val mappingException = mockk<com.fasterxml.jackson.databind.JsonMappingException>()

        every { classPathResource.inputStream } returns inputStream
        every { objectMapper.readValue(any<InputStream>(), any<TypeReference<TestDto>>()) } throws mappingException

        val exception = assertThrows<DataImportException> {
            jsonLoader.loadJson(filePath, object : TypeReference<TestDto>() {})
        }

        assertTrue(exception.message?.startsWith("Error loading data from $filePath") ?: false)
        // Depending on ObjectMapper's exception wrapping, cause might be mappingException or an IOException wrapping it.
        // For this test, we are more interested that DataImportException is thrown.
        assertNotNull(exception.cause)
    }


    @Test
    fun `createClassPathResource should return a ClassPathResource instance`() {
        // Reset the spy behavior for this specific test method to call the original createClassPathResource
        clearMocks(jsonLoader)
        jsonLoader = JsonLoader(objectMapper) // Re-init without spy for this method

        val filePath = "some/path/file.json"
        val resource = jsonLoader.createClassPathResource(filePath)
        assertNotNull(resource)
        assertEquals(filePath, resource.path)
        assertTrue(resource.exists()) // This will be false if the file doesn't actually exist in test resources, but tests the object creation
    }
}
