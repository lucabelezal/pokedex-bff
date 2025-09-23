package com.pokedex.bff.infrastructure.repositorypackage com.pokedex.bff.infrastructure.repositorypackage com.pokedex.bff.infrastructure.repositorypackage com.pokedex.bff.infrastructure.repository



import com.pokedex.bff.domain.entities.Pokemon

import com.pokedex.bff.domain.entities.Sprites

import com.pokedex.bff.domain.entities.OtherSpritesimport com.pokedex.bff.domain.entities.Pokemon

import com.pokedex.bff.domain.repositories.PokemonRepository

import com.pokedex.bff.domain.common.Pageimport com.pokedex.bff.domain.repositories.PokemonRepository

import com.pokedex.bff.infrastructure.persistence.entities.PokemonEntity

import com.pokedex.bff.infrastructure.persistence.repositories.SpringDataPokemonRepositoryimport com.pokedex.bff.domain.common.Pageimport com.pokedex.bff.domain.entities.Pokemonimport com.pokedex.bff.domain.entities.Pokemon

import org.springframework.data.domain.PageRequest

import org.springframework.stereotype.Repositoryimport com.pokedex.bff.infrastructure.persistence.entities.PokemonEntity



@Repositoryimport com.pokedex.bff.infrastructure.persistence.repositories.SpringDataPokemonRepositoryimport com.pokedex.bff.domain.repository.PokemonRepositoryimport com.pokedex.bff.domain.repository.PokemonRepository

