package com.pokedex.bff.application.usecase

import com.pokedex.bff.application.dtos.input.StartBattleInput
import com.pokedex.bff.application.dtos.output.BattleResultOutput

interface BattleUseCase {
    fun execute(input: StartBattleInput): BattleResultOutput
}
