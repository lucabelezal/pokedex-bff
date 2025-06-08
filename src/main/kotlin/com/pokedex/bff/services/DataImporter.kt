package com.pokedex.bff.services

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.pokedex.bff.models.* // Importa todas as classes do pacote models
import com.pokedex.bff.utils.JsonFile // Importa o novo enum JsonFile
import java.io.IOException

// DTOs para desserialização do JSON
data class RegionDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String
)

data class TypeDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("color")
    val color: String?
)

data class EggGroupDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String
)

data class SpeciesDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("pokemon_number")
    val pokemonNumber: String?,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("species_en")
    val speciesEn: String?,
    @JsonProperty("species_pt")
    val speciesPt: String?
)

data class GenerationDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("region_id")
    val regionId: Long
)

data class AbilityDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("introduced_generation_id")
    val introducedGenerationId: Long
)

data class StatsDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("total")
    val total: Int,
    @JsonProperty("hp")
    val hp: Int,
    @JsonProperty("attack")
    val attack: Int,
    @JsonProperty("defense")
    val defense: Int,
    @JsonProperty("sp_atk")
    val spAtk: Int,
    @JsonProperty("sp_def")
    val spDef: Int,
    @JsonProperty("speed")
    val speed: Int
)

// DTO simplificado para EvolutionChain para armazenar a cadeia completa como JSONB
data class EvolutionChainDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("chain")
    val chainData: Any // Usa Any para capturar a estrutura JSON completa da cadeia
)

// DTO para representar a habilidade dentro do Pokémon, incluindo is_hidden
data class PokemonAbilityDto(
    @JsonProperty("ability_id")
    val abilityId: Long,
    @JsonProperty("is_hidden")
    val isHidden: Boolean
)

data class PokemonDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("number")
    val number: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("height")
    val height: Double,
    @JsonProperty("weight")
    val weight: Double,
    @JsonProperty("stats_id")
    val statsId: Long,
    @JsonProperty("generation_id")
    val generationId: Long,
    @JsonProperty("species_id")
    val speciesId: Long,
    @JsonProperty("region_id")
    val regionId: Long?,
    @JsonProperty("evolution_chain_id")
    val evolutionChainId: Long,
    @JsonProperty("gender_rate_value")
    val genderRateValue: Int,
    @JsonProperty("egg_cycles")
    val eggCycles: Int?,
    @JsonProperty("egg_group_ids")
    val eggGroupIds: List<Long>,
    @JsonProperty("type_ids")
    val typeIds: List<Long>,
    @JsonProperty("abilities")
    val abilities: List<PokemonAbilityDto>, // Alterado para PokemonAbilityDto
    @JsonProperty("sprites")
    val sprites: SpritesDto
)

// Classes DTO para Sprites com funções de mapeamento para as classes de modelo
data class SpritesDto(
    @JsonProperty("back_default")
    val backDefault: String?,
    @JsonProperty("back_female")
    val backFemale: String?,
    @JsonProperty("back_shiny")
    val backShiny: String?,
    @JsonProperty("back_shiny_female")
    val backShinyFemale: String?,
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String?,
    @JsonProperty("other")
    val other: OtherSpritesDto?
) {
    fun toModel(): Sprites {
        return Sprites(
            backDefault = this.backDefault,
            backFemale = this.backFemale,
            backShiny = this.backShiny,
            backShinyFemale = this.backShinyFemale,
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale,
            frontShiny = this.frontShiny,
            frontShinyFemale = this.frontShinyFemale,
            other = this.other?.toModel()
        )
    }
}

data class OtherSpritesDto(
    @JsonProperty("dream_world")
    val dreamWorld: DreamWorldDto?,
    @JsonProperty("home")
    val home: HomeDto?,
    @JsonProperty("official-artwork")
    val officialArtwork: OfficialArtworkDto?,
    @JsonProperty("showdown")
    val showdown: ShowdownDto?
) {
    fun toModel(): OtherSprites {
        return OtherSprites(
            dreamWorld = this.dreamWorld?.toModel(),
            home = this.home?.toModel(),
            officialArtwork = this.officialArtwork?.toModel(),
            showdown = this.showdown?.toModel()
        )
    }
}

