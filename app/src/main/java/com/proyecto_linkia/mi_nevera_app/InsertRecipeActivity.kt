package com.proyecto_linkia.mi_nevera_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.RecipeEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeIngredientCrossReference
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityInsertRecipeBinding
import com.proyecto_linkia.mi_nevera_app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InsertRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //preparamos la parte visual
        setUp()

        //damos funcionalidad a los botones
        binding.btAddIngredient.setOnClickListener {
            addChipIfTextIsNotEmpty(binding.actvEntry, binding.cgIngredients)
        }
        binding.btAddRecipe.setOnClickListener {
            val recipe = obtainNewRecipeData()
            enterRecipeToDb(recipe)
            clearData()
        }
    }

    /**
     * Funcion que prepara la ui de la actividad
     *
     */
    private fun setUp() {
        //damos opciones de ingredientes
        fillActvEntry(binding.actvEntry)
        //damos las opciones de dificultad
        val difficultyOptions: Array<String> = resources.getStringArray(R.array.difficultyItems)
        fillSpinner(binding.spDifficulty, difficultyOptions)
    }

    /**
     * Función que obtiene los datos para una objeto receta
     * a partir de los datos del usuario
     *
     * @return Recipe
     */
    private fun obtainNewRecipeData(): Recipe {
        return Recipe(
            //leemos los datos introducidos por el usuario
            binding.etRecipeName.text.toString(),
            obtainSelectedIngredients(binding.cgIngredients),
            binding.swIsVegan.isChecked,
            binding.spDifficulty.selectedItem.toString(),
            binding.editTextNumber.text.toString().toInt(),
            ""
        )
    }

    /**
     * Funcion que reinicia la ui
     *
     */
    private fun clearData() {
        //ponemos todos los campos al estado inicial
        binding.etRecipeName.setText("")
        binding.actvEntry.setText("")
        binding.cgIngredients.removeAllViews()
        binding.spDifficulty.setSelection(0)
        binding.editTextNumber.setText("")
    }

    /**
     * Funcion que introduce una receta a la base de datos
     *
     * @param recipe
     */
    private fun enterRecipeToDb(recipe: Recipe) {
        //creamos una instancia de la base de datos
        //y obtenemos los dao necesarios
        val db = DataBaseBuilder.getInstance(this@InsertRecipeActivity)
        val recipeDao = db.getRecipeDao()
        val ingredientDao = db.getIngredientsDao()

        //lanzamos corutina para trabajar fuera de la ui
        CoroutineScope(Dispatchers.IO).launch {
            //trabajamos con withContext para que los pasos se hagan sequencialmente

            //obtenemos un numero de receta, para ello buscamos el numero de recetas
            //en la bd y sumamos uno para asegurarnos de que no hay errores
            val recipeNumber = withContext(Dispatchers.IO) {
                recipeDao.getUserRecipeNumber() + 1
            }
            //al numero de receta les añaimos B para diferenciarla de
            //las recetas de internet
            val recipeId = "B$recipeNumber"
            //insertamos receta
            val recipeToDB: RecipeEntity = recipe.toEntity(recipeId)
            withContext(Dispatchers.IO) {
                recipeDao.insertRecipe(recipeToDB)
            }
            //insertaremos ingredientes uno a uno
            //primero miramos si el ingrediente ya esta en el sistema
            //y lo añadimos con la misma logica que la receta
            //finalmente añadimos una relacion ingrediente/receta
            for (ingredient in recipe.ingredients) {
                val ingredientInDb = ingredientDao.checkIngredient(ingredient)
                if (ingredientInDb == 0) {
                    val idNumber: Int = ingredientDao.checkIngredientsNumber()
                    val ingredientId = "B$idNumber"
                    ingredientDao.insertIngredient(ingredient.toEntity(ingredientId))
                    recipeDao.insertRecipeIngredientsCrossReference(
                        RecipeIngredientCrossReference(
                            recipeId,
                            ingredientId
                        )
                    )
                } else {
                    val ingredientId = ingredientDao.getIngredientFromName(ingredient).ingredientId
                    recipeDao.insertRecipeIngredientsCrossReference(
                        RecipeIngredientCrossReference(
                            recipeId,
                            ingredientId
                        )
                    )
                }
            }
        }
        //cerramos conexion
        if (db.isOpen) {
            db.openHelper.close()
        }
    }
}