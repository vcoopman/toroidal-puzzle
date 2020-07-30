package com.curso.toroidal_puzzle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import com.curso.toroidal_puzzle.views.CuadroImageView
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    // Almacena posicion de los cuadros en el tablero.
    // Los index de este arreglo + 1, marcan la posicion actual del cuadro en el tablero.
    // Es decir, el cuadro que se encuentra en el index 5 de este arreglo, se encuentra en la
    // posicion 6 del tablero.

    // Distribucion del tablero

    // 1 | 2 | 3 | 4
    // 5 | 6 | 7 | 8
    // 9 | 10| 11| 12
    // 13| 14| 15| 16

    var posicionesCuadros = mutableMapOf<ImageView,Cuadro>()

    // Para cada posicion en el tablero se almacenan las posiciones que se ven afectadas con un movimiento.
    // Primer elemento del pair, representa a la posicion de donde inicia el movimiento. Segundo elemento,
    // los posiciones que se ven afectadas por el.
    // En este caso, movimientos horizontales.

    var movimientosHorizontal = mutableMapOf<ImageView, Array<ImageView>>()

    // En este caso, movimientos verticales.

    var movimientosVertical = mutableMapOf<ImageView, Array<ImageView>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val f1 = findViewById<ImageView>(R.id.cuadro1)
        val f2 = findViewById<ImageView>(R.id.cuadro2)
        val f3 = findViewById<ImageView>(R.id.cuadro3)
        val f4 = findViewById<ImageView>(R.id.cuadro4)
        val f5 = findViewById<ImageView>(R.id.cuadro5)
        val f6 = findViewById<ImageView>(R.id.cuadro6)
        val f7 = findViewById<ImageView>(R.id.cuadro7)
        val f8 = findViewById<ImageView>(R.id.cuadro8)
        val f9 = findViewById<ImageView>(R.id.cuadro9)
        val f10 = findViewById<ImageView>(R.id.cuadro10)
        val f11 = findViewById<ImageView>(R.id.cuadro11)
        val f12 = findViewById<ImageView>(R.id.cuadro12)
        val f13 = findViewById<ImageView>(R.id.cuadro13)
        val f14 = findViewById<ImageView>(R.id.cuadro14)
        val f15 = findViewById<ImageView>(R.id.cuadro15)
        val f16 = findViewById<ImageView>(R.id.cuadro16)

        posicionesCuadros.set(f1, Cuadro(R.drawable.imagen1,f1))
        posicionesCuadros.set(f2, Cuadro(R.drawable.imagen2,f2))
        posicionesCuadros.set(f3, Cuadro(R.drawable.imagen3,f3))
        posicionesCuadros.set(f4, Cuadro(R.drawable.imagen4,f4))
        posicionesCuadros.set(f5, Cuadro(R.drawable.imagen5,f5))
        posicionesCuadros.set(f6, Cuadro(R.drawable.imagen6,f6))
        posicionesCuadros.set(f7, Cuadro(R.drawable.imagen7,f7))
        posicionesCuadros.set(f8, Cuadro(R.drawable.imagen8,f8))
        posicionesCuadros.set(f9, Cuadro(R.drawable.imagen9,f9))
        posicionesCuadros.set(f10, Cuadro(R.drawable.imagen10,f10))
        posicionesCuadros.set(f11, Cuadro(R.drawable.imagen11,f11))
        posicionesCuadros.set(f12, Cuadro(R.drawable.imagen12,f12))
        posicionesCuadros.set(f13, Cuadro(R.drawable.imagen13,f13))
        posicionesCuadros.set(f14, Cuadro(R.drawable.imagen14,f14))
        posicionesCuadros.set(f15, Cuadro(R.drawable.imagen15,f15))
        posicionesCuadros.set(f16, Cuadro(R.drawable.imagen16,f16))

        movimientosHorizontal = mutableMapOf<ImageView, Array<ImageView>>(
            Pair(f1, arrayOf(f1,f2,f3,f4)),
            Pair(f2, arrayOf(f1,f2,f3,f4)),
            Pair(f3, arrayOf(f1,f2,f3,f4)),
            Pair(f4, arrayOf(f1,f2,f3,f4)),
            Pair(f5, arrayOf(f5,f6,f7,f8)),
            Pair(f6, arrayOf(f5,f6,f7,f8)),
            Pair(f7, arrayOf(f5,f6,f7,f8)),
            Pair(f8, arrayOf(f5,f6,f7,f8)),
            Pair(f9, arrayOf(f9,f10,f11,f12)),
            Pair(f10, arrayOf(f9,f10,f11,f12)),
            Pair(f11, arrayOf(f9,f10,f11,f12)),
            Pair(f12, arrayOf(f9,f10,f11,f12)),
            Pair(f13, arrayOf(f13,f14,f15,f16)),
            Pair(f14, arrayOf(f13,f14,f15,f16)),
            Pair(f15, arrayOf(f13,f14,f15,f16)),
            Pair(f16, arrayOf(f13,f14,f15,f16))
        )

        movimientosVertical = mutableMapOf<ImageView, Array<ImageView>>(
            Pair(f1, arrayOf(f1,f5,f9,f13)),
            Pair(f2, arrayOf(f2,f6,f10,f14)),
            Pair(f3, arrayOf(f3,f7,f11,f15)),
            Pair(f4, arrayOf(f4,f8,f12,f16)),
            Pair(f5, arrayOf(f1,f5,f9,f13)),
            Pair(f6, arrayOf(f2,f6,f10,f14)),
            Pair(f7, arrayOf(f3,f7,f11,f15)),
            Pair(f8, arrayOf(f4,f8,f12,f16)),
            Pair(f9, arrayOf(f1,f5,f9,f13)),
            Pair(f10, arrayOf(f2,f6,f10,f14)),
            Pair(f11, arrayOf(f3,f7,f11,f15)),
            Pair(f12, arrayOf(f4,f8,f12,f16)),
            Pair(f13, arrayOf(f1,f5,f9,f13)),
            Pair(f14, arrayOf(f2,f6,f10,f14)),
            Pair(f15, arrayOf(f3,f7,f11,f15)),
            Pair(f16, arrayOf(f4,f8,f12,f16))
        )

        updateGameView()

        // va a ser util despues

