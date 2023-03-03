package com.proyecto_linkia.mi_nevera_app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emptyrecipe_table")
data class EmptyRecipeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipeId")
    val id_recipe: Int?,
    @ColumnInfo(name = "recipeName")
    val recipeName: String,
    @ColumnInfo(name = "isVegan")
    val isVegan: Boolean
)
