package com.proyecto_linkia.mi_nevera_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.proyecto_linkia.mi_nevera_app.clases.Ingrediente
import com.proyecto_linkia.mi_nevera_app.clases.Receta


class MainActivity : AppCompatActivity() {
    private lateinit var actvEntry: AutoCompleteTextView
    private lateinit var btAddIngedient: Button
    private lateinit var cgIngredients : ChipGroup
    private lateinit var btSearch : Button
    private lateinit var listaRecetas:ArrayList<Receta>
    private lateinit var tvResultados: TextView
    private lateinit var sVegan:Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //creamos objetos para todos los Views
        actvEntry = findViewById(R.id.actvEntry)
        fillActvEntry()
        btAddIngedient = findViewById(R.id.btAddIngredient)
        cgIngredients = findViewById(R.id.cgIngredients)
        btSearch =findViewById(R.id.btSearch)
        sVegan=findViewById(R.id.sVegan)
        tvResultados=findViewById(R.id.tvResultados)

        //creamos recetas e ingredientes de prueva
        var arroz:Ingrediente= Ingrediente(null,"arroz")
        var macarrones:Ingrediente= Ingrediente(null,"macarrones")
        var tomate:Ingrediente= Ingrediente(null,"salsa de tomate")
        var manzana:Ingrediente= Ingrediente(null,"manzana")
        var huevo:Ingrediente= Ingrediente(null,"huevo")

        var ingArrozHervido: ArrayList<Ingrediente> = ArrayList<Ingrediente>()
        ingArrozHervido.add(arroz)

        var ingArrozConTomate:ArrayList<Ingrediente> = ArrayList<Ingrediente>()
        ingArrozConTomate.add(arroz)
        ingArrozConTomate.add(tomate)

        var ingPastaConTomate: ArrayList<Ingrediente> = ArrayList<Ingrediente>()
        ingPastaConTomate.add(macarrones)
        ingPastaConTomate.add(tomate)

        var ingArrozCubana: ArrayList<Ingrediente> = ArrayList<Ingrediente>()
        ingArrozCubana.add(arroz)
        ingArrozCubana.add(tomate)
        ingArrozCubana.add(huevo)

        var arrozHevido:Receta = Receta(null,"arroz hervido", ingArrozHervido,true)
        var arrozConTomate:Receta= Receta(null,"arroz con tomate",ingArrozConTomate,true,)
        var macarronesConTomate:Receta = Receta(null,"macarrones con tomate", ingPastaConTomate,true)
        var arrozCubana:Receta = Receta(null,"arroz a la cubana",ingArrozCubana,false)

        listaRecetas = ArrayList<Receta>()
        listaRecetas.add(arrozConTomate)
        listaRecetas.add(arrozCubana)
        listaRecetas.add(arrozHevido)
        listaRecetas.add(macarronesConTomate)


        //hacemos que al clicar al boton añadir se cree un chip
        btAddIngedient.setOnClickListener {
            if(actvEntry.text.toString().isNotEmpty()){
                addChip(actvEntry.text.toString())
                actvEntry.setText("")
            }
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
    private fun obtainRecipes():ArrayList<Receta>{
        return listaRecetas
    }

    /**
     * Compara la lista de recetas del sistema con la lista de ingredientes para ver cuales son las recetas adientes
     *
     * @param ingredients seleccionados por el usuario
     * @param recipes del sistema
     * @return lista con las recetas que cumplen los parametros
     */
    private fun findSuitableRecipes(ingredients:ArrayList<String>, recipes:ArrayList<Receta>):ArrayList<Receta>{
        var correctRecipes:ArrayList<Receta> = ArrayList<Receta>()
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
    private fun checkRecipe(recipe:Receta,selectedIngredients:ArrayList<String>):Boolean{
        var count:Int=0
        val ingredientNumber: Int =recipe.ingredientes.size
        var ingredientInRecipe:String
        if(checkVegan(sVegan)){
            if(!recipe.vegan)return false
        }
        for(i in 0 until recipe.ingredientes.size){
            ingredientInRecipe= recipe.ingredientes[i].toString()
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
    private fun printRecipes(recipes:ArrayList<Receta>):String{
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