//        for(view in posicionesCuadros.keys){
//            view.setOnTouchListener { v, event ->
//                when (event?.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        rotar(view,"Abajo",true)
//                        updateGameView()
//                    }
//                }
//                // Retorno obligatorio del touchListener
//                v?.onTouchEvent(event) ?: true
//            }
//        }

        buttondown1.setOnTouchListener { v, event ->
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        rotar(f1,"Abajo",true)
                        gameOver()
                        updateGameView()
                    }
                }
                // Retorno obligatorio del touchListener
                v?.onTouchEvent(event) ?: true
            }

        buttonright1.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    rotar(f1,"Derecha",false)
                    gameOver()
                    updateGameView()
                }
            }
            // Retorno obligatorio del touchListener
            v?.onTouchEvent(event) ?: true
        }

    }

    // Esta funcion realiza un movimiento y mueve a la posiciones afectadas por este.
    // Primer parametro, posicion desde la cual se inicia el movimiento.
    // Segundo parametro, direccion del movimiento, arriba/abajo/derecha/izquierda
    // Tercer parametro, true si el moviemiento es vertical, false si es horizontal

    fun rotar(view: ImageView, direccion: String, isVertical: Boolean): Boolean {
        if(isVertical){
            if(direccion == "Arriba"){
                // Lo mismo que abajo pero en el otro sentido
                return true
            }
            if(direccion == "Abajo"){

                // Se extran index posiciones afectadas
                val indexArray = movimientosVertical.getValue(view)

                // Se realizan los cambios en las posiciones
                val aux = posicionesCuadros.getValue(indexArray[3])
                posicionesCuadros.set(indexArray[3],posicionesCuadros.getValue(indexArray[2]))
                posicionesCuadros.set(indexArray[2],posicionesCuadros.getValue(indexArray[1]))
                posicionesCuadros.set(indexArray[1],posicionesCuadros.getValue(indexArray[0]))
                posicionesCuadros.set(indexArray[0],aux)
                return true

            }
        } else {
           if(direccion == "Derecha"){

                // Se extran index posiciones afectadas
                val indexArray = movimientosHorizontal.getValue(view)

                // Se realizan los cambios en las posiciones
                val aux = posicionesCuadros.getValue(indexArray[3])
                posicionesCuadros.set(indexArray[3],posicionesCuadros.getValue(indexArray[2]))
                posicionesCuadros.set(indexArray[2],posicionesCuadros.getValue(indexArray[1]))
                posicionesCuadros.set(indexArray[1],posicionesCuadros.getValue(indexArray[0]))
                posicionesCuadros.set(indexArray[0],aux)
                return true

            }
            if(direccion == "Izquierda"){
                // Lo mismo que derecha pero en el otro sentido
                return true
            }
        }

        // Caso Error
        Log.i("MOVIMIENTO","No se realizo el movimiento")
        return false
    }

    // Esta funcion verifica si el juego ha terminado, retorna true o false respectivamente.
    fun gameOver(): Boolean{
        for(vista in posicionesCuadros.keys){

            if(vista != posicionesCuadros[vista]!!.idVista){
                return false
            }
        }
        // ningun cuadro no esta en su posicion correcta --> todos los cuadros estan en su
        // posicion correcta.
        Toast.makeText(this,"YOU WIN", Toast.LENGTH_LONG).show()
        return true
    }
    fun updateGameView(){
        for(vista in posicionesCuadros.keys){
            vista.setImageResource(posicionesCuadros[vista]!!.imageResource)
        }
    }
}
