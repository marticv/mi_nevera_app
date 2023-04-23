package com.proyecto_linkia.mi_nevera_app.data.db.dao

import androidx.room.*
import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingrediententity")
    suspend fun getAllIngredients():List<IngredientEntity>

    @Query("SELECT * FROM ingrediententity WHERE ingredientName=:name")
    suspend fun getIngredientFromName(name:String):IngredientEntity

    @Query("SELECT COUNT(ingredientName) FROM ingrediententity WHERE ingredientName=:name")
    suspend fun checkIngredient(name:String):Int

    @Query("SELECT COUNT(ingredientName) FROM ingrediententity WHERE ingredientId LIKE 'A%'")
    suspend fun checkIngredientsNumber():Int

    @Query("DELETE FROM ingrediententity WHERE ingredientId LIKE 'A%'")
    suspend fun deleteIngredientsExternal()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredientEntity: IngredientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIngredientList(ingredientEntityList: List<IngredientEntity>)

    @Delete
    suspend fun deleteIngredient(ingredientEntity: IngredientEntity)
}