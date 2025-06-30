package com.pokedex.domain.repository

import com.pokedex.domain.entity.Pokemon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PokemonRepository : JpaRepository<Pokemon, String> {
    // You can add custom query methods here if needed
}
