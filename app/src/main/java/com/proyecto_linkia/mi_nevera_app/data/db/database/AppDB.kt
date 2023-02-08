package com.proyecto_linkia.mi_nevera_app.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto_linkia.mi_nevera_app.data.db.dao.MyIngredientDao
import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredientEntity

@Database(
    entities = [MyIngredientEntity::class],
    version = 1)
abstract class AppDB:RoomDatabase() {
    abstract fun getMyIngredientDao():MyIngredientDao
}