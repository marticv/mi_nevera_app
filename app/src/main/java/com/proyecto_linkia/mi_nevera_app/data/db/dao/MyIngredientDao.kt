package com.proyecto_linkia.mi_nevera_app.data.db.dao

import androidx.room.*
import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredientEntity

@Dao
interface MyIngredientDao {

    @Query("SELECT * FROM my_ingredients_table")
    suspend fun getAllMyIngredients():List<MyIngredientEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyIngredient(myIngredientEntity: MyIngredientEntity)

    @Delete
    suspend fun deleteMyIngredient(myIngredientEntity: MyIngredientEntity)
}