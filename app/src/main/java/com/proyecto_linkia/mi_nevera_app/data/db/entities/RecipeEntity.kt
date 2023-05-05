package com.proyecto_linkia.mi_nevera_app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeEntity(
    @PrimaryKey(autoGenerate = false)
    val recipeId: String,
    val recipeName: String,
    val recipeNameEnglish: String,
    var isVegan: Int,
    val difficulty: String,
    val time: Int,
    val image: String,
    val isFavourite: Int
)
