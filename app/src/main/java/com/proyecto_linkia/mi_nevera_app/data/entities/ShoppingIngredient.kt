package com.proyecto_linkia.mi_nevera_app.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ShoppingIngredient(
    @PrimaryKey(autoGenerate = false)
    val ingredientName: String,
    var toBuy: Int = 1,
    var bought: Int = 0
)
