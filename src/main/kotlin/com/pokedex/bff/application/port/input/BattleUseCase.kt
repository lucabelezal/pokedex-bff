package com.pokedex.bff.application.port.input

import com.pokedex.bff.application.dtos.input.StartBattleInput
import com.pokedex.bff.application.dtos.output.BattleResultOutput

/**
 * Port (interface) para o caso de uso de batalha entre Pokémon.
 * Define o contrato que o adapter de entrada (controller) usa.
 */
interface BattleUseCase {
    fun execute(input: StartBattleInput): BattleResultOutput
}
