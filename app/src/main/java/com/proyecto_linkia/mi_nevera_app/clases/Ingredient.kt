package com.proyecto_linkia.mi_nevera_app.clases

import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredientEntity

data class Ingredient(
    val id_ingredient: Int?,
    val ingredientName: String
)

fun MyIngredientEntity.toDomain()= Ingredient(null,ingredientName)