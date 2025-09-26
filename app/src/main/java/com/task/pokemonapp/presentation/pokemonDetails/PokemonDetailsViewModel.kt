package com.task.pokemonapp.presentation.pokemonDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.pokemonapp.domain.cache.core.PokemonRepository
import com.task.pokemonapp.domain.network.model.PokemonDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class PokemonDetailViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonDetail = MutableStateFlow<PokemonDetail?>(null)
    val pokemonDetail: StateFlow<PokemonDetail?> = _pokemonDetail

    private val _detailImages = MutableStateFlow<List<File>>(emptyList())
    val detailImages: StateFlow<List<File>> = _detailImages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadPokemonDetail(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val detail = repository.getPokemonDetail(name)
                _pokemonDetail.value = detail

                val id = detail.id
                val urls = buildDetailImageUrls(detail)
                    .ifEmpty { buildSpriteUrlsById(id) }
                    .let { ensureTwentyUrls(it, id) }

                val files = repository.cacheImages(urls, "detail_${id}")
                _detailImages.value = files
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load details: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun buildDetailImageUrls(d: PokemonDetail): List<String> {
        val urls = mutableListOf<String>()
        d.sprites.frontDefault?.let { urls += it }
        d.sprites.backDefault?.let { urls += it }
        d.sprites.frontShiny?.let { urls += it }
        d.sprites.backShiny?.let { urls += it }
        d.sprites.other?.officialArtwork?.frontDefault?.let { urls += it }
        return urls.distinct()
    }

    private fun buildSpriteUrlsById(id: Int): List<String> {
        val base = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon"
        return listOf(
            "$base/$id.png",
            "$base/back/$id.png",
            "$base/shiny/$id.png",
            "$base/back/shiny/$id.png",
            "$base/female/$id.png",
            "$base/back/female/$id.png",
            "$base/shiny/female/$id.png",
            "$base/back/shiny/female/$id.png",
            "$base/other/official-artwork/$id.png"
        )
    }

    private fun ensureTwentyUrls(src: List<String>, id: Int): List<String> {
        if (src.isEmpty()) {
            val fallback = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
            return List(20) { fallback }
        }
        val out = src.toMutableList()
        while (out.size < 20) out += src[out.size % src.size]
        return out.take(20)
    }
}