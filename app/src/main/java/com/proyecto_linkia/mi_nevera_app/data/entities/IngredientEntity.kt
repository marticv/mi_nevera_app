package com.proyecto_linkia.mi_nevera_app.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["ingredientName"], unique = true)])
data class IngredientEntity(
    @PrimaryKey(autoGenerate = false)
    val ingredientId: String,
    val ingredientName: String,
    val ingredientNameEnglish: String
)