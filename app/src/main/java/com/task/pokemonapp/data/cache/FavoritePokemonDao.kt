package com.task.pokemonapp.data.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.task.pokemonapp.domain.cache.entity.FavoritePokemon
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePokemonDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoritePokemon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(pokemon: FavoritePokemon)

    @Delete
    suspend fun deleteFavorite(pokemon: FavoritePokemon)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM favorites WHERE id = :id")
    suspend fun exists(id: Int): Int
}