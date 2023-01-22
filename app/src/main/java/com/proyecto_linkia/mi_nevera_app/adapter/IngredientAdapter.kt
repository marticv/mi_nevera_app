package com.proyecto_linkia.mi_nevera_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.R
import com.proyecto_linkia.mi_nevera_app.clases.Ingredient

class IngredientAdapter(private val ingredientList:List<Ingredient>) : RecyclerView.Adapter<IngredientViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return IngredientViewHolder(layoutInflater.inflate(R.layout.item_ingredient,parent,false))
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = ingredientList.size
}