package com.proyecto_linkia.mi_nevera_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityInsertRecipeBinding
import com.proyecto_linkia.mi_nevera_app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class InsertRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //preparamos la parte visual
        setUp()

        binding.btAddIngredient.setOnClickListener {
            addChipIfTextIsNotEmpty()
        }

        binding.btAddRecipe.setOnClickListener {
            obtainNewRecipeData()
        }
    }

    private fun setUp() {
        //damos opciones de ingredientes
        fillActvEntry(binding.actvEntry)
        //damos las opciones de dificultad
        val difficultyOptions: Array<String> = resources.getStringArray(R.array.difficultyItems)
        fillSpinner(binding.spDifficulty, difficultyOptions)
    }

    private fun addChipIfTextIsNotEmpty() {
        if (binding.actvEntry.text.toString().isNotEmpty()) {
            addChip(binding.actvEntry.text.toString(), binding.cgIngredients)
            binding.actvEntry.setText("")
        }
    }

    private fun obtainNewRecipeData(): Recipe {
        return Recipe(
            binding.etRecipeName.text.toString(),
            obtainSelectedIngredients(binding.cgIngredients),
            binding.swIsVegan.isChecked,
            binding.spDifficulty.selectedItem.toString(),
            binding.editTextNumber.text.toString().toInt(),
            ""
        )
    }

    private fun enterRecipeToDb(recipe: Recipe){
        val db = DataBaseBuilder.getInstance(this@InsertRecipeActivity)
        val recipeDao = db.getRecipeDao()
        val ingredientDao = db.getIngredientsDao()


        CoroutineScope(Dispatchers.IO).launch {
            var recipeNumber = recipeDao.getUserRecipeNumber()+1
            val recipeId ="B+$recipeNumber"

            recipeDao.insertRecipe(recipe.toEntity(recipeId))
            for(ingredients in recipe.ingredients){
             TODO()
            }
        }
    }
}