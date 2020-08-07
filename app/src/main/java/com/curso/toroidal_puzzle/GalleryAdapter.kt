package com.curso.toroidal_puzzle

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView

class GalleryAdapter(c: Context, list: MutableList<Bitmap>) : BaseAdapter() {

    private val contexto = c
    private val imagenes = list

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {

        val iv: ImageView

        if(convertView == null){
            // Para ahorrar tiempo y memoria, si el objeto ImageView ya existe,
            // este se recicla. En caso contrario se crea un nuevo

            iv = ImageView(contexto)
            iv.layoutParams = AbsListView.LayoutParams(200,200)
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            iv.setPadding(2,2,2,2)
        }
        else{
            iv = convertView as ImageView
        }

        iv.setImageBitmap(imagenes[pos])

        return iv
    }

    // Un adapter debe implementar esta función que retorna el número de elementos
    override fun getCount(): Int {
        return imagenes.size
    }

    override fun getItem(pos: Int): Bitmap? {
        return imagenes[pos]
    }

    // Un adapter debe implementar esta función que retorna el número de la fila
    // un elemento dada su posición

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

}