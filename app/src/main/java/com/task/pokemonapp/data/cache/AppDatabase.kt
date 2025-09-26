package com.task.pokemonapp.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.task.pokemonapp.domain.cache.entity.FavoritePokemon

@Database(entities = [FavoritePokemon::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun favoritePokemonDao(): FavoritePokemonDao
}