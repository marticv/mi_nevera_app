package com.proyecto_linkia.mi_nevera_app.data

import com.proyecto_linkia.mi_nevera_app.clases.Ingredient

class IngredientProvider {
    companion object {
        val ingredientList = listOf<Ingredient>(
            Ingredient(0, "arroz"),
            Ingredient(1, "macarrones"),
            Ingredient(2, "salsa de tomate"),
            Ingredient(3, "manzana"),
            Ingredient(4, "huevo"),
            Ingredient(5, "patatas"),
            Ingredient(6, "berenjena"),
            Ingredient(7, "carne picada"),
            Ingredient(8, "espaguetis"),
            Ingredient(9, "canela"),
            Ingredient(10, "leche"),
            Ingredient(11, "carne picada"),
            Ingredient(12, "cereales"),
            Ingredient(13, "pipas"),
            Ingredient(14, "cacahuetes"),
            Ingredient(15, "leche de coco"),
        )
    }
}