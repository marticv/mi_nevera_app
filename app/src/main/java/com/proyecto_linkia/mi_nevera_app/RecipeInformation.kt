package com.proyecto_linkia.mi_nevera_app


import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_linkia.mi_nevera_app.pojo.Recipe
import com.proyecto_linkia.mi_nevera_app.databinding.ActivityRecipeInformationBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class RecipeInformation : AppCompatActivity() {
    private lateinit var recipe: Recipe
    private lateinit var binding: ActivityRecipeInformationBinding
    private lateinit var stringShare: String


    override fun onCreate(savedInstanceState: Bundle?) {

        //añadimos la vista a la activity
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //recvimos los datos de la activity padre
        recipe = intent.extras?.get("recipe") as Recipe

        //preparamos datos
        stringShare = "Hoy cocinamos:\n${recipe.recipeName}\nEnviado desde mi_nevaraApp"

        //obtenemos las medidas del dispositivo y modificamos el tamaño
        //de la activity para dar efecto de sobreposicion
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        window.setLayout((width * 0.85).toInt(), (height * 0.7).toInt())

        binding.tvRecipeName.text = recipe.recipeName

        //modificamos el texto que ve el usuario segun la receta
        var ingredients = ""
        for (ingredient in recipe.ingredients) {
            ingredients += "$ingredient\n"
        }
        binding.tvIngredients.text = ingredients

        if (recipe.isVegan) {
            binding.tvVegan.text = "Vegana"
        }

        val image = recipe.image
        if (image != "nullFromUser") {
            Picasso.get().load(image).into(binding.ivRecipe)
        }else{
            binding.ivRecipe.setImageResource(R.mipmap.ic_launcher)
        }

        //damos funcionalidad a los botones
        binding.btClose.setOnClickListener {
            finish()
        }
        binding.btShare.setOnClickListener {
            share()
        }
    }


    /**
     * Funcion que permite compartir una imagen o la receta con varias apps
     *
     */
    private fun share() {

        //intentamos obtener una captura de pantalla
        val bitmap = getScreenshot(binding.clInsertRecipe)

        //si lo obtenemos guardamos la imagen para poder enviarla
        if (bitmap != null) {
            //creamos un nombre unico a partir de la fecha (incluidos los segundos)
            var idScreenShoot = SimpleDateFormat("yyyy/MM/dd").format(Date())
            idScreenShoot = idScreenShoot.replace(":", "")
            idScreenShoot = idScreenShoot.replace("/", "")

            //guardamos la imagen
            val path = saveImage(bitmap, "${idScreenShoot}.jpg")

            //obtenemos la ruta para compartirla
            val bitmapURI = Uri.parse(path)

            //creamos un intent con la imagen y el texto
            val intent = Intent(Intent.ACTION_SEND)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(Intent.EXTRA_STREAM, bitmapURI)
            intent.putExtra(Intent.EXTRA_TEXT, stringShare)
            intent.type = "image/jpg"

            //iniciamos el metodo de compartir pudiendo elegir varias apps
            val shareIntent = Intent.createChooser(intent,null)
            startActivity(shareIntent)
        } else {
            //si no tenemos screenshot haremos lo mismo solo con texto
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT, stringShare
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)
        }
    }

    /**
     * Funcion que guarda una imagen en la memoria del telefono
     *
     * @param bitmap
     * @param filename
     * @return ruta de la imagen
     */
    private fun saveImage(bitmap: Bitmap, filename: String): String? {
        //miramos si la version de android es suficientemente alta para tomar screenshots
        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.Q){
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/Screenshots")
            }
            //obtenemos la uri del screenshoot
            val uri = this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if(uri!=null){
                //lo pasamos a bitmap
                this.contentResolver.openOutputStream(uri).use { it ->
                    if (it == null) return@use
                    bitmap.compress(Bitmap.CompressFormat.PNG,85,it)
                    it.flush()
                    it.close()

                    MediaScannerConnection.scanFile(this, arrayOf(uri.toString()),null,null)
                }
            }
            return uri.toString()
        }
        return null
    }

    /**
     * Función que toma una captura del view que le pasemos
     *
     * @param view
     * @return Bitmap con la imagen del view
     */
    private fun getScreenshot(view: View): Bitmap? {
        var screenShoot: Bitmap? = null

        try {
            //creamos un bitmap con las medidas del view
            screenShoot = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            //creamos un canvas para dibujar el view en el bitmap
            val canvas = Canvas(screenShoot)
            view.draw(canvas)
        } catch (e: Exception) {
            Log.d(TAG, "error al crear bitmap")
        }
        //devolvemos el bitmap
        return screenShoot
    }
}