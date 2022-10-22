package com.proyecto_linkia.mi_nevera_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.proyecto_linkia.mi_nevera_app.clases.Ingrediente
import com.proyecto_linkia.mi_nevera_app.clases.Receta
import kotlinx.coroutines.newFixedThreadPoolContext


class MainActivity : AppCompatActivity() {
    private lateinit var actvEntry: AutoCompleteTextView
    private lateinit var btAddIngedient: Button
    private lateinit var cgIngredients : ChipGroup
    private lateinit var btSearch : Button
    private lateinit var listaRecetas:ArrayList<Receta>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //creamos objetos para todos los Views
        actvEntry = findViewById(R.id.actvEntry)
        fillActvEntry()
        btAddIngedient = findViewById(R.id.btAddIngredient)
        cgIngredients = findViewById(R.id.cgIngredients)
        btSearch =findViewById(R.id.btSearch)

        //creamos recetas e ingredientes de prueva
        var arroz:Ingrediente= Ingrediente(null,"arroz")
        var pasta:Ingrediente= Ingrediente(null,"pasta")
        var tomate:Ingrediente= Ingrediente(null,"salsa de tomate")
        var manzana:Ingrediente= Ingrediente(null,"manzana")
        var huevo:Ingrediente= Ingrediente(null,"huevo")

        var ingArrozHervido: ArrayList<Ingrediente> = ArrayList<Ingrediente>()
        ingArrozHervido.add(arroz)

        var ingArrozConTomate:ArrayList<Ingrediente> = ArrayList<Ingrediente>()
        ingArrozConTomate.add(arroz)
        ingArrozConTomate.add(tomate)

        var ingPastaConTomate: ArrayList<Ingrediente> = ArrayList<Ingrediente>()
        ingPastaConTomate.add(pasta)
        ingPastaConTomate.add(tomate)

        var ingArrozCubana: ArrayList<Ingrediente> = ArrayList<Ingrediente>()
        ingArrozCubana.add(arroz)
        ingArrozCubana.add(tomate)
        ingArrozCubana.add(huevo)

        var arrozHevido:Receta = Receta(null,"arroz hervido", ingArrozHervido)
        var arrozConTomate:Receta= Receta(null,"arroz con tomate",ingArrozConTomate)
        var macarronesConTomate:Receta = Receta(null,"macarrones con tomate", ingPastaConTomate)
        var arrozCubana:Receta = Receta(null,"arroz a la cubana",ingArrozCubana)

        listaRecetas = ArrayList<Receta>()
        listaRecetas.add(arrozConTomate)
        listaRecetas.add(arrozCubana)
        listaRecetas.add(arrozHevido)
        listaRecetas.add(macarronesConTomate)



        //hacemos que al clicar al boton añadir se cree un chip
        btAddIngedient.setOnClickListener {
            if(!actvEntry.text.toString().isEmpty()){
                addChip(actvEntry.text.toString())
                actvEntry.setText("")
            }
        }


        btSearch.setOnClickListener {
            var selectedIngr:ArrayList<String> = obtainSelectedIngredients()
            var myRecipies:ArrayList<Receta> = obtainRecipes()
            findSuitableRecipes(selectedIngr,myRecipies)
        }
        

    }

    /**
     * funcion que permite llenar el AutoCompleteTextView
     */
    private fun fillActvEntry(){
        var sistemIngredients : Array<String> = resources.getStringArray(R.array.sistemIngredients)
        var adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, sistemIngredients)
        actvEntry.setAdapter(adapter)
    }

    /**
     * Añade un chip al grupo con el texto introducido
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
     * Funcion que el nombre de los ingredientes seleccionados
     */
    fun obtainSelectedIngredients():ArrayList<String>{
        var ingredientList : ArrayList<String> = ArrayList<String>()
        var chip : Chip

        for(i in 0 .. cgIngredients.childCount-1){
            chip = cgIngredients.getChildAt(i) as Chip
            if(!ingredientList.contains(chip.text)){
                ingredientList.add(chip.text.toString())
                println(chip.text)
                // TODO: borrar funcion de imprimir por consola al implantar busqueda
            }
        }
        return ingredientList
    }

    /**
     * funcion que obtiene la lista de recetas
     */
    fun obtainRecipes():ArrayList<Receta>{
        return listaRecetas
    }

    /**
     * funcion que busca las recetas que contienen los ingredientes seleccionados
     */
    fun findSuitableRecipes(ingredients:ArrayList<String>,recetas:ArrayList<Receta>):ArrayList<Receta>{
        var correctRecipes:ArrayList<Receta> = ArrayList<Receta>()
        for(r in recetas){
            if(checkRecipe(r,ingredients)){
                correctRecipes.add(r)
                println(r.toString())
            }
        }
        return correctRecipes
    }

    /**
     * funcion que comprueba si una receta necesita los ingredientes que tenemos
     */
    fun checkRecipe(recipe:Receta,selectedIngredients:ArrayList<String>):Boolean{
// TODO: crear funcion  
        return false
    }
}