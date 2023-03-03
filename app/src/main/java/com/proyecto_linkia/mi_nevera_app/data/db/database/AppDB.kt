package com.proyecto_linkia.mi_nevera_app.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto_linkia.mi_nevera_app.data.db.dao.EmptyRecipeDao
import com.proyecto_linkia.mi_nevera_app.data.db.dao.IngredientDao
import com.proyecto_linkia.mi_nevera_app.data.db.dao.MyIngredientDao
import com.proyecto_linkia.mi_nevera_app.data.db.entities.EmptyRecipeEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredient


@Database(
    entities = [MyIngredient::class,IngredientEntity::class,EmptyRecipeEntity::class],
    version = 1)
abstract class AppDB:RoomDatabase() {
    abstract fun getMyIngredientDao():MyIngredientDao
    abstract fun getEmptyRecipeDao():EmptyRecipeDao
    abstract fun getIngredientsDao():IngredientDao
}