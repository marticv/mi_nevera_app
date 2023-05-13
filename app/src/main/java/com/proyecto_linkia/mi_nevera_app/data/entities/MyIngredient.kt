package com.proyecto_linkia.mi_nevera_app.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_ingredients_table")
data class MyIngredient(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="ingredientName")
    val ingredientName: String)

