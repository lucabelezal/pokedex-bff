package com.pokedex.bff.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.pokedex.bff.controllers.dtos.* // Importa todos os DTOs do seu pacote de DTOs
import com.pokedex.bff.models.*
import com.pokedex.bff.repositories.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

// Data class to store the result of each loading operation
data class LoadResult(
    val operationName: String,
    val success: Boolean,
    val count: Int = 0,
    val errorMessage: String? = null
)

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
    private val weaknessRepository: WeaknessRepository,
    private val evolutionChainRepository: EvolutionChainRepository,
    private val evolutionDetailRepository: EvolutionDetailRepository,
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
    // Using Input DTOs from AllJsonDto.kt
    private lateinit var statsSourceMap: Map<String, StatsInputDTO> // Map Stats by pokemonNationalPokedexNumber
    private lateinit var weaknessesSourceMap: Map<String, WeaknessInputDTO> // <--- ALTERADO AQUI: Map Weaknesses by pokemonName (String)
    private lateinit var evolutionChainsSourceList: List<EvolutionChainInputDTO> // Store the whole evolution chain structure

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
        results.add(wrapLoadFunction("07_stats.json (Stats Source)") { loadStatsPrelim() })
        results.add(wrapLoadFunction("10_weaknesses.json (Weaknesses Source)") { loadWeaknessesPrelim() })
        results.add(wrapLoadFunction("08_evolution_chains.json (Evolution Chains Source)") { loadEvolutionLinksPrelim() })

        // Load Evolution Chains Entities (needed before loading Pokemon to link EvolutionDetail)
        results.add(wrapLoadFunction("08_evolution_chains.json (Evolution Chains Entities)") { loadEvolutionChainsEntities() })

        // Load main Pokemon data and create all related entities (Stats, Junctions, Links)
        results.add(wrapLoadFunction("09_pokemon.json (Pokémon and Relations)") { loadPokemonAndRelations() })

        // Resumo final
        val successfulLoads = results.filter { it.success }
        val failedLoads = results.filter { !it.success }

        println("\n--- Sumário Final do Carregamento ---")
        println("Operações de Carregamento Concluídas: ${results.size}")
        println("Sucessos: ${successfulLoads.size}")
        successfulLoads.forEach { println("  ✅ ${it.operationName}: Processados ${it.count} registros/relações.") }

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
        val regions = loadJsonFile("01_region.json", RegionInputDTO::class.java).map {
            Region(id = it.id, name = it.name)
        }
        regionRepository.saveAll(regions)
        return regions.size // Return count of entities saved
    }

    private fun loadTypes(): Int {
        val types = loadJsonFile("02_type.json", TypeInputDTO::class.java).map {
            Type(id = it.id, name = it.name, color = it.color)
        }
        typeRepository.saveAll(types)
        typeMap = types.associateBy { it.id!! } // Populate maps for quick lookup
        typeNameMap = types.associateBy { it.name }
        return types.size // Return count of entities saved
    }

    private fun loadEggGroups(): Int {
        val eggGroups = loadJsonFile("03_egg_group.json", EggGroupInputDTO::class.java).map {
            EggGroup(id = it.id, name = it.name)
        }
        eggGroupRepository.saveAll(eggGroups)
        eggGroupMap = eggGroups.associateBy { it.id!! } // Populate map
        return eggGroups.size // Return count of entities saved
    }

    private fun loadSpecies(): Int {
        val speciesList = loadJsonFile("04_species.json", SpeciesInputDTO::class.java).map {
            Species(id = it.id, nationalPokedexNumber = it.nationalPokedexNumber, name = it.name, species_en = it.species_en, species_pt = it.species_pt)
        }
        speciesRepository.saveAll(speciesList)
        speciesMap = speciesList.associateBy { it.id!! } // Populate map
        return speciesList.size // Return count of entities saved
    }

    private fun loadGenerations(): Int {
        val generations = loadJsonFile("05_generation.json", GenerationInputDTO::class.java)
        val regionMap = regionRepository.findAll().associateBy { it.id!! } // Load regions into a temporary map

        val generationsToSave = generations.mapNotNull { genDto ->
            // Assume que GenerationInputDTO tem um campo 'regionId' para o ID da região
            val region = genDto.regionId?.let { regionMap[it] }
            if (region != null) {
                Generation(id = genDto.id, name = genDto.name, region = region)
            } else {
                println("WARNING: Região com ID ${genDto.regionId} não encontrada para a Geração ${genDto.name}. Pulando esta geração.")
                null // Skip this generation if region not found
            }
        }

        generationRepository.saveAll(generationsToSave)
        generationMap = generationsToSave.associateBy { it.id!! } // Populate map with saved entities
        return generationsToSave.size // Return count of entities saved
    }

    private fun loadAbilities(): Int {
        val abilities = loadJsonFile("06_ability.json", AbilityInputDTO::class.java)
        val generationMap = this.generationMap // Use the previously populated generation map

        val abilitiesToSave = abilities.map { abilityDto ->
            // introducedGenerationId is nullable, so generation can be null
            val generation = abilityDto.introducedGenerationId?.let { generationMap[it] }
            Ability(id = abilityDto.id, name = abilityDto.name, description = abilityDto.description, introducedGeneration = generation)
        }

        abilityRepository.saveAll(abilitiesToSave)
        abilityMap = abilitiesToSave.associateBy { it.id!! } // Populate map with saved entities
        return abilitiesToSave.size // Return count of entities saved
    }

    private fun loadEvolutionChainsEntities(): Int {
        val evolutionChainsJson = loadJsonFile("08_evolution_chains.json", EvolutionChainInputDTO::class.java)

        val evolutionChainsToSave = evolutionChainsJson.map { chainDto ->
            EvolutionChain(id = chainDto.id)
        }

        evolutionChainRepository.saveAll(evolutionChainsToSave)
        evolutionChainMap = evolutionChainsToSave.associateBy { it.id!! } // Populate map with saved entities
        return evolutionChainsToSave.size // Return count of entities saved
    }

    // --- Preliminary Load Functions (load JSON into maps/lists for later use) ---
    // These functions return the count of items READ from JSON

    private fun loadStatsPrelim(): Int {
        // Using StatsInputDTO as the source DTO
        val statsList = loadJsonFile("07_stats.json", StatsInputDTO::class.java)
        statsSourceMap = statsList.associateBy { it.pokemonNationalPokedexNumber }
        return statsList.size // Return count of items READ from JSON
    }

    private fun loadWeaknessesPrelim(): Int {
        // Using WeaknessInputDTO for the source JSON
        val weaknessesList = loadJsonFile("10_weaknesses.json", WeaknessInputDTO::class.java)
        // <--- ALTERADO AQUI: Mapeando por pokemonName (que é String e parece sempre presente)
        weaknessesSourceMap = weaknessesList.associateBy { it.pokemonName!! } // Assegura que pokemonName não é nulo para ser chave
        return weaknessesList.size // Return count of items READ from JSON
    }

    private fun loadEvolutionLinksPrelim(): Int {
        // Using EvolutionChainInputDTO for the source JSON
        evolutionChainsSourceList = loadJsonFile("08_evolution_chains.json", EvolutionChainInputDTO::class.java)
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
        val pokemonJsonList = loadJsonFile("09_pokemon.json", PokemonInputDTO::class.java)

        val pokemonToSave = mutableListOf<Pokemon>()
        val statsToSave = mutableListOf<Stats>()
        val pokemonTypesToSave = mutableListOf<PokemonType>()
        val pokemonAbilitiesToSave = mutableListOf<PokemonAbility>()
        val pokemonEggGroupsToSave = mutableListOf<PokemonEggGroup>()
        val weaknessesToSave = mutableListOf<Weakness>()
        val evolutionDetailsToSave = mutableListOf<EvolutionDetail>()

        // 1. Create and collect main Pokemon entities and their Stats
        pokemonJsonList.forEach { pokemonDto ->
            val generation = generationMap[pokemonDto.generationId]
            val species = speciesMap[pokemonDto.speciesId]

            if (generation == null) {
                println("WARNING: Generation with ID ${pokemonDto.generationId} not found for Pokemon ${pokemonDto.name}. Skipping this Pokemon.")
                return@forEach // Skip this pokemonDto
            } else if (species == null) {
                println("WARNING: Species with ID ${pokemonDto.speciesId} not found for Pokemon ${pokemonDto.name}. Skipping this Pokemon.")
                return@forEach // Skip to next pokemonDto
            }

            try {
                // Create Stats entity *before* Pokemon, if it exists
                val statsDto = statsSourceMap[pokemonDto.nationalPokedexNumber]
                val newStats = statsDto?.let {
                    Stats(
                        // id não é passado, pois é auto-gerado pelo banco de dados
                        pokemonNationalPokedexNumber = it.pokemonNationalPokedexNumber,
                        pokemonName = it.pokemonName.toString(), // toString() for nullable String?
                        total = it.total,
                        hp = it.hp,
                        attack = it.attack,
                        defense = it.defense,
                        spAtk = it.spAtk,
                        spDef = it.spDef,
                        speed = it.speed
                    )
                } ?: run {
                    println("WARNING: Stats data for Pokemon ${pokemonDto.name} (National Pokedex Number: ${pokemonDto.nationalPokedexNumber}) not found. Stats will be null.")
                    null
                }
                if (newStats != null) {
                    statsToSave.add(newStats)
                }

                // Create Pokemon entity
                val pokemon = Pokemon(
                    id = pokemonDto.id,
                    nationalPokedexNumber = pokemonDto.nationalPokedexNumber,
                    name = pokemonDto.name,
                    generation = generation,
                    species = species,
                    heightM = pokemonDto.heightM,
                    weightKg = pokemonDto.weightKg,
                    description = pokemonDto.description,
                    // Sprites are serialized as JSON string
                    sprites = pokemonDto.sprites?.let { objectMapper.writeValueAsString(it) },
                    gender_rate_value = pokemonDto.genderRateValue,
                    eggCycles = pokemonDto.eggCycles,
                    stats = newStats, // Link the Stats entity created above
                    evolutionChain = null // Will be set later when creating evolution details
                )
                pokemonToSave.add(pokemon)

            } catch (e: Exception) {
                println("ERROR: Error creating Pokemon entity for ${pokemonDto.name}: ${e.message}")
                e.printStackTrace()
            }
        }

        // Save Stats first if they are independent or have a one-to-one relationship
        statsRepository.saveAll(statsToSave)

        // Save the batch of Pokemon after Stats, so they can reference managed Stats entities.
        pokemonRepository.saveAll(pokemonToSave)

        // Create a map from the saved Pokemon list for efficient lookup by ID
        // This map holds the *managed* entities from the database
        val savedPokemonsLookupMap = pokemonRepository.findAllById(pokemonToSave.map { it.id }).associateBy { it.id }
        // Também um lookup por nome para fraquezas, já que o WeaknessSourceMap usa o nome
        val savedPokemonsNameLookupMap = pokemonRepository.findAllById(pokemonToSave.map { it.id }).associateBy { it.name }


        // 2. Process relations for saved Pokemon
        pokemonJsonList.forEach { pokemonDto ->
            val pokemon = savedPokemonsLookupMap[pokemonDto.id]

            if (pokemon == null) {
                println("WARNING: Saved Pokemon with ID ${pokemonDto.id} not found in lookup map after save. Skipping relations for this Pokemon.")
                return@forEach // Skip this pokemonDto
            }

            try {
                // Create Pokemon_Type entries
                pokemonDto.typeIds?.forEach { typeId ->
                    typeMap[typeId]?.let { type ->
                        pokemonTypesToSave.add(PokemonType(pokemon = pokemon, type = type))
                    } ?: println("WARNING: Type with ID $typeId not found for Pokemon ${pokemon.name}. Skipping PokemonType association.")
                }

                // Create Pokemon_Ability entries
                pokemonDto.abilities?.forEach { abilityDto ->
                    abilityMap[abilityDto.abilityId]?.let { ability ->
                        pokemonAbilitiesToSave.add(PokemonAbility(pokemon = pokemon, ability = ability, isHidden = abilityDto.isHidden))
                    } ?: println("WARNING: Ability with ID ${abilityDto.abilityId} not found for Pokemon ${pokemon.name}. Skipping PokemonAbility association.")
                }

                // Create Pokemon_Egg_Group entries
                pokemonDto.eggGroupIds?.forEach { eggGroupId ->
                    eggGroupMap[eggGroupId]?.let { eggGroup ->
                        pokemonEggGroupsToSave.add(PokemonEggGroup(pokemon = pokemon, eggGroup = eggGroup))
                    } ?: println("WARNING: Egg Group with ID $eggGroupId not found for Pokemon ${pokemon.name}. Skipping PokemonEggGroup association.")
                }

                // Create Weakness entries
                // <--- ALTERADO AQUI: Usando savedPokemonsNameLookupMap para buscar o Pokemon pelo nome
                // e weaknessesSourceMap que agora é mapeado por nome
                val pokemonForWeakness = savedPokemonsNameLookupMap[pokemonDto.name]
                if (pokemonForWeakness != null) {
                    weaknessesSourceMap[pokemonForWeakness.name]?.let { weaknessSourceDto ->
                        weaknessSourceDto.weaknesses.forEach { weaknessTypeName ->
                            typeNameMap[weaknessTypeName]?.let { type -> // Ensure the type actually exists in the DB
                                weaknessesToSave.add(Weakness(pokemon = pokemonForWeakness, pokemon_name = pokemonForWeakness.name, weakness_type = type.name))
                            } ?: println("WARNING: Weakness Type '$weaknessTypeName' not found for Pokemon ${pokemonForWeakness.name}. Skipping Weakness association.")
                        }
                    } ?: println("WARNING: No weakness data found for Pokemon ${pokemonForWeakness.name}.")
                } else {
                    println("WARNING: Saved Pokemon with name ${pokemonDto.name} not found in name lookup map after save. Skipping weakness relations for this Pokemon.")
                }


            } catch (e: Exception) {
                println("ERROR: Error processing relations for Pokemon ${pokemon.name}: ${e.message}")
                e.printStackTrace()
            }
        }

        // Save relation batches
        pokemonTypeRepository.saveAll(pokemonTypesToSave)
        pokemonAbilityRepository.saveAll(pokemonAbilitiesToSave)
        pokemonEggGroupRepository.saveAll(pokemonEggGroupsToSave)
        weaknessRepository.saveAll(weaknessesToSave)


        // 3. Create Evolution_Detail entries
        evolutionChainsSourceList.forEach { chainDto ->
            val evolutionChain = evolutionChainMap[chainDto.id]
            if (evolutionChain == null) {
                println("WARNING: Evolution Chain with ID ${chainDto.id} not found. Skipping evolution details for this chain.")
                return@forEach // Skip this chainDto
            }

            chainDto.chain.forEach { linkDto ->
                val sourcePokemon = savedPokemonsLookupMap[linkDto.pokemonId]
                if (sourcePokemon == null) {
                    println("WARNING: Source Pokemon with ID ${linkDto.pokemonId} not found for evolution chain ${chainDto.id}. Skipping link.")
                    return@forEach // Skip this linkDto
                }

                linkDto.evolutionDetails.forEach { detailDto ->
                    val targetPokemon = detailDto.targetPokemonId?.let { savedPokemonsLookupMap[it] }

                    if (detailDto.targetPokemonId != null && targetPokemon == null) {
                        println("WARNING: Target Pokemon with ID ${detailDto.targetPokemonId} not found for source ${sourcePokemon.name} in chain ${chainDto.id}. Skipping this evolution detail.")
                        return@forEach // Skip this specific detail
                    }

                    // Serialize condition to JSON string
                    val conditionJson = detailDto.condition?.let { objectMapper.writeValueAsString(it) }

                    evolutionDetailsToSave.add(
                        EvolutionDetail(
                            evolutionChain = evolutionChain,
                            pokemon = sourcePokemon,
                            targetPokemon = targetPokemon,
                            targetPokemonName = targetPokemon?.name,
                            condition_type = detailDto.condition?.trigger, // Using 'trigger' as condition_type
                            condition_value = conditionJson // Storing the full condition JSON in condition_value
                        )
                    )
                }
            }
        }
        evolutionDetailRepository.saveAll(evolutionDetailsToSave)

        // Return the total count of ALL entities SAVED within THIS function
        return pokemonToSave.size +
                statsToSave.size +
                pokemonTypesToSave.size +
                pokemonAbilitiesToSave.size +
                pokemonEggGroupsToSave.size +
                weaknessesToSave.size +
                evolutionDetailsToSave.size
    }
}