package com.proyecto_linkia.mi_nevera_app

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.internal.bind.util.ISO8601Utils.format
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityRecipeInformationBinding
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.util.*


class RecipeInformation : AppCompatActivity() {
    private lateinit var recipe: Recipe
    private var screenBitmap: Bitmap?=null
    private lateinit var binding: ActivityRecipeInformationBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRecipeInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)


            recipe = intent.extras?.get("recipe") as Recipe



        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        window.setLayout((width*0.85).toInt(),(height*0.7).toInt())

        binding.tvRecipeName.text = recipe.recipeName

        var ingredients =""
        for (ingredient in recipe.ingredients){
            ingredients+=ingredient
        }
        binding.tvIngredients.text = ingredients

        if(recipe.isVegan){
            binding.tvVegan.text = "Vegana"
        }

        val image ="https://www.cocinacaserayfacil.net/wp-content/uploads/2020/03/Recetas-faciles-de-cocinar-y-sobrevivir-en-casa-al-coronavirus_2.jpg"
        Picasso.get().load(image).into(binding.ivRecipe)

        binding.btClose.setOnClickListener {
            finish()
        }

        binding.btShare.setOnClickListener {
            share()
        }
    }

    private fun share() {
/*
        view = bind
        screenBitmap = Bitmap.createBitmap()

*/

        val intent= Intent().apply{
            action =Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Hoy cocinamos:\n" +
                    "${recipe.recipeName}\nEnviado desde mi_nevaraApp")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent,null)
        startActivity(shareIntent)
    }
}