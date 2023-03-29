package com.proyecto_linkia.mi_nevera_app.data.db.dao

import androidx.room.*
import com.proyecto_linkia.mi_nevera_app.data.db.entities.ShoppingIngredient

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM shoppingingredient WHERE toBuy=1")
    suspend fun getAllToBuyIngredients(): List<ShoppingIngredient>

    @Query("SELECT * FROM shoppingingredient WHERE bought=1")
    suspend fun getAllBoughtIngredients(): List<ShoppingIngredient>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToBuyIngredient(toBuyIngredient: ShoppingIngredient)

    @Update
    suspend fun updateShoppingIngredient(shoppingIngredient: ShoppingIngredient)

    @Delete
    suspend fun deleteShoppingIngredient(shoppingIngredient: ShoppingIngredient)
}