package com.proyecto_linkia.mi_nevera_app.adapterClases.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.R
import com.proyecto_linkia.mi_nevera_app.adapterClases.viewholders.MyIngredientViewHolder
import com.proyecto_linkia.mi_nevera_app.data.entities.MyIngredient

class MyIngredientAdapter(private val myIngredientList: List<MyIngredient>,
                          private val onClickListener:(Int)->Unit)
    : RecyclerView.Adapter<MyIngredientViewHolder>(){

    //al crear cada celda se usara el layout item_ingredient
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyIngredientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyIngredientViewHolder(layoutInflater.inflate(R.layout.item_ingredient,parent,false))
    }

    //a cada item de la lista le creamos un viewHolder
    override fun onBindViewHolder(holder: MyIngredientViewHolder, position: Int) {
        val item = myIngredientList[position]
        holder.render(item,onClickListener)
    }

    //obtenemos el numero de items en la lista
    override fun getItemCount(): Int = myIngredientList.size
}