data class DreamWorldDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?
) {
    fun toModel(): DreamWorldSprites {
        return DreamWorldSprites(
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale
        )
    }
}

data class HomeDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String?
) {
    fun toModel(): HomeSprites {
        return HomeSprites(
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale,
            frontShiny = this.frontShiny,
            frontShinyFemale = this.frontShinyFemale
        )
    }
}

data class OfficialArtworkDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?
) {
    fun toModel(): OfficialArtworkSprites {
        return OfficialArtworkSprites(
            frontDefault = this.frontDefault,
            frontShiny = this.frontShiny
        )
    }
}

data class ShowdownDto(
    @JsonProperty("back_default")
    val backDefault: String?,
    @JsonProperty("back_female")
    val backFemale: String?,
    @JsonProperty("back_shiny")
    val backShiny: String?,
    @JsonProperty("back_shiny_female")
    val backShinyFemale: String?,
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String?
) {
    fun toModel(): ShowdownSprites {
        return ShowdownSprites(
            backDefault = this.backDefault,
            backFemale = this.backFemale,
            backShiny = this.backShiny,
            backShinyFemale = this.backShinyFemale,
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale,
            frontShiny = this.frontShiny,
            frontShinyFemale = this.frontShinyFemale
        )
    }
}


data class WeaknessDto(
    @JsonProperty("id")
    val id: Long?,
    @JsonProperty("pokemon_id")
    val pokemonId: Long?,
    @JsonProperty("pokemon_name")
    val pokemonName: String,
    @JsonProperty("weaknesses")
    val weaknesses: List<String>
)

// Repositórios JPA
interface RegionRepository : JpaRepository<Region, Long>
interface TypeRepository : JpaRepository<Type, Long> {
    fun findByNameIn(names: List<String>): List<Type>
}
interface EggGroupRepository : JpaRepository<EggGroup, Long>
interface GenerationRepository : JpaRepository<Generation, Long>
interface AbilityRepository : JpaRepository<Ability, Long>
interface SpeciesRepository : JpaRepository<Species, Long>
interface StatsRepository : JpaRepository<Stats, Long>
interface EvolutionChainRepository : JpaRepository<EvolutionChain, Long>
interface PokemonRepository : JpaRepository<Pokemon, Long> {
    fun findByName(name: String): Pokemon?
    fun findByIdIn(ids: List<Long>): List<Pokemon>
}
interface PokemonAbilityRepository : JpaRepository<PokemonAbility, Long>


// Classe para contar sucessos e erros por operação de importação
data class ImportCounts(var success: Int = 0, var errors: Int = 0)

