package com.proyecto_linkia.mi_nevera_app.data.db.database

import android.content.Context
import androidx.room.Room

object DataBaseBuilderUIThread {

    private var INSTANCE:AppDB? = null
    fun getInstance(context:Context):AppDB{
        if(INSTANCE==null){
            synchronized(AppDB::class){
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }
    private fun buildRoomDB(context: Context)=
        Room.databaseBuilder(
            context.applicationContext,
            AppDB::class.java,
            "database-app"
        ).allowMainThreadQueries().build()
}