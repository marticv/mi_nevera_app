package com.proyecto_linkia.mi_nevera_app.utils

import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.RecipeEntity
import com.proyecto_linkia.mi_nevera_app.internet.IngredientExternal
import com.proyecto_linkia.mi_nevera_app.internet.RecipeExternal

fun IngredientExternal.toEntity(): IngredientEntity {
    return IngredientEntity(this.ingredient, this.ingredientName, this.ingredientNameEnglish)
}

fun RecipeExternal.toEmptyRecipeEntity(): RecipeEntity {
    return RecipeEntity(
        this.recipeId,
        this.recipeName,
        this.recipeNameEnglish,
        if (this.isVegan) {
            1
        } else {
            0
        },
        this.difficulty,
        this.time,
        this.image
    )
}