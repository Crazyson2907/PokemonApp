package com.task.pokemonapp.presentation.pokemonList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.pokemonapp.domain.cache.core.PokemonRepository
import com.task.pokemonapp.domain.cache.entity.FavoritePokemon
import com.task.pokemonapp.domain.network.model.PokemonListResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PokemonListViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<List<PokemonListResult>>(emptyList())
    val pokemonList: StateFlow<List<PokemonListResult>> = _pokemonList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val favorites: StateFlow<List<FavoritePokemon>> =
        repository.getFavoriteListFlow()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            runCatching {
                _isLoading.value = true
                repository.fetchNextPage()
                repository.getCachedPokemonList()
            }.onSuccess { cached ->
                _pokemonList.value = cached
            }.onFailure { e ->
                _errorMessage.value = "Failed to load Pokémon list: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            if (_isLoading.value) return@launch
            _isLoading.value = true
            runCatching {
                repository.fetchNextPage()
                repository.getCachedPokemonList()
            }.onSuccess { cached ->
                _pokemonList.value = cached
            }.onFailure { e ->
                _errorMessage.value = "Error loading more Pokémon: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun onFavoriteToggle(pokemon: PokemonListResult) {
        viewModelScope.launch {
            val id = repository.extractPokemonId(pokemon)
            val isFav = favorites.value.any { it.id == id }
            runCatching {
                if (isFav) repository.removeFavorite(id)
                else repository.addFavorite(id, pokemon.name)
            }.onFailure { e ->
                _errorMessage.value = "Failed to update favorite: ${e.message}"
            }
        }
    }
}