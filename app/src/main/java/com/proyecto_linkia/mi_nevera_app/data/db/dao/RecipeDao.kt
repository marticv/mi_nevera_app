package com.proyecto_linkia.mi_nevera_app.data.db.dao

import androidx.room.*
import com.proyecto_linkia.mi_nevera_app.data.db.entities.RecipeEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeWithIngredients
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeIngredientCrossReference

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipeentity")
    suspend fun getAllRecipe():List<RecipeEntity>

    @Query("SELECT COUNT(recipeId) FROM recipeentity WHERE recipeId LIKE 'B%'")
    suspend fun getUserRecipeNumber():Int

    @Query("SELECT COUNT(recipeId) FROM recipeentity WHERE recipeId LIKE :recipeId")
    suspend fun recipeInDB(recipeId: String):Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(emptyRecipeEntity: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipeList(recipeEntityList: List<RecipeEntity>)

    @Delete
    suspend fun deleteRecipe(emptyRecipeEntity: RecipeEntity)

    @Query("UPDATE recipeentity SET isFavourite =1 WHERE recipeName =:recipeName ")
    suspend fun udateToFavourite(recipeName:String)

    @Query("UPDATE recipeentity SET isFavourite =0 WHERE recipeName =:recipeName ")
    suspend fun udateToNotFavourite(recipeName:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredientsCrossReference(crossReference: RecipeIngredientCrossReference)

    @Transaction
    @Query("SELECT * FROM recipeentity WHERE recipeId = :recipeId")
    suspend fun getIngredientsOfRecipe(recipeId:String):RecipeWithIngredients
}