package com.pokedex.bff.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.StandardCharsets
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord


@Component
class DataLoaderService(
    private val jdbcTemplate: JdbcTemplate,
    @Value("\${app.csv.location}") private val csvFilesLocation: String
) : CommandLineRunner {

    // Contadores para o resumo final
    private var successCount: Int = 0
    private var failCount: Int = 0

    // Optimized set of column names that should be converted to Int
    private val intColumns = setOf(
        "id", "appeal", "jam", "main_region_id", "generation_id",
        "damage_class_id", "power", "pp", "accuracy", "priority",
        "target_id", "effect_id", "effect_chance", "contest_type_id",
        "contest_effect_id", "super_contest_effect_id", "evolves_from_species_id",
        "evolution_chain_id", "color_id", "shape_id", "habitat_id",
        "gender_rate", "capture_rate", "base_happiness", "hatch_counter",
        "growth_rate_id",
        "forms_switchable", "order_index",
        "conquest_order", "species_id", "height", "weight", "base_experience", "stat_id", "base_stat",
        "effort", "type_id", "slot", "ability_id", "pokemon_species_id",
        "egg_group_id", "berry_id", "flavor_id", "version_group_id",
        "language_id", "item_id", "category_id", "cost", "fling_power",
        "fling_effect_id", "level", "pokemon_move_method_id", "move_id",
        "game_index", "gene_mod_5", "decreased_stat_id", "increased_stat_id",
        "hates_flavor_id", "likes_flavor_id", "pocket_id", "growth_time",
        "max_harvest", "natural_gift_power", "size", "smoothness",
        "local_language_id", "display_order", "region_id", "group_order",
        "baby_trigger_item_id", "move_effect_id", "pokemon_id", "introduced_in_version_group_id",
        "firmness_id", "natural_gift_type_id", "soil_dryness", "order_index", "form_order",
        "flavor_id", "move_order", "mastery", "location_id"
    )

    // Optimized set of boolean column names
    private val booleanColumns = setOf(
        "is_battle_only", "is_baby", "has_gender_differences",
        "is_legendary", "is_mythical", "is_default", "is_main_series",
        "is_mega", "official", "is_legendary", "is_mythical", "is_hidden"
    )

    override fun run(vararg args: String?) {
        println("===================================================================")
        println(" Iniciando o carregamento de dados CSV para o banco de dados...")
        println(" Localização dos CSVs: $csvFilesLocation")
        println("===================================================================")

        try {
            successCount = 0
            failCount = 0

            // *** A ORDEM DE CARREGAMENTO É CRÍTICA DEVIDO ÀS RESTRIÇÕES DE CHAVE ESTRANGEIRA ***

            loadTable("regions.csv", "regions", listOf("id", "identifier"))
            loadTable("move_damage_classes.csv", "damage_classes", listOf("id", "identifier"))
            loadTable("super_contest_effects.csv", "super_contest_effects", listOf("id", "appeal"))
            loadTable("move_targets.csv", "move_targets", listOf("id", "identifier"))
            loadTable("move_effects.csv", "move_effects", listOf("id"))
            loadTable("growth_rates.csv", "growth_rates", listOf("id", "identifier", "formula"))
            loadTable("egg_groups.csv", "egg_groups", listOf("id", "identifier"))
            loadTable("genders.csv", "genders", listOf("id", "identifier"))
            loadTable("pokemon_colors.csv", "pokemon_colors", listOf("id", "identifier"))
            loadTable("pokemon_shapes.csv", "pokemon_shapes", listOf("id", "identifier"))
            loadTable("pokemon_habitats.csv", "pokemon_habitats", listOf("id", "identifier"))
            loadTable("contest_types.csv", "contest_types", listOf("id", "identifier"))
            loadTable("contest_effects.csv", "contest_effects", listOf("id", "appeal", "jam"))
            loadTable("flavors.csv", "flavors", listOf("id", "identifier"))
            loadTable("pokemon_move_methods.csv", "pokemon_move_methods", listOf("id", "identifier"))
            loadTable("item_categories.csv", "item_categories", listOf("id", "pocket_id", "identifier"))
            loadTable("languages.csv", "languages", listOf("id", "iso639", "iso3166", "identifier", "official", "display_order"))

            loadTable("generations.csv", "generations", listOf("id", "main_region_id", "identifier"))
            loadTable("types.csv", "types", listOf("id", "identifier", "generation_id", "damage_class_id"))
            loadTable("stats.csv", "stats", listOf("id", "damage_class_id", "identifier", "is_battle_only", "game_index"))
            loadTable("abilities.csv", "abilities", listOf("id", "identifier", "generation_id", "is_main_series"))
            loadTable("characteristics.csv", "characteristics", listOf("id", "stat_id", "gene_mod_5"))
            loadTable("natures.csv", "natures", listOf("id", "identifier", "decreased_stat_id", "increased_stat_id", "hates_flavor_id", "likes_flavor_id", "game_index"))
            loadTable("items.csv", "items", listOf("id", "identifier", "category_id", "cost", "fling_power", "fling_effect_id"))
            loadTable("locations.csv", "locations", listOf("id", "region_id", "identifier"))
            loadTable("version_groups.csv", "version_groups", listOf("id", "identifier", "generation_id", "group_order"))
            loadTable("evolution_chains.csv", "evolution_chains", listOf("id", "baby_trigger_item_id"))
            loadTable("berries.csv", "berries", listOf("id", "item_id", "firmness_id", "natural_gift_power", "natural_gift_type_id", "size", "max_harvest", "growth_time", "soil_dryness", "smoothness"))

            loadTable("move_effect_prose.csv", "move_effect_prose", listOf("move_effect_id", "local_language_id", "short_effect", "effect"))
            loadTable("moves.csv", "moves", listOf(
                "id", "identifier", "generation_id", "type_id", "power", "pp",
                "accuracy", "priority", "target_id", "damage_class_id",
                "effect_id", "effect_chance", "contest_type_id", "contest_effect_id",
                "super_contest_effect_id"
            ))
            loadTable("pokemon_species.csv", "pokemon_species", listOf(
                "id", "identifier", "generation_id", "evolves_from_species_id",
                "evolution_chain_id", "color_id", "shape_id", "habitat_id",
                "gender_rate", "capture_rate", "base_happiness", "is_baby",
                "hatch_counter", "has_gender_differences", "growth_rate_id",
                "forms_switchable",
                "is_legendary", "is_mythical", "order_index",
                "conquest_order"
            ))
            loadTable("pokemon.csv", "pokemon", listOf("id", "identifier", "species_id", "height", "weight", "base_experience", "order_index", "is_default"))
            loadTable("location_areas.csv", "location_areas", listOf("id", "location_id", "game_index", "identifier"))
            loadTable("pokemon_forms.csv", "pokemon_forms", listOf(
                "id", "identifier", "form_identifier", "pokemon_id", "introduced_in_version_group_id",
                "is_default", "is_battle_only", "is_mega", "form_order", "order_index"
            ))

            loadTable("pokemon_stats.csv", "pokemon_stats", listOf("pokemon_id", "stat_id", "base_stat", "effort"))
            loadTable("pokemon_types.csv", "pokemon_types", listOf("pokemon_id", "type_id", "slot"))
            loadTable("pokemon_abilities.csv", "pokemon_abilities", listOf("pokemon_id", "ability_id", "is_hidden", "slot"))
            loadTable("pokemon_egg_groups.csv", "pokemon_egg_groups", listOf("pokemon_species_id", "egg_group_id"))
            loadTable("berry_flavors.csv", "berry_flavors", listOf("berry_id", "contest_type_id", "flavor_id"))
            loadTable("versions.csv", "versions", listOf("id", "version_group_id", "identifier"))
            loadTable("ability_prose.csv", "ability_prose", listOf("ability_id", "local_language_id", "short_effect", "effect"))
            loadTable("ability_flavor_text.csv", "ability_flavor_text", listOf("ability_id", "version_group_id", "language_id", "flavor_text"))
            loadTable("pokemon_moves.csv", "pokemon_moves", listOf("pokemon_id", "version_group_id", "move_id", "pokemon_move_method_id", "level", "move_order", "mastery"))


            println("===================================================================")
            println(" Carregamento de dados finalizado!")
            println(" CSVs processados com sucesso: ${successCount}")
            println(" CSVs com falha: ${failCount}")
            println("===================================================================")

        } catch (e: Exception) {
            System.err.println("===================================================================")
            System.err.println(" ERRO DURANTE O CARREGAMENTO DOS DADOS:")
            System.err.println(" Mensagem: ${e.message}")
            System.err.println(" Por favor, verifique:")
            System.err.println(" 1. A ordem de carregamento dos CSVs (chaves estrangeiras).")
            System.err.println(" 2. Nomes das colunas nos CSVs e no código.")
            System.err.println(" 3. Tipos de dados nos CSVs vs. tipos de banco de dados.")
            System.err.println(" 4. Cláusulas ON CONFLICT para chaves primárias compostas.")
            System.err.println("===================================================================")
            e.printStackTrace()
            throw RuntimeException("Falha ao carregar os dados iniciais.", e)
        }
    }

    private fun loadTable(csvFileName: String, tableName: String, columnNames: List<String>) {
        val csvFile = File(csvFilesLocation, csvFileName)
        if (!csvFile.exists()) {
            println("  AVISO: Arquivo CSV não encontrado para '$tableName' em $csvFile. Ignorando carregamento.")
            failCount++
            return
        }

        println("  -> Carregando '$csvFileName' na tabela '$tableName'...")

        val insertSql = getInsertSql(tableName, columnNames)
        val batchArgs = mutableListOf<Array<Any?>>()
        var lineCount = 0
        var recordCount = 0

        try {
            csvFile.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                val parser = CSVFormat.DEFAULT
                    .withHeader(*columnNames.toTypedArray())
                    .withSkipHeaderRecord(true)
                    .withDelimiter(',')
                    .withQuote('"')
                    .withIgnoreSurroundingSpaces(true)
                    .parse(reader)

                val headerMap = parser.headerMap
                if (headerMap.isEmpty()) {
                    System.err.println("  ERRO: CSV '$csvFileName' não tem cabeçalho ou o cabeçalho não pôde ser lido pelo Commons CSV.")
                    failCount++
                    return
                }

                if (!columnNames.all { it in headerMap }) {
                    val missingCols = columnNames.filter { it !in headerMap }
                    System.err.println("  ERRO: CSV '$csvFileName' não contém todas as colunas esperadas: $missingCols. Cabeçalho lido: $headerMap")
                    failCount++
                    return
                }

                for (record: CSVRecord in parser) {
                    lineCount++
                    val args: Array<Any?> = columnNames.map { colName ->
                        val stringValue = record.get(colName)?.trim()

                        if (stringValue.isNullOrBlank()) {
                            null // Passa null para strings em branco/nulas
                        } else {
                            when {
                                colName in intColumns -> stringValue.toIntOrNull()
                                colName in booleanColumns -> {
                                    when (stringValue.lowercase()) {
                                        "true", "1" -> true
                                        "false", "0" -> false
                                        else -> {
                                            System.err.println("  AVISO: Linha ${record.recordNumber}, valor booleano inválido para coluna '$colName' em '$csvFileName': '$stringValue'. Definindo como null.")
                                            null
                                        }
                                    } as Boolean? // Cast explícito para Boolean?
                                }
                                else -> stringValue // Para outros tipos, mantém como String
                            }
                        }
                    }.toTypedArray()
                    batchArgs.add(args)
                    recordCount++
                }
            }
        } catch (e: Exception) {
            System.err.println("  ERRO ao ler o CSV '$csvFileName' com Apache Commons CSV: ${e.message}")
            System.err.println("  Verifique a formatação do CSV. A linha do erro pode ser próxima à linha ${lineCount + 1}.")
            failCount++
            e.printStackTrace()
            return
        }

        if (batchArgs.isNotEmpty()) {
            try {
                val updatedRows = jdbcTemplate.batchUpdate(insertSql, batchArgs)
                println("  -> Inseridas ${updatedRows.size} registros na tabela '$tableName' de um total de $recordCount registros válidos no CSV.")
                successCount++
            } catch (e: DataIntegrityViolationException) {
                System.err.println("  ERRO DE INTEGRIDADE DE DADOS ao carregar '$tableName': ${e.message}")
                System.err.println("  Isso pode acontecer se houver dados no CSV violando NOT NULL, FKs ou chaves únicas.")
                System.err.println("  SQL utilizado: $insertSql")
                failCount++
                throw e
            } catch (e: Exception) {
                System.err.println("  ERRO DESCONHECIDO ao carregar '$tableName': ${e.message}")
                System.err.println("  SQL utilizado: $insertSql")
                e.printStackTrace()
                failCount++
                throw e
            }
        } else {
            println("  Nenhum registro válido para inserir na tabela '$tableName' do arquivo '$csvFileName'.")
        }
    }

    private fun getInsertSql(tableName: String, columnNames: List<String>): String {
        val placeHolders = columnNames.joinToString { "?" }
        val columns = columnNames.joinToString(", ") { col ->
            when (col) {
                "order" -> "\"order\""
                // Adicione outras palavras reservadas aqui se necessário
                else -> col
            }
        }

        val onConflictClause = when (tableName) {
            "pokemon_stats" -> "ON CONFLICT (pokemon_id, stat_id) DO NOTHING"
            "pokemon_types" -> "ON CONFLICT (pokemon_id, type_id) DO NOTHING"
            "pokemon_abilities" -> "ON CONFLICT (pokemon_id, ability_id) DO NOTHING"
            "pokemon_egg_groups" -> "ON CONFLICT (pokemon_species_id, egg_group_id) DO NOTHING"
            "berry_flavors" -> "ON CONFLICT (berry_id, contest_type_id) DO NOTHING"
            "ability_prose" -> "ON CONFLICT (ability_id, local_language_id) DO NOTHING"
            "ability_flavor_text" -> "ON CONFLICT (ability_id, version_group_id, language_id) DO NOTHING"
            "move_effect_prose" -> "ON CONFLICT (move_effect_id, local_language_id) DO NOTHING"
            "pokemon_moves" -> "ON CONFLICT (pokemon_id, version_group_id, move_id, pokemon_move_method_id) DO NOTHING"
            // Default ON CONFLICT para chaves primárias de coluna única (assumindo 'id')
            else -> "ON CONFLICT (id) DO NOTHING"
        }

        return "INSERT INTO $tableName ($columns) VALUES ($placeHolders) $onConflictClause"
    }
}