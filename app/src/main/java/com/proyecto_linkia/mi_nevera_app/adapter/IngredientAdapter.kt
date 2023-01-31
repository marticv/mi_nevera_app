package com.proyecto_linkia.mi_nevera_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.R
import com.proyecto_linkia.mi_nevera_app.clases.Ingredient

class IngredientAdapter(private val ingredientList:List<Ingredient>,
                        private val onClickListener:(Ingredient)->Unit,
                        private val onClickDeleted:(Int)-> Unit)
    : RecyclerView.Adapter<IngredientViewHolder>(){

    //al crear cada celda se usara el layout item_ingredient
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return IngredientViewHolder(layoutInflater.inflate(R.layout.item_ingredient,parent,false))
    }

    //a cada item de la lista le creamos un viewHolder
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val item = ingredientList[position]
        holder.render(item,onClickListener,onClickDeleted)
    }

    //obtenemos el numero de items en la lista
    override fun getItemCount(): Int = ingredientList.size
}