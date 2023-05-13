package com.proyecto_linkia.mi_nevera_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.proyecto_linkia.mi_nevera_app.adapters.BoughtIngredientAdapter
import com.proyecto_linkia.mi_nevera_app.adapters.ToBuyIngredientAdapter
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.ShoppingIngredient
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityShoppingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingBinding
    private lateinit var toBuyAdapter: ToBuyIngredientAdapter
    private lateinit var boughtAdapter: BoughtIngredientAdapter
    private val glManagerToBuy = GridLayoutManager(this,2)
    private val glManagerBought = GridLayoutManager(this,2)
    var toBuyList: MutableList<ShoppingIngredient> = mutableListOf()
    var boughtList: MutableList<ShoppingIngredient> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        //añadimos el layout a la activity
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //obtenemos datos de la base de datos y lo pasamos al recyclerView
        getData()
        initToBuyRecycleView()
        initBoughtRecycleView()

        //damos funcionalidad a los botones
        binding.btAddToShopingList.setOnClickListener { addIngredient() }
        binding.btDeleteBought.setOnClickListener { deleteBoughtItems() }
    }

    /**
     * FUncion que elimina todos los elementos del recycleview
     * ingredientes recientes
     */
    private fun deleteBoughtItems() {
        //si hay elementos en la lista procedemos al borrado
        if(boughtList.size>0) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = DataBaseBuilder.getInstance(this@ShoppingActivity)
                val dao = db.getShoppingIngredientDao()
                //borramos los elementos de la bd y de la lista
                for (item in boughtList) {
                    dao.deleteShoppingIngredient(item)
                    boughtList.remove(item)
                }
                //en la ui, borramos los datos si queda alguo por error y notificaos al dapter
                runOnUiThread {
                    boughtList.clear()
                    boughtAdapter.notifyDataSetChanged()
                }
                db.close()
            }
        }
    }

    /**
     * Funcion que añade un elemento al recicleview y la bd
     */
    private fun addIngredient() {
        //cogemos el texto y lo convertimos en un ingrediente que pasamos al listado
        val ingredient= ShoppingIngredient(binding.actvEntry.text.toString())

        //comprobamos si el ingrediente esta ya en la lista
        if(!toBuyList.contains(ingredient)) {

            toBuyList.add(0, ingredient)

            //hacemos que el adaptador sepa que hay un nuevo item y lo ponemos en primer lugar
            toBuyAdapter.notifyItemInserted(0)
            glManagerToBuy.scrollToPosition(0)

            //guardamos el item en la base de datos
            CoroutineScope(Dispatchers.IO).launch {
                val db = DataBaseBuilder.getInstance(this@ShoppingActivity)
                val dao = db.getShoppingIngredientDao()
                dao.insertToBuyIngredient(ingredient)
                db.close()
            }
        }else{
            //informamos al usuario
            Toast.makeText(this, "${ingredient.ingredientName} ya en la lista", Toast.LENGTH_LONG).show()
        }
        //reiniciamos el texto
        binding.actvEntry.setText("")
    }

    /**
     * Funcion que mueve un elemento para comprar a recientes
     * y lo modifica en la bd
     */
    private fun addFromToBuyList(position: Int){
        //obtenemos el nombre del ingrediente y le cambiamos el si es para comprar o vender
        val ingredient=ShoppingIngredient( toBuyList[position].ingredientName)
        ingredient.apply {
            toBuy=0
            bought = 1
        }
        //modificamos el item en la bd
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@ShoppingActivity)
            val dao = db.getShoppingIngredientDao()
            dao.updateShoppingIngredient(ingredient)
            db.close()
        }
        //añadimos a recientes y notificamos al adapter para que lo pinte
        boughtList.add(0,ingredient)
        boughtAdapter.notifyItemInserted(0)
    }

    /**
     * Funcion que pasa un item de recientes a para comprar
     */
    private fun addFromBoughtList(position: Int){
        //obtenemos el ingrediente y le cambiamos los parametros para comprar y reciente
        val ingredient=ShoppingIngredient(boughtList[position].ingredientName)
        ingredient.apply {
            toBuy= 1
            bought = 0
        }
        //modificamos en la bd
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@ShoppingActivity)
            val dao = db.getShoppingIngredientDao()
            dao.updateShoppingIngredient(ingredient)
            db.close()
        }
        //pintamos el cambio en la activity
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


    /**
     * Inicia el recycleview comprados recientemente
     */
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

    /**
     * FUncion que elimina un item del ingredientes a comprar
     */
    private fun onDeletedToBuyItem(position:Int){
        //Obtenemos el ingrediente a eliminar de la lista y lo pasamos a una corrutina
        //para eliminarlo tambien de la base de datos
        val myIngredient = toBuyList[position]
        addFromToBuyList(position)
        //eliminamos el item del recyclerView y avisamos al adaptador
        toBuyList.removeAt(position)
        toBuyAdapter.notifyItemRemoved(position)
    }

    /**
     * funcion que elimina un item de comprados recientemente
     */
    private fun onDeletedBoughtItem(position:Int){
        //Obtenemos el ingrediente a eliminar de la lista y lo pasamos a una corrutina
        //para eliminarlo tambien de la base de datos
        addFromBoughtList(position)
        //eliminamos el item del recyclerView y avisamos al adaptador
        boughtList.removeAt(position)
        boughtAdapter.notifyItemRemoved(position)
    }

    /**
     * Funcion que obtiene los datos de la bd
     */
    private fun getData(){
        //iniciamos corrutina para obtener datos
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DataBaseBuilder.getInstance(this@ShoppingActivity)
                val dao = db.getShoppingIngredientDao()

                //obtenemos los datos de ingredientes para comprar
                //y comprados recientemente
                val toBuyIngredientsList = dao.getAllToBuyIngredients()
                for (item in toBuyIngredientsList) {
                    toBuyList.add(item)
                }

                val boughtIngredientsList = dao.getAllBoughtIngredients()
                for (item in boughtIngredientsList) {
                    boughtList.add(item)
                }
                //pintamos en la activity
                runOnUiThread {
                    toBuyAdapter.notifyDataSetChanged()
                    boughtAdapter.notifyDataSetChanged()
                }
                db.close()
            }catch (e:Exception){
                showError()
            }
        }
    }

    /**
     * Informa al usuario con un toast
     */
    private fun showError() {
        Toast.makeText(this@ShoppingActivity, "error", Toast.LENGTH_LONG).show()
    }

    /**
     * al destruir la activity cerramos la bd si estuviera abierta
     */
    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch {
            val db = DataBaseBuilder.getInstance(this@ShoppingActivity)
            db.close()
        }
    }
}