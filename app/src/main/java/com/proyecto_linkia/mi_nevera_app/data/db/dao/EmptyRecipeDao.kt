package com.proyecto_linkia.mi_nevera_app.data.db.dao

import androidx.room.*
import com.proyecto_linkia.mi_nevera_app.data.db.entities.EmptyRecipeEntity

@Dao
interface EmptyRecipeDao {
    @Query("SELECT * FROM emptyrecipe_table")
    suspend fun getAllMyEmptyRecipe():List<EmptyRecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmptyRecipe(emptyRecipeEntity: EmptyRecipeEntity)

    @Delete
    suspend fun deleteEmptyRecipe(emptyRecipeEntity: EmptyRecipeEntity)
}