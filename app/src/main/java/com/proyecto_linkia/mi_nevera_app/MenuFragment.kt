package com.proyecto_linkia.mi_nevera_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        _binding = FragmentMenuBinding.inflate(inflater,container,false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //al crear el fragment marcamos la activity seleccionada
        when(activity?.javaClass?.simpleName){
            "MyIngredients"->binding.ibMyIngredients.setImageResource(resources.getDrawable(R.drawable.ic_myfridge_selected))
            "SearchActivity"-> binding.ibSearch.setImageDrawable(R.drawable.ic_myfridge_selected.toDrawable())
            "InsertRecipeActivity"-> binding.ibEnterRecipe.setImageDrawable(R.drawable.ic_myfridge_selected.toDrawable())
            "ShoppingActivity"-> binding.ibShopingList.setImageDrawable(R.drawable.ic_myfridge_selected.toDrawable())
        }

        //damos funcionalidad a los botones
        binding.ibMyIngredients.setOnClickListener {
            startActivity((Intent(context,MyIngredients::class.java)))
            activity?.finish()
        }
        binding.ibSearch.setOnClickListener {
            startActivity((Intent(context,SearchActivity::class.java)))
            activity?.finish()
        }
        binding.ibEnterRecipe.setOnClickListener {
            startActivity((Intent(context,InsertRecipeActivity::class.java)))
            activity?.finish()
        }
        binding.ibShopingList.setOnClickListener {
            startActivity(Intent(context, ShoppingActivity::class.java))
            activity?.finish()
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