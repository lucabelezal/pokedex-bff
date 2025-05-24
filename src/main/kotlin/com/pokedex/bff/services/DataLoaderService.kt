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

    // Este método é executado automaticamente quando a aplicação Spring Boot é iniciada
    override fun run(vararg args: String?) {
        println("===================================================================")
        println(" Iniciando o carregamento de dados do CSV para o banco de dados...")
        println(" Localização dos CSVs: $csvFilesLocation")
        println("===================================================================")

        try {
            // *** ORDEM DE CARREGAMENTO É CRÍTICA DEVIDO ÀS CHAVES ESTRANGEIRAS ***
            // Carregue as tabelas "pai" primeiro (que não dependem de outras tabelas),
            // depois as tabelas "filhas" que dependem das tabelas pai, e assim por diante.

            // Nível 0: Tabelas sem dependências de FK para outras tabelas
            loadTable("regions.csv", "regions", listOf("id", "identifier"))
            loadTable("damage_classes.csv", "damage_classes", listOf("id", "identifier"))
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


            // Nível 1: Tabelas que dependem apenas das tabelas de Nível 0
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
            loadTable("berries.csv", "berries", listOf("id", "identifier", "growth_time", "max_harvest", "natural_gift_power", "size", "smoothness", "item_id"))


            // Nível 2: Tabelas com dependências de Nível 1 ou PKs compostas
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


            // Nível 3: Tabelas de junção com PKs compostas ou dependências de Nível 2
            // ATENÇÃO: As cláusulas ON CONFLICT para tabelas com PK composta DEVEM ser
            // ajustadas na função `getInsertSql` ou tratadas individualmente.
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
            println(" Carregamento de dados concluído com sucesso!")
            println("===================================================================")

        } catch (e: Exception) {
            System.err.println("===================================================================")
            System.err.println(" ERRO DURANTE O CARREGAMENTO DE DADOS:")
            System.err.println(" Mensagem: ${e.message}")
            System.err.println(" Por favor, verifique:")
            System.err.println(" 1. A ordem de carregamento dos CSVs (chaves estrangeiras).")
            System.err.println(" 2. Os nomes das colunas nos CSVs e no código.")
            System.err.println(" 3. Os tipos de dados nos CSVs versus os tipos no DB.")
            System.err.println(" 4. As cláusulas ON CONFLICT para chaves primárias compostas.")
            System.err.println("===================================================================")
            e.printStackTrace()
            // Em caso de erro fatal na inicialização, você pode querer lançar uma exceção para
            // impedir que a aplicação continue com dados incompletos ou incorretos.
            throw RuntimeException("Falha ao carregar dados iniciais.", e)
        }
    }

    /**
     * Carrega dados de um arquivo CSV para uma tabela do banco de dados.
     * @param csvFileName O nome do arquivo CSV (ex: "regions.csv").
     * @param tableName O nome da tabela no banco de dados.
     * @param columnNames A lista de nomes das colunas na ordem em que aparecem no CSV e na tabela.
     */
    private fun loadTable(csvFileName: String, tableName: String, columnNames: List<String>) {
        val csvFile = File(csvFilesLocation, csvFileName)
        if (!csvFile.exists()) {
            println("AVISO: Arquivo CSV não encontrado para $tableName em $csvFile. Pulando carregamento.")
            return
        }

        println("  -> Carregando '$csvFileName' para a tabela '$tableName'...")

        val insertSql = getInsertSql(tableName, columnNames)
        val batchArgs = mutableListOf<Array<Any?>>()
        var lineCount = 0

        // --- INÍCIO DA CORREÇÃO: Lendo todas as linhas para uma lista ---
        val allLines = csvFile.readLines(StandardCharsets.UTF_8)
        if (allLines.isEmpty()) {
            System.err.println("  ERRO: CSV '$csvFileName' está vazio.")
            return
        }

        val header = allLines.first().split(",").map { it.trim() }
        val dataLines = allLines.drop(1) // As linhas de dados sem o cabeçalho

        if (header.isEmpty()) {
            System.err.println("  ERRO: CSV '$csvFileName' sem cabeçalho.")
            return
        }
        // Verifica se todas as colunas esperadas estão no cabeçalho do CSV
        if (!columnNames.all { it in header }) {
            val missingCols = columnNames.filter { it !in header }
            System.err.println("  ERRO: CSV '$csvFileName' não contém todas as colunas esperadas: $missingCols. Cabeçalho CSV: $header")
            return
        }

        for (line in dataLines) { // Iterando sobre a lista de linhas de dados
            if (line.isBlank()) continue

            lineCount++
            val values = line.split(",").map { it.trim() }
            val rowMap = header.zip(values).toMap()

            // Converte os valores da linha CSV para os tipos apropriados para o JDBC
            val args: Array<Any?> = columnNames.map { colName ->
                val value = rowMap[colName]
                if (value == null || value.isBlank()) {
                    null // Trata strings vazias ou nulas do CSV como NULL para o banco de dados
                } else {
                    // Tenta converter para o tipo correto baseado no nome da coluna ou um padrão
                    when (colName) {
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
                        "local_language_id", "display_order" -> {
                            value.toIntOrNull() // Tenta converter para Int
                        }
                        "is_battle_only", "is_baby", "has_gender_differences", "forms_switchable",
                        "is_legendary", "is_mythical", "is_default", "is_main_series",
                        "is_mega", "official" -> { // 'official' no languages.csv é um booleano (0 ou 1)
                            when (value.lowercase()) {
                                "true", "1" -> true
                                "false", "0" -> false
                                else -> null // ou lançar erro se não for válido
                            }
                        }
                        else -> value // Manter como String para outras colunas
                    }
                }
            }.toTypedArray()

            batchArgs.add(args)
        }
        // --- FIM DA CORREÇÃO ---


        if (batchArgs.isNotEmpty()) {
            try {
                val updatedRows = jdbcTemplate.batchUpdate(insertSql, batchArgs)
                println("  -> Inseridas ${updatedRows.size} linhas na tabela '$tableName' de um total de $lineCount linhas no CSV.")
            } catch (e: DataIntegrityViolationException) {
                System.err.println("  ERRO DE INTEGRIDADE DE DADOS ao carregar '$tableName': ${e.message}")
                System.err.println("  Isso pode ocorrer se houver dados no CSV que violam NOT NULL, FKs ou chaves únicas.")
                System.err.println("  SQL utilizado: $insertSql")
                throw e // Relançar para que a exceção seja tratada pelo método run
            } catch (e: Exception) {
                System.err.println("  ERRO DESCONHECIDO ao carregar '$tableName': ${e.message}")
                System.err.println("  SQL utilizado: $insertSql")
                e.printStackTrace()
                throw e // Relançar
            }
        } else {
            println("  Nenhuma linha válida para inserir na tabela '$tableName' do arquivo '$csvFileName'.")
        }
    }

    /**
     * Constrói a query SQL de INSERT com a cláusula ON CONFLICT apropriada.
     */
    private fun getInsertSql(tableName: String, columnNames: List<String>): String {
        val placeHolders = columnNames.joinToString { "?" }
        val columns = columnNames.joinToString(", ")

        // Define a cláusula ON CONFLICT baseada na chave primária da tabela
        // VOCÊ DEVE GARANTIR QUE ISTO ESTÁ COMPLETO PARA TODAS AS SUAS TABELAS COM CHAVES COMPOSTAS
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
            // Adicione outras tabelas com PK composta aqui se houver
            else -> "ON CONFLICT (id) DO NOTHING" // Padrão para tabelas com PK 'id'
        }

        return "INSERT INTO $tableName ($columns) VALUES ($placeHolders) $onConflictClause"
    }
}