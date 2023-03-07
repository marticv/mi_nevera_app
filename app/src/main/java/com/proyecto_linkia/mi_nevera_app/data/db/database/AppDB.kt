package com.proyecto_linkia.mi_nevera_app.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto_linkia.mi_nevera_app.data.db.dao.RecipeDao
import com.proyecto_linkia.mi_nevera_app.data.db.dao.IngredientDao
import com.proyecto_linkia.mi_nevera_app.data.db.dao.MyIngredientDao
import com.proyecto_linkia.mi_nevera_app.data.db.entities.RecipeEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredient
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeIngredientCrossReference


@Database(
    entities = [
        MyIngredient::class,
        RecipeEntity::class,
        IngredientEntity::class,
        RecipeIngredientCrossReference::class],
    version = 1
)
abstract class AppDB : RoomDatabase() {
    abstract fun getMyIngredientDao(): MyIngredientDao
    abstract fun getRecipeDao(): RecipeDao
    abstract fun getIngredientsDao(): IngredientDao
}