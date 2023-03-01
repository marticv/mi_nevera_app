package com.proyecto_linkia.mi_nevera_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.proyecto_linkia.mi_nevera_app.clases.Recipie
import com.proyecto_linkia.mi_nevera_app.data.RecipeResponse
import com.proyecto_linkia.mi_nevera_app.internet.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoadingActivity : AppCompatActivity() {

    private val recipeList: MutableList<Recipie> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        screenSplash.setKeepOnScreenCondition { true }

        getData()

        //val intent = Intent(this, MainActivity::class.java)
        //startActivity(intent)
        //finish()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://api.npoint.io/281f74aeedbb04eb4d6b/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getData(){
        CoroutineScope(Dispatchers.IO).launch {
            val call: Response<RecipeResponse> = getRetrofit().create(APIService::class.java).getRecipes("")
            val result: RecipeResponse? = call.body()
            runOnUiThread {
                if(call.isSuccessful){
                    val recipies: List<Recipie> = result?.recipies ?: emptyList()
                    recipeList.clear()
                    recipeList.addAll(recipies)
                    val intent = Intent(this@LoadingActivity, MainActivity::class.java)
                    intent.putExtra("data",recipeList as java.io.Serializable)
                    startActivity(intent)
                    finish()
                }else{
                    showError()
                }
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "error", Toast.LENGTH_LONG).show()
    }
}