package com.task.pokemonapp.presentation.pokemonList

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.task.pokemonapp.domain.cache.core.PokemonRepository
import com.task.pokemonapp.domain.network.model.PokemonListResult

@Composable
fun PokemonListItem(
    pokemon: PokemonListResult,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    repository: PokemonRepository
) {
    val context = LocalContext.current

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(pokemon.name) {
        val id = pokemon.url.trimEnd('/').substringAfterLast('/').toInt()
        imageBitmap = repository.getImageBitmapForPokemon(id)
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageBitmap != null) {
            Image(bitmap = imageBitmap!!, contentDescription = pokemon.name,
                modifier = Modifier.size(64.dp))
        } else {
            Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))

        Text(pokemon.name.replaceFirstChar { it.uppercaseChar() }, modifier = Modifier.weight(1f))

        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.Star,
                contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                tint = if (isFavorite) Color.Yellow else Color.Gray
            )
        }
    }
}