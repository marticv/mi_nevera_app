package com.proyecto_linkia.mi_nevera_app.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["ingredientName"], unique = true)])
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val ingredientId: Int?,
    val ingredientName: String
)
