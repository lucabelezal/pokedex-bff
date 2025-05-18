package com.pokedex.bff.services

import com.pokedex.bff.exceptions.CsvImportException
import com.pokedex.bff.models.Pokemon
import com.pokedex.bff.repositories.PokemonRepository
import com.pokedex.bff.utils.CsvParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CsvImportService(
    private val repository: PokemonRepository
) {
    private val logger = LoggerFactory.getLogger(CsvImportService::class.java)

    fun importPokemonCsv(file: MultipartFile) {
        logger.info("Iniciando importação do CSV: ${file.originalFilename}, tamanho: ${file.size} bytes")

        val rows = CsvParser.parse(file.inputStream)
        logger.debug("Número de linhas lidas do CSV (exceto cabeçalho): ${rows.size}")

        val pokemons = rows.mapNotNull { row ->
            try {
                Pokemon(
                    id = row["id"]!!.toLong(),
                    identifier = row["identifier"]!!,
                    speciesId = row["species_id"]?.toLongOrNull(),
                    height = row["height"]?.toIntOrNull(),
                    weight = row["weight"]?.toIntOrNull(),
                    baseExperience = row["base_experience"]?.toIntOrNull(),
                    order = row["order"]?.toIntOrNull(),
                    isDefault = row["is_default"] == "1"
                )
            } catch (e: Exception) {
                logger.error("Erro ao converter linha CSV: $row", e)
                throw CsvImportException("Erro ao importar CSV: ${e.message}", e)
            }
        }

        repository.deleteAll() // Limpa todos os dados antes de salvar novos
        repository.saveAll(pokemons)

        logger.info("Importação concluída com sucesso! Registros salvos: ${pokemons.size}")
    }
}