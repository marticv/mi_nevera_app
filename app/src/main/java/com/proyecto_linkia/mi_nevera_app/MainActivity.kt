package com.proyecto_linkia.mi_nevera_app


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.proyecto_linkia.mi_nevera_app.adapter.RecipeAdapter
import com.proyecto_linkia.mi_nevera_app.clases.DbNevera
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.IngredientEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.RecipeEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeIngredientCrossReference
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var actvEntry: AutoCompleteTextView
    private lateinit var btAddIngedient: Button
    private lateinit var btMyIngredients: Button
    private lateinit var cgIngredients : ChipGroup
    private lateinit var btSearch : Button
    private lateinit var tvResultados: TextView
    private lateinit var sVegan:Switch
    private lateinit var binding:ActivityMainBinding
    var recipeList: MutableList<Recipe> = mutableListOf()
    var correctRecipes: MutableList<Recipe> = mutableListOf()
    private lateinit var adapter: RecipeAdapter
    private val llManager = LinearLayoutManager(this)


    private lateinit var db: DbNevera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start DB configuration
        db = DbNevera(this)

        //creamos objetos para todos los Views
        actvEntry = binding.actvEntry
        btAddIngedient = binding.btAddIngredient
        btMyIngredients = binding.btMyIngredients
        cgIngredients = binding.cgIngredients
        btSearch =binding.btSearch
        sVegan=binding.sVegan
        tvResultados=binding.tvResultados

        //getData()
        recipeList = intent.extras?.get("data") as MutableList<Recipe>
        fillActvEntry(recipeList)
        initRecycleView()
        //tvResultados.text = printRecipes(recipeList)

        //hacemos que al clicar al boton añadir se cree un chip
        btAddIngedient.setOnClickListener {
            if(actvEntry.text.toString().isNotEmpty()){
                addChip(actvEntry.text.toString())
                actvEntry.setText("")
            }
        }

        btMyIngredients.setOnClickListener {
            val intent = Intent(this, MyIngredients::class.java)
            intent.putExtra("data",recipeList as java.io.Serializable)
            startActivity(intent)
        }



        btSearch.setOnClickListener {
            correctRecipes.clear()
            val selectedIngr:ArrayList<String> = obtainSelectedIngredients()
            var myRecipes = recipeList
            var resultRecipes = findSuitableRecipes(selectedIngr,myRecipes)

            if(checkVegan(sVegan)){
                for(i in 0..resultRecipes.size-1){
                    if(resultRecipes.get(i).isVegan==false){
                        resultRecipes.remove(resultRecipes.get(i))
                    }
                }
            }

            for (recipe in resultRecipes){
                correctRecipes.add((recipe))
                adapter.notifyDataSetChanged()
            }

            binding.rvRecipe.visibility = View.VISIBLE
        }



        /*
        val emptyRecipeEntity = RecipeEntity(null,"arroz con leche",false)
        val ingredient1=IngredientEntity(null,"arroz")
        val ingredient2=IngredientEntity(null,"leche")
        var ingredientsList:ArrayList<IngredientEntity> = ArrayList<IngredientEntity>()

        ingredientsList.add(ingredient1)
        ingredientsList.add(ingredient2)

        val recipeCrosRef = RecipeIngredientCrossReference(1,1)
        val recipeCrosRef2 = RecipeIngredientCrossReference(1,2)

        val recipeList: List<RecipeIngredientCrossReference> = listOf(recipeCrosRef,recipeCrosRef2)

        addtodb(ingredientsList, emptyRecipeEntity,recipeList)*/
    }



    /**
     * Funcion que rellena el AutoCompleteTextView
     *
     */
    private fun fillActvEntry(recipeList: List<Recipe>){
        var ingredientsList: ArrayList<String> = ArrayList<String>()
        for(recipe in recipeList){
            for(ingredient in recipe.ingredients){
                if(!ingredientsList.contains(ingredient)){
                    ingredientsList.add(ingredient)
                }
            }
        }
        var systemIngredients : Array<String> = resources.getStringArray(R.array.sistemIngredients)
        var adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, ingredientsList)
        actvEntry.setAdapter(adapter)
    }

    /**
     * Funcion que añade chips al chipgroup
     *
     * @param text que contrendra el chip
     */
    private fun addChip(text:String){
        //creamos un chip
        val chip =Chip(this)

        //pasamos el texto al chip y definimos su comportamiento
        chip.text =text
        chip.isCloseIconVisible = true
        cgIngredients.addView(chip)
        chip.setOnCloseIconClickListener {
            cgIngredients.removeView(chip)
        }
    }

    /**
     * Obtenemos la lista de ingredientes que hay en el chipgroup
     *
     * @return lista con el nombre de los ingredientes seleccionados
     */
    private fun obtainSelectedIngredients():ArrayList<String>{
        var ingredientList : ArrayList<String> = ArrayList<String>()
        var chip : Chip

        for(i in 0 until cgIngredients.childCount){
            chip = cgIngredients.getChildAt(i) as Chip
            if(!ingredientList.contains(chip.text)){
                ingredientList.add(chip.text.toString())
            }
        }
        return ingredientList
    }


    /**
     * Compara la lista de recetas del sistema con la lista de ingredientes para ver cuales son las recetas adientes
     *
     * @param ingredients seleccionados por el usuario
     * @param recipes del sistema
     * @return lista con las recetas que cumplen los parametros
     */
    private fun findSuitableRecipes(ingredients:ArrayList<String>, recipes:List<Recipe>):ArrayList<Recipe>{
        var correctRecipes:ArrayList<Recipe> = ArrayList<Recipe>()
        for(i in 0 until recipes.size){
            if(checkRecipe(recipes[i],ingredients)) correctRecipes.add(recipes[i])
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
    private fun checkRecipe(recipe:Recipe, selectedIngredients:ArrayList<String>):Boolean{
        var count=0
        val ingredientNumber: Int =recipe.ingredients.size
        var ingredientInRecipe:String
        if(checkVegan(sVegan)){
            if(!recipe.isVegan)return false
        }
        for(i in 0 until recipe.ingredients.size){
            ingredientInRecipe= recipe.ingredients[i]
            if(selectedIngredients.contains(ingredientInRecipe))count++
        }
        return count==ingredientNumber
    }

    /**
     * transforma el array de recetas a una String
     *
     * @param recipes
     * @return String con la lista de recetas
     */
    private fun printRecipes(recipes:List<Recipe>):String{
        var myList:String=""
        for(element in recipes){
            myList= "$myList\n${element.recipeName} "
        }
        return myList
    }

    /**
     * Comprobamos si el estado del switch
     *
     * @param sVegan
     * @return true si esta checked
     */
    private fun checkVegan(sVegan:Switch):Boolean{
        return sVegan.isChecked
    }

    private fun addtodb(
        ingredients: ArrayList<IngredientEntity>,
        recipeEntity: RecipeEntity,
        crosRefList: List<RecipeIngredientCrossReference>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@MainActivity)
            val dao = db.getRecipeDao()
            val dao2 = db.getIngredientsDao()
            dao.insertRecipe(recipeEntity)
            for (ingredient in ingredients) {
                dao2.insertIngredient(ingredient)
            }
            for (crosRef in crosRefList) {
                dao.insertRecipeIngredientsCrossReference(crosRef)
            }
            val listRecipe = dao.getIngredientsOfRecipe(1)

            runOnUiThread {
                tvResultados.text=listRecipe[0].toString()
            }
        }
    }

    private fun initRecycleView(){
        //creamos el adapter y lo pasamos al recyclerview para que se renderice
        val recyclerView=binding.rvRecipe
        adapter = RecipeAdapter(recipeList = correctRecipes,
            onClickListener = {position ->
                showRecipe(position)
            })
        recyclerView.layoutManager = llManager
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun onDeletedItem(position:Int){
        //Obtenemos el ingrediente a eliminar de la lista y lo pasamos a una corrutina
        //para eliminarlo tambien de la base de datos
        val recipe = recipeList[position]

        //eliminamos el item del recyclerView y avisamos al adaptador
        recipeList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun showRecipe(position:Int){
        val recipe =recipeList[position]

        val intent = Intent(this, RecipeInformation::class.java)
        intent.putExtra("recipe",recipe as java.io.Serializable)
        startActivity(intent)
    }
}
