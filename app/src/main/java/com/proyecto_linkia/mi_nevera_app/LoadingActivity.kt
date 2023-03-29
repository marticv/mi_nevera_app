package com.proyecto_linkia.mi_nevera_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.proyecto_linkia.mi_nevera_app.clases.Recipe
import com.proyecto_linkia.mi_nevera_app.data.RecipeResponse
import com.proyecto_linkia.mi_nevera_app.internet.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoadingActivity : AppCompatActivity() {

    private val recipeList: MutableList<Recipe> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        screenSplash.setKeepOnScreenCondition { true }

        getData()

    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://api.npoint.io/281f74aeedbb04eb4d6b/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getData(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call: Response<RecipeResponse> =
                    getRetrofit().create(APIService::class.java).getRecipes("")
                val result: RecipeResponse? = call.body()
                runOnUiThread {
                    if (call.isSuccessful) {
                        val recipes: List<Recipe> = result?.recipes ?: emptyList()
                        recipeList.clear()
                        recipeList.addAll(recipes)
                        val intent = Intent(this@LoadingActivity, MainActivity::class.java)
                        intent.putExtra("data", recipeList as java.io.Serializable)
                        startActivity(intent)
                        finish()
                    } else {
                        showError()
                        val recipes: List<Recipe> = emptyList()
                        val intent = Intent(this@LoadingActivity, MainActivity::class.java)
                        intent.putExtra("data", recipes as java.io.Serializable)
                        startActivity(intent)
                        finish()
                    }
                }
            }catch (e:Exception){
                val recipes: List<Recipe> = emptyList()
                val intent = Intent(this@LoadingActivity, MainActivity::class.java)
                intent.putExtra("data", recipes as java.io.Serializable)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showError() {
        Toast.makeText(this@LoadingActivity, "error", Toast.LENGTH_LONG).show()
    }
}