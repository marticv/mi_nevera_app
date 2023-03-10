package com.proyecto_linkia.mi_nevera_app

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityRecipeInformationBinding

class RecipeInformation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val binding = ActivityRecipeInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipe =intent.extras?.get("recipe") as Recipe

        var metrics = DisplayMetrics()
        //val display = Context.getDisplay()
        windowManager.defaultDisplay.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        window.setLayout((width*0.85).toInt(),(height*0.5).toInt())

        binding.tvRecipeName.text = recipe.recipeName

        var ingredients =""
        for (ingredient in recipe.ingredients){
            ingredients+=ingredient
        }
        binding.tvIngredients.text = ingredients

        if(recipe.isVegan){
            binding.tvVegan.text = "Vegana"
        }

        binding.btFinish.setOnClickListener {
            finish()
        }
    }
}