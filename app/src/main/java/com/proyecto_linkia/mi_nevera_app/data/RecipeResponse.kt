package com.proyecto_linkia.mi_nevera_app.data

import com.google.gson.annotations.SerializedName
import com.proyecto_linkia.mi_nevera_app.clases.Recipie

data class RecipeResponse(
    @SerializedName("recipies")var recipies : List<Recipie>,
    @SerializedName("succes") var succes:Boolean)
