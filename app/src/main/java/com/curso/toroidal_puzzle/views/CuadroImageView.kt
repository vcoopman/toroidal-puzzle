package com.curso.toroidal_puzzle.views

import android.content.Context
import android.util.AttributeSet
import com.curso.toroidal_puzzle.R

class CuadroImageView(context: Context?, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {
    private var nro : Int = 0

    init {
        val a = context!!.obtainStyledAttributes(attrs, R.styleable.CuadroImageView)
        val nro = a.getInteger(R.styleable.CuadroImageView_nro, 0)
        this.nro = nro
        a.recycle()
    }
}