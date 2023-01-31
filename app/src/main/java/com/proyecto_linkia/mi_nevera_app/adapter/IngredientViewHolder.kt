package com.proyecto_linkia.mi_nevera_app.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.clases.Ingredient
import com.proyecto_linkia.mi_nevera_app.databinding.ItemIngredientBinding

class IngredientViewHolder(view: View) :RecyclerView.ViewHolder(view){

    private val binding= ItemIngredientBinding.bind(view)

    fun render(
        ingredientModel: Ingredient,
        onClickListener: (Ingredient) -> Unit,
        onClickDeleted: (Int) -> Unit
    ){
        binding.tvIngredientName.text=ingredientModel.ingredientName

        itemView.setOnClickListener { onClickDeleted(adapterPosition)}
    }
}