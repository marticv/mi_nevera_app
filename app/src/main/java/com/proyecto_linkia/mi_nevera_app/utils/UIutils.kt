package com.proyecto_linkia.mi_nevera_app.utils

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeWithIngredients
import kotlinx.coroutines.*


/**
 * Funcion que devuelve una lista de todas las recetas de la bd
 *
 * @param context
 * @return
 */
fun getRecipesList(context: Context): MutableList<Recipe> {
    //creamos variables y conexion a la base de datso
    val recipeList: MutableList<Recipe> = mutableListOf()
    val db = DataBaseBuilder.getInstance(context)
    val recipeDao = db.getRecipeDao()

    //iniciamos coroutina para trabajar con la bd
    CoroutineScope(Dispatchers.IO).async {
        //intentamos obtenes la lista de recetas o devolvemos una vacia para evitar errores
        //y cerramos la base de datos
        try {
            //obtenemos la lista de recetas
            val recipes = recipeDao.getAllRecipe()
            val list: MutableList<RecipeWithIngredients> = mutableListOf()

            //obtenemos la lista de ingredientes para cada receta
            for (recipe in recipes) {
                list.add(recipeDao.getIngredientsOfRecipe(recipe.recipeId))
            }

            //creamos una receta completa para cada receta de la bd
            for (recipeWithIngredients in list) {
                recipeList.add(recipeWithIngredients.toRecipe())
            }
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "error al obtener recetas")
        } finally {
            db.close()
        }
    }
    return recipeList
}

/**
 * funcion que rellena la info de un autocomlpeteTextView con los ingredientes de la bd
 *
 * @param actvEntry
 */
fun fillActvEntry(actvEntry: AutoCompleteTextView) {
    //obtenemos lista de ingredientes
    val ingredientsList = getAllIngredients(actvEntry.context)
    //creamos el adaptados y lo pasamos al autocompletetextview
    val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
        actvEntry.context,
        android.R.layout.simple_dropdown_item_1line,
        ingredientsList
    )
    actvEntry.setAdapter(adapter)
}


/**
 * Funcion que devuelve un arraylist de strings con el nombre de todos los inrgedientes
 *
 * @param context
 * @return ArrayList<String>
 */
fun getAllIngredients(context: Context): ArrayList<String> {
    //preparamos variables
    var ingredientList = mutableListOf<IngredientEntity>()
    val result: ArrayList<String> = ArrayList()

    //iniciamos coroutina para obtener datos de room
    runBlocking {
        ingredientList = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val db = DataBaseBuilder.getInstance(context)
            val dao = db.getIngredientsDao()

            //devolvemos lista de ingredients o vacia si hay error
            try {
                ingredientList = dao.getAllIngredients() as MutableList<IngredientEntity>
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "error al obtener ingredientes")
            } finally {
                db.close()
            }
            ingredientList
        }
    }
    //pasamos resultado a arraylist<string>
    for (ingerdient in ingredientList) {
        result.add(ingerdient.ingredientName)
    }
    return result
}
