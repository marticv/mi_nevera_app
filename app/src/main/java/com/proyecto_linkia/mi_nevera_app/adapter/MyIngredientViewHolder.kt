package com.proyecto_linkia.mi_nevera_app.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredient
import com.proyecto_linkia.mi_nevera_app.databinding.ItemIngredientBinding

class MyIngredientViewHolder(view: View) :RecyclerView.ViewHolder(view){

    private val binding= ItemIngredientBinding.bind(view)

    fun render(
        ingredientModel: MyIngredient,
        onClickListener: (Int) -> Unit
    ){
        binding.tvIngredientName.text=ingredientModel.ingredientName

        itemView.setOnClickListener { onClickListener(adapterPosition)}
    }
}