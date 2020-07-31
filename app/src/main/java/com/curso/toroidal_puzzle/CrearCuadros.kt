package com.curso.toroidal_puzzle

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

//Clase para crear los 16 cuadros a partir de una imagen
class CrearCuadros {

    //Tamaño de la imagen debe ser de 400x400 pixeles
    fun crearCuadros(imagen: Bitmap): List<Bitmap>{

        return listarCuadros(imagen)
    }

    //Coloca los 16 cuadros en una lista
    private fun listarCuadros(imagen: Bitmap): List<Bitmap>{

        val listaMutable = mutableListOf<Bitmap>()

        //Rectángulo con la coordenada del cuadro a cortar
        var rect = Rect(0,0,100,100)
        var cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(100,0,200,100)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(200,0,300,100)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(300,0,400,100)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(0,100,100,200)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(100,100,200,200)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(200,100,300,200)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(300,100,400,200)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(0,200,100,300)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(100,200,200,300)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(200,200,300,300)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(300,200,400,300)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(0,300,100,400)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(100,300,200,400)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(200,300,300,400)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        rect = Rect(300,300,400,400)
        cuadro = recortarCuadros(imagen, rect)
        listaMutable.add(cuadro)

        //Convierte la lista mutable a inmutable
        return listaMutable.toList()
    }

    //Recorta un cuadro de la imagen
    private fun recortarCuadros(imagen: Bitmap, rect: Rect): Bitmap{

        //Tamaño de cada cuadro
        val cutImagen = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(cutImagen)
        //Se corta la imagen según el rectángulo "rect" recibido
        canvas.drawBitmap(imagen,rect, Rect(0,0,100,100), null)

        //Retorna el cuadro de 100x100px
        return cutImagen
    }
}