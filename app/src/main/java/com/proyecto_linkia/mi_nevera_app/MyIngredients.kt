package com.proyecto_linkia.mi_nevera_app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.proyecto_linkia.mi_nevera_app.adapter.MyIngredientAdapter
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.MyIngredient
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityMyIngredientsBinding
import com.proyecto_linkia.mi_nevera_app.utils.fillActvEntry
import com.proyecto_linkia.mi_nevera_app.utils.getRecipesList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyIngredients : AppCompatActivity() {

    private lateinit var binding: ActivityMyIngredientsBinding
    private var ingredientsMutableList:MutableList<MyIngredient> = mutableListOf()
    private lateinit var adapter: MyIngredientAdapter
    private val glManager =GridLayoutManager(this,2)
    var recipeList: MutableList<Recipe> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUp()

        binding.btAddMyIngredient.setOnClickListener { addIngredient() }
        binding.btBack.setOnClickListener { finish() }
    }

    private fun setUp() {
        recipeList = getRecipesList(this@MyIngredients)
        fillActvEntry(binding.actvEntry)
        initRecycleView()
        getData()
    }

    /**
     * Funcion que añade un item al recyclerview y a la base de datos
     *
     */
    private fun addIngredient() {
        //cogemos el texto y lo convertimos en un ingrediente que pasamos al listado
        val ingredient= MyIngredient(binding.actvEntry.text.toString())

        //comprobamos si el ingrediente esta ya en la lista
        if(!ingredientsMutableList.contains(ingredient)) {

            ingredientsMutableList.add(0, ingredient)

            //hacemos que el adaptador sepa que hay un nuevo item y lo ponemos en primer lugar
            adapter.notifyItemInserted(0)
            glManager.scrollToPosition(0)

            //guardamos el item en la base de datos
            CoroutineScope(Dispatchers.IO).launch {
                val db = DataBaseBuilder.getInstance(this@MyIngredients)
                val dao = db.getMyIngredientDao()
                dao.insertMyIngredient(ingredient)
            }
        }else{
            //informamos al usuario
            Toast.makeText(this, "${ingredient.ingredientName} ya en la lista", Toast.LENGTH_LONG).show()
        }
        //reiniciamos el texto
        binding.actvEntry.setText("")
    }

    /**
     * Inicia el recyclerView
     *
     */
    private fun initRecycleView(){
        //creamos el adapter y lo pasamos al recyclerview para que se renderice
        val recyclerView=binding.rvIngredients
        adapter = MyIngredientAdapter(myIngredientList = ingredientsMutableList,
            onClickListener =  {position ->
                onDeletedItem(position)
            })
        recyclerView.layoutManager = glManager
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    /**
     * Elimina un item del recyclerView y de la base de datos
     *
     * @param position
     */
    private fun onDeletedItem(position:Int){
        //Obtenemos el ingrediente a eliminar de la lista y lo pasamos a una corrutina
        //para eliminarlo tambien de la base de datos
        val myIngredient = ingredientsMutableList[position]
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@MyIngredients)
            val dao =db.getMyIngredientDao()
            dao.deleteMyIngredient(myIngredient)
        }
        //eliminamos el item del recyclerView y avisamos al adaptador
        ingredientsMutableList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }


    /**
     * obtenemos todos los registros de la tabla my_ingredients_table
     *
     */
    private fun getData(){
        //iniciamos corrutina para obtener datos
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DataBaseBuilder.getInstance(this@MyIngredients)
                val dao = db.getMyIngredientDao()
                val myIngredientsList = dao.getAllMyIngredients()
                //pasamos la lista a mutablelist
                //val list=myIngredientsList.toMutableList()
                for (item in myIngredientsList) {
                    ingredientsMutableList.add(item)
                }

                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }catch (e:Exception){
                showError()
            }

        }
    }

    private fun showError() {
        Toast.makeText(this@MyIngredients, "error", Toast.LENGTH_LONG).show()
    }

    /**
     * Cerramos la base de datos
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@MyIngredients)
            db.close()
        }
    }

}