package com.task.pokemonapp.domain.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritePokemon(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String
)