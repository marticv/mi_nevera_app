package com.proyecto_linkia.mi_nevera_app.data.db.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.RecipeEntity


data class RecipeWithIngredients(
    @Embedded val recipeEntity: RecipeEntity,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "ingredientId",
        associateBy = Junction(RecipeIngredientCrossReference::class)
    )
    val ingredients: List<IngredientEntity>
)
