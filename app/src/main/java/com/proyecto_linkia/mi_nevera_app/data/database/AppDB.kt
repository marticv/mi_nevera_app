package com.proyecto_linkia.mi_nevera_app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto_linkia.mi_nevera_app.data.dao.IngredientDao
import com.proyecto_linkia.mi_nevera_app.data.dao.MyIngredientDao
import com.proyecto_linkia.mi_nevera_app.data.dao.RecipeDao
import com.proyecto_linkia.mi_nevera_app.data.dao.ShoppingItemDao
import com.proyecto_linkia.mi_nevera_app.data.entities.relations.RecipeIngredientCrossReference
import com.proyecto_linkia.mi_nevera_app.data.entities.IngredientEntity
import com.proyecto_linkia.mi_nevera_app.data.entities.MyIngredient
import com.proyecto_linkia.mi_nevera_app.data.entities.RecipeEntity
import com.proyecto_linkia.mi_nevera_app.data.entities.ShoppingIngredient


@Database(
    entities = [
        MyIngredient::class,
        RecipeEntity::class,
        IngredientEntity::class,
        RecipeIngredientCrossReference::class,
        ShoppingIngredient::class],
    version = 1
)
abstract class AppDB : RoomDatabase() {
    abstract fun getMyIngredientDao(): MyIngredientDao
    abstract fun getRecipeDao(): RecipeDao
    abstract fun getShoppingIngredientDao(): ShoppingItemDao
    abstract fun getIngredientsDao(): IngredientDao
}