package com.curso.toroidal_puzzle.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.curso.toroidal_puzzle.CrearCuadros
import com.curso.toroidal_puzzle.Cuadro
import com.curso.toroidal_puzzle.R
import kotlinx.android.synthetic.main.activity_game.*
import java.io.*

class GameFragment : Fragment() {
    var isRunning : Boolean = false
    var cronometro : Chronometer? = null
    var pauseOffSet : Long = 0
    var movimientosRealizados : Long = 0
    var hayTiempoGuardado : Boolean = false
    var seHizoLoad : Boolean = false
    var seHizoSave: Boolean = false


    // Almacena posicion de los cuadros en el tablero.
    // Los index de este arreglo + 1, marcan la posicion actual del cuadro en el tablero.
    // Es decir, el cuadro que se encuentra en el index 5 de este arreglo, se encuentra en la
    // posicion 6 del tablero.

    // Distribucion del tablero

    // 1 | 2 | 3 | 4
    // 5 | 6 | 7 | 8
    // 9 | 10| 11| 12
    // 13| 14| 15| 16

    var posicionesCuadros = mutableMapOf<ImageView, Cuadro>()

    // Para cada posicion en el tablero se almacenan las posiciones que se ven afectadas con un movimiento.
    // Primer elemento del pair, representa a la posicion de donde inicia el movimiento. Segundo elemento,
    // los posiciones que se ven afectadas por el.
    // En este caso, movimientos horizontales.

    var movimientosHorizontal = mutableMapOf<ImageView, Array<ImageView>>()

    // En este caso, movimientos verticales.

    var movimientosVertical = mutableMapOf<ImageView, Array<ImageView>>()

    //Clase para crear las imágenes de cada cuadro
    val crearCuadros = CrearCuadros()

    //Obtiene la lista de 16 cuadros de 100x100px cada uno
    var listaCuadros = mutableListOf<Bitmap>()

    companion object {
        fun newInstance() = GameFragment()
    }

    private lateinit var viewModel: GameViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cronometro = view?.findViewById(R.id.cronometro)
        try {
            if(readFromInternalStorage("cronometro") > 0 ){
                hayTiempoGuardado = true
            }
        }catch (e: Exception){
            // Error pq no hay archivo de save
        }

        val f1 = view.findViewById<ImageView>(R.id.cuadro1)
        val f2 = view.findViewById<ImageView>(R.id.cuadro2)
        val f3 = view.findViewById<ImageView>(R.id.cuadro3)
        val f4 = view.findViewById<ImageView>(R.id.cuadro4)
        val f5 = view.findViewById<ImageView>(R.id.cuadro5)
        val f6 = view.findViewById<ImageView>(R.id.cuadro6)
        val f7 = view.findViewById<ImageView>(R.id.cuadro7)
        val f8 = view.findViewById<ImageView>(R.id.cuadro8)
        val f9 = view.findViewById<ImageView>(R.id.cuadro9)
        val f10 = view.findViewById<ImageView>(R.id.cuadro10)
        val f11 = view.findViewById<ImageView>(R.id.cuadro11)
        val f12 = view.findViewById<ImageView>(R.id.cuadro12)
        val f13 = view.findViewById<ImageView>(R.id.cuadro13)
        val f14 = view.findViewById<ImageView>(R.id.cuadro14)
        val f15 = view.findViewById<ImageView>(R.id.cuadro15)
        val f16 = view.findViewById<ImageView>(R.id.cuadro16)
        var arrowUp1 = view.findViewById<ImageButton>(R.id.arrowUp1)
        var arrowUp2 = view.findViewById<ImageButton>(R.id.arrowUp2)
        var arrowUp3 = view.findViewById<ImageButton>(R.id.arrowUp3)
        var arrowUp4 = view.findViewById<ImageButton>(R.id.arrowUp4)
        var arrowDown1 = view.findViewById<ImageButton>(R.id.arrowDown1)
        var arrowDown2 = view.findViewById<ImageButton>(R.id.arrowDown2)
        var arrowDown3 = view.findViewById<ImageButton>(R.id.arrowDown3)
        var arrowDown4 = view.findViewById<ImageButton>(R.id.arrowDown4)
        var arrowLeft1 = view.findViewById<ImageButton>(R.id.arrowLeft1)
        var arrowLeft2 = view.findViewById<ImageButton>(R.id.arrowLeft2)
        var arrowLeft3 = view.findViewById<ImageButton>(R.id.arrowLeft3)
        var arrowLeft4 = view.findViewById<ImageButton>(R.id.arrowLeft4)
        var arrowRight1 = view.findViewById<ImageButton>(R.id.arrowRight1)
        var arrowRight2 = view.findViewById<ImageButton>(R.id.arrowRight2)
        var arrowRight3 = view.findViewById<ImageButton>(R.id.arrowRight3)
        var arrowRight4 = view.findViewById<ImageButton>(R.id.arrowRight4)
        var saveButton = view.findViewById<Button>(R.id.saveButton)
        var loadButton = view.findViewById<Button>(R.id.loadButton)
        var shuffleButton = view.findViewById<Button>(R.id.shuffleButton)

