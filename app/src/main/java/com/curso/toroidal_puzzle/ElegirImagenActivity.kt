package com.curso.toroidal_puzzle

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import androidx.core.content.ContentProviderCompat.requireContext
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

//Número cualquiera
val PICK_IMAGE = 100

//TODO: Llamar a esta actividad

class ElegirImagenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elegir_imagen)

        val ga = mostrarImagen()
        val gv = findViewById<GridView>(R.id.gridViewGaleria)
        gv.adapter = ga

        if (gv != null) {

            gv.onItemClickListener = AdapterView.OnItemClickListener{ parent, v, pos, id ->
               
                if (ga != null) {
                    ga.getItem(pos)?.let { usarImagen(it) }
                }
            }
        }
    }

    //Llama a la cámara
    fun tomarFoto(view: View) {

        val camaraIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        if (camaraIntent.resolveActivity(packageManager) != null) {
            startActivity(camaraIntent)
        }
    }

    fun cargarImagen(view: View) {

        //Crea el intent para seleccionar la imagen a utilizar en el tablero
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        imageCrop(uri)
                    }
                }
                //Si ocurre algún error al seleccionar la imagen
                else {
                    Log.e("SelectImageError", "Error al seleccionar imagen")
                }
            }

            //Al realizar el cortado de la imagen
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {

                    //Obtiene el Bitmap a partir del URI
                    var imagen = BitmapFactory.decodeFile(result.uri.path)
                    imagen = Bitmap.createScaledBitmap(imagen, 400, 400, false)

                    guardarImagen(imagen)

                    usarImagen(imagen)
                }

                //Si ocurre algún error al cortar la imagen
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e("CropError", "Error al cortar imagen: ${result.getError()}")
                }
            }
        }
    }

    //Función para lanzar la actividad para seleccionar la imagen a utilizar en el tablero
    fun imageCrop(uri: Uri) {
        CropImage.activity(uri)
            //La resolución mínima de la imagen a cortar es de 400px
            //.setMinCropResultSize(400, 400)

            //No se muestran las guías de ayuda al realizar el cortado
            .setGuidelines(CropImageView.Guidelines.OFF)

            //La relación de aspecto de la imagen es de 1:1
            .setAspectRatio(1, 1)

            //La forma de la selección a cortar es un rectángulo
            .setCropShape(CropImageView.CropShape.RECTANGLE)

            //Se escala el tamaño de la imagen seleccionada a 400px
            //.setRequestedSize(400, 400, CropImageView.RequestSizeOptions.RESIZE_INSIDE)

            //Se lanza la actividad para realizar la selección y cortado de la imagen
            .start(this)
    }

    fun guardarImagen(imagen: Bitmap){

        try {
            //Ubicación donde se guardan las imágenes
            val path = File(applicationContext.dataDir.toString() + File.separator + "gallery")

            //Si la ubicación no existe, se crea
            if (!path.exists()) path.mkdirs()

            //Se obtiene el timestamp para el nombre
            val name = System.currentTimeMillis()

            //Nombre del archivo a guardar
            val outFile = File(path, "$name.png")

            //Guarda el archivo en formato PNG
            val outStream = FileOutputStream(outFile)
            imagen.compress(Bitmap.CompressFormat.PNG, 100, outStream)

            //Cierra el archivo
            outStream.close()
        }
        catch (e: FileNotFoundException){
            Log.v("ErrorGuardarArchivo", "Archivo no encontrado: " + e.message!!)
        }
        catch (e: IOException){
            Log.v("ErrorGuardarArchivo", "Error de Entrada Salida: " + e.message!!)
        }
    }

    fun mostrarImagen() : GalleryAdapter?{

        try {
            val bitmapList : MutableList<Bitmap> = mutableListOf()

            //Se agrega a la lista de bitmap la imagen por defecto
            var imagenDefecto = BitmapFactory.decodeResource(resources,R.drawable.imagen_udec)
            imagenDefecto = Bitmap.createScaledBitmap(imagenDefecto, 400, 400, false)
            bitmapList.add(imagenDefecto)

            //Ubicación donde se guardan las imágenes
            val path = File(applicationContext.dataDir.toString() + File.separator + "gallery")

            //Arreglo de todos los archivos en path
            val imgs = path.listFiles()

            if(imgs!=null) {
                for (i in imgs) {

                    //Guarda los archivos como Bitmap
                    bitmapList.add(BitmapFactory.decodeFile(i.toString()))
                }
            }

            return GalleryAdapter(this, bitmapList)
        }
        catch (e: FileNotFoundException){

            Log.v("ErrorCargarArchivo", "Archivo no encontrado: " + e.message!!)

            return null
        }
        catch (e: IOException){

            Log.v("ErrorCargarArchivo", "Error de Entrada Salida: " + e.message!!)

            return null
        }
    }

    //Inicia el juego con la imagen seleccionada
    fun usarImagen(img: Bitmap){

        val imagen = Bitmap.createScaledBitmap(img, 400, 400, false)

        val crearCuadros = CrearCuadros()

        val listaCuadros = crearCuadros.crearCuadros(imagen).toMutableList()

        var i = 1

        for(bm in listaCuadros) {

            try {
                //Ubicación donde se guardan las imágenes
                val path = File(applicationContext.dataDir.toString() + File.separator + "img")

                //Si la ubicación no existe, se crea
                if (!path.exists()) path.mkdirs()

                //Nombre del archivo a guardar
                val outFile = File(path, "img${i}.png")

                //Guarda el archivo en formato PNG
                val outStream = FileOutputStream(outFile)
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream)

                //Cierra el archivo
                outStream.close()

                ++i

            } catch (e: FileNotFoundException) {
                Log.v("ErrorGuardarArchivo", "Archivo no encontrado: " + e.message!!)
            } catch (e: IOException) {
                Log.v("ErrorGuardarArchivo", "Error de Entrada Salida: " + e.message!!)
            }
        }

        //TODO: llamar a GameFragment

        /*
        val intent = Intent(this, GameActivity::class.java )
        intent.putExtra("imagen", imagen)

        if(intent.resolveActivity(packageManager)!=null){
            startActivity(intent)
        }
        */
    }
}

