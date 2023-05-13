package com.proyecto_linkia.mi_nevera_app

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.proyecto_linkia.mi_nevera_app.adapters.MyIngredientAdapter
import com.proyecto_linkia.mi_nevera_app.adapters.RecipeAdapter
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredient
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeWithIngredients
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMyIngredientsBinding
import com.proyecto_linkia.mi_nevera_app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyIngredients : AppCompatActivity() {

    private lateinit var binding: ActivityMyIngredientsBinding
    private var ingredientsMutableList: MutableList<MyIngredient> = mutableListOf()
    private lateinit var adapterIngredients: MyIngredientAdapter
    private lateinit var adapterRecipes: RecipeAdapter
    private val glManager = GridLayoutManager(this, 2)
    var recipeList: MutableList<Recipe> = mutableListOf()
    var correctRecipes: MutableList<Recipe> = mutableListOf()
    var ingredientList: MutableList<String> = mutableListOf()
    private val llManager = LinearLayoutManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUp()

        binding.btAddMyIngredient.setOnClickListener { addIngredient() }
        binding.btRecipes.setOnClickListener {
            hideIngredientsAndFilters()
            showRecipes()
            getIngredients()
            if (recipeList.size == 0) {
                getRecipesList()
            }
            correctRecipes.clear()
            adapterRecipes.notifyDataSetChanged()
            searchSuitableRecipes()
        }
        binding.btFilters.setOnClickListener {
            hideRecipes()
            showIngredientsAndFilters()
        }
    }

    private fun getIngredients(): ArrayList<String> {
        var ingredientsInList = ArrayList<String>()
        for (ingredient in ingredientsMutableList) {
            ingredientsInList.add(ingredient.ingredientName)
        }
        return ingredientsInList
    }

    private fun initRecycleViewRecipes() {
        //creamos el adapter y lo pasamos al recyclerview para que se renderice
        val recyclerView = binding.rvRecipes
        adapterRecipes = RecipeAdapter(recipeList = correctRecipes, onClickListener = { position ->
            showRecipe(position)
        }, onClickFavourite = { position -> onClickFavourite(position) })
        recyclerView.layoutManager = llManager
        recyclerView.adapter = adapterRecipes
        adapterRecipes.notifyDataSetChanged()
    }

    private fun onClickFavourite(position: Int) {
        val recipe = correctRecipes[position]

        lifecycleScope.launch(Dispatchers.IO) {
            val db = DataBaseBuilder.getInstance(this@MyIngredients)
            val recipeDao = db.getRecipeDao()
            if (recipe.isFavourite) {
                recipeDao.udateToNotFavourite(recipe.recipeName)
            } else {
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

    private fun getRecipesList() {
        //creamos variables y conexion a la base de datso
        //val recipeList: MutableList<Recipe> = mutableListOf()
        val db = DataBaseBuilder.getInstance(this@MyIngredients)
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

    private fun setUp() {
        //getRecipesList()
        applyUserPreferences()
        fillActvEntry(binding.actvEntry)
        val difficultyOptions: Array<String> = resources.getStringArray(R.array.difficultyItems)
        fillSpinner(binding.spDifficulty, difficultyOptions)
        val timeOptions:Array<String> = resources.getStringArray(R.array.TimeItems)
        fillSpinner(binding.spTime,timeOptions)
        initRecycleViewIngredients()
        initRecycleViewRecipes()
        binding.rvRecipes.visibility = View.INVISIBLE
        getData()
    }

    /**
     * Funcion que añade un item al recyclerview y a la base de datos
     *
     */
    private fun addIngredient() {
        //cogemos el texto y lo convertimos en un ingrediente que pasamos al listado
        val ingredient = MyIngredient(binding.actvEntry.text.toString())

        //comprobamos si el ingrediente esta ya en la lista
        if (!ingredientsMutableList.contains(ingredient)) {

            ingredientsMutableList.add(0, ingredient)

            //hacemos que el adaptador sepa que hay un nuevo item y lo ponemos en primer lugar
            adapterIngredients.notifyItemInserted(0)
            glManager.scrollToPosition(0)

            //guardamos el item en la base de datos
            CoroutineScope(Dispatchers.IO).launch {
                val db = DataBaseBuilder.getInstance(this@MyIngredients)
                val dao = db.getMyIngredientDao()
                dao.insertMyIngredient(ingredient)
            }
        } else {
            //informamos al usuario
            //Toast.makeText(this, "${ingredient.ingredientName} ya en la lista", Toast.LENGTH_LONG)
            //.show()
        }
        //reiniciamos el texto
        binding.actvEntry.setText("")
    }

    /**
     * Inicia el recyclerView
     *
     */
    private fun initRecycleViewIngredients() {
        //creamos el adapter y lo pasamos al recyclerview para que se renderice
        val recyclerView = binding.rvIngredients
        adapterIngredients = MyIngredientAdapter(myIngredientList = ingredientsMutableList,
            onClickListener = { position ->
                onDeletedItem(position)
            })
        recyclerView.layoutManager = glManager
        recyclerView.adapter = adapterIngredients
        adapterIngredients.notifyDataSetChanged()
    }

    /**
     * Elimina un item del recyclerView y de la base de datos
     *
     * @param position
     */
    private fun onDeletedItem(position: Int) {
        //Obtenemos el ingrediente a eliminar de la lista y lo pasamos a una corrutina
        //para eliminarlo tambien de la base de datos
        val myIngredient = ingredientsMutableList[position]
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@MyIngredients)
            val dao = db.getMyIngredientDao()
            dao.deleteMyIngredient(myIngredient)
        }
        //eliminamos el item del recyclerView y avisamos al adaptador
        ingredientsMutableList.removeAt(position)
        adapterIngredients.notifyItemRemoved(position)
    }


    /**
     * obtenemos todos los registros de la tabla my_ingredients_table
     *
     */
    private fun getData() {
        //iniciamos corrutina para obtener datos
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DataBaseBuilder.getInstance(this@MyIngredients)
                val dao = db.getMyIngredientDao()
                val myIngredientsList = dao.getAllMyIngredients()
                //pasamos la lista a mutablelist
                //val list=myIngredientsList.toMutableList()
                for (item in myIngredientsList) {
                    ingredientsMutableList.add(item)
                }
                runOnUiThread {
                    adapterIngredients.notifyDataSetChanged()
                }
                db.close()
            } catch (e: Exception) {
                showError()
            }
        }
    }

    private fun applyUserPreferences() {
        lifecycleScope.launch(Dispatchers.IO) {
            getUserPreferences().collect {
                withContext(Dispatchers.Main) {
                    if (it) {
                        binding.sVegan.isChecked = true
                    }
                }
            }
        }
    }

    private fun getUserPreferences() = dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(name = "vegan")] ?: false
    }

    private fun showError() {
        // Toast.makeText(this@MyIngredients, "error", Toast.LENGTH_LONG).show()
    }

    private fun showRecipes() {
        binding.rvRecipes.visibility = View.VISIBLE
    }

    private fun hideRecipes() {
        binding.rvRecipes.visibility = View.INVISIBLE
    }

    private fun showIngredientsAndFilters() {
        binding.tvFiltros.visibility = View.VISIBLE
        binding.tvFavourite.visibility = View.VISIBLE
        binding.tvVegan.visibility = View.VISIBLE
        binding.tvDifficulty.visibility = View.VISIBLE
        binding.tvTime.visibility = View.VISIBLE
        binding.swFavourites.visibility = View.VISIBLE
        binding.sVegan.visibility = View.VISIBLE
        binding.spDifficulty.visibility = View.VISIBLE
        binding.spTime.visibility = View.VISIBLE
        binding.tvInstructions.visibility = View.VISIBLE
        binding.actvEntry.visibility = View.VISIBLE
        binding.btAddMyIngredient.visibility = View.VISIBLE
        binding.rvIngredients.visibility = View.VISIBLE
    }

    private fun hideIngredientsAndFilters() {
        binding.tvFiltros.visibility = View.INVISIBLE
        binding.tvFavourite.visibility = View.INVISIBLE
        binding.tvVegan.visibility = View.INVISIBLE
        binding.tvDifficulty.visibility = View.INVISIBLE
        binding.tvTime.visibility = View.INVISIBLE
        binding.swFavourites.visibility = View.INVISIBLE
        binding.sVegan.visibility = View.INVISIBLE
        binding.spDifficulty.visibility = View.INVISIBLE
        binding.spTime.visibility = View.INVISIBLE
        binding.tvInstructions.visibility = View.INVISIBLE
        binding.actvEntry.visibility = View.INVISIBLE
        binding.btAddMyIngredient.visibility = View.INVISIBLE
        binding.rvIngredients.visibility = View.INVISIBLE
    }

    private fun searchSuitableRecipes() {
        correctRecipes.clear()
        if (recipeList.isEmpty()) {
            //tvResultados.text = "sin resultados"
        } else {
            val selectedIngr: ArrayList<String> = getIngredients()
            var myRecipes = recipeList
            var resultRecipes = findSuitableRecipes(selectedIngr, myRecipes)

            if (checkVegan(binding.sVegan)) {
                for (i in 0..resultRecipes.size - 1) {
                    if (resultRecipes.get(i).isVegan == false) {
                        resultRecipes.remove(resultRecipes.get(i))
                    }
                }
            }

            for (recipe in resultRecipes) {
                correctRecipes.add((recipe))
                adapterRecipes.notifyDataSetChanged()
            }
        }
    }

    private fun findSuitableRecipes(
        ingredients: ArrayList<String>, recipes: List<Recipe>
    ): ArrayList<Recipe> {
        val correctRecipes: ArrayList<Recipe> = ArrayList()
        for (recipe in recipes) {
            if (checkRecipe(recipe, ingredients)) correctRecipes.add(recipe)
        }
        return correctRecipes
    }

    private fun checkRecipe(recipe: Recipe, selectedIngredients: ArrayList<String>): Boolean {
        var count = 0
        val ingredientNumber: Int = recipe.ingredients.size
        var ingredientInRecipe: String
        if (checkVegan(binding.sVegan)) {
            if (!recipe.isVegan) return false
        }

        if (!checkDifficulty(recipe)) return false
        if (!checkFavourites(binding.swFavourites, recipe)) return false
        if(!checkTime(recipe)) return false

        for (i in 0 until recipe.ingredients.size) {
            ingredientInRecipe = recipe.ingredients[i]
            if (selectedIngredients.contains(ingredientInRecipe)) count++
        }
        return count == ingredientNumber
    }

    private fun checkTime(recipe: Recipe): Boolean{
        when(binding.spTime.selectedItemPosition){
            0-> if (recipe.time<=30) return true
            1-> if (recipe.time<=45) return true
            2-> if (recipe.time<=60) return true
            3-> if (recipe.time<=90) return true
            4-> return true
        }
        return false
    }

    private fun checkDifficulty(recipe: Recipe): Boolean {
        when (binding.spDifficulty.selectedItemPosition) {
            0 -> if (recipe.difficulty == "easy") return true
            1 -> return recipe.difficulty != "difficult"
            2 -> return true
            else -> return false
        }
        return false
    }

    private fun checkFavourites(switch: SwitchMaterial, recipe: Recipe): Boolean {
        if (switch.isChecked) {
            if (!recipe.isFavourite) return false
        }
        return true
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


    /**
     * Cerramos la base de datos
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@MyIngredients)
            db.close()
        }
    }

}