        //Convierte la imagen que está en Resources en Bitmap
        var bitmap = BitmapFactory.decodeResource(resources,
            R.drawable.vicente
        )

        //Escala la imagen a 400x400px
        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false)

        //Obtiene la lista de 16 cuadros de 100x100px cada uno
        listaCuadros = crearCuadros.crearCuadros(bitmap).toMutableList()

        //Coloca los cuadros en el mapa
        posicionesCuadros.set(f1,
            Cuadro(listaCuadros[0], f1, 1)
        )
        posicionesCuadros.set(f2,
            Cuadro(listaCuadros[1], f2, 2)
        )
        posicionesCuadros.set(f3,
            Cuadro(listaCuadros[2], f3, 3)
        )
        posicionesCuadros.set(f4,
            Cuadro(listaCuadros[3], f4, 4)
        )
        posicionesCuadros.set(f5,
            Cuadro(listaCuadros[4], f5, 5)
        )
        posicionesCuadros.set(f6,
            Cuadro(listaCuadros[5], f6, 6)
        )
        posicionesCuadros.set(f7,
            Cuadro(listaCuadros[6], f7, 7)
        )
        posicionesCuadros.set(f8,
            Cuadro(listaCuadros[7], f8, 8)
        )
        posicionesCuadros.set(f9,
            Cuadro(listaCuadros[8], f9, 9)
        )
        posicionesCuadros.set(f10,
            Cuadro(listaCuadros[9], f10, 10)
        )
        posicionesCuadros.set(f11,
            Cuadro(listaCuadros[10], f11, 11)
        )
        posicionesCuadros.set(f12,
            Cuadro(listaCuadros[11], f12, 12)
        )
        posicionesCuadros.set(f13,
            Cuadro(listaCuadros[12], f13, 13)
        )
        posicionesCuadros.set(f14,
            Cuadro(listaCuadros[13], f14, 14)
        )
        posicionesCuadros.set(f15,
            Cuadro(listaCuadros[14], f15, 15)
        )
        posicionesCuadros.set(f16,
            Cuadro(listaCuadros[15], f16, 16)
        )

        movimientosHorizontal = mutableMapOf<ImageView, Array<ImageView>>(
            Pair(f1, arrayOf(f1, f2, f3, f4)),
            Pair(f2, arrayOf(f1, f2, f3, f4)),
            Pair(f3, arrayOf(f1, f2, f3, f4)),
            Pair(f4, arrayOf(f1, f2, f3, f4)),
            Pair(f5, arrayOf(f5, f6, f7, f8)),
            Pair(f6, arrayOf(f5, f6, f7, f8)),
            Pair(f7, arrayOf(f5, f6, f7, f8)),
            Pair(f8, arrayOf(f5, f6, f7, f8)),
            Pair(f9, arrayOf(f9, f10, f11, f12)),
            Pair(f10, arrayOf(f9, f10, f11, f12)),
            Pair(f11, arrayOf(f9, f10, f11, f12)),
            Pair(f12, arrayOf(f9, f10, f11, f12)),
            Pair(f13, arrayOf(f13, f14, f15, f16)),
            Pair(f14, arrayOf(f13, f14, f15, f16)),
            Pair(f15, arrayOf(f13, f14, f15, f16)),
            Pair(f16, arrayOf(f13, f14, f15, f16))
        )

        movimientosVertical = mutableMapOf<ImageView, Array<ImageView>>(
            Pair(f1, arrayOf(f1, f5, f9, f13)),
            Pair(f2, arrayOf(f2, f6, f10, f14)),
            Pair(f3, arrayOf(f3, f7, f11, f15)),
            Pair(f4, arrayOf(f4, f8, f12, f16)),
            Pair(f5, arrayOf(f1, f5, f9, f13)),
            Pair(f6, arrayOf(f2, f6, f10, f14)),
            Pair(f7, arrayOf(f3, f7, f11, f15)),
            Pair(f8, arrayOf(f4, f8, f12, f16)),
            Pair(f9, arrayOf(f1, f5, f9, f13)),
            Pair(f10, arrayOf(f2, f6, f10, f14)),
            Pair(f11, arrayOf(f3, f7, f11, f15)),
            Pair(f12, arrayOf(f4, f8, f12, f16)),
            Pair(f13, arrayOf(f1, f5, f9, f13)),
            Pair(f14, arrayOf(f2, f6, f10, f14)),
            Pair(f15, arrayOf(f3, f7, f11, f15)),
            Pair(f16, arrayOf(f4, f8, f12, f16))
        )

        updateGameView()

        arrowUp1.setOnTouch(f1, "Arriba")
        arrowUp2.setOnTouch(f2, "Arriba")
        arrowUp3.setOnTouch(f3, "Arriba")
        arrowUp4.setOnTouch(f4, "Arriba")
        arrowRight1.setOnTouch(f1, "Derecha")
        arrowRight2.setOnTouch(f5, "Derecha")
        arrowRight3.setOnTouch(f9, "Derecha")
        arrowRight4.setOnTouch(f13, "Derecha")
        arrowDown1.setOnTouch(f1, "Abajo")
        arrowDown2.setOnTouch(f2, "Abajo")
        arrowDown3.setOnTouch(f3, "Abajo")
        arrowDown4.setOnTouch(f4, "Abajo")
        arrowLeft1.setOnTouch(f1, "Izquierda")
        arrowLeft2.setOnTouch(f5, "Izquierda")
        arrowLeft3.setOnTouch(f9, "Izquierda")
        arrowLeft4.setOnTouch(f13, "Izquierda")

        saveButton.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> guardarPartida()
            }
            // Retorno obligatorio del touchListener
            v?.onTouchEvent(event) ?: true
        }

        loadButton.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    try {
                        cargarPartida()
                    }catch (e: Exception){
                        Toast.makeText(this.activity, "Error al cargar la partida", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            // Retorno obligatorio del touchListener
            v?.onTouchEvent(event) ?: true
        }

        shuffleButton.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    shuffle()
                    updateGameView()
                }
            }
            // Retorno obligatorio del touchListener
            v?.onTouchEvent(event) ?: true
        }

        iniciarCronometro.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    iniciarCronometro()
                }
            }
            // Retorno obligatorio del touchListener
            v?.onTouchEvent(event) ?: true
        }

        pausarCronometro.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    pausarCronometro()
                }
            }
            // Retorno obligatorio del touchListener
            v?.onTouchEvent(event) ?: true
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)
        // TODO: Use the ViewModel
    }

    fun iniciarCronometro(){

        if(!isRunning && hayTiempoGuardado && seHizoLoad){
            cronometro!!.base = SystemClock.elapsedRealtime() - pauseOffSet - readFromInternalStorage("cronometro")
            cronometro!!.start()
            isRunning = true
            hayTiempoGuardado = false
            return
        }

        if(!isRunning && hayTiempoGuardado && seHizoSave){
            cronometro!!.base = SystemClock.elapsedRealtime() - pauseOffSet
            cronometro!!.start()
            isRunning = true
            hayTiempoGuardado = false
            seHizoSave = false
            return
        }

//        if(!isRunning && hayTiempoGuardado ){
//            cronometro!!.base = SystemClock.elapsedRealtime() - pauseOffSet - readFromInternalStorage("cronometro")
//            cronometro!!.start()
//            isRunning = true
//            hayTiempoGuardado = false
//            return
//        }

        if(!isRunning){
            cronometro!!.base = SystemClock.elapsedRealtime() - pauseOffSet
            cronometro!!.start()
            isRunning = true
        }
    }


    fun pausarCronometro(){
        if(isRunning){
            cronometro!!.stop()
            pauseOffSet = SystemClock.elapsedRealtime() - cronometro!!.base
            isRunning = false
        }
    }

