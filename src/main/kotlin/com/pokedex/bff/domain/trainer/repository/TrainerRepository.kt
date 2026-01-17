package com.pokedex.bff.domain.trainer.repository

import com.pokedex.bff.domain.trainer.entities.Trainer
import com.pokedex.bff.domain.trainer.valueobject.TrainerId

interface TrainerRepository {
    fun findById(id: TrainerId): Trainer?
    fun save(trainer: Trainer): Trainer
}