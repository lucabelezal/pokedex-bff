package com.pokedex.bff.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.StandardCharsets

@Component
class DataLoaderService(
    private val jdbcTemplate: JdbcTemplate,
    @Value("\${app.csv.location}") private val csvFilesLocation: String
) : CommandLineRunner {

    // Optimized set of column names that should be converted to Int
    private val intColumns = setOf(
        "id", "appeal", "jam", "main_region_id", "generation_id",
        "damage_class_id", "power", "pp", "accuracy", "priority",
        "target_id", "effect_id", "effect_chance", "contest_type_id",
        "contest_effect_id", "super_contest_effect_id", "evolves_from_species_id",
        "evolution_chain_id", "color_id", "shape_id", "habitat_id",
        "gender_rate", "capture_rate", "base_happiness", "hatch_counter",
        "growth_rate_id", "forms_switchable", "is_legendary", "is_mythical", "order_index",
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
        "firmness_id", "natural_gift_type_id", "soil_dryness"
    )

    // Optimized set of boolean column names
    private val booleanColumns = setOf(
        "is_battle_only", "is_baby", "has_gender_differences", "forms_switchable",
        "is_legendary", "is_mythical", "is_default", "is_main_series",
        "is_mega", "official"
    )

    /**
     * This method runs automatically when the Spring Boot application starts.
     * It orchestrates the loading of data from CSV files into the database.
     */
    override fun run(vararg args: String?) {
        println("===================================================================")
        println(" Starting CSV data loading into the database...")
        println(" CSVs location: $csvFilesLocation")
        println("===================================================================")

        try {
            // *** LOADING ORDER IS CRITICAL DUE TO FOREIGN KEY CONSTRAINTS ***
            // Load "parent" tables first (those without foreign key dependencies on other tables),
            // then "child" tables that depend on parent tables, and so on.

            // Level 0: Tables without FK dependencies on other tables
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


            // Level 1: Tables that only depend on Level 0 tables
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


            // Level 2: Tables with dependencies on Level 1 or composite primary keys
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
                "forms_switchable", "is_legendary", "is_mythical", "order_index",
                "conquest_order"
            ))
            loadTable("pokemon.csv", "pokemon", listOf("id", "identifier", "species_id", "height", "weight", "base_experience", "order_index", "is_default"))
            loadTable("pokemon_location_areas.csv", "pokemon_location_areas", listOf("id", "location_id", "game_index", "identifier"))
            loadTable("pokemon_forms.csv", "pokemon_forms", listOf(
                "id", "identifier", "form_identifier", "pokemon_id", "introduced_in_version_group_id",
                "is_default", "is_battle_only", "is_mega", "form_order", "order_index"
            ))


            // Level 3: Join tables with composite primary keys or Level 2 dependencies
            loadTable("pokemon_stats.csv", "pokemon_stats", listOf("pokemon_id", "stat_id", "base_stat", "effort"))
            loadTable("pokemon_types.csv", "pokemon_types", listOf("pokemon_id", "type_id", "slot"))
            loadTable("pokemon_abilities.csv", "pokemon_abilities", listOf("pokemon_id", "ability_id"))
            loadTable("pokemon_egg_groups.csv", "pokemon_egg_groups", listOf("pokemon_species_id", "egg_group_id"))
            loadTable("berry_flavors.csv", "berry_flavors", listOf("berry_id", "contest_type_id", "flavor_id"))
            loadTable("versions.csv", "versions", listOf("id", "version_group_id", "identifier"))
            loadTable("ability_prose.csv", "ability_prose", listOf("ability_id", "local_language_id", "short_effect"))
            loadTable("ability_flavor_text.csv", "ability_flavor_text", listOf("ability_id", "version_group_id", "language_id", "flavor_text"))
            loadTable("pokemon_moves.csv", "pokemon_moves", listOf("pokemon_id", "version_group_id", "move_id", "pokemon_move_method_id", "level", "move_order", "mastery"))


            println("===================================================================")
            println(" Data loading completed successfully!")
            println("===================================================================")

        } catch (e: Exception) {
            System.err.println("===================================================================")
            System.err.println(" ERROR DURING DATA LOADING:")
            System.err.println(" Message: ${e.message}")
            System.err.println(" Please check:")
            System.err.println(" 1. The loading order of CSVs (foreign keys).")
            System.err.println(" 2. Column names in CSVs and in the code.")
            System.err.println(" 3. Data types in CSVs versus database types.")
            System.err.println(" 4. ON CONFLICT clauses for composite primary keys.")
            System.err.println("===================================================================")
            e.printStackTrace()
            throw RuntimeException("Failed to load initial data.", e)
        }
    }

    /**
     * Loads data from a CSV file into a database table.
     * @param csvFileName The name of the CSV file (e.g., "regions.csv").
     * @param tableName The name of the database table.
     * @param columnNames The list of column names in the order they appear in the CSV and the table.
     */
    private fun loadTable(csvFileName: String, tableName: String, columnNames: List<String>) {
        val csvFile = File(csvFilesLocation, csvFileName)
        if (!csvFile.exists()) {
            println("WARNING: CSV file not found for $tableName at $csvFile. Skipping load.")
            return
        }

        println("  -> Loading '$csvFileName' into table '$tableName'...")

        val insertSql = getInsertSql(tableName, columnNames)
        val batchArgs = mutableListOf<Array<Any?>>()
        var lineCount = 0

        val allLines = csvFile.readLines(StandardCharsets.UTF_8)
        if (allLines.isEmpty()) {
            System.err.println("  ERROR: CSV '$csvFileName' is empty.")
            return
        }

        val header = allLines.first().split(",").map { it.trim() }
        val dataLines = allLines.drop(1)

        if (header.isEmpty()) {
            System.err.println("  ERROR: CSV '$csvFileName' has no header.")
            return
        }
        if (!columnNames.all { it in header }) {
            val missingCols = columnNames.filter { it !in header }
            System.err.println("  ERROR: CSV '$csvFileName' does not contain all expected columns: $missingCols. CSV Header: $header")
            return
        }

        for (line in dataLines) {
            if (line.isBlank()) continue

            lineCount++
            val values = line.split(",").map { it.trim() }
            if (values.size != header.size) {
                System.err.println("  WARNING: Skipping malformed line in '$csvFileName'. Expected ${header.size} columns, got ${values.size}. Line: '$line'")
                continue
            }
            val rowMap = header.zip(values).toMap()

            val args: Array<Any?> = columnNames.map { colName ->
                val stringValue = rowMap[colName]?.trim()
                if (stringValue.isNullOrBlank()) {
                    null // Always pass null for blank/null strings, let DB handle default/nullable
                } else {
                    when (colName) {
                        in intColumns -> stringValue.toIntOrNull() // Use toIntOrNull for safer parsing
                        in booleanColumns -> {
                            when (stringValue.lowercase()) {
                                "true", "1" -> true
                                "false", "0" -> false
                                else -> {
                                    System.err.println("  WARNING: Invalid boolean value for column '$colName' in '$csvFileName': '$stringValue'. Setting to null.")
                                    null
                                }
                            }
                        }
                        else -> stringValue
                    }
                }
            }.toTypedArray()

            batchArgs.add(args)
        }

        if (batchArgs.isNotEmpty()) {
            try {
                val updatedRows = jdbcTemplate.batchUpdate(insertSql, batchArgs)
                println("  -> Inserted ${updatedRows.size} rows into table '$tableName' from a total of $lineCount lines in CSV.")
            } catch (e: DataIntegrityViolationException) {
                System.err.println("  DATA INTEGRITY ERROR when loading '$tableName': ${e.message}")
                System.err.println("  This can happen if there's data in the CSV violating NOT NULL, FKs, or unique keys.")
                System.err.println("  SQL used: $insertSql")
                throw e
            } catch (e: Exception) {
                System.err.println("  UNKNOWN ERROR when loading '$tableName': ${e.message}")
                System.err.println("  SQL used: $insertSql")
                e.printStackTrace()
                throw e
            }
        } else {
            println("  No valid rows to insert into table '$tableName' from file '$csvFileName'.")
        }
    }

    /**
     * Constructs the SQL INSERT query with the appropriate ON CONFLICT clause.
     */
    private fun getInsertSql(tableName: String, columnNames: List<String>): String {
        val placeHolders = columnNames.joinToString { "?" }
        val columns = columnNames.joinToString(", ")

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
            // Default ON CONFLICT for single-column primary keys (assuming 'id')
            else -> "ON CONFLICT (id) DO NOTHING"
        }

        return "INSERT INTO $tableName ($columns) VALUES ($placeHolders) $onConflictClause"
    }
}