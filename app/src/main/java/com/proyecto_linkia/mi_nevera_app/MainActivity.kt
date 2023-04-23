package com.proyecto_linkia.mi_nevera_app


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import com.proyecto_linkia.mi_nevera_app.adapter.RecipeAdapter
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeWithIngredients
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMainBinding
import com.proyecto_linkia.mi_nevera_app.utils.*
import kotlinx.coroutines.*

//import com.proyecto_linkia.mi_nevera_app.utils.*


class MainActivity : AppCompatActivity() {
    private lateinit var actvEntry: AutoCompleteTextView
    private lateinit var btAddIngedient: Button
    private lateinit var btMyIngredients: Button
    private lateinit var cgIngredients: ChipGroup
    private lateinit var btSearch: Button
    private lateinit var tvResultados: TextView
    private lateinit var sVegan: SwitchMaterial
    private lateinit var swDatkMode: SwitchMaterial
    private lateinit var swLanguage: SwitchMaterial
    private lateinit var binding: ActivityMainBinding
    var recipeList: MutableList<Recipe> = mutableListOf()
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
        btMyIngredients = binding.btMyIngredients
        cgIngredients = binding.cgIngredients
        btSearch = binding.btSearch
        sVegan = binding.sVegan
        swDatkMode = binding.swColorMode
        swLanguage = binding.swLanguage
        tvResultados = binding.tvResultados

        //preparamos la activity
        setUp()

        //damos funcionalidad a los botones
        btAddIngedient.setOnClickListener {
            addChipIfTextIsNotEmpty(actvEntry,cgIngredients)
        }

        btMyIngredients.setOnClickListener {
            val intent = Intent(this, MyIngredients::class.java)
            startActivity(intent)
        }

        binding.btShopingList.setOnClickListener {
            startActivity(Intent(this, Shopping::class.java))
        }

        btSearch.setOnClickListener {
            searchSuitableRecipes()
        }

        swDatkMode.setOnCheckedChangeListener { _, isSelected ->
            if (isSelected) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
        }
    }

    private fun getRecipesList() {
        //creamos variables y conexion a la base de datso
        //val recipeList: MutableList<Recipe> = mutableListOf()
        val db = DataBaseBuilder.getInstance(this@MainActivity)
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
                for (recipeWithIngredients in list) {
                    recipeList.add(recipeWithIngredients.toRecipe())
                }
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "error al obtener recetas")
            } finally {
                db.close()
            }
        }
    }

    private fun searchSuitableRecipes() {
        if (recipeList.isEmpty()) {
            tvResultados.text = "sin resultados"
        }
        correctRecipes.clear()
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

    private fun setUp() {
        getRecipesList()
        fillActvEntry(actvEntry)
        initRecycleView()
    }


    private fun enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        delegate.applyDayNight()
    }

    private fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        delegate.applyDayNight()
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
        var correctRecipes: ArrayList<Recipe> = ArrayList<Recipe>()
        for (i in 0 until recipes.size) {
            if (checkRecipe(recipes[i], ingredients)) correctRecipes.add(recipes[i])
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
        for (i in 0 until recipe.ingredients.size) {
            ingredientInRecipe = recipe.ingredients[i]
            if (selectedIngredients.contains(ingredientInRecipe)) count++
        }
        return count == ingredientNumber
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
        })
        recyclerView.layoutManager = llManager
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun onDeletedItem(position: Int) {
        //Obtenemos el ingrediente a eliminar de la lista y lo pasamos a una corrutina
        //para eliminarlo tambien de la base de datos
        val recipe = recipeList[position]

        //eliminamos el item del recyclerView y avisamos al adaptador
        recipeList.removeAt(position)
        adapter.notifyItemRemoved(position)
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
        val db = DataBaseBuilder.getInstance(this@MainActivity)
        db.close()
    }
}
