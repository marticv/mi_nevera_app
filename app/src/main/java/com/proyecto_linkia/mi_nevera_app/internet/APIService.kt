package com.proyecto_linkia.mi_nevera_app.internet

import com.proyecto_linkia.mi_nevera_app.internet.response.IngredientsResponse
import com.proyecto_linkia.mi_nevera_app.internet.response.RecipesWithoutIngredientsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * interfaz que permite la conexion a internet
 */
interface APIService {
    @GET
    suspend fun getRecipesWithoutIngredients(@Url url:String):Response<RecipesWithoutIngredientsResponse>

    @GET
    suspend fun getIngredients(@Url url: String):Response<IngredientsResponse>
}