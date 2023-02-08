package com.proyecto_linkia.mi_nevera_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.proyecto_linkia.mi_nevera_app.adapter.MyIngredientAdapter
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredient
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMyIngredientsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyIngredients : AppCompatActivity() {

    private lateinit var binding: ActivityMyIngredientsBinding
    private var ingredientsMutableList:MutableList<MyIngredient> = mutableListOf()
    private lateinit var adapter: MyIngredientAdapter
    private val glManager =GridLayoutManager(this,2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycleView()

        binding.btAddMyIngredient.setOnClickListener { addIngredient() }
    }

    private fun addIngredient() {
        val name:String = binding.actvEntry.text.toString()
        val ingredient= MyIngredient(name)
        ingredientsMutableList.add(0,ingredient)
        adapter.notifyItemInserted(0)
        glManager.scrollToPosition(0)
    }

    private fun initRecycleView(){

        val recyclerView=binding.rvIngredients
        adapter = MyIngredientAdapter(myIngredientList = ingredientsMutableList,
            onClickListener = {position ->
                onDeletedItem(position)
            })
        getData()
        recyclerView.layoutManager = glManager
        recyclerView.adapter = adapter
    }

    private fun onDeletedItem(position:Int){
        val myIngredient = ingredientsMutableList[position]
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@MyIngredients)
            val dao =db.getMyIngredientDao()
            dao.deleteMyIngredient(myIngredient)
        }
        ingredientsMutableList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun getData(){

        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@MyIngredients)
            val dao=db.getMyIngredientDao()
            val myIngredientsList=dao.getAllMyIngredients()
            val list=myIngredientsList.toMutableList()
            for(item in list){
                ingredientsMutableList.add(item)
            }

            adapter.notifyDataSetChanged()
        }
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