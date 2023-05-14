package com.proyecto_linkia.mi_nevera_app

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.proyecto_linkia.mi_nevera_app.adapterClases.adapters.MyIngredientAdapter
import com.proyecto_linkia.mi_nevera_app.adapterClases.adapters.RecipeAdapter
import com.proyecto_linkia.mi_nevera_app.pojo.Recipe
import com.proyecto_linkia.mi_nevera_app.data.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.entities.MyIngredient
import com.proyecto_linkia.mi_nevera_app.data.entities.relations.RecipeWithIngredients
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
    private val llManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        //creamos un link entre la activity y el layout
        super.onCreate(savedInstanceState)
        binding = ActivityMyIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //iniciamos la parte grafica
        setUp()

        //damos funcionalidad a los botones
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

    /**
     * Funcion que obtiene los ingredientes de  ingedientsmutablelist
     * y los pasa a un arrylist
     *
     * @return
     */
    private fun getIngredients(): ArrayList<String> {
        var ingredientsInList = ArrayList<String>()
        for (ingredient in ingredientsMutableList) {
            ingredientsInList.add(ingredient.ingredientName)
        }
        return ingredientsInList
    }

    /**
     * FUncion que inicia el recicleview de recetas
     *
     */
    private fun initRecycleViewRecipes() {
        //creamos el adapter y lo pasamos al recyclerview para que se renderice
        val recyclerView = binding.rvRecipes
        adapterRecipes = RecipeAdapter(recipeList = correctRecipes, onClickListener = { position ->
            showRecipe(position)
        }, onClickFavourite = { position -> onClickFavourite(position) })
        //le asignamos el adapter y el manage
        recyclerView.layoutManager = llManager
        recyclerView.adapter = adapterRecipes
        adapterRecipes.notifyDataSetChanged()
    }

    /**
     * FUncion que controla el paso de favorito a no favorito
     *
     * @param position
     */
    private fun onClickFavourite(position: Int) {
        //obtenemos la receta
        val recipe = correctRecipes[position]

        //informamos a la bd y cambiamos el icono
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

    /**
     * Mostramos la activi con la info de la receta al clicar
     *
     * @param position
     */
    private fun showRecipe(position: Int) {
        val recipe = correctRecipes[position]
        val intent = Intent(this, RecipeInformation::class.java)
        intent.putExtra("recipe", recipe as java.io.Serializable)
        startActivity(intent)
    }

    /**
     * Obtenemos las recetas de la bd
     *
     */
    private fun getRecipesList() {
        //creamos variables y conexion a la base de datso
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

    /**
     * Prepara la parte visual de la activity
     *
     */
    private fun setUp() {
        getRecipesList()
        applyUserPreferences()
        fillActvEntry(binding.actvEntry)
        val difficultyOptions: Array<String> = resources.getStringArray(R.array.difficultyItems)
        fillSpinner(binding.spDifficulty, difficultyOptions)
        val timeOptions: Array<String> = resources.getStringArray(R.array.TimeItems)
        fillSpinner(binding.spTime, timeOptions)
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
            Toast.makeText(this, "${ingredient.ingredientName} ya en la lista", Toast.LENGTH_LONG)
                .show()
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

    /**
     * FUncion que busca en las datastore preferences
     * las preferencias del usuario sobre recetas veganas
     *
     */
    private fun applyUserPreferences() {
        //lanzamos coroutina para obtener los datos
        lifecycleScope.launch(Dispatchers.IO) {
            getUserPreferences().collect {
                //cambiamos el contexto para aplicar al hilo principal (ui)
                withContext(Dispatchers.Main) {
                    if (it) {
                        binding.sVegan.isChecked = true
                    }
                }
            }
        }
    }

    /**
     * Funcion que obtiene las preferencias dle usuario
     *
     * @return flow con las preferencias del usuario
     */
    private fun getUserPreferences() = dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(name = "vegan")] ?: false
    }

    /**
     * FUncion que mustra un toast con el indicando que hay un error
     *
     */
    private fun showError() {
        Toast.makeText(this@MyIngredients, "error", Toast.LENGTH_LONG).show()
    }

    /**
     * hace visible el recicleview con las recetas
     *
     */
    private fun showRecipes() {
        binding.rvRecipes.visibility = View.VISIBLE
    }

    /**
     * Hace invisible el recicleview con las recetas
     *
     */
    private fun hideRecipes() {
        binding.rvRecipes.visibility = View.INVISIBLE
    }

    /**
     * hace visible los view relacionados con los filtros
     *
     */
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

    /**
     * Hacemos invisible los views relacionados con los filtros
     *
     */
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

    /**
     * FUncion que busca las recetas correctas y las muestra
     *
     */
    private fun searchSuitableRecipes() {
        correctRecipes.clear()
        val selectedIngr: ArrayList<String> = getIngredients()
        var myRecipes = recipeList
        var resultRecipes = findSuitableRecipes(selectedIngr, myRecipes)

        //quitamos las recetas no veganas de la lista
        if (checkVegan(binding.sVegan)) {
            for (i in 0..resultRecipes.size - 1) {
                if (resultRecipes.get(i).isVegan == false) {
                    resultRecipes.remove(resultRecipes.get(i))
                }
            }
        }

        //añadimos las correctas a la lista y notificamos al adapter
        for (recipe in resultRecipes) {
            correctRecipes.add((recipe))
        }
        adapterRecipes.notifyDataSetChanged()
    }

    /**
     * funcion que busca las recetas correctas segun los criterios
     *
     * @param ingredients
     * @param recipes
     * @return ArrayList con las recetas correctas segun los ingredientes
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
     * Funcion que mira si una receta es correcta respecto a los ingredientes entrados
     *
     * @param recipe
     * @param selectedIngredients
     * @return true si la receta es correcta
     */
    private fun checkRecipe(recipe: Recipe, selectedIngredients: ArrayList<String>): Boolean {
        //iniciamos un contador para ver si las recetas tienn los ingredientes necesarios
        var count = 0
        val ingredientNumber: Int = recipe.ingredients.size
        var ingredientInRecipe: String
        //comprovamos que las recetas cumplan los parametros, sino, ya devolvemos false
        if (checkVegan(binding.sVegan)) {
            if (!recipe.isVegan) return false
        }
        if (!checkDifficulty(recipe)) return false
        if (!checkFavourites(binding.swFavourites, recipe)) return false
        if (!checkTime(recipe)) return false
        //miramos si la receta tiene todos los ingredientes de la lista o devolvemos false
        for (i in 0 until recipe.ingredients.size) {
            ingredientInRecipe = recipe.ingredients[i]
            if (selectedIngredients.contains(ingredientInRecipe)) count++
        }
        return count == ingredientNumber
    }

    /**
     * Funcion que compara el tiempo maximo del usuario
     * con el de la receta
     *
     * @param recipe
     * @return
     */
    private fun checkTime(recipe: Recipe): Boolean {
        when (binding.spTime.selectedItemPosition) {
            0 -> if (recipe.time <= 30) return true
            1 -> if (recipe.time <= 45) return true
            2 -> if (recipe.time <= 60) return true
            3 -> if (recipe.time <= 90) return true
            4 -> return true
        }
        return false
    }

    /**
     * Funcion que compara la dificultad maxima del usuario
     * con el de la receta
     *
     * @param recipe
     * @return
     */
    private fun checkDifficulty(recipe: Recipe): Boolean {
        when (binding.spDifficulty.selectedItemPosition) {
            0 -> if (recipe.difficulty == "easy") return true
            1 -> return recipe.difficulty != "difficult"
            2 -> return true
            else -> return false
        }
        return false
    }

    /**
     * Funcion que compara si la receta esta en favorito o no
     *
     * @param switch
     * @param recipe
     * @return
     */
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