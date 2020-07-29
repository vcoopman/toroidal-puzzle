package com.curso.toroidal_puzzle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.curso.toroidal_puzzle.R

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
    }

    fun back (view: View){
        finish()
    }
}
