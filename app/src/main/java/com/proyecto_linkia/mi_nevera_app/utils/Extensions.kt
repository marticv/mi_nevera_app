package com.proyecto_linkia.mi_nevera_app.utils

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

//creamos el delegado con el by para que sea un singleton
val Context.dataStore by preferencesDataStore(name = "USER_PREFERENCES_NAME")