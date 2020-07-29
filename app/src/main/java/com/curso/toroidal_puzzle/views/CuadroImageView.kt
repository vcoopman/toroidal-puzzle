package com.curso.toroidal_puzzle.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.curso.toroidal_puzzle.R

class CuadroImageView(context: Context?, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {
    var nro : Int = 0;

    init {
        var a = context!!.obtainStyledAttributes(attrs, R.styleable.CuadroImageView)
        var nro = a.getInteger(R.styleable.CuadroImageView_nro, 0)
        this.nro = nro
        a.recycle()
    }
}