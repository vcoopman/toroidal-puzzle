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
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

//Número cualquiera
val PICK_IMAGE = 100

//TODO: Lanzar esta actividad desde el Navigation Drawer
class ElegirImagenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elegir_imagen)
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

        val crearCuadros = CrearCuadros()

        //Al seleccionar la imagen se llama a la función imageCrop para seleccionar una
        //parte de la imagen y mantener el aspecto 1:1
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
                    val imagen = BitmapFactory.decodeFile(result.uri.path)

                    //TODO: Implentar función para iniciar nuevo juego con esta imagen
                    //Llama a función para generar las imágenes en cada cuadro

                    //Escala la imagen a 400x400px (por si acaso)
                    //imagen = Bitmap.createScaledBitmap(imagen, 400, 400, false)

                    crearCuadros.crearCuadros(imagen)
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
            .setMinCropResultSize(400, 400)

            //No se muestran las guías de ayuda al realizar el cortado
            .setGuidelines(CropImageView.Guidelines.OFF)

            //La relación de aspecto de la imagen es de 1:1
            .setAspectRatio(1, 1)

            //La forma de la selección a cortar es un rectángulo
            .setCropShape(CropImageView.CropShape.RECTANGLE)

            //Se escala el tamaño de la imagen seleccionada a 400px
            .setRequestedSize(400, 400, CropImageView.RequestSizeOptions.RESIZE_INSIDE)

            //Se lanza la actividad para realizar la selección y cortado de la imagen
            .start(this)
    }
}
