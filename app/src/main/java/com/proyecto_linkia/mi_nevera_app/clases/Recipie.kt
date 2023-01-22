package com.proyecto_linkia.mi_nevera_app.clases

data class Recipie(
    val id_receta: Int?,
    val nombreReceta: String,
    val ingredients: ArrayList<Ingredient>,
    val vegan: Boolean
)