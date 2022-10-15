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
import kotlinx.coroutines.newFixedThreadPoolContext


class MainActivity : AppCompatActivity() {
    private lateinit var actvEntry: AutoCompleteTextView
    private lateinit var btAddIngedient: Button
    private lateinit var cgIngredients : ChipGroup
    private lateinit var btSearch : Button

    var arroz:Ingrediente= Ingrediente(null,"arroz")
    var pasta:Ingrediente= Ingrediente(null,"pasta")
    var tomate:Ingrediente= Ingrediente(null,"tomate")
    var manzana:Ingrediente= Ingrediente(null,"manzana")
    var huevo:Ingrediente= Ingrediente(null,"huevo")

    var ingArrozHervido: ArrayList<Ingrediente> = ArrayList<Ingrediente>()
    var ingArrozConTomate:ArrayList<Ingrediente> = ArrayList<Ingrediente>()
    var ingPastaConTomate: ArrayList<Ingrediente> = ArrayList<Ingrediente>()
    var ingArrozCubana: ArrayList<Ingrediente> = ArrayList<Ingrediente>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //creamos objetos para todos los Views
        actvEntry = findViewById(R.id.actvEntry)
        fillActvEntry()
        btAddIngedient = findViewById(R.id.btAddIngredient)
        cgIngredients = findViewById(R.id.cgIngredients)
        btSearch =findViewById(R.id.btSearch)

        //creamos variables para usar el AutocompleteTextView y un adaptador para pasarle los datos


        //hacemos que al clicar al boton añadir se cree un chip
        btAddIngedient.setOnClickListener {
            if(!actvEntry.text.toString().isEmpty()){
                addChip(actvEntry.text.toString())
                actvEntry.setText("")
            }
        }

        // TODO: borrar funcion de imprimir por consola al implantar busqueda
        btSearch.setOnClickListener {
            findRecetas()
        }
        // TODO: implantar busqueda

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
     * Funcion que busca recetas con los ingredientes añadidos
     */
    private fun findRecetas(){
        var ingredientList : ArrayList<String> = ArrayList<String>()
        var chip : Chip

        for(i in 0 .. cgIngredients.childCount-1){
            chip = cgIngredients.getChildAt(i) as Chip
            if(!ingredientList.contains(chip.text)){
                ingredientList.add(chip.text.toString())
                println(chip.text)
            }
        }
    }
}