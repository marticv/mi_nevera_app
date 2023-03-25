package com.proyecto_linkia.mi_nevera_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.R
import com.proyecto_linkia.mi_nevera_app.data.db.entities.ShoppingIngredient

class BoughtIngredientAdapter(private val myIngredientList: List<ShoppingIngredient>,
                              private val onClickListener:(Int)->Unit)
    : RecyclerView.Adapter<BoughtIngredientViewHolder>(){

    //al crear cada celda se usara el layout item_ingredient
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoughtIngredientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BoughtIngredientViewHolder(layoutInflater.inflate(R.layout.item_boughtingredient,parent,false))
    }

    //a cada item de la lista le creamos un viewHolder
    override fun onBindViewHolder(holder: BoughtIngredientViewHolder, position: Int) {
        val item = myIngredientList[position]
        holder.render(item,onClickListener)
    }

    //obtenemos el numero de items en la lista
    override fun getItemCount(): Int = myIngredientList.size
}