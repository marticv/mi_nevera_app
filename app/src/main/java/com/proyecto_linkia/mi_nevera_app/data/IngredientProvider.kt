package com.proyecto_linkia.mi_nevera_app.data

import com.proyecto_linkia.mi_nevera_app.clases.Ingredient

class IngredientProvider {
    companion object {
        val ingredientList = listOf<Ingredient>(
            Ingredient(null, "arroz"),
            Ingredient(null, "macarrones"),
            Ingredient(null, "salsa de tomate"),
            Ingredient(null, "manzana"),
            Ingredient(null, "huevo")
        )
    }
}