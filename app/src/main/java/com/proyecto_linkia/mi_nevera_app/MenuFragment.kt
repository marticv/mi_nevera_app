package com.proyecto_linkia.mi_nevera_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.proyecto_linkia.mi_nevera_app.databinding.FragmentMenuBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //obtenemos el nombre de la activity padre
        val activityName = activity?.javaClass?.simpleName

        //al crear el fragment marcamos la activity seleccionada.
        //Primero obtenemos el nombre de la activity padre y cambiamos
        //la src del imagebutton que corresponada a la activity seleccionada
        when (activityName) {
            "MyIngredients" -> binding.ibMyIngredients.setImageResource(R.drawable.ic_myfridge_selected)
            "SearchActivity" -> binding.ibSearch.setImageResource(R.drawable.ic_improvise_selected)
            "InsertRecipeActivity" -> binding.ibEnterRecipe.setImageResource(R.drawable.ic_addrecipe_selected)
            "ShoppingActivity" -> binding.ibShopingList.setImageResource(R.drawable.ic_shoppingchart_selected)
        }

        //damos funcionalidad a los botones,
        //para cada uno será abrir la activity seleccionada y cerrar la actual
        //en caso de que estemos en la activiy, no hacemos nada
        binding.ibMyIngredients.setOnClickListener {
            if (!activityName.equals("MyIngredients")) {
                startActivity((Intent(context, MyIngredients::class.java)))
                activity?.finish()
            }
        }
        binding.ibSearch.setOnClickListener {
            if (!activityName.equals("SearchActivity")) {
                startActivity((Intent(context, SearchActivity::class.java)))
                activity?.finish()
            }
        }
        binding.ibEnterRecipe.setOnClickListener {
            if (!activityName.equals("InsertRecipeActivity")) {
                startActivity((Intent(context, InsertRecipeActivity::class.java)))
                activity?.finish()
            }
        }
        binding.ibShopingList.setOnClickListener {
            if (!activityName.equals("ShoppingActivity")) {
                startActivity(Intent(context, ShoppingActivity::class.java))
                activity?.finish()
            }
        }
    }

    /*
    Codigo prehecho al crear el fragment con andorid studio
     */
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}