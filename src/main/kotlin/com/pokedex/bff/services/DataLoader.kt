package com.pokedex.bff.services

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.controllers.dtos.* // Ensure your DTOs are correctly imported
import com.pokedex.bff.models.*
import com.pokedex.bff.repositories.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Value
import java.math.BigDecimal
import java.util.* // Import for Optional

// Data class to store the result of each loading operation
data class LoadResult(
    val operationName: String,
    val success: Boolean,
    val count: Int = 0,
    val errorMessage: String? = null
)

// Assuming source JSONs have DTOs matching their structure
// Using the DTOs you provided previously.
// Import these from their package (e.g., com.pokedex.bff.controllers.dtos.*)
// Make sure these Source DTOs match the EXACT structure of your JSON files.

// Example Source DTOs (Adjust these based on your actual JSON file structures)
data class RegionSourceDto(val id: Int, val name: String)
data class TypeSourceDto(val id: Int, val name: String, val color: String?)
data class EggGroupSourceDto(val id: Int, val name: String)
data class SpeciesSourceDto(val id: Int, val national_pokedex_number: String, val name: String, val species_en: String?, val species_pt: String?)
data class GenerationSourceDto(val id: Int, val name: String, @JsonProperty("regiaoId") val regionId: Int) // Assuming "regiaoId" in 05_generation.json
data class AbilitySourceDto(@JsonProperty("id") val id: Int, val name: String, val description: String?, @JsonProperty("introducedGenerationId") val introducedGenerationId: Int?) // Assuming id and introducedGenerationId in 06_ability.json
data class StatsSourceDto(val id: Int, @JsonProperty("pokemon_national_pokedex_number") val pokemon_national_pokedex_number: String, val total: Int?, val hp: Int?, val attack: Int?, val defense: Int?, @JsonProperty("sp_atk") val sp_atk: Int?, @JsonProperty("sp_def") val sp_def: Int?, val speed: Int?) // Assuming this structure for 07_stats.json (snake_case fields)

// Re-import/re-define DTOs from controllers.dtos needed for parsing other JSONs
// These should match the structures defined previously for 09, 10, 08 json
// Assuming these are the same as in your controllers.dtos file
// data class PokemonJsonDto(...)
// data class PokemonAbilityJsonDto(...)
// data class SpritesJsonDto(...)
// data class OtherSpritesJsonDto(...)
// ... other sprites DTOs ...
// data class WeaknessJsonDto(...)
// data class EvolutionChainJsonDto(...)
// data class EvolutionLinkJsonDto(...)
// data class EvolutionDetailJsonDto(...)
// data class EvolutionConditionJsonDto(...)


