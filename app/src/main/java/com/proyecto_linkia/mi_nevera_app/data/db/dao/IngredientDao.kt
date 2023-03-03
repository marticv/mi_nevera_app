package com.proyecto_linkia.mi_nevera_app.data.db.dao

import androidx.room.*
import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients_table")
    suspend fun getAllMyIngredients():List<IngredientEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredientEntity: IngredientEntity)

    @Delete
    suspend fun deleteIngredient(ingredientEntity: IngredientEntity)
}