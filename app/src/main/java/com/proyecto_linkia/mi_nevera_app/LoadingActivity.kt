package com.proyecto_linkia.mi_nevera_app

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.proyecto_linkia.mi_nevera_app.data.IngredientsResponse
import com.proyecto_linkia.mi_nevera_app.data.RecipesWithoutIngredientsResponse
import com.proyecto_linkia.mi_nevera_app.data.db.database.DataBaseBuilder
import com.proyecto_linkia.mi_nevera_app.data.db.entities.relations.RecipeIngredientCrossReference
import com.proyecto_linkia.mi_nevera_app.internet.APIService
import com.proyecto_linkia.mi_nevera_app.internet.IngredientExternal
import com.proyecto_linkia.mi_nevera_app.internet.RecipeExternal
import com.proyecto_linkia.mi_nevera_app.utils.toEmptyRecipeEntity
import com.proyecto_linkia.mi_nevera_app.utils.toEntity
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //creamos instancia del splash antes de que se cree la vista
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        //ponemos la condicion de acabar el splash. Al poner true no se ira nunca si no hacemos nada
        //haremos que cambie de activity por codigo
        screenSplash.setKeepOnScreenCondition { true }

        //habrimos corrutina para poder iniciar las diferentes suspend fun (acceder a API y room)
        CoroutineScope(Dispatchers.IO).launch {

            //usamos las coroutinas con "withContext" para que no se solapen los metodos asincronos
            //primero obtenemos los datos de Firebase y despues los insertamos en la base de datos
            val ingredients = withContext(Dispatchers.IO) {
                getIngredientsFromFirebase()
            }
            val recipes = withContext(Dispatchers.IO) {
                getRecipesFromFirebase()
            }
            withContext(Dispatchers.IO) {
                addIngredientToDB(ingredients)
                addRecipesToDB(recipes)
            }

            // una vez listo cambiamos de activity y cerramos esta para que no se pueda accefer por error
            val intent = Intent(this@LoadingActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Funcion que nos devuelve un Retrofit al que hacer peticiones de red
     *
     * @return Retrofit con nuestra base url base
     */
    private fun getFirebaseData(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://mineveraapp-linkiafp-default-rtdb.europe-west1.firebasedatabase.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Función que nos devuelve una lista de ingredientes desde Firebase
     *
     * @return lista de ingredientes
     */
    private suspend fun getIngredientsFromFirebase(): List<IngredientExternal> {
        //intentamos obtener los ingredientes de internet o devolvemos una lista vacia para evitar errores
        return try {
            //hacemos la llamda a la API
            val call: Response<IngredientsResponse> =
                getFirebaseData().create(APIService::class.java)
                    .getIngredients("myingredients.json")
            val result: IngredientsResponse? = call.body()
            if (call.isSuccessful) {
                //en caso de exito devolvemos la lista de ingredientes
                result?.ingredients!!
            } else {
                //si la api no contiene datos devolvemos una lista vacia
                emptyList()
            }
        } catch (e: Exception) {
            //en caso de error devolvemos una lista vacia
            emptyList()
        }
    }

    /**
     * Función que nos devuelve una lista de recetas desde Firebase
     *
     * @return lista con recetas
     */
    private suspend fun getRecipesFromFirebase(): List<RecipeExternal> {
        //intentamos obtener las recetas de internet o devolvemos una lista vacia para evitar errores
        return try {
            //hacemos la llamda a la API
            val call: Response<RecipesWithoutIngredientsResponse> =
                getFirebaseData().create(APIService::class.java)
                    .getRecipesWithoutIngredients("myrecipes.json")
            val result: RecipesWithoutIngredientsResponse? = call.body()
            //en caso de exito devolvemos la lista de ingredientes
            //y si hay problemas devolvemos una lista vacia para evitar errores
            if (call.isSuccessful) {
                result?.recipes!!
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


    /**
     * Función que añade los ingredientes a nuestra base de datos
     *
     * @param ingredients
     */
    private suspend fun addIngredientToDB(ingredients: List<IngredientExternal>) {
        try {
            //creamos una instancia de la base de datos y nuestro DAO
            val db = DataBaseBuilder.getInstance(this@LoadingActivity)
            val dao = db.getIngredientsDao()

            //insertamos los elementos de la lista
            for (ingredient in ingredients) {
                dao.insertIngredient(ingredient.toEntity())
            }
            //cerramos la conexion a la base de datos
            db.close()
        } catch (e: Exception) {
            Log.d(TAG, "${e.message}")
        }
    }

    /**
     * Función que añade los ingredientes a la base de datos
     *
     * @param recipes
     */
    private suspend fun addRecipesToDB(recipes: List<RecipeExternal>) {
        try {
            //creamos una instancia de la base de datos y nuestro DAO
            val db = DataBaseBuilder.getInstance(this@LoadingActivity)
            val dao = db.getRecipeDao()

            //añadimos las recetas y la relacion con los ingredientes a la base de datos
            for (recipe in recipes) {
                dao.insertRecipe(recipe.toEmptyRecipeEntity())
                for (ingredient in recipe.ingredients) {
                    dao.insertRecipeIngredientsCrossReference(
                        RecipeIngredientCrossReference(recipe.recipeId, ingredient)
                    )
                }
            }
            //cerramos la conexion a la base de datos
            db.close()
        } catch (e: Exception) {
            Log.d(TAG, "error recipes db")
        }
    }

    /**
     * Al cerrar la ventana nos aseguramos de
     * que la conexion a la bd se cierre
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        //cerramos la conexion a la base de datos si estubiera abierta
        val db = DataBaseBuilder.getInstance(this@LoadingActivity)
        db.close()
    }
}