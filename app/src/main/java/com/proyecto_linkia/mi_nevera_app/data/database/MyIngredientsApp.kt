package com.proyecto_linkia.mi_nevera_app.data.database

import android.app.Application
import androidx.room.Room

class MyIngredientsApp:Application() {
    companion object{
        lateinit var database: AppDB
    }

    override fun onCreate() {
        super.onCreate()
        database =  Room.databaseBuilder(this, AppDB::class.java, "appdb").build()
    }

}