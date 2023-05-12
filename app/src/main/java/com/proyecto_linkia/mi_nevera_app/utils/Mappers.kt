package com.proyecto_linkia.mi_nevera_app.utils

import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.RecipeEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeWithIngredients
import com.proyecto_linkia.mi_nevera_app.internet.pojoExternal.IngredientExternal
import com.proyecto_linkia.mi_nevera_app.internet.pojoExternal.RecipeExternal

/*
* EStas funciones permiten pasar de un objeto a otro
* entidad - externo - pojo
* */

/**
 * Transforma un ingredientExternal a entity
 */
fun IngredientExternal.toEntity(): IngredientEntity {
    return IngredientEntity(this.ingredient, this.ingredientName, this.ingredientNameEnglish)
}

/**
 * Transforma una recipeExterternal en entity
 */
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
        this.image,
        0
    )
}

/**
 * Transforma una recipeeithingredients en recipe
 */
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
        recipeEntity.image,
        this.recipeEntity.isFavourite != 0
    )
}

/**
 * transforma una recipe del usuario en entity
 */
fun Recipe.toEntity(id: String): RecipeEntity {
    return RecipeEntity(
        id, this.recipeName, "nullfromUser", if (this.isVegan) {
            1
        } else {
            0
        }, this.difficulty, this.time, if (this.image == "") {
            "nullFromUser"
        } else {
            this.image
        }, if (this.isFavourite) {
            1
        } else {
            0
        }
    )
}

/**
 * transforma una sring en IngrdientEntity
 */
fun String.toEntity(id: String): IngredientEntity {
    return IngredientEntity(id, this, "nullfromUser")
}