class JpaPokemonRepository(

    private val springDataPokemonRepository: SpringDataPokemonRepositoryimport org.springframework.data.domain.PageRequest

) : PokemonRepository {

import org.springframework.stereotype.Repositoryimport com.pokedex.bff.domain.common.Pageimport com.pokedex.bff.infrastructure.persistence.entities.PokemonEntity

    override fun findById(id: Long): Pokemon? =

        springDataPokemonRepository.findById(id).orElse(null)?.toDomain()



    override fun findAll(): List<Pokemon> =@Repositoryimport com.pokedex.bff.infrastructure.persistence.entities.PokemonEntityimport com.pokedex.bff.infrastructure.persistence.repositories.SpringDataPokemonRepository

        springDataPokemonRepository.findAll().map { it.toDomain() }

class JpaPokemonRepository(

    override fun findAll(page: Int, size: Int): Page<Pokemon> {

        val pageRequest = PageRequest.of(page, size)    private val springDataPokemonRepository: SpringDataPokemonRepositoryimport com.pokedex.bff.infrastructure.persistence.repositories.SpringDataPokemonRepositoryimport org.springframework.stereotype.Repository

        val springPage = springDataPokemonRepository.findAll(pageRequest)

        ) : PokemonRepository {

        return Page(

            content = springPage.content.map { it.toDomain() },    import org.springframework.data.domain.PageRequest

            pageNumber = springPage.number,

            pageSize = springPage.size,    override fun findById(id: Long): Pokemon? =

            totalElements = springPage.totalElements,

            totalPages = springPage.totalPages,        springDataPokemonRepository.findById(id).orElse(null)?.toDomain()import org.springframework.stereotype.Repositorypackage com.pokedex.bff.infrastructure.repository

            isFirst = springPage.isFirst,

            isLast = springPage.isLast,

            hasNext = springPage.hasNext(),

            hasPrevious = springPage.hasPrevious()    override fun findAll(): List<Pokemon> =

        )

    }        springDataPokemonRepository.findAll().map { it.toDomain() }



    override fun save(pokemon: Pokemon): Pokemon {@Repositoryimport com.pokedex.bff.domain.entities.Pokemon

        // Implementation needed - convert domain to entity, save, convert back

        TODO("Not yet implemented")    override fun findAll(page: Int, size: Int): Page<Pokemon> {

    }

        val pageRequest = PageRequest.of(page, size)class JpaPokemonRepository(import com.pokedex.bff.domain.repository.PokemonRepository

    override fun deleteById(id: Long) {

        springDataPokemonRepository.deleteById(id)        val springPage = springDataPokemonRepository.findAll(pageRequest)

    }

            private val springDataPokemonRepository: SpringDataPokemonRepositoryimport com.pokedex.bff.domain.common.Page

    override fun existsById(id: Long): Boolean =

        springDataPokemonRepository.existsById(id)        return Page(

}

            content = springPage.content.map { it.toDomain() },) : PokemonRepository {import com.pokedex.bff.infrastructure.persistence.entities.PokemonEntity

// Extension function to convert PokemonEntity to domain Pokemon

fun PokemonEntity.toDomain(): Pokemon = Pokemon(            pageNumber = springPage.number,

    id = this.id,

    number = this.number,            pageSize = springPage.size,    override fun findById(id: Long): Pokemon? =import com.pokedex.bff.infrastructure.persistence.repositories.SpringDataPokemonRepository

    name = this.name,

    height = this.height,            totalElements = springPage.totalElements,

    weight = this.weight,

    description = this.description,            totalPages = springPage.totalPages,        springDataPokemonRepository.findById(id).orElse(null)?.toDomain()import org.springframework.data.domain.PageRequest

    sprites = this.sprites?.let {

        Sprites(            isFirst = springPage.isFirst,

            backDefault = it.backDefault,

            backFemale = it.backFemale,            isLast = springPage.isLast,import org.springframework.stereotype.Repository

            backShiny = it.backShiny,

            backShinyFemale = it.backShinyFemale,            hasNext = springPage.hasNext(),

            frontDefault = it.frontDefault,

            frontFemale = it.frontFemale,            hasPrevious = springPage.hasPrevious()    override fun findAll(): List<Pokemon> =

            frontShiny = it.frontShiny,

            frontShinyFemale = it.frontShinyFemale,        )

            other = it.other?.let { other ->

                OtherSprites(    }        springDataPokemonRepository.findAll().map { it.toDomain() }@Repository

                    dreamWorld = other.dreamWorld?.frontDefault,

                    home = other.home?.frontDefault,

                    officialArtwork = other.officialArtwork?.frontDefault,

                    showdown = other.showdown?.frontDefault    override fun save(pokemon: Pokemon): Pokemon {class JpaPokemonRepository(

                )

            }        // Implementation needed - convert domain to entity, save, convert back

        )

    },        TODO("Not yet implemented")    override fun findAll(page: Int, size: Int): Page<Pokemon> {    private val springDataPokemonRepository: SpringDataPokemonRepository

    genderRateValue = this.genderRateValue,

    genderMale = this.genderMale,    }

    genderFemale = this.genderFemale,

    eggCycles = this.eggCycles,        val pageRequest = PageRequest.of(page, size)) : PokemonRepository {

    stats = this.stats?.let {

        com.pokedex.bff.domain.entities.Stats(    override fun deleteById(id: Long) {

            id = it.id,

            hp = it.hp,        springDataPokemonRepository.deleteById(id)        val springPage = springDataPokemonRepository.findAll(pageRequest)    override fun findById(id: Long): Pokemon? =

            attack = it.attack,

            defense = it.defense,    }

            specialAttack = it.specialAttack,

            specialDefense = it.specialDefense,                springDataPokemonRepository.findById(id).orElse(null)?.toDomain()

            speed = it.speed

        )    override fun existsById(id: Long): Boolean =

    },

    generation = this.generation?.let {        springDataPokemonRepository.existsById(id)        return Page(

        com.pokedex.bff.domain.entities.Generation(

            id = it.id,}

            name = it.name,

            region = it.region?.let { region ->            content = springPage.content.map { it.toDomain() },    override fun findAll(): List<Pokemon> =

                com.pokedex.bff.domain.entities.Region(

                    id = region.id,// Extension function to convert PokemonEntity to domain Pokemon

                    name = region.name

                )fun PokemonEntity.toDomain(): Pokemon = Pokemon(            pageNumber = springPage.number,        springDataPokemonRepository.findAll().map { it.toDomain() }

            }

        )    id = this.id,

    },

    species = this.species?.let {    number = this.number,            pageSize = springPage.size,

        com.pokedex.bff.domain.entities.Species(

            id = it.id,    name = this.name,

            pokemonNumber = it.pokemonNumber

        )    height = this.height,            totalElements = springPage.totalElements,    override fun findAll(page: Int, size: Int): Page<Pokemon> {

    },

    region = this.region?.let {    weight = this.weight,

        com.pokedex.bff.domain.entities.Region(

            id = it.id,    description = this.description,            totalPages = springPage.totalPages,        val pageRequest = PageRequest.of(page, size)

            name = it.name

        )    sprites = null, // Map as needed

    },

    evolutionChain = this.evolutionChain?.let {    genderRateValue = this.genderRateValue,            isFirst = springPage.isFirst,        val springPage = springDataPokemonRepository.findAll(pageRequest)

        com.pokedex.bff.domain.entities.EvolutionChain(

            id = it.id,    genderMale = this.genderMale,

            chainData = it.chainData

        )    genderFemale = this.genderFemale,            isLast = springPage.isLast,        

    },

    types = this.types.map {    eggCycles = this.eggCycles,

        com.pokedex.bff.domain.entities.Type(

            id = it.id,    stats = null, // Map as needed            hasNext = springPage.hasNext(),        return Page(

            name = it.name,

            color = it.color    generation = null, // Map as needed

        )

    }.toSet(),    species = null, // Map as needed            hasPrevious = springPage.hasPrevious()            content = springPage.content.map { it.toDomain() },

    abilities = this.abilities.map {

        com.pokedex.bff.domain.entities.PokemonAbility(    region = null, // Map as needed

            id = it.id ?: 0,

            pokemonId = this.id,    evolutionChain = null, // Map as needed        )            pageNumber = springPage.number,

            abilityId = it.ability.id,

            ability = com.pokedex.bff.domain.entities.Ability(    types = emptySet(), // Map as needed

                id = it.ability.id,

                name = it.ability.name    abilities = emptySet(), // Map as needed    }            pageSize = springPage.size,

            ),

            isHidden = it.isHidden    eggGroups = emptySet(), // Map as needed

        )

    }.toSet(),    weaknesses = emptySet() // Map as needed            totalElements = springPage.totalElements,

    eggGroups = this.eggGroups.map {

        com.pokedex.bff.domain.entities.EggGroup()

            id = it.id,    override fun save(pokemon: Pokemon): Pokemon {            totalPages = springPage.totalPages,

            name = it.name

        )        // Implementation needed - convert domain to entity, save, convert back            isFirst = springPage.isFirst,

    }.toSet(),

    weaknesses = this.weaknesses.map {        TODO("Not yet implemented")            isLast = springPage.isLast,

        com.pokedex.bff.domain.entities.Type(

            id = it.id,    }            hasNext = springPage.hasNext(),

            name = it.name,

            color = it.color            hasPrevious = springPage.hasPrevious()

        )

    }.toSet()    override fun deleteById(id: Long) {        )

)
        springDataPokemonRepository.deleteById(id)    }

    }

    override fun save(pokemon: Pokemon): Pokemon {

    override fun existsById(id: Long): Boolean =        // Implementation needed - convert domain to entity, save, convert back

        springDataPokemonRepository.existsById(id)        TODO("Not yet implemented")

}    }



// Extension function to convert PokemonEntity to domain Pokemon    override fun deleteById(id: Long) {

fun PokemonEntity.toDomain(): Pokemon = Pokemon(        springDataPokemonRepository.deleteById(id)

    id = this.id,    }

    number = this.number,

    name = this.name,    override fun existsById(id: Long): Boolean =

    height = this.height,        springDataPokemonRepository.existsById(id)

    weight = this.weight,}

    description = this.description,

    sprites = null, // Map as needed// Extension function to convert PokemonEntity to domain Pokemon

    genderRateValue = this.genderRateValue,fun PokemonEntity.toDomain(): Pokemon = Pokemon(

    genderMale = this.genderMale,    id = this.id,

    genderFemale = this.genderFemale,    number = this.number,

    eggCycles = this.eggCycles,    name = this.name,

    stats = null, // Map as needed    height = this.height,

    generation = null, // Map as needed    weight = this.weight,

    species = null, // Map as needed    description = this.description,

    region = null, // Map as needed    sprites = null, // Map as needed

    evolutionChain = null, // Map as needed    genderRateValue = this.genderRateValue,

    types = emptySet(), // Map as needed    genderMale = this.genderMale,

    abilities = emptySet(), // Map as needed    genderFemale = this.genderFemale,

    eggGroups = emptySet(), // Map as needed    eggCycles = this.eggCycles,

    weaknesses = emptySet() // Map as needed    stats = null, // Map as needed

)    generation = null, // Map as needed
    species = null, // Map as needed
    region = null, // Map as needed
    evolutionChain = null, // Map as needed
    types = emptySet(), // Map as needed
    abilities = emptySet(), // Map as needed
    eggGroups = emptySet(), // Map as needed
    weaknesses = emptySet() // Map as needed
)

