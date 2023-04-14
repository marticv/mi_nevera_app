package com.proyecto_linkia.mi_nevera_app.data

import com.google.gson.annotations.SerializedName
import com.proyecto_linkia.mi_nevera_app.internet.IngredientExternal

data class IngredientsResponse(
    @SerializedName("ingredients")var ingredients: List<IngredientExternal>
)