@Component
class DataLoader(
    private val regionRepository: RegionRepository,
    private val typeRepository: TypeRepository,
    private val eggGroupRepository: EggGroupRepository,
    private val speciesRepository: SpeciesRepository,
    private val generationRepository: GenerationRepository,
    private val abilityRepository: AbilityRepository,
    private val statsRepository: StatsRepository,
    private val pokemonRepository: PokemonRepository,
    private val pokemonTypeRepository: PokemonTypeRepository,
    private val pokemonAbilityRepository: PokemonAbilityRepository,
    private val pokemonEggGroupRepository: PokemonEggGroupRepository,
    private val pokemonWeaknessRepository: PokemonWeaknessRepository,
    private val evolutionChainRepository: EvolutionChainRepository,
    private val evolutionLinkRepository: EvolutionLinkRepository,
    @Value("\${app.data.location}") private val jsonDataLocation: String
) : CommandLineRunner {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    // Maps for quick lookup of related entities after initial loads
    private lateinit var generationMap: Map<Int, Generation>
    private lateinit var speciesMap: Map<Int, Species>
    private lateinit var typeMap: Map<Int, Type> // For Type by ID
    private lateinit var typeNameMap: Map<String, Type> // For Type by Name
    private lateinit var eggGroupMap: Map<Int, EggGroup>
    private lateinit var abilityMap: Map<Int, Ability> // For Ability by ID
    private lateinit var evolutionChainMap: Map<Int, EvolutionChain> // For EvolutionChain by ID

    // Maps/Lists for data from JSONs that need processing after Pokemon load
    private lateinit var statsSourceMap: Map<String, StatsSourceDto> // Map Stats by national_pokedex_number (assuming StatsSourceDto exists)
    private lateinit var weaknessesSourceMap: Map<Int, WeaknessJsonDto> // Map Weaknesses by pokemon_id
    private lateinit var evolutionChainsSourceList: List<EvolutionChainJsonDto> // Store the whole evolution chain structure for creating links

    override fun run(vararg args: String?) {
        if (pokemonRepository.count() == 0L) { // Check based on Pokemon table being empty
            println("Iniciando o carregamento dos dados de Pokémon a partir de '$jsonDataLocation' no classpath...")
            loadData()
            println("Carregamento de dados de Pokémon concluído.")
        } else {
            println("Dados de Pokémon já existem no banco de dados. Pulando o carregamento inicial.")
        }
    }

    @Transactional // Ensure data loading is atomic
    fun loadData() {
        val results = mutableListOf<LoadResult>()

        println("\n--- Relatório de Carregamento de Dados ---")
        println("Iniciando operações de carregamento...")

        // Load foundational lookup data first
        results.add(wrapLoadFunction("01_region.json (Regiões)") { loadRegions() })
        results.add(wrapLoadFunction("02_type.json (Tipos)") { loadTypes() })
        results.add(wrapLoadFunction("03_egg_group.json (Grupos de Ovos)") { loadEggGroups() })
        results.add(wrapLoadFunction("04_species.json (Espécies)") { loadSpecies() })

        // Load dependent lookup data (needs foundational data)
        results.add(wrapLoadFunction("05_generation.json (Gerações)") { loadGenerations() })
        results.add(wrapLoadFunction("06_ability.json (Habilidades)") { loadAbilities() })

        // Load preliminary data into memory (needed for relations later)
        results.add(wrapLoadFunction("07_stats.json (Stats Source)") { loadStatsPrelim() }) // Now reports number of stats read
        results.add(wrapLoadFunction("10_weaknesses.json (Weaknesses Source)") { loadWeaknessesPrelim() }) // Now reports number of weaknesses read
        results.add(wrapLoadFunction("08_evolution_chains.json (Evolution Chains Source)") { loadEvolutionLinksPrelim() }) // Now reports total number of evolution links read

        // Load Evolution Chains Entities (needed before loading Pokemon to link EvolutionLink)
        results.add(wrapLoadFunction("08_evolution_chains.json (Evolution Chains Entities)") { loadEvolutionChainsEntities() })


        // Load main Pokemon data and create all related entities (Stats, Junctions, Links)
        results.add(wrapLoadFunction("09_pokemon.json (Pokémon and Relations)") { loadPokemonAndRelations() })


        // Resumo final
        val successfulLoads = results.filter { it.success }
        val failedLoads = results.filter { !it.success }

        println("\n--- Sumário Final do Carregamento ---")
        println("Operações de Carregamento Concluídas: ${results.size}")
        println("Sucessos: ${successfulLoads.size}")
        successfulLoads.forEach { println("  ✅ ${it.operationName}: Processados ${it.count} registros/relações.") } // Updated log message

        println("Falhas: ${failedLoads.size}")
        if (failedLoads.isNotEmpty()) {
            failedLoads.forEach { println("  ❌ ${it.operationName}: Erro -> ${it.errorMessage}") }
            println("\nATENÇÃO: Houve falhas no carregamento de dados. Verifique os logs detalhados acima para mais informações.")
        } else {
            println("  Nenhuma falha registrada.")
        }
    }

    // Helper to wrap load functions
    private fun wrapLoadFunction(operationName: String, action: () -> Int): LoadResult {
        return try {
            val count = action()
            LoadResult(operationName, true, count)
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown error"
            println("Exception during operation '$operationName':")
            e.printStackTrace() // Print the full stack trace for debugging
            LoadResult(operationName, false, errorMessage = errorMessage)
        }
    }

    // Function to load JSONs from classpath
    private fun <T> loadJsonFile(fileName: String, type: Class<T>): List<T> {
        return try {
            val fullPath = "$jsonDataLocation$fileName"
            val resource = ClassPathResource(fullPath)
            if (!resource.exists()) {
                throw IllegalStateException("File not found in classpath: $fullPath")
            }
            objectMapper.readValue(resource.inputStream, objectMapper.typeFactory.constructCollectionType(List::class.java, type))
        } catch (e: Exception) {
            // Re-throw the exception so wrapLoadFunction can catch and log the failure
            throw IllegalStateException("Failed to read JSON file '$fileName': ${e.message}", e)
        }
    }

    // --- Individual Load Functions (map to Entities and save) ---
    // These functions now return the count of entities SAVED

    private fun loadRegions(): Int {
        // Assuming 01_region.json structure matches RegionSourceDto {id, name}
        val regions = loadJsonFile("01_region.json", RegionSourceDto::class.java).map {
            Region(id = it.id, name = it.name)
        }
        regionRepository.saveAll(regions)
        return regions.size // Return count of entities saved
    }

    private fun loadTypes(): Int {
        // Assuming 02_type.json structure matches TypeSourceDto {id, name, color}
        val types = loadJsonFile("02_type.json", TypeSourceDto::class.java).map {
            Type(id = it.id, name = it.name, color = it.color)
        }
        typeRepository.saveAll(types)
        typeMap = types.associateBy { it.id!! } // Populate maps for quick lookup
        typeNameMap = types.associateBy { it.name }
        return types.size // Return count of entities saved
    }

    private fun loadEggGroups(): Int {
        // Assuming 03_egg_group.json structure matches EggGroupSourceDto {id, name}
        val eggGroups = loadJsonFile("03_egg_group.json", EggGroupSourceDto::class.java).map {
            EggGroup(id = it.id, name = it.name)
        }
        eggGroupRepository.saveAll(eggGroups)
        eggGroupMap = eggGroups.associateBy { it.id!! } // Populate map
        return eggGroups.size // Return count of entities saved
    }

    private fun loadSpecies(): Int {
        // Assuming 04_species.json structure matches SpeciesSourceDto {id, national_pokedex_number, name, species_en, species_pt}
        val speciesList = loadJsonFile("04_species.json", SpeciesSourceDto::class.java).map {
            Species(id = it.id, nationalPokedexNumber = it.national_pokedex_number, name = it.name, speciesEn = it.species_en, speciesPt = it.species_pt)
        }
        speciesRepository.saveAll(speciesList)
        speciesMap = speciesList.associateBy { it.id!! } // Populate map
        return speciesList.size // Return count of entities saved
    }

    private fun loadGenerations(): Int {
        // Assuming 05_generation.json structure matches GenerationSourceDto {id, name, regionId}
        val generations = loadJsonFile("05_generation.json", GenerationSourceDto::class.java)
        val regionMap = regionRepository.findAll().associateBy { it.id!! } // Load regions into a temporary map

        val generationsToSave = generations.mapNotNull { genDto ->
            val region = regionMap[genDto.regionId]
            if (region != null) {
                Generation(id = genDto.id, name = genDto.name, region = region)
            } else {
                println("WARNING: Region with ID ${genDto.regionId} not found for Generation ${genDto.name}. Skipping generation.")
                null // Skip this generation if region not found
            }
        }

        generationRepository.saveAll(generationsToSave)
        generationMap = generationsToSave.associateBy { it.id!! } // Populate map with saved entities
        return generationsToSave.size // Return count of entities saved
    }

    // Assuming 06_ability.json has { "id": ..., "name": ..., "description": ..., "introducedGenerationId": ... }
    // Assuming you have AbilitySourceDto { id, name, description, introducedGenerationId }
    private fun loadAbilities(): Int {
        val abilities = loadJsonFile("06_ability.json", AbilitySourceDto::class.java)
        val generationMap = this.generationMap // Use the previously populated generation map

        val abilitiesToSave = abilities.mapNotNull { abilityDto ->
            val generation = abilityDto.introducedGenerationId?.let { generationMap[it] }
            // It's okay if generation is null if introducedGenerationId is nullable in JSON/DTO/Entity

            Ability(id = abilityDto.id, name = abilityDto.name, description = abilityDto.description, introducedGeneration = generation)
        }

        abilityRepository.saveAll(abilitiesToSave)
        abilityMap = abilitiesToSave.associateBy { it.id!! } // Populate map with saved entities
        return abilitiesToSave.size // Return count of entities saved
    }

    // New function to load and save Evolution Chains entities
    private fun loadEvolutionChainsEntities(): Int {
        // Assuming 08_evolution_chains.json structure matches EvolutionChainJsonDto { id, chain: [...] }
        // We only need the top-level ID for the Chain entity
        val evolutionChainsJson = loadJsonFile("08_evolution_chains.json", EvolutionChainJsonDto::class.java)

        val evolutionChainsToSave = evolutionChainsJson.map { chainDto ->
            EvolutionChain(id = chainDto.id)
        }

        evolutionChainRepository.saveAll(evolutionChainsToSave)
        evolutionChainMap = evolutionChainsToSave.associateBy { it.id!! } // Populate map with saved entities
        return evolutionChainsToSave.size // Return count of entities saved
    }


    // --- Preliminary Load Functions (load JSON into maps/lists for later use) ---
    // These functions return the count of items READ from JSON

    // Assuming 07_stats.json structure matches StatsSourceDto {id, pokemon_national_pokedex_number, ...}
    private fun loadStatsPrelim(): Int {
        val statsList = loadJsonFile("07_stats.json", StatsSourceDto::class.java)
        statsSourceMap = statsList.associateBy { it.pokemon_national_pokedex_number }
        return statsList.size // Return count of items READ from JSON
    }

    // Assuming 10_weaknesses.json structure matches WeaknessJsonDto {id, pokemon_id, pokemon_name, weaknesses}
    private fun loadWeaknessesPrelim(): Int {
        val weaknessesList = loadJsonFile("10_weaknesses.json", WeaknessJsonDto::class.java)
        weaknessesSourceMap = weaknessesList.associateBy { it.pokemonId }
        return weaknessesList.size // Return count of items READ from JSON
    }

    // Assuming 08_evolution_chains.json structure matches EvolutionChainJsonDto { id, chain: [...] }
    // This loads the full structure for later processing of links
    private fun loadEvolutionLinksPrelim(): Int {
        evolutionChainsSourceList = loadJsonFile("08_evolution_chains.json", EvolutionChainJsonDto::class.java)
        // Return the total number of evolution details (links) found across all chains in the source data
        return evolutionChainsSourceList.sumOf { chainDto ->
            chainDto.chain.sumOf { linkDto ->
                linkDto.evolutionDetails.size
            }
        }
    }

    // --- Main Pokemon Load Function (creates Pokemon and all related entities) ---
    // This function returns the total count of ALL entities SAVED within THIS function

    private fun loadPokemonAndRelations(): Int {
        // Assuming 09_pokemon.json structure matches PokemonJsonDto {id, nationalPokedexNumber, name, ... typeId, abilities, eggGroupIds, typeMatchup }
        val pokemonJsonList = loadJsonFile("09_pokemon.json", PokemonJsonDto::class.java)

        val pokemonToSave = mutableListOf<Pokemon>()
        val statsToSave = mutableListOf<Stats>()
        val pokemonTypesToSave = mutableListOf<PokemonType>()
        val pokemonAbilitiesToSave = mutableListOf<PokemonAbility>()
        val pokemonEggGroupsToSave = mutableListOf<PokemonEggGroup>()
        val pokemonWeaknessesToSave = mutableListOf<PokemonWeakness>()
        val evolutionLinksToSave = mutableListOf<EvolutionLink>()


        // 1. Create and collect main Pokemon entities
        pokemonToSave.addAll(pokemonJsonList.mapNotNull { pokemonDto ->
            val generation = generationMap[pokemonDto.generationId]
            val species = speciesMap[pokemonDto.speciesId]

            if (generation == null) {
                println("WARNING: Generation with ID ${pokemonDto.generationId} not found for Pokemon ${pokemonDto.name}. Skipping this Pokemon.")
                null // Skip this pokemonDto
            } else if (species == null) {
                println("WARNING: Species with ID ${pokemonDto.speciesId} not found for Pokemon ${pokemonDto.name}. Skipping this Pokemon.")
                null // Skip to next pokemonDto
            } else {
                try {
                    Pokemon(
                        id = pokemonDto.id, // Assuming IDs from JSON are used, not generated by DB for Pokemon
                        nationalPokedexNumber = pokemonDto.nationalPokedexNumber, // No longer unique in DB
                        name = pokemonDto.name,
                        generation = generation,
                        species = species,
                        heightM = pokemonDto.heightM,
                        weightKg = pokemonDto.weightKg,
                        description = pokemonDto.description,
                        sprites = pokemonDto.sprites?.let { objectMapper.writeValueAsString(it) }, // Serialize sprites DTO to JSON string
                        genderRateValue = pokemonDto.genderRateValue,
                        eggCycles = pokemonDto.eggCycles
                        // stats relation is now handled from Stats side
                    )
                } catch (e: Exception) {
                    println("ERROR: Error creating Pokemon entity for ${pokemonDto.name}: ${e.message}")
                    e.printStackTrace()
                    null // Skip on error
                }
            }
        })


        // Save the batch of Pokemon first to ensure they are in the database and managed
        // This is crucial for creating relations that point *to* these Pokemon.
        pokemonRepository.saveAll(pokemonToSave)

        // Create a map from the saved list for efficient lookup by ID
        // This map holds the *managed* entities from the database
        val savedPokemonsLookupMap = pokemonRepository.findAllById(pokemonToSave.mapNotNull { it.id }).associateBy { it.id!! }


        // 2. Process relations based on the saved Pokemon entities and preliminary data
        pokemonJsonList.forEach { pokemonDto ->
            val pokemon = savedPokemonsLookupMap[pokemonDto.id] // Get the managed entity

            if (pokemon == null) {
                println("WARNING: Saved Pokemon with ID ${pokemonDto.id} not found in lookup map after save. Skipping relations for this Pokemon.")
                return@forEach // Skip this pokemonDto
            }

            try {
                // Create Stats (depends on Pokemon ID)
                statsSourceMap[pokemonDto.nationalPokedexNumber]?.let { statsDto ->
                    statsToSave.add(
                        Stats(
                            pokemonId = pokemon.id!!, // Use the Pokemon's ID as PK/FK
                            pokemon = pokemon, // Link the managed Pokemon entity
                            total = statsDto.total,
                            hp = statsDto.hp,
                            attack = statsDto.attack,
                            defense = statsDto.defense,
                            spAtk = statsDto.sp_atk,
                            spDef = statsDto.sp_def,
                            speed = statsDto.speed
                        )
                    )
                } // Warning for missing stats logged in prelim load if needed, or add one here


                // Create Pokemon_Type entries (depends on Pokemon and Type)
                pokemonDto.typeId?.forEach { typeId ->
                    typeMap[typeId]?.let { type ->
                        pokemonTypesToSave.add(PokemonType(pokemon = pokemon, type = type)) // JPA handles composite PK
                    } ?: println("WARNING: Type with ID $typeId not found for Pokemon ${pokemon.name}. Skipping PokemonType association.")
                }


                // Create Pokemon_Ability entries (depends on Pokemon and Ability by ID)
                pokemonDto.abilities?.forEach { abilityDto ->
                    // Find Ability by ID using the abilityMap populated by loadAbilities
                    abilityMap[abilityDto.abilityId]?.let { ability ->
                        pokemonAbilitiesToSave.add(PokemonAbility(pokemon = pokemon, ability = ability, isHidden = abilityDto.isHidden)) // JPA handles composite PK
                    } ?: println("WARNING: Ability with ID ${abilityDto.abilityId} not found for Pokemon ${pokemon.name}. Skipping PokemonAbility association.")
                }


                // Create Pokemon_Egg_Group entries (depends on Pokemon and EggGroup)
                pokemonDto.eggGroupIds?.forEach { eggGroupId ->
                    eggGroupMap[eggGroupId]?.let { eggGroup ->
                        pokemonEggGroupsToSave.add(PokemonEggGroup(pokemon = pokemon, eggGroup = eggGroup)) // JPA handles composite PK
                    } ?: println("WARNING: Egg Group with ID $eggGroupId not found for Pokemon ${pokemon.name}. Skipping PokemonEggGroup association.")
                }

                // Create Pokemon_Weakness entries (depends on Pokemon and Type by Name)
                weaknessesSourceMap[pokemonDto.id]?.let { weaknessDto -> // Lookup weaknesses by pokemon_id from weakness file
                    weaknessDto.weaknesses.forEach { weaknessTypeName ->
                        typeNameMap[weaknessTypeName]?.let { weaknessType ->
                            pokemonWeaknessesToSave.add(PokemonWeakness(pokemon = pokemon, weaknessType = weaknessType)) // JPA handles composite PK
                        } ?: println("WARNING: Weakness Type '$weaknessTypeName' not found for Pokemon ${pokemon.name}. Skipping PokemonWeakness association.")
                    }
                } // Warning for missing weakness data logged in prelim load or add one here


            } catch (e: Exception) {
                println("ERROR: Error processing relations for Pokemon ${pokemon.name}: ${e.message}")
                e.printStackTrace()
            }
        }

        // Save relation batches
        statsRepository.saveAll(statsToSave)
        pokemonTypeRepository.saveAll(pokemonTypesToSave)
        pokemonAbilityRepository.saveAll(pokemonAbilitiesToSave)
        pokemonEggGroupRepository.saveAll(pokemonEggGroupsToSave)
        pokemonWeaknessRepository.saveAll(pokemonWeaknessesToSave)

        // 3. Create Evolution_Link entries (depends on Pokemon, target Pokemon, EvolutionChain)
        // Process the loaded evolution chain structure (evolutionChainsSourceList)
        evolutionChainsSourceList.forEach { chainDto ->
            try {
                // Find the managed EvolutionChain entity using the map populated by loadEvolutionChainsEntities()
                val evolutionChain = evolutionChainMap[chainDto.id]
                if (evolutionChain == null) {
                    println("WARNING: Evolution Chain with ID ${chainDto.id} not found in map. Skipping links for this chain.")
                    return@forEach // Skip this chainDto
                }

                chainDto.chain.forEach { linkDto ->
                    // Find the managed source Pokemon entity from the lookup map of saved Pokemon
                    val sourcePokemon = savedPokemonsLookupMap[linkDto.pokemonId]

                    if (sourcePokemon == null) {
                        println("WARNING: Source Pokemon with ID ${linkDto.pokemonId} not found (source of evolution link) in chain ${chainDto.id}. Skipping this link.")
                        return@forEach // Skip this linkDto
                    }

                    linkDto.evolutionDetails.forEach { detailDto ->
                        // Find the managed target Pokemon entity (can be null) from the lookup map
                        val targetPokemon = detailDto.targetPokemonId?.let { targetId ->
                            savedPokemonsLookupMap[targetId]
                        }

                        // If targetPokemonId is not null but the entity wasn't found, log a warning
                        if (detailDto.targetPokemonId != null && targetPokemon == null) {
                            println("WARNING: Target Pokemon with ID ${detailDto.targetPokemonId} not found (target of evolution link) in chain ${chainDto.id} for source Pokemon ${sourcePokemon.name}. Skipping this evolution detail.")
                            // Continue to the next detailDto in this case, don't skip the whole linkDto
                            return@forEach // Skips the rest of this evolutionDetails loop iteration
                        }

                        // Serialize the condition DTO to JSON string for the JSONB column
                        val conditionJson = detailDto.condition?.let { objectMapper.writeValueAsString(it) }

                        evolutionLinksToSave.add(
                            EvolutionLink(
                                evolutionChain = evolutionChain, // Managed entity
                                pokemon = sourcePokemon, // Managed entity (source)
                                targetPokemon = targetPokemon, // Managed entity (target, can be null)
                                condition = conditionJson // Store the JSON string
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                println("ERROR: Error processing evolution links for chain ${chainDto.id}: ${e.message}")
                e.printStackTrace()
            }
        }
        evolutionLinkRepository.saveAll(evolutionLinksToSave)


        // Return the total count of ALL entities SAVED within THIS function
        return pokemonToSave.size + // Count of main Pokemon entities saved
                statsToSave.size +
                pokemonTypesToSave.size +
                pokemonAbilitiesToSave.size +
                pokemonEggGroupsToSave.size +
                pokemonWeaknessesToSave.size +
                evolutionLinksToSave.size // Count of EvolutionLink entities saved
    }

}