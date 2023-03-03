package com.proyecto_linkia.mi_nevera_app.clases

import java.io.Serializable

data class Recipe(
    val id_recipe: Int?,
    val recipeName: String,
    val ingredients: ArrayList<String>,
    val isVegan: Boolean
): Serializable