package com.proyecto_linkia.mi_nevera_app.data.db.entities.relations

import androidx.room.Entity

@Entity(primaryKeys = ["recipeId","ingredientId"])
data class RecipeIngredientCrossReference(
    val recipeId:Int,
    val ingredientId:Int
)