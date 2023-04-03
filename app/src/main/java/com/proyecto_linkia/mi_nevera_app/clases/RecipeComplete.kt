package com.proyecto_linkia.mi_nevera_app.clases

import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity
import java.io.Serializable

data class RecipeComplete(
    val recipeId: String,
    val recipeName: String,
    val recipeNameEnglish: String,
    val ingredients: ArrayList<Int>,
    val isVegan: Boolean,
    val difficulty: String,
    val time: Int,
    val image: String
) : Serializable
