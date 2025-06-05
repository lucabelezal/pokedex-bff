package com.pokedex.bff.services

import com.pokedex.bff.controllers.dtos.*
import com.pokedex.bff.models.*
import com.pokedex.bff.repositories.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Value

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

    override fun run(vararg args: String?) {
        if (pokemonRepository.count() == 0L) {
            println("Iniciando o carregamento dos dados de Pokémon a partir de '$jsonDataLocation' no classpath...")
            loadData()
            println("Carregamento de dados de Pokémon concluído.")
        } else {
            println("Dados de Pokémon já existem no banco de dados. Pulando o carregamento inicial.")
        }
    }

    @Transactional
    fun loadData() {
        loadRegions()
        loadTypes()
        loadEggGroups()
        loadSpecies()
        loadGenerations()
        loadAbilities()
        loadStats()
        loadPokemon()
        loadPokemonTypes()
        loadPokemonAbilities()
        loadPokemonEggGroups()
        loadPokemonWeaknesses()
        loadEvolutionChainsAndLinks()
    }

    private fun <T> loadJsonFile(fileName: String, type: Class<T>): List<T> {
        return try {
            // Concatena o caminho base com o nome do arquivo
            val fullPath = "$jsonDataLocation$fileName"
            val resource = ClassPathResource(fullPath).inputStream
            objectMapper.readValue(resource, objectMapper.typeFactory.constructCollectionType(List::class.java, type))
        } catch (e: Exception) {
            println("Erro ao carregar $fileName do caminho '$jsonDataLocation': ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    private fun loadRegions() {
        val regions = loadJsonFile("01_region.json", Region::class.java)
        regionRepository.saveAll(regions)
        println("Carregadas ${regions.size} regiões.")
    }

    private fun loadTypes() {
        val types = loadJsonFile("02_type.json", Type::class.java)
        typeRepository.saveAll(types)
        println("Carregados ${types.size} tipos.")
    }

    private fun loadEggGroups() {
        val eggGroups = loadJsonFile("03_egg_group.json", EggGroup::class.java)
        eggGroupRepository.saveAll(eggGroups)
        println("Carregados ${eggGroups.size} grupos de ovos.")
    }

    private fun loadSpecies() {
        val speciesList = loadJsonFile("04_species.json", Species::class.java)
        speciesRepository.saveAll(speciesList)
        println("Carregadas ${speciesList.size} espécies.")
    }

    private fun loadGenerations() {
        val generationsJson = loadJsonFile("05_generation.json", Map::class.java) as List<Map<String, Any>>
        val generations = generationsJson.mapNotNull { genData ->
            val regionId = (genData["regiaoId"] as Number).toInt()
            val region = regionRepository.findById(regionId).orElse(null)
            if (region != null) {
                Generation(
                    id = (genData["id"] as Number).toInt(),
                    name = genData["name"] as String,
                    region = region
                )
            } else {
                println("Região com ID $regionId não encontrada para Geração ${genData["name"]}. Pulando.")
                null
            }
        }
        generationRepository.saveAll(generations)
        println("Carregadas ${generations.size} gerações.")
    }

    private fun loadAbilities() {
        val abilitiesJson = loadJsonFile("06_ability.json", Map::class.java) as List<Map<String, Any>>
        val abilities = abilitiesJson.mapNotNull { abilityData ->
            val introducedGenId = (abilityData["introducedGenerationId"] as Number?)?.toInt()
            val generation = introducedGenId?.let { generationRepository.findById(it).orElse(null) }

            Ability(
                name = abilityData["name"] as String,
                description = abilityData["description"] as String?,
                introducedGeneration = generation
            )
        }
        abilityRepository.saveAll(abilities)
        println("Carregadas ${abilities.size} habilidades.")
    }

    private fun loadStats() {
        val statsList = loadJsonFile("07_stats.json", Stats::class.java)
        statsRepository.saveAll(statsList)
        println("Carregados ${statsList.size} conjuntos de stats.")
    }

    private fun loadPokemon() {
        val pokemonJsonList = loadJsonFile("09_pokemon.json", PokemonJsonDto::class.java)

        val pokemonList = pokemonJsonList.mapNotNull { pokemonDto ->
            val generation = generationRepository.findById(pokemonDto.generationId).orElse(null)
            val species = speciesRepository.findById(pokemonDto.speciesId).orElse(null)
            val stats = pokemonDto.statsId?.let { statsRepository.findById(it).orElse(null) }

            if (generation == null) {
                println("Geração com ID ${pokemonDto.generationId} não encontrada para Pokemon ${pokemonDto.name}. Pulando.")
                null
            } else if (species == null) {
                println("Espécie com ID ${pokemonDto.speciesId} não encontrada para Pokemon ${pokemonDto.name}. Pulando.")
                null
            } else {
                Pokemon(
                    id = pokemonDto.id,
                    nationalPokedexNumber = pokemonDto.nationalPokedexNumber,
                    name = pokemonDto.name,
                    generation = generation,
                    species = species,
                    heightM = pokemonDto.heightM,
                    weightKg = pokemonDto.weightKg,
                    description = pokemonDto.description,
                    sprites = pokemonDto.sprites?.let { objectMapper.writeValueAsString(it) },
                    genderRateValue = pokemonDto.genderRateValue,
                    eggCycles = pokemonDto.eggCycles,
                    stats = stats
                )
            }
        }
        pokemonRepository.saveAll(pokemonList)
        println("Carregados ${pokemonList.size} Pokémon principais.")
    }

    private fun loadPokemonTypes() {
        val pokemonJsonList = loadJsonFile("09_pokemon.json", PokemonJsonDto::class.java)
        val pokemonTypesToSave = mutableListOf<PokemonType>()

        pokemonJsonList.forEach { pokemonDto ->
            val pokemon = pokemonRepository.findById(pokemonDto.id).orElse(null)
            if (pokemon != null && pokemonDto.typeId != null) {
                pokemonDto.typeId.forEach { typeId ->
                    val type = typeRepository.findById(typeId).orElse(null)
                    if (type != null) {
                        pokemonTypesToSave.add(PokemonType(pokemon = pokemon, type = type))
                    } else {
                        println("Tipo com ID $typeId não encontrado para Pokémon ${pokemon.name}. Pulando.")
                    }
                }
            }
        }
        pokemonTypeRepository.saveAll(pokemonTypesToSave)
        println("Carregadas ${pokemonTypesToSave.size} associações Pokémon-Tipo.")
    }

    private fun loadPokemonAbilities() {
        val pokemonJsonList = loadJsonFile("09_pokemon.json", PokemonJsonDto::class.java)
        val pokemonAbilitiesToSave = mutableListOf<PokemonAbility>()

        pokemonJsonList.forEach { pokemonDto ->
            val pokemon = pokemonRepository.findById(pokemonDto.id).orElse(null)
            if (pokemon != null && pokemonDto.abilities != null) {
                pokemonDto.abilities.forEach { abilityDto ->
                    val ability = abilityRepository.findById(abilityDto.abilityId).orElse(null)
                    if (ability != null) {
                        pokemonAbilitiesToSave.add(PokemonAbility(pokemon = pokemon, ability = ability, isHidden = abilityDto.isHidden))
                    } else {
                        println("Habilidade com nome '${abilityDto.abilityId}' não encontrada para Pokémon ${pokemon.name}. Pulando.")
                    }
                }
            }
        }
        pokemonAbilityRepository.saveAll(pokemonAbilitiesToSave)
        println("Carregadas ${pokemonAbilitiesToSave.size} associações Pokémon-Habilidade.")
    }

    private fun loadPokemonEggGroups() {
        val pokemonJsonList = loadJsonFile("09_pokemon.json", PokemonJsonDto::class.java)
        val pokemonEggGroupsToSave = mutableListOf<PokemonEggGroup>()

        pokemonJsonList.forEach { pokemonDto ->
            val pokemon = pokemonRepository.findById(pokemonDto.id).orElse(null)
            if (pokemon != null && pokemonDto.eggGroupIds != null) {
                pokemonDto.eggGroupIds.forEach { eggGroupId ->
                    val eggGroup = eggGroupRepository.findById(eggGroupId).orElse(null)
                    if (eggGroup != null) {
                        pokemonEggGroupsToSave.add(PokemonEggGroup(pokemon = pokemon, eggGroup = eggGroup))
                    } else {
                        println("Grupo de Ovos com ID $eggGroupId não encontrado para Pokémon ${pokemon.name}. Pulando.")
                    }
                }
            }
        }
        pokemonEggGroupRepository.saveAll(pokemonEggGroupsToSave)
        println("Carregadas ${pokemonEggGroupsToSave.size} associações Pokémon-Grupo de Ovos.")
    }

    private fun loadPokemonWeaknesses() {
        val weaknessesJsonList = loadJsonFile("10_weaknesses.json", WeaknessJsonDto::class.java)
        val pokemonWeaknessesToSave = mutableListOf<PokemonWeakness>()

        weaknessesJsonList.forEach { weaknessDto ->
            val pokemon = pokemonRepository.findById(weaknessDto.pokemonId).orElse(null)
            if (pokemon != null && weaknessDto.weaknesses.isNotEmpty()) {
                weaknessDto.weaknesses.forEach { weaknessTypeName ->
                    val weaknessType = typeRepository.findByName(weaknessTypeName)
                    if (weaknessType != null) {
                        pokemonWeaknessesToSave.add(PokemonWeakness(pokemon = pokemon, weaknessType = weaknessType))
                    } else {
                        println("Tipo de fraqueza '$weaknessTypeName' não encontrado para Pokémon ${pokemon.name}. Pulando.")
                    }
                }
            }
        }
        pokemonWeaknessRepository.saveAll(pokemonWeaknessesToSave)
        println("Carregadas ${pokemonWeaknessesToSave.size} associações Pokémon-Fraqueza.")
    }


    private fun loadEvolutionChainsAndLinks() {
        val evolutionChainsJson = loadJsonFile("08_evolution_chains.json", EvolutionChainJsonDto::class.java)

        evolutionChainsJson.forEach { chainDto ->
            val evolutionChain = EvolutionChain(id = chainDto.id)
            evolutionChainRepository.save(evolutionChain)

            chainDto.chain.forEach { linkDto ->
                val pokemon = pokemonRepository.findById(linkDto.pokemonId).orElse(null)

                if (pokemon == null) {
                    println("Pokemon com ID ${linkDto.pokemonId} não encontrado para EvolutionLink na cadeia ${chainDto.id}. Pulando este link de evolução.")
                    return@forEach // Pula este linkDto e vai para o próximo na cadeia
                }

                // Itera sobre os detalhes de evolução dentro do linkDto
                linkDto.evolutionDetails.forEach { detailDto ->
                    val targetPokemon = detailDto.targetPokemonId?.let { pokemonRepository.findById(it).orElse(null) }

                    // Verifica se o targetPokemon é nulo APENAS se targetPokemonId NÃO FOR nulo no JSON.
                    // targetPokemonId pode ser nulo no JSON se for o último na cadeia.
                    if (detailDto.targetPokemonId != null && targetPokemon == null) {
                        println("Target Pokemon com ID ${detailDto.targetPokemonId} não encontrado para EvolutionLink na cadeia ${chainDto.id}. Pulando este detalhe de evolução.")
                        return@forEach // Pula este detailDto e vai para o próximo detalhe de evolução
                    }

                    val evolutionLink = EvolutionLink(
                        evolutionChain = evolutionChain,
                        pokemon = pokemon,
                        targetPokemon = targetPokemon, // Agora acessado corretamente de detailDto
                        conditionType = detailDto.condition?.type,
                        conditionValue = detailDto.condition?.value
                    )
                    evolutionLinkRepository.save(evolutionLink)
                }
            }
        }
        println("Carregadas ${evolutionChainsJson.size} cadeias de evolução e seus links.")
    }
}