package com.proyecto_linkia.mi_nevera_app.internet

import com.proyecto_linkia.mi_nevera_app.data.RecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getRecipes(@Url url:String):Response<RecipeResponse>
}