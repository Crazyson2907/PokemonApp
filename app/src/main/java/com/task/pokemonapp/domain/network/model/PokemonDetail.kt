package com.task.pokemonapp.domain.network.model

import com.google.gson.annotations.SerializedName

data class PokemonDetail(
    val id: Int,
    val name: String,
    val sprites: Sprites
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("back_default") val backDefault: String?,
    @SerializedName("front_shiny") val frontShiny: String?,
    @SerializedName("back_shiny") val backShiny: String?,
    @SerializedName("other") val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    @SerializedName("front_default") val frontDefault: String?
)
