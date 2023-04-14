package com.proyecto_linkia.mi_nevera_app.data

import com.google.gson.annotations.SerializedName
import com.proyecto_linkia.mi_nevera_app.internet.RecipeExternal

data class RecipesWithoutIngredientsResponse(
    @SerializedName("recipes")var recipes : List<RecipeExternal>
)
