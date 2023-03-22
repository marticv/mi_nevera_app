package com.proyecto_linkia.mi_nevera_app.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredient
import com.proyecto_linkia.mi_nevera_app.data.db.entities.ToBuyIngredient
import com.proyecto_linkia.mi_nevera_app.databinding.ItemIngredientBinding
import com.proyecto_linkia.mi_nevera_app.databinding.ItemTobuyingredientBinding

class ToBuyIngredientViewHolder(view: View) :RecyclerView.ViewHolder(view){

    private val binding= ItemTobuyingredientBinding.bind(view)

    fun render(
        ingredientModel: ToBuyIngredient,
        onClickListener: (Int) -> Unit
    ){
        binding.tvIngredientName.text=ingredientModel.ingredientName
        itemView.setOnClickListener { onClickListener(adapterPosition)}
    }
}