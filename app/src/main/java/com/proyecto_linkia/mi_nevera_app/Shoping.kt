package com.proyecto_linkia.mi_nevera_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.proyecto_linkia.mi_nevera_app.adapter.BoughtIngredientAdapter
import com.proyecto_linkia.mi_nevera_app.adapter.ToBuyIngredientAdapter
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.ShoppingIngredient
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityShopingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Shoping : AppCompatActivity() {

    private lateinit var binding: ActivityShopingBinding
    private lateinit var toBuyAdapter: ToBuyIngredientAdapter
    private lateinit var boughtAdapter: BoughtIngredientAdapter
    private val glManagerToBuy = GridLayoutManager(this,2)
    private val glManagerBought = GridLayoutManager(this,2)
    var toBuyList: MutableList<ShoppingIngredient> = mutableListOf()
    var boughtList: MutableList<ShoppingIngredient> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()

        //obtenemos datos de la base de datos y lo pasamos al recyclerView
        initToBuyRecycleView()
        initBoughtRecycleView()

        binding.btAddToShopingList.setOnClickListener { addIngredient() }
    }

    private fun addIngredient() {
        //cogemos el texto y lo convertimos en un ingrediente que pasamos al listado
        val ingredient= ShoppingIngredient(binding.actvEntry.text.toString())
        binding.textView2.text = ingredient.toBuy.toString()
        binding.textView3.text = ingredient.bought.toString()

        //comprobamos si el ingrediente esta ya en la lista
        if(!toBuyList.contains(ingredient)) {

            toBuyList.add(0, ingredient)

            //hacemos que el adaptador sepa que hay un nuevo item y lo ponemos en primer lugar
            toBuyAdapter.notifyItemInserted(0)
            glManagerToBuy.scrollToPosition(0)

            //guardamos el item en la base de datos
            CoroutineScope(Dispatchers.IO).launch {
                val db = DataBaseBuilder.getInstance(this@Shoping)
                val dao = db.getShoppingIngredientDao()
                dao.insertToBuyIngredient(ingredient)
            }

        }else{
            //informamos al usuario
            Toast.makeText(this, "${ingredient.ingredientName} ya en la lista", Toast.LENGTH_LONG).show()
        }
        //reiniciamos el texto
        binding.actvEntry.setText("")
    }

    private fun addFromToBuyList(position: Int){
        val ingredient=ShoppingIngredient(toBuyList[position].ingredientName)
        ingredient.apply {
            toBuy=0
            bought = 1
        }
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@Shoping)
            val dao = db.getShoppingIngredientDao()
            dao.updateShopingIngredient(ingredient)
        }

        binding.textView2.text = ingredient.toBuy.toString()
        binding.textView3.text = ingredient.bought.toString()
        boughtList.add(0,ingredient)
        boughtAdapter.notifyItemInserted(0)
    }

    private fun addFromBoughtList(position: Int){
        val ingredient=ShoppingIngredient(boughtList[position].ingredientName)
        ingredient.apply {
            toBuy= 1
            bought = 0
        }
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@Shoping)
            val dao = db.getShoppingIngredientDao()
            dao.updateShopingIngredient(ingredient)
        }
        binding.textView2.text = ingredient.toBuy.toString()
        binding.textView3.text = ingredient.bought.toString()
        toBuyList.add(0,ingredient)
        toBuyAdapter.notifyItemInserted(0)
    }

    /**
     * Inicia el recyclerView
     *
     */
    private fun initToBuyRecycleView(){
        //creamos el adapter y lo pasamos al recyclerview para que se renderice
        val recyclerView=binding.rvToBuy
        toBuyAdapter = ToBuyIngredientAdapter(myIngredientList = toBuyList,
            onClickListener =  {position ->
                onDeletedToBuyItem(position)
            })
        recyclerView.layoutManager = glManagerToBuy
        recyclerView.adapter = toBuyAdapter
        toBuyAdapter.notifyDataSetChanged()
    }


    private fun initBoughtRecycleView(){
        //creamos el adapter y lo pasamos al recyclerview para que se renderice
        val recyclerView=binding.rvBought
        boughtAdapter = BoughtIngredientAdapter(myIngredientList = boughtList,
            onClickListener =  {position ->
                onDeletedBoughtItem(position)
            })
        recyclerView.layoutManager = glManagerBought
        recyclerView.adapter = boughtAdapter
        boughtAdapter.notifyDataSetChanged()
    }

    private fun onDeletedToBuyItem(position:Int){
        //Obtenemos el ingrediente a eliminar de la lista y lo pasamos a una corrutina
        //para eliminarlo tambien de la base de datos
        val myIngredient = toBuyList[position]
        addFromToBuyList(position)
        //eliminamos el item del recyclerView y avisamos al adaptador
        toBuyList.removeAt(position)
        toBuyAdapter.notifyItemRemoved(position)
    }

    private fun onDeletedBoughtItem(position:Int){
        //Obtenemos el ingrediente a eliminar de la lista y lo pasamos a una corrutina
        //para eliminarlo tambien de la base de datos
        addFromBoughtList(position)
        //eliminamos el item del recyclerView y avisamos al adaptador
        boughtList.removeAt(position)
        boughtAdapter.notifyItemRemoved(position)
    }

    private fun getData(){
        //iniciamos corrutina para obtener datos
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@Shoping)
            val dao=db.getShoppingIngredientDao()
            val toBuyIngredientsList = dao.getAllToBuyIngredients()
            for(item in toBuyIngredientsList){
                toBuyList.add(item)
            }

            val boughtIngredientsList = dao.getAllBoughtIngredients()
            for(item in boughtIngredientsList){
                boughtList.add(item)
            }

            runOnUiThread {
                toBuyAdapter.notifyDataSetChanged()
                boughtAdapter.notifyDataSetChanged()
            }
        }
    }
}