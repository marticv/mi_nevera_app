package com.proyecto_linkia.mi_nevera_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_linkia.mi_nevera_app.R
import com.proyecto_linkia.mi_nevera_app.clases.Recipe

class RecipeAdapter(
    private val recipeList: List<Recipe>,
    private val onClickListener: (Int) -> Unit,
    private val onClickFavourite: (Int) -> Unit
) : RecyclerView.Adapter<RecipeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RecipeViewHolder(layoutInflater.inflate(R.layout.item_recipe, parent, false))
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val item = recipeList[position]
        holder.render(item, onClickListener, onClickFavourite)
    }

    override fun getItemCount(): Int = recipeList.size
}