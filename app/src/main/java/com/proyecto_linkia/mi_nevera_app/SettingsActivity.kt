package com.proyecto_linkia.mi_nevera_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatDelegate
import com.proyecto_linkia.mi_nevera_app.databinding.ActivitySettingsBinding
import com.proyecto_linkia.mi_nevera_app.utils.getAllIngredients

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //llenamos el spinner con las opciones
        fillSpinner()

        //damos funcionalidad al switch
        binding.swDarkMode.setOnCheckedChangeListener { _, isSelected ->
            if (isSelected) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
        }

        binding.button.setOnClickListener {
            finish()
        }
    }


    private fun fillSpinner(){
        //obtenemos las opciones y las pasamos al adapter
        val activityOptions: Array<String> = resources.getStringArray(R.array.ActivityItems)
        com.proyecto_linkia.mi_nevera_app.utils.fillSpinner(binding.spChoseActivity, activityOptions)
    }

    private fun enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
    }

    private fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()
    }
}