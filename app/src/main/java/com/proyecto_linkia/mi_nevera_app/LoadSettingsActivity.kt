package com.proyecto_linkia.mi_nevera_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.proyecto_linkia.mi_nevera_app.clases.UserProfile
import com.proyecto_linkia.mi_nevera_app.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoadSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_settings)

        lifecycleScope.launch(Dispatchers.IO) {
            getUserPreferences().collect {
                withContext(Dispatchers.Main) {
                    if (it.mode) {
                        enableDarkMode()
                    }
                    when (it.activity) {
                        "Mis Ingredientes" -> startActivity(
                            Intent(
                                this@LoadSettingsActivity,
                                MyIngredients::class.java
                            )
                        )
                        "Receta rapida" -> startActivity(
                            Intent(
                                this@LoadSettingsActivity,
                                SearchActivity::class.java
                            )
                        )
                        "Lista de la compra" -> startActivity(
                            Intent(
                                this@LoadSettingsActivity,
                                ShoppingActivity::class.java
                            )
                        )
                        "Introducir receta" -> startActivity(
                            Intent(
                                this@LoadSettingsActivity,
                                InsertRecipeActivity::class.java
                            )
                        )
                        else -> startActivity(
                            Intent(
                                this@LoadSettingsActivity,
                                MyIngredients::class.java
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getUserPreferences() = dataStore.data.map { preferences ->
        UserProfile(
            activity = preferences[stringPreferencesKey("activity")].orEmpty(),
            mode = preferences[booleanPreferencesKey("mode")] ?: false
        )
    }

    private fun enableDarkMode() {
        //cambiamos al modo oscuro com predeterinado y lo aplicamos
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
    }
}