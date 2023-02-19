package com.proyecto_linkia.mi_nevera_app

import com.proyecto_linkia.mi_nevera_app.MyIngredients
import com.proyecto_linkia.mi_nevera_app.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.proyecto_linkia.mi_nevera_app.clases.Recipie
import com.proyecto_linkia.mi_nevera_app.data.RecipeResponse
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMainBinding
import com.proyecto_linkia.mi_nevera_app.internet.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var actvEntry: AutoCompleteTextView
    private lateinit var btAddIngedient: Button
    private lateinit var btMyIngredients: Button
    private lateinit var cgIngredients : ChipGroup
    private lateinit var btSearch : Button
    private lateinit var tvResultados: TextView
    private lateinit var sVegan:Switch
    private lateinit var binding:ActivityMainBinding
    val recipeList: MutableList<Recipie> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //creamos objetos para todos los Views
        actvEntry = binding.actvEntry
        fillActvEntry()
        btAddIngedient = binding.btAddIngredient
        btMyIngredients = binding.btMyIngredients
        cgIngredients = binding.cgIngredients
        btSearch =binding.btSearch
        sVegan=binding.sVegan
        tvResultados=binding.tvResultados

        getData()


        /*val ingredients = IngredientProvider.ingredientList

        var ingArrozHervido: ArrayList<String> = ArrayList<String>()
        ingArrozHervido.add("arroz")

        var ingArrozConTomate:ArrayList<String> = ArrayList<String>()
        ingArrozConTomate.add("arroz")
        ingArrozConTomate.add("tomate")

        var ingPastaConTomate: ArrayList<String> = ArrayList<String>()
        ingPastaConTomate.add("pasta")
        ingPastaConTomate.add("tomate")

        var ingArrozCubana: ArrayList<String> = ArrayList<String>()
        ingArrozCubana.add("arroz")
        ingArrozCubana.add("tomate")
        ingArrozCubana.add("huevo")

        var arrozHevido:Recipie = Recipie(null,"arroz hervido", ingArrozHervido,true)
        var arrozConTomate:Recipie= Recipie(null,"arroz con tomate",ingArrozConTomate,true,)
        var macarronesConTomate:Recipie = Recipie(null,"macarrones con tomate", ingPastaConTomate,true)
        var arrozCubana:Recipie = Recipie(null,"arroz a la cubana",ingArrozCubana,false)

        listaRecipies = ArrayList<Recipie>()
        listaRecipies.add(arrozConTomate)
        listaRecipies.add(arrozCubana)
        listaRecipies.add(arrozHevido)
        listaRecipies.add(macarronesConTomate)*/


        //hacemos que al clicar al boton añadir se cree un chip
        btAddIngedient.setOnClickListener {
            if(actvEntry.text.toString().isNotEmpty()){
                addChip(actvEntry.text.toString())
                actvEntry.setText("")
            }
        }

        btMyIngredients.setOnClickListener {
            startActivity(Intent(this,MyIngredients::class.java))
        }



        btSearch.setOnClickListener {
            var selectedIngr:ArrayList<String> = obtainSelectedIngredients()
            var myRecipes = recipeList
            var resultRecipes = findSuitableRecipes(selectedIngr,myRecipes)

            if(checkVegan(sVegan)){
                for(i in 0..resultRecipes.size-1){
                    if(resultRecipes.get(i).isVegan==false){
                        resultRecipes.remove(resultRecipes.get(i))
                    }
                }
            }
            var resultString =printRecipes(resultRecipes)

            tvResultados.text=resultString
        }
    }

    /**
     * Funcion que rellena el AutoCompleteTextView
     *
     */
    private fun fillActvEntry(){
        var systemIngredients : Array<String> = resources.getStringArray(R.array.sistemIngredients)
        var adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, systemIngredients)
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
    private fun findSuitableRecipes(ingredients:ArrayList<String>, recipes:List<Recipie>):ArrayList<Recipie>{
        var correctRecipes:ArrayList<Recipie> = ArrayList<Recipie>()
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
    private fun checkRecipe(recipe:Recipie, selectedIngredients:ArrayList<String>):Boolean{
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
    private fun printRecipes(recipes:List<Recipie>):String{
        var myList:String=""
        for(element in recipes){
            myList= "$myList\n${element.recipieName} "
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

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder().baseUrl("https://api.npoint.io/281f74aeedbb04eb4d6b/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getData(){
        CoroutineScope(Dispatchers.IO).launch {
            val call: Response<RecipeResponse> = getRetrofit().create(APIService::class.java).getRecipes("")
            val result: RecipeResponse? = call.body()
            runOnUiThread {
                if(call.isSuccessful){
                    val recipies: List<Recipie> = result?.recipies ?: emptyList()
                    recipeList.clear()
                    recipeList.addAll(recipies)
                    tvResultados.text = printRecipes(recipeList)
                }else{
                    showError()
                }
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "error",Toast.LENGTH_LONG).show()
    }
}
