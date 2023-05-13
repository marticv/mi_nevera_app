package com.proyecto_linkia.mi_nevera_app.internet.response

import com.google.gson.annotations.SerializedName
import com.proyecto_linkia.mi_nevera_app.pojo.Recipe

data class RecipeResponse(
    @SerializedName("recipes")var recipes : List<Recipe>,
    @SerializedName("succes") var succes:Boolean)