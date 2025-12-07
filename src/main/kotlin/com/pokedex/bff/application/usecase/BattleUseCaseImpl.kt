package com.pokedex.bff.application.usecase

import com.pokedex.bff.application.port.input.BattleUseCase
import com.pokedex.bff.application.dtos.input.StartBattleInput
import com.pokedex.bff.application.dtos.output.BattleResultOutput
import com.pokedex.bff.domain.pokemon.repository.PokemonRepository
import com.pokedex.bff.domain.pokemon.exception.PokemonNotFoundException
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import org.slf4j.LoggerFactory
import kotlin.math.max

class BattleUseCaseImpl(
    private val pokemonRepository: PokemonRepository
) : BattleUseCase {
    
    private val logger = LoggerFactory.getLogger(BattleUseCaseImpl::class.java)
    
    override fun execute(input: StartBattleInput): BattleResultOutput {
        logger.info("Starting battle between Pokemon {} and {}", input.pokemonId, input.opponentId)
        
        // Buscar os Pokémons
        val pokemon1 = pokemonRepository.findById(input.pokemonId)
            ?: throw PokemonNotFoundException("Pokemon with ID ${input.pokemonId} not found")
            
        val pokemon2 = pokemonRepository.findById(input.opponentId)
            ?: throw PokemonNotFoundException("Pokemon with ID ${input.opponentId} not found")
        
        // Calcular poder de batalha
        val power1 = calculateBattlePower(pokemon1)
        val power2 = calculateBattlePower(pokemon2)
        
        // Calcular efetividade de tipo
        val typeEffectiveness1 = pokemon1.calculateTypeEffectiveness(pokemon2)
        val typeEffectiveness2 = pokemon2.calculateTypeEffectiveness(pokemon1)
        
        // Calcular dano total
        val totalDamage1 = (power1 * typeEffectiveness1).toInt()
        val totalDamage2 = (power2 * typeEffectiveness2).toInt()
        
        // Determinar vencedor
        val (winner, loser, winnerDamage, loserDamage) = if (totalDamage1 > totalDamage2) {
            Tuple4(pokemon1, pokemon2, totalDamage1, totalDamage2)
        } else if (totalDamage2 > totalDamage1) {
            Tuple4(pokemon2, pokemon1, totalDamage2, totalDamage1)
        } else {
            // Empate: usar speed como desempate
            if ((pokemon1.stats?.speed ?: 0) >= (pokemon2.stats?.speed ?: 0)) {
                Tuple4(pokemon1, pokemon2, totalDamage1, totalDamage2)
            } else {
                Tuple4(pokemon2, pokemon1, totalDamage2, totalDamage1)
            }
        }
        
        val summary = buildBattleSummary(winner, loser, winnerDamage, loserDamage)
        
        logger.info("Battle completed. Winner: {} ({})", winner.name, winner.id)
        
        return BattleResultOutput(
            winnerId = winner.id.toString(),
            loserId = loser.id.toString(),
            summary = summary
        )
    }
    
    /**
     * Calcula o poder de batalha baseado nas stats do Pokémon
     */
    private fun calculateBattlePower(pokemon: Pokemon): Double {
        val stats = pokemon.stats ?: return 100.0
        
        // Fórmula simplificada: média ponderada de attack, defense, hp e speed
        val attackPower = stats.attack * 1.5
        val defensePower = stats.defense * 1.0
        val hpPower = stats.hp * 1.2
        val speedPower = stats.speed * 0.8
        
        return attackPower + defensePower + hpPower + speedPower
    }
    
    /**
     * Constrói um resumo detalhado da batalha
     */
    private fun buildBattleSummary(
        winner: Pokemon,
        loser: Pokemon,
        winnerDamage: Int,
        loserDamage: Int
    ): String {
        return buildString {
            appendLine("⚔️ BATTLE SUMMARY ⚔️")
            appendLine()
            appendLine("${winner.name} vs ${loser.name}")
            appendLine()
            appendLine("${winner.name} dealt $winnerDamage damage")
            appendLine("${loser.name} dealt $loserDamage damage")
            appendLine()
            appendLine("🏆 WINNER: ${winner.name}!")
            
            if (winner.isLegendary()) {
                appendLine("(Legendary Pokémon dominates the battle!)")
            }
        }
    }
    
    private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
