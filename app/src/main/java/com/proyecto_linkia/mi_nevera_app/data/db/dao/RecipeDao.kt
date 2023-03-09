package com.proyecto_linkia.mi_nevera_app.data.db.dao

import androidx.room.*
import com.proyecto_linkia.mi_nevera_app.data.db.entities.RecipeEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeWithIngredients
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeIngredientCrossReference

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipeentity")
    suspend fun getAllRecipe():List<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(emptyRecipeEntity: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(emptyRecipeEntity: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredientsCrossReference(crossReference: RecipeIngredientCrossReference)

    @Transaction
    @Query("SELECT * FROM recipeentity WHERE recipeId = :recipeId")
    suspend fun getIngredientsOfRecipe(recipeId:Int):List<RecipeWithIngredients>
}