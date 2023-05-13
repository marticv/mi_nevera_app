package com.proyecto_linkia.mi_nevera_app.data.dao

import androidx.room.*
import com.proyecto_linkia.mi_nevera_app.data.entities.MyIngredient

@Dao
interface MyIngredientDao {

    @Query("SELECT * FROM my_ingredients_table")
    suspend fun getAllMyIngredients():List<MyIngredient>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyIngredient(myIngredient: MyIngredient)

    @Delete
    suspend fun deleteMyIngredient(myIngredient: MyIngredient)
}