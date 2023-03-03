package com.proyecto_linkia.mi_nevera_app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients_table")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_ingredient")
    val id_ingredient: Int,
    @ColumnInfo(name = "ingredientName")
    val ingredientName: String
)
