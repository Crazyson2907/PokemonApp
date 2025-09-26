package com.task.pokemonapp.data.network

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.task.pokemonapp.domain.cache.core.PokemonRepository
import com.task.pokemonapp.domain.network.core.PokeApiService
import com.task.pokemonapp.domain.network.model.PokemonDetail
import com.task.pokemonapp.domain.network.model.PokemonListResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.io.FileOutputStream

class PokemonRepositoryImpl(
    private val api: PokeApiService,
    private val context: Context,
    private val favoritesDao: FavoritePokemonDao,
    private val pageSize: Int = 20
) : PokemonRepository {

    private var currentOffset = 0
    private val allPokemon = mutableListOf<PokemonListResult>()
    private val imageCache = mutableMapOf<String, ImageBitmap>()

    override suspend fun fetchNextPage(): List<PokemonListResult> {
        val response = api.getPokemonList(offset = currentOffset, limit = pageSize)
        val newItems = response.results
        currentOffset += pageSize
        allPokemon.addAll(newItems)
        return newItems
    }

    override fun getCachedPokemonList(): List<PokemonListResult> = allPokemon

    override suspend fun getPokemonDetail(name: String): PokemonDetail = api.getPokemonDetail(name)

    override suspend fun getImageBitmapForPokemon(id: Int): ImageBitmap {
        val fileName = "pokemon_$id.png"
        imageCache[fileName]?.let { return it }

        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
        val file = downloadImageToDisk(url, fileName)
        val bmp = withContext(Dispatchers.IO) { BitmapFactory.decodeFile(file.absolutePath) }
            ?: error("Failed to decode image for id=$id")

        return bmp.asImageBitmap().also { imageCache[fileName] = it }
    }

    // ----- Favorites -----
    override fun getFavoriteListFlow(): Flow<List<FavoritePokemon>> = favoritesDao.getAllFavorites()

    override suspend fun addFavorite(id: Int, name: String) {
        favoritesDao.insertFavorite(FavoritePokemon(id = id, name = name))
    }

    override suspend fun removeFavorite(id: Int) {
        favoritesDao.deleteById(id)            // see DAO addition below
    }

    override suspend fun isFavorite(id: Int): Boolean =
        favoritesDao.exists(id) > 0            // see DAO addition below

    // ----- Utilities -----
    override fun extractPokemonIdFromUrl(url: String): Int =
        url.trimEnd('/').substringAfterLast('/').toInt()

    // ----- Internal: manual download -----
    private suspend fun downloadImageToDisk(imageUrl: String, fileName: String): File {
        val file = File(context.cacheDir, fileName)
        if (file.exists()) return file
        return withContext(Dispatchers.IO) {
            val conn = (URL(imageUrl).openConnection() as HttpURLConnection).apply {
                doInput = true
                connect()
            }
            try {
                require(conn.responseCode == HttpURLConnection.HTTP_OK) {
                    "HTTP ${conn.responseCode} for $imageUrl"
                }
                conn.inputStream.use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
                file
            } finally { conn.disconnect() }
        }
    }
}