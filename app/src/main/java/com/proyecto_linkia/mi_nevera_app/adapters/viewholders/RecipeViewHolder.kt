package com.proyecto_linkia.mi_nevera_app.adapters.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.R
import com.proyecto_linkia.mi_nevera_app.pojo.Recipe
import com.proyecto_linkia.mi_nevera_app.databinding.ItemRecipeBinding
import com.squareup.picasso.Picasso

class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemRecipeBinding.bind(view)

    fun render(
        recipeModel: Recipe,
        onClickListener: (Int) -> Unit,
        onClickFavourite: (Int) -> Unit
    ) {
        binding.tvRecipeName.text = recipeModel.recipeName

        val image = recipeModel.image
        if (image != "nullFromUser") {
            Picasso.get().load(image).into(binding.imageView)
        }else{
            binding.imageView.setImageResource(R.mipmap.ic_launcher)
        }

        if (recipeModel.isFavourite) {
            binding.ibFavourite.setImageResource(R.drawable.ic_favourite_selected)
        } else {
            binding.ibFavourite.setImageResource(R.drawable.ic_favourite)
        }

        itemView.setOnClickListener { onClickListener(adapterPosition) }
        binding.ibFavourite.setOnClickListener {
            onClickFavourite(adapterPosition)
            changeFavourite(recipeModel)
        }
    }

    private fun changeFavourite(recipeModel: Recipe) {
        if (recipeModel.isFavourite) {
            binding.ibFavourite.setImageResource(R.drawable.ic_favourite)
            recipeModel.isFavourite = false
        } else {
            binding.ibFavourite.setImageResource(R.drawable.ic_favourite_selected)
            recipeModel.isFavourite = true
        }
    }
}