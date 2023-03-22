package com.proyecto_linkia.mi_nevera_app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BoughtIngredient(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "ingredientName")
    val ingredientName: String
)
