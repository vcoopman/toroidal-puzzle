package com.curso.toroidal_puzzle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.curso.toroidal_puzzle.views.CuadroImageView

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }

    // Almacena posicion de los cuadros en el tablero.
    // Los index de este arreglo + 1, marcan la posicion actual del cuadro en el tablero.
    // Es decir, el cuadro que se encuentra en el index 5 de este arreglo, se encuentra en la
    // posicion 6 del tablero.

    // Distribucion del tablero

    // 1 | 2 | 3 | 4
    // 5 | 6 | 7 | 8
    // 9 | 10| 11| 12
    // 13| 14| 15| 16

    var posicionesCuadros = arrayOf<CuadroImageView>()

    // Para cada posicion en el tablero se almacenan las posiciones que se ven afectadas con un movimiento.
    // Primer elemento del pair, representa a la posicion de donde inicia el movimiento. Segundo elemento,
    // los posiciones que se ven afectadas por el.
    // En este caso, movimientos horizontales.

    val movimientosHorizontal = mutableMapOf<Int, Array<Int>>(
        Pair(1, arrayOf(1,2,3,4)),
        Pair(2, arrayOf(1,2,3,4)),
        Pair(3, arrayOf(1,2,3,4)),
        Pair(4, arrayOf(1,2,3,4)),
        Pair(5, arrayOf(5,6,7,8)),
        Pair(6, arrayOf(5,6,7,8)),
        Pair(7, arrayOf(5,6,7,8)),
        Pair(8, arrayOf(5,6,7,8)),
        Pair(9, arrayOf(9,10,11,12)),
        Pair(10, arrayOf(9,10,11,12)),
        Pair(11, arrayOf(9,10,11,12)),
        Pair(12, arrayOf(9,10,11,12)),
        Pair(13, arrayOf(13,14,15,16)),
        Pair(14, arrayOf(13,14,15,16)),
        Pair(15, arrayOf(13,14,15,16)),
        Pair(16, arrayOf(13,14,15,16))
    )

    // En este caso, movimientos verticales.

    val movimientoVertical = mutableMapOf<Int, Array<Int>>(
        Pair(1, arrayOf(1,5,9,13)),
        Pair(2, arrayOf(2,6,10,14)),
        Pair(3, arrayOf(3,7,11,15)),
        Pair(4, arrayOf(4,8,12,16)),
        Pair(5, arrayOf(1,5,9,13)),
        Pair(6, arrayOf(2,6,10,14)),
        Pair(7, arrayOf(3,7,11,15)),
        Pair(8, arrayOf(4,8,12,16)),
        Pair(9, arrayOf(1,5,9,13)),
        Pair(10, arrayOf(2,6,10,14)),
        Pair(11, arrayOf(3,7,11,15)),
        Pair(12, arrayOf(4,8,12,16)),
        Pair(13, arrayOf(1,5,9,13)),
        Pair(14, arrayOf(2,6,10,14)),
        Pair(15, arrayOf(3,7,11,15)),
        Pair(16, arrayOf(4,8,12,16))
    )

    // Esta funcion realiza un movimiento y mueve a la posiciones afectadas por este.
    // Primer parametro, posicion desde la cual se inicia el movimiento.
    // Segundo parametro, direccion del movimiento, arriba/abajo/derecha/izquierda
    // Tercer parametro, true si el moviemiento es vertical, false si es horizontal
    fun rotar(posicionMovimiento: Int, direccion: String, isVertical: Boolean): Boolean {
        if(isVertical){
            if(direccion == "Arriba"){
                // Lo mismo que abajo pero en el otro sentido
                return true
            }
            if(direccion == "Abajo"){

                // Se extran index posiciones afectadas
                val indexArray = movimientoVertical.getValue(posicionMovimiento)

                // Se realizan los cambios en las posiciones
                val aux = posicionesCuadros[indexArray[3]]
                posicionesCuadros[indexArray[3]] = posicionesCuadros[indexArray[2]]
                posicionesCuadros[indexArray[2]] = posicionesCuadros[indexArray[1]]
                posicionesCuadros[indexArray[1]] = posicionesCuadros[indexArray[0]]
                posicionesCuadros[indexArray[0]] = aux

                return true

            }
        } else {
            if(direccion == "Derecha"){
                val indexArray = movimientosHorizontal.getValue(posicionMovimiento)
                val aux = posicionesCuadros[indexArray[3]]
                posicionesCuadros[indexArray[3]] = posicionesCuadros[indexArray[2]]
                posicionesCuadros[indexArray[2]] = posicionesCuadros[indexArray[1]]
                posicionesCuadros[indexArray[1]] = posicionesCuadros[indexArray[0]]
                posicionesCuadros[indexArray[0]] = aux
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
        var value = 1
        for(cuadro in posicionesCuadros){
            // si estos valores coinciden para un cuadro, significa que el cuadro
            // esta en su posicion correcta
            if(cuadro!!.nro != value){
                // si alguno no esta en su posicion correcta, aun no gana el juego.
                return false
            }
            ++value
        }
        // ningun cuadro no esta en su posicion correcta --> todos los cuadros estan en su
        // posicion correcta.
        return true
    }
}