//    fun reiniciarCronometro(){
//        cronometro!!.base = SystemClock.elapsedRealtime()
//        pauseOffSet = 0
//    }

    fun tiempoCronometro(): Long {
        var tiempo = SystemClock.elapsedRealtime() - cronometro!!.base
        return tiempo
    }


    // Esta funcion realiza un movimiento y mueve a la posiciones afectadas por este.
    // Primer parametro, posicion desde la cual se inicia el movimiento.
    // Segundo parametro, direccion del movimiento, arriba/abajo/derecha/izquierda
    // Tercer parametro, true si el moviemiento es vertical, false si es horizontal

    fun rotar(view: ImageView, direccion: String, isVertical: Boolean): Boolean {
        if(isRunning) {
            if (isVertical) {
                if (direccion == "Arriba") {
                    // Lo mismo que abajo pero en el otro sentido
                    val indexArray = movimientosVertical.getValue(view)

                    val aux = posicionesCuadros.getValue(indexArray[0])
                    posicionesCuadros.set(indexArray[0], posicionesCuadros.getValue(indexArray[1]))
                    posicionesCuadros.set(indexArray[1], posicionesCuadros.getValue(indexArray[2]))
                    posicionesCuadros.set(indexArray[2], posicionesCuadros.getValue(indexArray[3]))
                    posicionesCuadros.set(indexArray[3], aux)
                    movimientosRealizados++

                    return true
                }
                if (direccion == "Abajo") {

                    // Se extran index posiciones afectadas
                    val indexArray = movimientosVertical.getValue(view)

                    // Se realizan los cambios en las posiciones
                    val aux = posicionesCuadros.getValue(indexArray[3])
                    posicionesCuadros.set(indexArray[3], posicionesCuadros.getValue(indexArray[2]))
                    posicionesCuadros.set(indexArray[2], posicionesCuadros.getValue(indexArray[1]))
                    posicionesCuadros.set(indexArray[1], posicionesCuadros.getValue(indexArray[0]))
                    posicionesCuadros.set(indexArray[0], aux)
                    movimientosRealizados++

                    return true

                }
            } else {
                if (direccion == "Derecha") {

                    // Se extran index posiciones afectadas
                    val indexArray = movimientosHorizontal.getValue(view)

                    // Se realizan los cambios en las posiciones
                    val aux = posicionesCuadros.getValue(indexArray[3])
                    posicionesCuadros.set(indexArray[3], posicionesCuadros.getValue(indexArray[2]))
                    posicionesCuadros.set(indexArray[2], posicionesCuadros.getValue(indexArray[1]))
                    posicionesCuadros.set(indexArray[1], posicionesCuadros.getValue(indexArray[0]))
                    posicionesCuadros.set(indexArray[0], aux)
                    movimientosRealizados++

                    return true

                }
                if (direccion == "Izquierda") {
                    // Lo mismo que derecha pero en el otro sentido
                    val indexArray = movimientosHorizontal.getValue(view)

                    val aux = posicionesCuadros.getValue(indexArray[0])
                    posicionesCuadros.set(indexArray[0], posicionesCuadros.getValue(indexArray[1]))
                    posicionesCuadros.set(indexArray[1], posicionesCuadros.getValue(indexArray[2]))
                    posicionesCuadros.set(indexArray[2], posicionesCuadros.getValue(indexArray[3]))
                    posicionesCuadros.set(indexArray[3], aux)
                    movimientosRealizados++

                    return true
                }
            }

            // Caso Error
            Log.i("MOVIMIENTO", "No se realizo el movimiento")
            return false
        }
        return false
    }

    // Esta funcion verifica si el juego ha terminado, retorna true o false respectivamente.
    fun gameOver(): Boolean {
        if(isRunning) {
            for (vista in posicionesCuadros.keys) {

                if (vista != posicionesCuadros[vista]!!.idVista) {
                    return false
                }
            }
            // ningun cuadro no esta en su posicion correcta --> todos los cuadros estan en su
            // posicion correcta.
            Toast.makeText(this.activity, "YOU WIN", Toast.LENGTH_LONG).show()
            return true
        }
        return false
    }

    fun updateGameView() {
        for (vista in posicionesCuadros.keys) {
            vista.setImageBitmap(posicionesCuadros[vista]!!.imageResource)
        }
    }

    private fun saveToInternalStorage(toSave : Long, fileName : String){
        val fOut = requireContext().openFileOutput("$fileName.txt", Context.MODE_PRIVATE)

        try {
            var stringToWrite = toSave.toString()
            fOut.write(stringToWrite.toByteArray())


        }catch (e: Exception){
            e.printStackTrace()
        }

        fOut.close()
    }

    private fun readFromInternalStorage(fileName: String) : Long{
        var fileInputStream: FileInputStream? = null
        fileInputStream = context?.openFileInput("$fileName.txt")
        var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
        var text: String? = null
        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        var finalString = stringBuilder.toString()
        return finalString.toLong()
    }

    fun guardarImagen(imagen: Bitmap, fileName: String){

        try {
            //Ubicación donde se guardan las imágenes
            val path = File(requireContext().applicationContext.dataDir.toString() + File.separator + "img")

            //Si la ubicación no existe, se crea
            if (!path.exists()) path.mkdirs()

            //Nombre del archivo a guardar
            val outFile = File(path, "$fileName.png")

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

    fun recuperarImagen(){
        val path = File(requireContext().applicationContext.dataDir.toString() + File.separator + "img")
        var i = 1
        val posicionesCuadrosKeys = posicionesCuadros.keys.toMutableList()


        while( i <= 16) {
            var img = File(path, "img$i.png")

            var bitmap = BitmapFactory.decodeFile(img.absolutePath)

            posicionesCuadros[posicionesCuadrosKeys[i-1]]!!.imageResource = bitmap

            ++i
        }
        updateGameView()
    }

    fun shuffle(){
        var listaCuadrosAux = listaCuadros
        listaCuadrosAux = listaCuadrosAux.toMutableList()
        var max = 15

        for (value in posicionesCuadros.values){
            var index = (0..max).random()
            value.imageResource = listaCuadrosAux[index]
            listaCuadrosAux.removeAt(index)
            --max
        }
        updateGameView()
    }

    fun guardarPartida(){
        // Se guardan imagenes
        var i = 1
        for(cuadro in posicionesCuadros.values){
            guardarImagen(cuadro.imageResource,"img${i}")
            ++i
        }
        // Se guarda tiempo cronometro y cantidad de movimientos
        iniciarCronometro()
        pausarCronometro()
        saveToInternalStorage(tiempoCronometro(), "cronometro")
        saveToInternalStorage(movimientosRealizados, "movimientosRealizados")
        hayTiempoGuardado = true
        seHizoSave = true

    }

    fun cargarPartida(){
        // Se recuperan imagenes
        recuperarImagen()

        // Se recuperan cronometro y cantidad de movimientos
        pausarCronometro()
        cronometro!!.base = SystemClock.elapsedRealtime()
        cronometro!!.base -= readFromInternalStorage("cronometro")
        movimientosRealizados = readFromInternalStorage("movimientosRealizados")
        seHizoLoad = true
        updateGameView()

    }

    fun ImageButton.setOnTouch(f:ImageView, direccion:String) {
        this.setOnTouchListener { view, event->
            when(Pair(event?.action, direccion)) {
                Pair(MotionEvent.ACTION_DOWN, "Arriba") -> rotar(f, direccion, true)
                Pair(MotionEvent.ACTION_DOWN, "Abajo") -> rotar(f, direccion, true)
                Pair(MotionEvent.ACTION_DOWN, "Derecha") -> rotar(f, direccion, false)
                Pair(MotionEvent.ACTION_DOWN, "Izquierda") -> rotar(f, direccion, false)
            }
            gameOver()
            updateGameView()
            view?.onTouchEvent(event) ?: true
        }
    }
}

