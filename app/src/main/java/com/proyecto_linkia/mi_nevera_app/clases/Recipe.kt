package com.proyecto_linkia.mi_nevera_app.clases

import java.io.Serializable

data class Recipe(
    val recipeName: String,
    val ingredients: ArrayList<String>,
    val isVegan: Boolean,
    val difficulty: String,
    val time: Int,
    val image: String,
    var isFavourite: Boolean
): Serializable