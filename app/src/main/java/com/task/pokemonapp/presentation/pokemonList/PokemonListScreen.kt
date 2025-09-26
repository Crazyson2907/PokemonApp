package com.task.pokemonapp.presentation.pokemonList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel = viewModel(),
    onOpenDetail: (String) -> Unit
    ) {
    val pokemonList by viewModel.pokemonList.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    errorMessage?.let { error -> Text("Error: $error") }

    LazyColumn {
        itemsIndexed(pokemonList) { index, pokemon ->
            val id = remember(pokemon.url) { viewModel.getPokemonId(pokemon) }

            PokemonListItem(
                pokemon = pokemon,
                id = id,
                isFavorite = favorites.any { it.id == id },
                onToggleFavorite = { viewModel.onFavoriteToggle(pokemon) },
                imageLoader = { neededId -> viewModel.loadImageBitmap(neededId) },
                onClick = { onOpenDetail(pokemon.name) }
            )

            if (index == pokemonList.lastIndex && !isLoading) {
                LaunchedEffect(Unit) { viewModel.loadNextPage() }
            }
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.padding(16.dp))
                }
            }
        }
    }
}