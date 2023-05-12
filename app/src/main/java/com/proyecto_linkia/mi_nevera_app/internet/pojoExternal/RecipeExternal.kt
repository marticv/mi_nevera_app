package com.proyecto_linkia.mi_nevera_app.internet.pojoExternal

import java.io.Serializable

data class RecipeExternal(
    val recipeId: String,
    val recipeName: String,
    val recipeNameEnglish: String,
    val ingredients: ArrayList<String>,
    val isVegan: Boolean,
    val difficulty: String,
    val time: Int,
    val image: String
) : Serializable
