package com.proyecto_linkia.mi_nevera_app.internet.response

import com.google.gson.annotations.SerializedName
import com.proyecto_linkia.mi_nevera_app.internet.pojoExternal.RecipeExternal

data class RecipesWithoutIngredientsResponse(
    @SerializedName("recipes")var recipes : List<RecipeExternal>
)