// Serviço de Importação
@Service
class PokemonImportService(
    private val regionRepository: RegionRepository,
    private val typeRepository: TypeRepository,
    private val eggGroupRepository: EggGroupRepository,
    private val generationRepository: GenerationRepository,
    private val abilityRepository: AbilityRepository,
    private val speciesRepository: SpeciesRepository,
    private val statsRepository: StatsRepository,
    private val evolutionChainRepository: EvolutionChainRepository,
    private val pokemonRepository: PokemonRepository,
    private val pokemonAbilityRepository: PokemonAbilityRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(PokemonImportService::class.java)

    private fun <T> loadJson(filePath: String, typeReference: TypeReference<T>): T {
        try {
            val resource = ClassPathResource(filePath)
            return resource.inputStream.use { objectMapper.readValue(it, typeReference) }
        } catch (e: IOException) {
            logger.error("Failed to load JSON from $filePath: ${e.message}", e)
            throw RuntimeException("Error loading data from $filePath", e)
        }
    }

    // Retorna ImportCounts para que os totais possam ser acumulados
    private fun <T : Any, D> importSimpleData(
        dtos: List<D>,
        repository: JpaRepository<T, Long>,
        transform: (D) -> T
    ): ImportCounts {
        val counts = ImportCounts()
        dtos.forEach { dto ->
            try {
                val entity = transform(dto)
                repository.save(entity)
                counts.success++
            } catch (e: Exception) {
                counts.errors++
                // Log individual error for debugging, but don't stop the process
                logger.error("Error importing data with value $dto: ${e.message}", e)
            }
        }
        return counts
    }

    // Função auxiliar para logar os resultados de importação com emojis
    private fun logImportResult(entityName: String, counts: ImportCounts) {
        val statusEmoji = if (counts.errors == 0) "✅" else "❌"
        logger.info("  $statusEmoji Importação de $entityName concluída. Sucessos: ${counts.success}, Falhas: ${counts.errors}")
    }

    @Transactional
    fun importAll() {
        logger.info("====== 🚀 INICIANDO IMPORTAÇÃO DOS DADOS DA POKEDEX 🚀 ======")

        var totalSuccess = 0
        var totalErrors = 0

        logger.info("Iniciando importação de Regiões...")
        val regionsCounts = importSimpleData(loadJson(JsonFile.REGIONS.filePath, object : TypeReference<List<RegionDto>>() {}), regionRepository) { dto -> Region(id = dto.id, name = dto.name) }
        logImportResult("Regiões", regionsCounts)
        totalSuccess += regionsCounts.success
        totalErrors += regionsCounts.errors

        logger.info("Iniciando importação de Tipos...")
        val typesCounts = importSimpleData(loadJson(JsonFile.TYPES.filePath, object : TypeReference<List<TypeDto>>() {}), typeRepository) { dto -> Type(id = dto.id, name = dto.name, color = dto.color) }
        logImportResult("Tipos", typesCounts)
        totalSuccess += typesCounts.success
        totalErrors += typesCounts.errors

        logger.info("Iniciando importação de Grupos de Ovos...")
        val eggGroupsCounts = importSimpleData(loadJson(JsonFile.EGG_GROUPS.filePath, object : TypeReference<List<EggGroupDto>>() {}), eggGroupRepository) { dto -> EggGroup(id = dto.id, name = dto.name) }
        logImportResult("Grupos de Ovos", eggGroupsCounts)
        totalSuccess += eggGroupsCounts.success
        totalErrors += eggGroupsCounts.errors

        logger.info("Iniciando importação de Gerações...")
        val allRegionsMapForGenerations = regionRepository.findAll().associateBy { it.id } // Renomeado
        val generationsCounts = importSimpleData(loadJson(JsonFile.GENERATIONS.filePath, object : TypeReference<List<GenerationDto>>() {}), generationRepository) { dto ->
            val region = allRegionsMapForGenerations[dto.regionId] // Usando o mapa renomeado
                ?: throw IllegalArgumentException("Region with ID ${dto.regionId} not found for Generation ${dto.name}")
            Generation(id = dto.id, name = dto.name, region = region)
        }
        logImportResult("Gerações", generationsCounts)
        totalSuccess += generationsCounts.success
        totalErrors += generationsCounts.errors

        logger.info("Iniciando importação de Habilidades...")
        val allGenerationsMapForAbilities = generationRepository.findAll().associateBy { it.id } // Renomeado
        val abilitiesCounts = importSimpleData(loadJson(JsonFile.ABILITIES.filePath, object : TypeReference<List<AbilityDto>>() {}), abilityRepository) { dto ->
            val generation = allGenerationsMapForAbilities[dto.introducedGenerationId] // Usando o mapa renomeado
                ?: throw IllegalArgumentException("Generation with ID ${dto.introducedGenerationId} not found for Ability ${dto.name}")
            Ability(id = dto.id, name = dto.name, description = dto.description, introducedGeneration = generation)
        }
        logImportResult("Habilidades", abilitiesCounts)
        totalSuccess += abilitiesCounts.success
        totalErrors += abilitiesCounts.errors

        logger.info("Iniciando importação de Espécies...")
        val speciesCounts = importSimpleData(loadJson(JsonFile.SPECIES.filePath, object : TypeReference<List<SpeciesDto>>() {}), speciesRepository) { dto ->
            Species(id = dto.id, name = dto.name, pokemon_number = dto.pokemonNumber, speciesEn = dto.speciesEn, speciesPt = dto.speciesPt)
        }
        logImportResult("Espécies", speciesCounts)
        totalSuccess += speciesCounts.success
        totalErrors += speciesCounts.errors

        logger.info("Iniciando importação de Estatísticas...")
        val statsCounts = importSimpleData(loadJson(JsonFile.STATS.filePath, object : TypeReference<List<StatsDto>>() {}), statsRepository) { dto ->
            Stats(id = dto.id, total = dto.total, hp = dto.hp, attack = dto.attack, defense = dto.defense, spAtk = dto.spAtk, spDef = dto.spDef, speed = dto.speed)
        }
        logImportResult("Estatísticas", statsCounts)
        totalSuccess += statsCounts.success
        totalErrors += statsCounts.errors

        logger.info("Iniciando importação de Cadeias de Evolução...")
        val evolutionChainsCounts = importSimpleData(loadJson(JsonFile.EVOLUTION_CHAINS.filePath, object : TypeReference<List<EvolutionChainDto>>() {}), evolutionChainRepository) { dto ->
            val chainDataJsonString = objectMapper.writeValueAsString(dto.chainData)
            EvolutionChain(id = dto.id, chainData = chainDataJsonString)
        }
        logImportResult("Cadeias de Evolução", evolutionChainsCounts)
        totalSuccess += evolutionChainsCounts.success
        totalErrors += evolutionChainsCounts.errors

        // Pokemons e Weaknesses devem ser importados após a maioria das entidades relacionadas
        logger.info("Iniciando importação de Pokémons...")
        val pokemonDtos = loadJson(JsonFile.POKEMONS.filePath, object : TypeReference<List<PokemonDto>>() {})
        val pokemonsCounts = ImportCounts()
        val regionsMapForPokemon = regionRepository.findAll().associateBy { it.id } // Renomeado
        val statsMapForPokemon = statsRepository.findAll().associateBy { it.id } // Renomeado
        val generationsMapForPokemon = generationRepository.findAll().associateBy { it.id } // Renomeado
        val speciesMapForPokemon = speciesRepository.findAll().associateBy { it.id } // Renomeado
        val eggGroupsMapForPokemon = eggGroupRepository.findAll().associateBy { it.id } // Renomeado
        val typesMapForPokemon = typeRepository.findAll().associateBy { it.id } // Renomeado
        val abilitiesMapForPokemon = abilityRepository.findAll().associateBy { it.id } // Renomeado
        val evolutionChainsMapForPokemon = evolutionChainRepository.findAll().associateBy { it.id } // Renomeado

        for (dto in pokemonDtos) {
            try {
                val region = dto.regionId?.let { regionsMapForPokemon[it] }
                val stats = statsMapForPokemon[dto.statsId] ?: throw IllegalArgumentException("Stats with ID ${dto.statsId} not found for Pokemon ${dto.name}")
                val generation = generationsMapForPokemon[dto.generationId] ?: throw IllegalArgumentException("Generation with ID ${dto.generationId} not found for Pokemon ${dto.name}")
                val species = speciesMapForPokemon[dto.speciesId] ?: throw IllegalArgumentException("Species with ID ${dto.speciesId} not found for Pokemon ${dto.name}")
                val evolutionChain = evolutionChainsMapForPokemon[dto.evolutionChainId]
                    ?: throw IllegalArgumentException("EvolutionChain with ID ${dto.evolutionChainId} not found for Pokemon ${dto.name}. Ensure EvolutionChains are imported first.")

                val eggGroups = dto.eggGroupIds.mapNotNull { eggGroupsMapForPokemon[it] }.toMutableSet()
                val types = dto.typeIds.mapNotNull { typesMapForPokemon[it] }.toMutableSet()

                val pokemon = Pokemon(
                    id = dto.id,
                    number = dto.number,
                    name = dto.name,
                    description = dto.description,
                    height = dto.height,
                    weight = dto.weight,
                    genderRateValue = dto.genderRateValue,
                    eggCycles = dto.eggCycles,
                    stats = stats,
                    generation = generation,
                    species = species,
                    evolutionChain = evolutionChain,
                    region = region,
                    eggGroups = eggGroups,
                    types = types,
                    sprites = dto.sprites.toModel()
                )
                pokemonRepository.save(pokemon)

                dto.abilities.forEach { abilityDto ->
                    val ability = abilitiesMapForPokemon[abilityDto.abilityId]
                        ?: throw IllegalArgumentException("Ability with ID ${abilityDto.abilityId} not found for Pokemon ${dto.name}")
                    pokemonAbilityRepository.save(PokemonAbility(pokemon = pokemon, ability = ability, isHidden = abilityDto.isHidden))
                }
                pokemonsCounts.success++
            } catch (e: Exception) {
                pokemonsCounts.errors++
                logger.error("Error importing Pokemon ${dto.name}: ${e.message}", e)
            }
        }
        logImportResult("Pokémons", pokemonsCounts)
        totalSuccess += pokemonsCounts.success
        totalErrors += pokemonsCounts.errors

        logger.info("Iniciando importação de Fraquezas...")
        val weaknessesDtos = loadJson(JsonFile.WEAKNESSES.filePath, object : TypeReference<List<WeaknessDto>>() {})
        val weaknessesCounts = ImportCounts()
        val pokemonMapByName = pokemonRepository.findAll().associateBy { it.name }
        val typeMapByName = typeRepository.findAll().associateBy { it.name }

        weaknessesDtos.forEach { weaknessDto ->
            try {
                val pokemon = pokemonMapByName[weaknessDto.pokemonName]
                if (pokemon == null) {
                    logger.warn("Pokemon with name '${weaknessDto.pokemonName}' not found for weakness import. Skipping.")
                    weaknessesCounts.errors++
                    return@forEach
                }

                val weaknessTypes = weaknessDto.weaknesses.mapNotNull { weaknessTypeName ->
                    val type = typeMapByName[weaknessTypeName]
                    if (type == null) {
                        logger.warn("Weakness type '${weaknessTypeName}' not found for Pokemon '${pokemon.name}'. Skipping this type.")
                        null
                    } else {
                        type
                    }
                }.toMutableSet()

                pokemon.weaknesses.addAll(weaknessTypes)
                pokemonRepository.save(pokemon)
                weaknessesCounts.success++
            } catch (e: Exception) {
                weaknessesCounts.errors++
                logger.error("Error importing weaknesses for ${weaknessDto.pokemonName}: ${e.message}", e)
            }
        }
        logImportResult("Fraquezas", weaknessesCounts)
        totalSuccess += weaknessesCounts.success
        totalErrors += weaknessesCounts.errors

        logger.info("====== ✅ IMPORTAÇÃO DOS DADOS DA POKEDEX CONCLUÍDA ✅ ======")
        logger.info("✨ Resumo Geral da Importação: Sucessos Totais: $totalSuccess, Falhas Totais: $totalErrors ✨")
    }
}

@Component
class DataImportRunner(private val importService: PokemonImportService) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(DataImportRunner::class.java)

    override fun run(vararg args: String?) {
        logger.info("Executing Pokedex data import on application startup...")
        importService.importAll()
    }
}
