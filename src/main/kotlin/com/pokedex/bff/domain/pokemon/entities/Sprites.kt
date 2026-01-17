package com.pokedex.bff.domain.pokemon.entities

/**
 * Sprites representa os diferentes sprites/imagens do Pokémon.
 * 
 * Contém URLs para as diferentes versões visuais do Pokémon em diferentes jogos
 * e contextos (frente, costas, shiny, female, etc.).
 * 
 * @property backDefault Sprite padrão de costas
 * @property backFemale Sprite de costas da versão feminina
 * @property backShiny Sprite de costas na versão shiny
 * @property backShinyFemale Sprite de costas shiny feminina
 * @property frontDefault Sprite padrão de frente (principal)
 * @property frontFemale Sprite de frente da versão feminina
 * @property frontShiny Sprite de frente na versão shiny
 * @property frontShinyFemale Sprite de frente shiny feminina
 * @property other Outros sprites especiais (artwork oficial, home, showdown)
 * @property versions Sprites organizados por versões de jogos
 */
data class Sprites(
    val backDefault: String? = null,
    val backFemale: String? = null,
    val backShiny: String? = null,
    val backShinyFemale: String? = null,
    val frontDefault: String? = null,
    val frontFemale: String? = null,
    val frontShiny: String? = null,
    val frontShinyFemale: String? = null,
    val other: OtherSprites? = null,
    val versions: GameVersionsSprites? = null
)

/**
 * Sprites especiais de diferentes fontes
 */
data class OtherSprites(
    val home: HomeSprites? = null,
    val showdown: ShowdownSprites? = null,
    val dreamWorld: DreamWorldSprites? = null,
    val officialArtwork: OfficialArtworkSprites? = null
)

/**
 * Sprites do Pokémon Home
 */
data class HomeSprites(
    val frontDefault: String? = null,
    val frontShiny: String? = null,
    val frontFemale: String? = null,
    val frontShinyFemale: String? = null
)

/**
 * Sprites do Showdown
 */
data class ShowdownSprites(
    val frontDefault: String? = null,
    val frontShiny: String? = null,
    val frontFemale: String? = null,
    val frontShinyFemale: String? = null,
    val backDefault: String? = null,
    val backShiny: String? = null,
    val backFemale: String? = null,
    val backShinyFemale: String? = null
)

/**
 * Sprites do Dream World
 */
data class DreamWorldSprites(
    val frontDefault: String? = null,
    val frontFemale: String? = null
)

/**
 * Artwork oficial
 */
data class OfficialArtworkSprites(
    val frontDefault: String? = null,
    val frontShiny: String? = null
)

/**
 * Sprites organizados por gerações e versões de jogos
 */
data class GameVersionsSprites(
    val generationI: GenerationISprites? = null,
    val generationII: GenerationIISprites? = null,
    val generationIII: GenerationIIISprites? = null,
    val generationIV: GenerationIVSprites? = null,
    val generationV: GenerationVSprites? = null,
    val generationVI: GenerationVISprites? = null,
    val generationVII: GenerationVIISprites? = null,
    val generationVIII: GenerationVIIISprites? = null
)

// Generation I
data class GenerationISprites(
    val redBlue: GameSprites? = null,
    val yellow: GameSprites? = null
)

// Generation II
data class GenerationIISprites(
    val crystal: GameSprites? = null,
    val gold: GameSprites? = null,
    val silver: GameSprites? = null
)

// Generation III
data class GenerationIIISprites(
    val emerald: GameSprites? = null,
    val fireredLeafgreen: GameSprites? = null,
    val rubySapphire: GameSprites? = null
)

// Generation IV
data class GenerationIVSprites(
    val diamondPearl: GameSprites? = null,
    val heartgoldSoulsilver: GameSprites? = null,
    val platinum: GameSprites? = null
)

// Generation V
data class GenerationVSprites(
    val blackWhite: AnimatedSprites? = null
)

// Generation VI
data class GenerationVISprites(
    val omegarubyAlphasapphire: GameSprites? = null,
    val xY: GameSprites? = null
)

// Generation VII
data class GenerationVIISprites(
    val icons: GameSprites? = null,
    val ultraSunUltraMoon: GameSprites? = null
)

// Generation VIII
data class GenerationVIIISprites(
    val icons: GameSprites? = null
)

// Sprites básicos de jogos
data class GameSprites(
    val backDefault: String? = null,
    val backFemale: String? = null,
    val backShiny: String? = null,
    val backShinyFemale: String? = null,
    val frontDefault: String? = null,
    val frontFemale: String? = null,
    val frontShiny: String? = null,
    val frontShinyFemale: String? = null,
    val backGray: String? = null,
    val backTransparent: String? = null,
    val frontGray: String? = null,
    val frontTransparent: String? = null
)

// Sprites com animação (Generation V)
data class AnimatedSprites(
    val animated: GameSprites? = null,
    val backDefault: String? = null,
    val backFemale: String? = null,
    val backShiny: String? = null,
    val backShinyFemale: String? = null,
    val frontDefault: String? = null,
    val frontFemale: String? = null,
    val frontShiny: String? = null,
    val frontShinyFemale: String? = null
)