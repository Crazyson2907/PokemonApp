package com.task.pokemonapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.task.pokemonapp.data.cache.AppDatabase
import com.task.pokemonapp.data.network.PokemonRepositoryImpl
import com.task.pokemonapp.domain.cache.core.PokemonRepository
import com.task.pokemonapp.domain.network.core.PokeApiService
import com.task.pokemonapp.presentation.pokemonDetails.PokemonDetailScreen
import com.task.pokemonapp.presentation.pokemonDetails.PokemonDetailViewModel
import com.task.pokemonapp.presentation.pokemonList.PokemonListScreen
import com.task.pokemonapp.presentation.pokemonList.PokemonListViewModel
import com.task.pokemonapp.ui.theme.PokemonAppTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var repository: PokemonRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(PokeApiService::class.java)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "pokemon.db"
        ).build()

        repository = PokemonRepositoryImpl(
            api = api,
            context = applicationContext,
            favoritesDao = db.favoritePokemonDao()
        )

        setContent {
            PokemonAppTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "list",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("list") {
                            val listVm: PokemonListViewModel = viewModel(
                                factory = PokemonVMFactory(repository)
                            )
                            PokemonListScreen(
                                viewModel = listVm,
                                onOpenDetail = { name ->
                                    navController.navigate("detail/$name")
                                }
                            )
                        }
                        composable(
                            route = "detail/{name}",
                            arguments = listOf(navArgument("name") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: return@composable
                            val detailVm: PokemonDetailViewModel = viewModel(
                                factory = PokemonVMFactory(repository)
                            )
                            PokemonDetailScreen(
                                name = name,
                                viewModel = detailVm
                            )
                        }
                    }
                }
            }
        }
    }
}