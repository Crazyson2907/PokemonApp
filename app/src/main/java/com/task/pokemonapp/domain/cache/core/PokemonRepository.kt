package com.task.pokemonapp.domain.cache.core

import androidx.compose.ui.graphics.ImageBitmap
import com.task.pokemonapp.domain.cache.entity.FavoritePokemon
import com.task.pokemonapp.domain.network.model.PokemonDetail
import com.task.pokemonapp.domain.network.model.PokemonListResult
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {

    suspend fun fetchNextPage(): List<PokemonListResult>
    fun getCachedPokemonList(): List<PokemonListResult>
    suspend fun getPokemonDetail(name: String): PokemonDetail

    suspend fun getImageBitmapForPokemon(id: Int): ImageBitmap

    fun getFavoriteListFlow(): Flow<List<FavoritePokemon>>
    suspend fun addFavorite(id: Int, name: String)
    suspend fun removeFavorite(id: Int)
    suspend fun isFavorite(id: Int): Boolean

    fun extractPokemonIdFromUrl(url: String): Int
    fun extractPokemonId(p: PokemonListResult): Int = extractPokemonIdFromUrl(p.url)
}