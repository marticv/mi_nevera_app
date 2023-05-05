package com.proyecto_linkia.mi_nevera_app


import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.internal.LifecycleActivity
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import com.proyecto_linkia.mi_nevera_app.adapter.RecipeAdapter
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.clases.UserProfile
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeWithIngredients
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMainBinding
import com.proyecto_linkia.mi_nevera_app.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map


class SearchActivity : AppCompatActivity() {
    private lateinit var actvEntry: AutoCompleteTextView
    private lateinit var btAddIngedient: Button
    private lateinit var cgIngredients: ChipGroup
    private lateinit var btSearch: Button
    private lateinit var tvResultados: TextView
    private lateinit var sVegan: SwitchMaterial
    private lateinit var spDifficulty: Spinner
    private lateinit var binding: ActivityMainBinding
    private var recipeList: MutableList<Recipe> = mutableListOf()
    var correctRecipes: MutableList<Recipe> = mutableListOf()
    private lateinit var adapter: RecipeAdapter
    private val llManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        //creamos el binding no tener que hacer el findviewbyid
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //creamos objetos para todos los Views
        actvEntry = binding.actvEntry
        btAddIngedient = binding.btAddIngredient
        cgIngredients = binding.cgIngredients
        btSearch = binding.btSearch
        sVegan = binding.sVegan
        tvResultados = binding.tvResultados
        spDifficulty = binding.spDifficulty

        //preparamos la activity
        setUp()

        tvResultados.text = recipeList.size.toString()

        //damos funcionalidad a los botones
        btAddIngedient.setOnClickListener {
            addChipIfTextIsNotEmpty(actvEntry, cgIngredients)
        }

