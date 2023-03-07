package com.proyecto_linkia.mi_nevera_app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val recipeId: Int?,
    val recipeName: String,
    val isVegan: Boolean
)
