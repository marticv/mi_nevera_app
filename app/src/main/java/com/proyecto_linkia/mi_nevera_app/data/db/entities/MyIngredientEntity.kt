package com.proyecto_linkia.mi_nevera_app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.proyecto_linkia.mi_nevera_app.clases.Ingredient

@Entity(tableName = "my_ingredients_table")
data class MyIngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id_ingredient:Int,
    @ColumnInfo(name="ingredientName")
    val ingredientName: String)

fun Ingredient.toEntity() = MyIngredientEntity(0,ingredientName)