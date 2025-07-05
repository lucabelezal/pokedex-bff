package com.pokedex.bff.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "evolution_chains")
data class EvolutionChainEntity(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "chain_data", columnDefinition = "jsonb")
    var chainData: String? = null
)