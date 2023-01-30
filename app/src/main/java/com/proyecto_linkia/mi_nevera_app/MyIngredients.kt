package com.proyecto_linkia.mi_nevera_app

import android.app.ActivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyecto_linkia.mi_nevera_app.adapter.IngredientAdapter
import com.proyecto_linkia.mi_nevera_app.data.IngredientProvider
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMainBinding
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMyIngredientsBinding

class MyIngredients : AppCompatActivity() {

    private lateinit var binding: ActivityMyIngredientsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycleView()
    }

    private fun initRecycleView(){
        val recyclerView=binding.rvIngredients
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = IngredientAdapter(IngredientProvider.ingredientList)
    }
}