package com.proyecto_linkia.mi_nevera_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.proyecto_linkia.mi_nevera_app.adapter.IngredientAdapter
import com.proyecto_linkia.mi_nevera_app.clases.Ingredient
import com.proyecto_linkia.mi_nevera_app.clases.toDomain
import com.proyecto_linkia.mi_nevera_app.data.IngredientProvider
import com.proyecto_linkia.mi_nevera_app.data.db.dao.MyIngredientDao
import com.proyecto_linkia.mi_nevera_app.data.db.database.MyIngredientsApp
import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredientEntity
import com.proyecto_linkia.mi_nevera_app.data.db.entities.toEntity
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMyIngredientsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyIngredients : AppCompatActivity() {

    private lateinit var binding: ActivityMyIngredientsBinding
    private var ingredientsMutableList:MutableList<Ingredient> = IngredientProvider.ingredientList.toMutableList()
    private lateinit var adapter: IngredientAdapter
    private val glManager =GridLayoutManager(this,2)
    val app = applicationContext as MyIngredientsApp
    var dao: MyIngredientDao = app.room.getMyIngredientDao()
    lateinit var entityList:List<MyIngredientEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initRecycleView()

        binding.btAddMyIngredient.setOnClickListener { addIngredient() }
    }

    private fun addIngredient() {
        val name:String = binding.actvEntry.text.toString()
        val ingredient:Ingredient= Ingredient(null,name)
        ingredientsMutableList.add(0,ingredient)
        adapter.notifyItemInserted(0)
        glManager.scrollToPosition(0)

        CoroutineScope(Dispatchers.IO).launch {
            dao.insertMyIngredient(ingredient.toEntity())
        }
    }

    private fun initRecycleView(){

        val recyclerView=binding.rvIngredients
        adapter = IngredientAdapter(ingredientList = ingredientsMutableList,
        onClickListener = {position ->
            onDeletedItem(position)
        })
        recyclerView.layoutManager = glManager
        recyclerView.adapter = adapter
    }

    private fun onDeletedItem(position:Int){
        val myIngredient = ingredientsMutableList[position]
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteMyIngredient(myIngredient.toEntity())
        }
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