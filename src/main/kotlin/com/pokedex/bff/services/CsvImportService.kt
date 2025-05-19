package com.pokedex.bff.service

import com.pokedex.bff.models.Pokemon
import com.pokedex.bff.repositories.PokemonRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.nio.charset.StandardCharsets

@Service
class CsvImportService(private val pokemonRepository: PokemonRepository) {

    private val logger = LoggerFactory.getLogger(CsvImportService::class.java)

    @Transactional
    fun importPokemonFromCsv(inputStream: InputStream) {
        try {
            val reader = inputStream.bufferedReader(StandardCharsets.UTF_8)
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())

            csvParser.use { parser ->
                for (record in parser) {
                    try {
                        val id = record.get("id").toLongOrNull()
                        val identifier = record.get("identifier")
                        val speciesId = record.get("species_id").toLongOrNull()
                        val height = record.get("height").toIntOrNull()
                        val weight = record.get("weight").toIntOrNull()
                        val baseExperience = record.get("base_experience").toIntOrNull()
                        val order = record.get("order").toIntOrNull()
                        val isDefault = record.get("is_default").toBooleanStrictOrNull() ?: false

                        if (id == null || identifier.isNullOrBlank()) {
                            logger.warn("Linha CSV ignorada devido a ID ou identifier ausente/inválido: $record")
                            continue
                        }

                        val pokemon = Pokemon(
                            id = id,
                            identifier = identifier,
                            speciesId = speciesId,
                            height = height,
                            weight = weight,
                            baseExperience = baseExperience,
                            order = order,
                            isDefault = isDefault
                        )
                        pokemonRepository.save(pokemon)
                    } catch (e: Exception) {
                        logger.error("Erro ao processar a linha CSV: $record", e)
                    }
                }
            }
            logger.info("Importação de Pokémon concluída com sucesso.")
        } catch (e: Exception) {
            logger.error("Erro ao importar o arquivo CSV de Pokémon", e)
            throw e
        }
    }
}