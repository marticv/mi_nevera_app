package com.proyecto_linkia.mi_nevera_app.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.databinding.ItemRecipeBinding

class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemRecipeBinding.bind(view)

    fun render(
        recipeModel: Recipe,
        onClickListener: (Int) -> Unit
    ){
        binding.tvRecipeName.text=recipeModel.recipeName

        var ingredientList =""
        for(ingredient in recipeModel.ingredients){
            ingredientList+="$ingredient\n"
        }

        binding.tvIngredientList.text = ingredientList

        itemView.setOnClickListener { onClickListener(adapterPosition)}
    }
}