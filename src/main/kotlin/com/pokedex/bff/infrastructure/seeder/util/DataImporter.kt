package com.pokedex.bff.infrastructure.seeder.util

import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.exception.DataImportException
import org.slf4j.Logger
import org.springframework.data.jpa.repository.JpaRepository

fun <T: Any, D> importData(
    dtos: List<D>,
    repository: JpaRepository<T, Long>,
    transform: (D) -> T,
    logger: Logger,
    entityName: String
): ImportCounts {
    val counts = ImportCounts()
    dtos.forEach { dto ->
        try {
            repository.save(transform(dto))
            counts.success++
        } catch (e: DataImportException) {
            counts.errors++
            logger.error("Data dependency error for $entityName with value $dto: ${e.message}")
        } catch (e: Exception) {
            counts.errors++
            logger.error("Error importing data for $entityName with value $dto: ${e.message}", e)
        }
    }
    return counts
}
