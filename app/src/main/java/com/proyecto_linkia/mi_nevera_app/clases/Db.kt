package com.proyecto_linkia.mi_nevera_app.clases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val DB_NEVERA = "miNevera.db"

val TABLE_INGREDIENTES = "ingredientes"
val TABLE_INGREDIENTES_COL_ID = "_id"
val TABLE_INGREDIENTES_COL_NAME = "name"
val TABLE_INGREDIENTES_COL_NAME_EN = "nameEn"
val TABLE_INGREDIENTES_COL_IS_VEGAN = "isVegan"

val TABLE_RECETA = "recetas"
val TABLE_RECETA_COL_ID = "_id"
val TABLE_RECETA_COL_NAME = "name"
val TABLE_RECETA_COL_NAME_EN = "nameEn"

val TABLE_RECETA_CON_INGREDINTES = "recetasConIngredeintes"
val TABLE_RECETA_CON_INGREDINTES_COL_RECETA_ID = "recetaId"
val TABLE_RECETA_CON_INGREDINTES_COL_INGREDIENTE_ID = "ingredienteId"

val TABLE_AVAILABLE_INGREDIENTES = "availableIngredientes"
val TABLE_AVAILABLE_INGREDIENTES_INGDIENTE_ID = "ingredienteId"
val TABLE_AVAILABLE_INGREDIENTES_CANTIDAD = "cantidad"


class DbNevera(ctx: Context): SQLiteOpenHelper(ctx, DB_NEVERA, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        // Create Ingredients table
        var sqlOrder =
            "CREATE TABLE $TABLE_INGREDIENTES (" +
                    "$TABLE_INGREDIENTES_COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$TABLE_INGREDIENTES_COL_NAME TEXT," +
                    "$TABLE_INGREDIENTES_COL_NAME_EN TEXT," +
                    "$TABLE_INGREDIENTES_COL_IS_VEGAN BIT)"
        db!!.execSQL(sqlOrder)

        // Create Receta table
        sqlOrder = "CREATE TABLE $TABLE_RECETA (" +
                "$TABLE_RECETA_COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$TABLE_RECETA_COL_NAME TEXT," +
                "$TABLE_RECETA_COL_NAME_EN TEXT)"
        db!!.execSQL(sqlOrder)

        // Create RecetasConIngredientes table
        sqlOrder = "CREATE TABLE $TABLE_RECETA_CON_INGREDINTES (" +
                "$TABLE_RECETA_CON_INGREDINTES_COL_RECETA_ID INT NOT NULL," +
                "$TABLE_RECETA_CON_INGREDINTES_COL_INGREDIENTE_ID INT NOT NULL," +
                "PRIMARY KEY ($TABLE_RECETA_CON_INGREDINTES_COL_RECETA_ID, $TABLE_RECETA_CON_INGREDINTES_COL_INGREDIENTE_ID)"
                "FOREIGN KEY ($TABLE_RECETA_CON_INGREDINTES_COL_RECETA_ID) REFERENCES $TABLE_RECETA($TABLE_RECETA_COL_ID))" +
                "FOREIGN KEY ($TABLE_RECETA_CON_INGREDINTES_COL_INGREDIENTE_ID) REFERENCES $TABLE_INGREDIENTES($TABLE_INGREDIENTES_COL_ID))"
        db!!.execSQL(sqlOrder)

        // Create AvailableIngredients table
        sqlOrder = "CREATE TABLE $TABLE_AVAILABLE_INGREDIENTES (" +
                "$TABLE_AVAILABLE_INGREDIENTES_INGDIENTE_ID INT NOT NULL," +
                "$TABLE_AVAILABLE_INGREDIENTES_CANTIDAD INT," +
                "PRIMARY KEY $TABLE_AVAILABLE_INGREDIENTES_INGDIENTE_ID"
                "FOREIGN KEY ($TABLE_AVAILABLE_INGREDIENTES_INGDIENTE_ID) REFERENCES $TABLE_INGREDIENTES($TABLE_INGREDIENTES_COL_ID))"
        db!!.execSQL(sqlOrder)

        // Populate tables with data
        populateTables(db);
    }

    override fun onUpgrade(db: SQLiteDatabase?, origVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun populateTables(db: SQLiteDatabase) {
        val sqlOrder =
            "INSERT INTO $TABLE_INGREDIENTES (" +
                    "$TABLE_INGREDIENTES_COL_NAME," +
                    "$TABLE_INGREDIENTES_COL_NAME_EN," +
                    "$TABLE_INGREDIENTES_COL_IS_VEGAN)" +
                "values (" +
                    "Patata," +
                    "Potato," +
                    "true" +
                "), (" +
                    "Leche," +
                    "Milk," +
                    "false" +
                ")"
        db!!.execSQL(sqlOrder)
    }

    // TODO: this functions should execute the select instructions
    fun getIngredeintes(db: SQLiteDatabase) {

    }

    fun setAvailableIngrediente() {
        // Add or update register
    }

    fun getRecetas() {}

    fun getRecetasByAvailableIngrediente() {}

}