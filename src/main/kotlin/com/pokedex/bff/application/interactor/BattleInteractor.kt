package com.pokedex.bff.application.interactor

import com.pokedex.bff.application.dtos.input.StartBattleInput
import com.pokedex.bff.application.dtos.output.BattleResultOutput
import com.pokedex.bff.application.usecase.BattleUseCase

class BattleInteractor : BattleUseCase {
    override fun execute(input: StartBattleInput): BattleResultOutput {
        // TODO: Implementar lógica de batalha
        return BattleResultOutput(
            winnerId = "winner-placeholder",
            loserId = "loser-placeholder",
            summary = "Battle logic not implemented yet."
        )
    }
}
