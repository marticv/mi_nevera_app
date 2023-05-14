package com.proyecto_linkia.mi_nevera_app.adapterClases.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.data.entities.ShoppingIngredient
import com.proyecto_linkia.mi_nevera_app.databinding.ItemTobuyingredientBinding

class ToBuyIngredientViewHolder(view: View) :RecyclerView.ViewHolder(view){

    private val binding= ItemTobuyingredientBinding.bind(view)

    fun render(
        ingredientModel: ShoppingIngredient,
        onClickListener: (Int) -> Unit
    ){
        binding.tvIngredientName.text=ingredientModel.ingredientName
        itemView.setOnClickListener { onClickListener(adapterPosition)}
    }
}