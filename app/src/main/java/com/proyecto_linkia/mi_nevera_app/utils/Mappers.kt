package com.proyecto_linkia.mi_nevera_app.utils

import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.RecipeEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeWithIngredients
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

fun RecipeWithIngredients.toRecipe(): Recipe {
    val ingredientsArray: ArrayList<String> = arrayListOf()
    for (ingredient in this.ingredients) {
        ingredientsArray.add(ingredient.ingredientName)
    }

    return Recipe(
        recipeEntity.recipeName,
        ingredientsArray,
        recipeEntity.isVegan == 1,
        recipeEntity.difficulty,
        recipeEntity.time,
        recipeEntity.image
    )
}

fun Recipe.toEntity(id: String): RecipeEntity {
    return RecipeEntity(
        id, this.recipeName, "nullfromUser", if (this.isVegan) {
            1
        } else {
            0
        }, this.difficulty, this.time, this.image
    )
}