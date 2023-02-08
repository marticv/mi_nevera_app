package com.proyecto_linkia.mi_nevera_app.data.db.database

import android.app.Application
import androidx.room.Room

class MyIngredientsApp:Application() {
    val room = Room.databaseBuilder(
        this,
        AppDB::class.java,
        "database").build()
}