package com.task.pokemonapp.presentation.pokemonDetails

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PokemonDetailScreen(
    name: String,
    viewModel: PokemonDetailViewModel = viewModel()
) {
    val detail by viewModel.pokemonDetail.collectAsState()
    val images by viewModel.detailImages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(name) {
        viewModel.loadPokemonDetail(name)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (errorMessage != null) {
        Text("Error: $errorMessage", color = Color.Red, modifier = Modifier.padding(16.dp))
    } else {
        detail?.let { pokemon ->
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = "${pokemon.name.capitalize()} (ID: ${pokemon.id})", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Sprites:", style = MaterialTheme.typography.titleMedium)

                LazyRow {
                    items(images) { file ->
                        // Decode file to ImageBitmap
                        val bitmap = remember(file.path) {
                            BitmapFactory.decodeFile(file.path)
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Sprite",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(96.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}