package com.proyecto_linkia.mi_nevera_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.proyecto_linkia.mi_nevera_app.adapter.IngredientAdapter
import com.proyecto_linkia.mi_nevera_app.clases.Ingredient
import com.proyecto_linkia.mi_nevera_app.data.IngredientProvider
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMyIngredientsBinding

class MyIngredients : AppCompatActivity() {

    private lateinit var binding: ActivityMyIngredientsBinding
    private var ingredientsMutableList:MutableList<Ingredient> = IngredientProvider.ingredientList.toMutableList()
    private lateinit var adapter: IngredientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycleView()
    }

    private fun initRecycleView(){
        val recyclerView=binding.rvIngredients
        adapter = IngredientAdapter(ingredientList = ingredientsMutableList,
        onClickListener = {ingredient ->
            onItemSelected(ingredient)
        },
        onClickDeleted = {position->onDeletedItem(position)})
        recyclerView.layoutManager = GridLayoutManager(this,2)
        recyclerView.adapter = adapter
    }

    private fun onItemSelected(ingredient: Ingredient){
        Toast.makeText( this,ingredient.ingredientName, Toast.LENGTH_SHORT).show()
    }

    private fun onDeletedItem(position:Int){
        ingredientsMutableList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    /*
    private fun fillActvEntry(){
        var systemIngredients = IngredientProvider.ingredientList

        for(i in 0 until systemIngredients.size){

        }
        var systemIngredientsList : Array<String> = resources.getStringArray(R.array.sistemIngredients)
        var adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, systemIngredients)
        actvEntry.setAdapter(adapter)
    }*/
}