package com.proyecto_linkia.mi_nevera_app.data

import com.google.gson.annotations.SerializedName
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.clases.RecipeComplete

data class RecipesWithoutIngredientsResponse(
    @SerializedName("recipes")var recipes : List<RecipeComplete>
)