        btSearch.setOnClickListener {
            if (recipeList.size == 0) {
                getRecipesList()
            }
            searchSuitableRecipes()
        }
    }

    private fun getRecipesList() {
        //creamos variables y conexion a la base de datso
        //val recipeList: MutableList<Recipe> = mutableListOf()
        val db = DataBaseBuilder.getInstance(this@SearchActivity)
        val recipeDao = db.getRecipeDao()

        //iniciamos coroutina para trabajar con la bd
        CoroutineScope(Dispatchers.IO).launch {
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
                runOnUiThread {
                    for (recipeWithIngredients in list) {
                        recipeList.add(recipeWithIngredients.toRecipe())
                    }
                }
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "error al obtener recetas")
            } finally {
                db.close()
            }
        }
    }

    private fun searchSuitableRecipes() {
        correctRecipes.clear()
        if (recipeList.isEmpty()) {
            tvResultados.text = "sin resultados"
        } else {
            val selectedIngr: ArrayList<String> = obtainSelectedIngredients(cgIngredients)
            var myRecipes = recipeList
            var resultRecipes = findSuitableRecipes(selectedIngr, myRecipes)

            if (checkVegan(sVegan)) {
                for (i in 0..resultRecipes.size - 1) {
                    if (resultRecipes.get(i).isVegan == false) {
                        resultRecipes.remove(resultRecipes.get(i))
                    }
                }
            }

            if (correctRecipes.isEmpty()) tvResultados.text = "sin resultados"

            for (recipe in resultRecipes) {
                correctRecipes.add((recipe))
                adapter.notifyDataSetChanged()
            }

            binding.rvRecipe.visibility = View.VISIBLE
        }
    }

    private fun setUp() {
        getRecipesList()
        fillActvEntry(actvEntry)
        val difficultyOptions: Array<String> = resources.getStringArray(R.array.difficultyItems)
        fillSpinner(spDifficulty, difficultyOptions)
        initRecycleView()
        applyUserPreferences()
    }

    private fun applyUserPreferences(){
        lifecycleScope.launch(Dispatchers.IO) {
            getUserPreferences().collect{
                withContext(Dispatchers.Main){
                    if(it){
                        binding.sVegan.isChecked=true
                    }
                }
            }
        }
    }
    private fun getUserPreferences() = dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(name = "vegan")] ?:false
    }


    /**
     * Compara la lista de recetas del sistema con la lista de ingredientes para ver cuales son las recetas adientes
     *
     * @param ingredients seleccionados por el usuario
     * @param recipes del sistema
     * @return lista con las recetas que cumplen los parametros
     */
    private fun findSuitableRecipes(
        ingredients: ArrayList<String>, recipes: List<Recipe>
    ): ArrayList<Recipe> {
        val correctRecipes: ArrayList<Recipe> = ArrayList()
        for (recipe in recipes) {
            if (checkRecipe(recipe, ingredients)) correctRecipes.add(recipe)
        }
        return correctRecipes
    }

    /**
     * comprueba si una receta se puede hacer con los ingredientes seleccionados
     *
     * @param recipe del sistema
     * @param selectedIngredients ingredientes entrados por el usuario
     * @return true si la receta cumple los criterios o false si no
     */
    private fun checkRecipe(recipe: Recipe, selectedIngredients: ArrayList<String>): Boolean {
        var count = 0
        val ingredientNumber: Int = recipe.ingredients.size
        var ingredientInRecipe: String
        if (checkVegan(sVegan)) {
            if (!recipe.isVegan) return false
        }

        if(!checkDifficulty(recipe)) return false
        if(!checkFavourites(binding.swFavourites,recipe)) return false

        for (i in 0 until recipe.ingredients.size) {
            ingredientInRecipe = recipe.ingredients[i]
            if (selectedIngredients.contains(ingredientInRecipe)) count++
        }
        return count == ingredientNumber
    }

    private fun checkFavourites(switch: SwitchMaterial,recipe: Recipe): Boolean {
        if(switch.isChecked){
            if(!recipe.isFavourite) return false
        }
        return true
    }

    /**
     * Funcion que compara la dificultad de una receta
     * con la maxima requerida por el usuario
     *
     * @param recipe
     * @return
     */
    private fun checkDifficulty(recipe: Recipe): Boolean {
        when (spDifficulty.selectedItem.toString()) {
            "facil" -> return recipe.difficulty == "facil"
            "medio" -> return recipe.difficulty == "facil" || recipe.difficulty == "medio"
            "dificil" -> return true
            else -> return false
        }
    }


    /**
     * Comprobamos si el estado del switch
     *
     * @param sVegan
     * @return true si esta checked
     */
    private fun checkVegan(sVegan: SwitchMaterial): Boolean {
        return sVegan.isChecked
    }


    private fun initRecycleView() {
        //creamos el adapter y lo pasamos al recyclerview para que se renderice
        val recyclerView = binding.rvRecipe
        adapter = RecipeAdapter(recipeList = correctRecipes, onClickListener = { position ->
            showRecipe(position)
        }, onClickFavourite = { position -> onClickFavourite(position) })
        recyclerView.layoutManager = llManager
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    private fun onClickFavourite(position: Int) {
        val recipe = correctRecipes[position]

        lifecycleScope.launch(Dispatchers.IO) {
            val db = DataBaseBuilder.getInstance(this@SearchActivity)
            val recipeDao = db.getRecipeDao()
            if(recipe.isFavourite){
                recipeDao.udateToNotFavourite(recipe.recipeName)
            }else{
                recipeDao.udateToFavourite(recipe.recipeName)
            }
            db.close()
        }
    }

    private fun showRecipe(position: Int) {
        val recipe = correctRecipes[position]

        val intent = Intent(this, RecipeInformation::class.java)
        intent.putExtra("recipe", recipe as java.io.Serializable)
        startActivity(intent)
    }

    /**
     * Al cerrar la ventana nos aseguramos de
     * que la conexion a la bd se cierre
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        //cerramos la conexion a la base de datos si estubiera abierta
        val db = DataBaseBuilder.getInstance(this@SearchActivity)
        db.close()
    }
}
