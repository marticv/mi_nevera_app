package com.proyecto_linkia.mi_nevera_app.data.entities.relations

import androidx.room.Entity

@Entity(primaryKeys = ["recipeId","ingredientId"])
data class RecipeIngredientCrossReference(
    val recipeId:String,
    val ingredientId:String
)