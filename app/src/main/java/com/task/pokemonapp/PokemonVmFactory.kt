package com.task.pokemonapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.task.pokemonapp.domain.cache.core.PokemonRepository
import com.task.pokemonapp.presentation.pokemonDetails.PokemonDetailViewModel
import com.task.pokemonapp.presentation.pokemonList.PokemonListViewModel

class PokemonVMFactory(
    private val repository: PokemonRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(PokemonListViewModel::class.java) ->
            PokemonListViewModel(repository) as T
        modelClass.isAssignableFrom(PokemonDetailViewModel::class.java) ->
            PokemonDetailViewModel(repository) as T
        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}