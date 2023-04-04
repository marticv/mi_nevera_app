package com.proyecto_linkia.mi_nevera_app.internet

import com.proyecto_linkia.mi_nevera_app.data.IngredientsResponse
import com.proyecto_linkia.mi_nevera_app.data.RecipeResponse
import com.proyecto_linkia.mi_nevera_app.data.RecipesWithoutIngredientsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getRecipes(@Url url:String):Response<RecipeResponse>

    @GET
    suspend fun getRecipesWithoutIngredients(@Url url:String):Response<RecipesWithoutIngredientsResponse>

    @GET
    suspend fun getIngredients(@Url url: String):Response<IngredientsResponse>
}