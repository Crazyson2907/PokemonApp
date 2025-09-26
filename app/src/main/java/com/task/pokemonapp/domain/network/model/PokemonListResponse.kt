package com.task.pokemonapp.domain.network.model

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val results: List<PokemonListResult>
)