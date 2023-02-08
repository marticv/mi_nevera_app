package com.proyecto_linkia.mi_nevera_app.data.db.database

import android.app.Application
import androidx.room.Room

class MyIngredientsApp:Application() {
    companion object{
        lateinit var database:AppDB
    }

    override fun onCreate() {
        super.onCreate()
        MyIngredientsApp.database =  Room.databaseBuilder(this, AppDB::class.java, "appdb").build()
    }

}