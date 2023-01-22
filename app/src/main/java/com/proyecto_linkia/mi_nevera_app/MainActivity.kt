package com.proyecto_linkia.mi_nevera_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.proyecto_linkia.mi_nevera_app.clases.Ingredient
import com.proyecto_linkia.mi_nevera_app.clases.Recipie
import com.proyecto_linkia.mi_nevera_app.data.IngredientProvider


class MainActivity : AppCompatActivity() {
    private lateinit var actvEntry: AutoCompleteTextView
    private lateinit var btAddIngedient: Button
    private lateinit var btMyIngredients: Button
    private lateinit var cgIngredients : ChipGroup
    private lateinit var btSearch : Button
    private lateinit var listaRecipies:ArrayList<Recipie>
    private lateinit var tvResultados: TextView
    private lateinit var sVegan:Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //creamos objetos para todos los Views
        actvEntry = findViewById(R.id.actvEntry)
        fillActvEntry()
        btAddIngedient = findViewById(R.id.btAddIngredient)
        btMyIngredients = findViewById(R.id.btMyIngredients)
        cgIngredients = findViewById(R.id.cgIngredients)
        btSearch =findViewById(R.id.btSearch)
        sVegan=findViewById(R.id.sVegan)
        tvResultados=findViewById(R.id.tvResultados)


        val ingredients = IngredientProvider.ingredientList

        var ingArrozHervido: ArrayList<Ingredient> = ArrayList<Ingredient>()
        ingArrozHervido.add(ingredients[0])

        var ingArrozConTomate:ArrayList<Ingredient> = ArrayList<Ingredient>()
        ingArrozConTomate.add(ingredients[0])
        ingArrozConTomate.add(ingredients[2])

        var ingPastaConTomate: ArrayList<Ingredient> = ArrayList<Ingredient>()
        ingPastaConTomate.add(ingredients[1])
        ingPastaConTomate.add(ingredients[2])

        var ingArrozCubana: ArrayList<Ingredient> = ArrayList<Ingredient>()
        ingArrozCubana.add(ingredients[0])
        ingArrozCubana.add(ingredients[2])
        ingArrozCubana.add(ingredients[4])

        var arrozHevido:Recipie = Recipie(null,"arroz hervido", ingArrozHervido,true)
        var arrozConTomate:Recipie= Recipie(null,"arroz con tomate",ingArrozConTomate,true,)
        var macarronesConTomate:Recipie = Recipie(null,"macarrones con tomate", ingPastaConTomate,true)
        var arrozCubana:Recipie = Recipie(null,"arroz a la cubana",ingArrozCubana,false)

        listaRecipies = ArrayList<Recipie>()
        listaRecipies.add(arrozConTomate)
        listaRecipies.add(arrozCubana)
        listaRecipies.add(arrozHevido)
        listaRecipies.add(macarronesConTomate)


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
            var myRecipes = obtainRecipes()
            var resultRecipes = findSuitableRecipes(selectedIngr,myRecipes)

            if(checkVegan(sVegan)){
                for(i in 0..resultRecipes.size-1){
                    if(resultRecipes.get(i).vegan==false){
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
     * Obtiene una lista de recetas del almacenamiento
     *
     * @return lista con las recetas del sistema
     */
    private fun obtainRecipes():ArrayList<Recipie>{
        return listaRecipies
    }

    /**
     * Compara la lista de recetas del sistema con la lista de ingredientes para ver cuales son las recetas adientes
     *
     * @param ingredients seleccionados por el usuario
     * @param recipes del sistema
     * @return lista con las recetas que cumplen los parametros
     */
    private fun findSuitableRecipes(ingredients:ArrayList<String>, recipes:ArrayList<Recipie>):ArrayList<Recipie>{
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
        var count:Int=0
        val ingredientNumber: Int =recipe.ingredients.size
        var ingredientInRecipe:String
        if(checkVegan(sVegan)){
            if(!recipe.vegan)return false
        }
        for(i in 0 until recipe.ingredients.size){
            ingredientInRecipe= recipe.ingredients[i].toString()
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
    private fun printRecipes(recipes:ArrayList<Recipie>):String{
        var myList:String=""
        for(i in 0 until recipes.size){
            myList=myList+ recipes[i].toString()+" "
        }
        return myList
    }

    /**
     * Comprobamos si el estado del switch
     *
     * @param sVegan
     * @return true si esta checked
     */
    fun checkVegan(sVegan:Switch):Boolean{
        return sVegan.isChecked
    }
}