package com.curso.toroidal_puzzle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.curso.toroidal_puzzle.R

class CreditosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creditos)
    }
    fun back (view: View){
        finish()
    }
}
