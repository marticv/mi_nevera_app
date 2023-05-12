package com.proyecto_linkia.mi_nevera_app.utils

import android.widget.AutoCompleteTextView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * Obtenemos la lista de ingredientes que hay en el chipgroup
 *
 * @return lista con el nombre de los ingredientes seleccionados
 */
fun obtainSelectedIngredients(cgIngredients:ChipGroup):ArrayList<String>{
    var ingredientList : ArrayList<String> = ArrayList<String>()
    var chip : Chip
    //para cada chip obtenemos el nombre y lo añadimos a un arraylist
    for(i in 0 until cgIngredients.childCount){
        chip = cgIngredients.getChildAt(i) as Chip
        if(!ingredientList.contains(chip.text)){
            ingredientList.add(chip.text.toString())
        }
    }
    return ingredientList
}


/**
 * Funcion que añade chips al chipgroup
 *
 * @param text que contrendra el chip
 */
fun addChip(text:String, chipGroup: ChipGroup){
    //creamos un chip
    val chip =Chip(chipGroup.context)

    //pasamos el texto al chip y definimos su comportamiento
    chip.text =text.trim().lowercase()
    chip.isCloseIconVisible = true
    chipGroup.addView(chip)
    chip.setOnCloseIconClickListener {
        chipGroup.removeView(chip)
    }
}

/**
 * Función que controla que el texto que se introduce un
 * acutocompleteTextView no este vacio y entonces
 * crea un chip al chipgroup entrado por parametro
 *
 * @param actEntry
 * @param chipGroup
 */
fun addChipIfTextIsNotEmpty(actEntry:AutoCompleteTextView, chipGroup: ChipGroup) {
    //mieramos si el texto no esta vacio,
    //si no lo esta, añadimos chip y vaciamos el autocompletetextview
    if (actEntry.text.toString().isNotEmpty()) {
        addChip(actEntry.text.toString(), chipGroup)
        actEntry.setText("")
    }
}