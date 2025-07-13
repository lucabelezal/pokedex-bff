package com.pokedex.bff.infrastructure.seeder.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pokedex.bff.infrastructure.seeder.exception.DataImportException
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class JsonLoader(private val objectMapper: ObjectMapper) {

    companion object {
        private val logger = LoggerFactory.getLogger(JsonLoader::class.java)
    }

    internal open fun createClassPathResource(filePath: String): ClassPathResource {
        return ClassPathResource(filePath)
    }

    final inline fun <reified T> loadJson(filePath: String): T {
        return loadJson(filePath, object : TypeReference<T>() {})
    }

    fun <T> loadJson(filePath: String, typeReference: TypeReference<T>): T {
        try {
            val resource = createClassPathResource(filePath)
            return resource.inputStream.use { objectMapper.readValue(it, typeReference) }
        } catch (e: IOException) {
            logger.error("Failed to load JSON from $filePath: ${e.message}", e)
            throw DataImportException("Error loading data from $filePath", e)
        }
    }
}
