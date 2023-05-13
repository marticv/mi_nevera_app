package com.proyecto_linkia.mi_nevera_app.internet.response

import com.google.gson.annotations.SerializedName
import com.proyecto_linkia.mi_nevera_app.internet.pojoExternal.IngredientExternal

data class IngredientsResponse(
    @SerializedName("ingredients")var ingredients: List<IngredientExternal>
)