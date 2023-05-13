package com.proyecto_linkia.mi_nevera_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.proyecto_linkia.mi_nevera_app.pojo.UserProfile
import com.proyecto_linkia.mi_nevera_app.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoadSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_settings)

        //iniciamos una coroutina para acceder a la datastore preferences
        lifecycleScope.launch(Dispatchers.IO){
            getUserPreferences().collect {
                //cambiamos al contexto del hilo principal (ui) para actuar segun
                //las preferencias del usuario
                withContext(Dispatchers.Main) {
                    //si quiere modo oscuro lo activamos
                    //y ya se mantiene en las otras activities
                    if (it.mode) {
                        enableDarkMode()
                    }
                    //iniciamos la activity que el usuario quiera
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


    /**
     * Funcion que obtiene las preferencias de usuario de la datastore preferences
     *
     * @return objeto con las preferencias de usuario
     */
    private fun getUserPreferences() =
        //obtenemos las preferencias de la datastores y las pasamos a un objetouserprofile
        //ya que sino lo hacemos asi, solo permite obtener el ultimo valor
        //en caso de que no se haya guardado nada devolvemos un objeto igualmente para evitar valores
        //nulos
        dataStore.data.map { preferences ->
            UserProfile(
                activity = preferences[stringPreferencesKey("activity")].orEmpty(),
                mode = preferences[booleanPreferencesKey("mode")] ?: false
            )
        }


    /**
     * Funcion que activa el modo oscuro
     *
     */
    private fun enableDarkMode() {
        //cambiamos al modo oscuro com predeterinado y lo aplicamos
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
    }
}