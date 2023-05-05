package com.proyecto_linkia.mi_nevera_app

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.proyecto_linkia.mi_nevera_app.clases.UserProfile
import com.proyecto_linkia.mi_nevera_app.databinding.ActivitySettingsBinding
import com.proyecto_linkia.mi_nevera_app.utils.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //preparamos la activity
        setup()

        //damos funcionalidad al switch y al boton
        binding.swDarkMode.setOnCheckedChangeListener { _, isSelected ->
            if (isSelected) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
        }

        binding.btSave.setOnClickListener {
            saveValues(
                isUsingNightModeResources(),
                binding.swVegan.isChecked,
                binding.spChoseActivity.selectedItem.toString()
            )
        }

        binding.btClose.setOnClickListener {
            finish()
        }
    }

    private fun saveValues(mode: Boolean, vegan: Boolean, activity: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[booleanPreferencesKey("mode")] = mode
                preferences[stringPreferencesKey("activity")] = activity
                preferences[booleanPreferencesKey(name = "vegan")] = vegan
            }
        }
    }


    /**
     * Funcion que prepara la ui de la activity
     *
     */
    private fun setup() {
        //llenamos el spinner con las opciones
        fillSpinner()
        //si el modo oscuro esta activado, activamos el switch
        if (isUsingNightModeResources()) binding.swDarkMode.isChecked = true
        //si el usuario prefiere sempre vegano activamos el switch
        lifecycleScope.launch(Dispatchers.IO) {
            getUserPreferences().collect{
                withContext(Dispatchers.Main){
                    if(it){
                        binding.swVegan.isChecked=true
                    }
                }
            }
        }
    }

    /**
     * Funcion que rellena el spiner
     *
     */
    private fun fillSpinner() {
        //obtenemos las opciones y las pasamos al adapter
        val activityOptions: Array<String> = resources.getStringArray(R.array.ActivityItems)
        com.proyecto_linkia.mi_nevera_app.utils.fillSpinner(
            binding.spChoseActivity,
            activityOptions
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

    /**
     * Funcion que desactiva el modo oscuro
     *
     */
    private fun disableDarkMode() {
        //cambiamos al modo lumimoso com predeterinado y lo aplicamos
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()
    }

    /**
     * Función que comprueba si la app esta usanando el modo oscuro
     *
     * @return true si estamos con modo oscuro
     */
    private fun isUsingNightModeResources(): Boolean {
        return when (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    private fun getUserPreferences() = dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(name = "vegan")] ?:false
    }

}