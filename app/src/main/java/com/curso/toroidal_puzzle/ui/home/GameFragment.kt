package com.curso.toroidal_puzzle.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.curso.toroidal_puzzle.CrearCuadros
import com.curso.toroidal_puzzle.Cuadro
import com.curso.toroidal_puzzle.R
import kotlinx.android.synthetic.main.game_fragment.*
import java.io.*
import kotlin.math.ceil

class GameFragment : Fragment() {

    // Estado del juego
    var isRunning : Boolean = false

    // Varibles cronometro
    var cronometro : Chronometer? = null
    var pauseTime : Long = 0
    var startTime : Long = 0

    // Nro Jugadas
    var movimientosRealizados : Long = 0
    var hayTiempoGuardado : Boolean = false
    var seHizoSave: Boolean = false
    val calcWidth = { size: Point, percentage: Double -> ceil(percentage * size.x).toInt()}
    val calcHeight = { size: Point, percentage: Double -> ceil(percentage * size.y).toInt()}
    val defDeviceWidth = 1080
    val defDeviceHeight = 1920
    lateinit var displaySize : Point

    // Estado del guardado
    var hayGuardado : Boolean = false

    // Estado de carga de partida
    var seHizoLoad : Boolean = false

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


    private lateinit var viewModel: GameViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_fragment, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDisplaySize()
        cronometro = view?.findViewById(R.id.cronometro)
        // Init cronometro
        cronometro = view.findViewById(R.id.cronometro)

        // Revisa si hay guardado

        try {
            if(readFromInternalStorage("cronometro") > 0 ){
                hayGuardado = true
            }
        }catch (e: Exception){
            // Error pq no hay archivo de save
            // No es necesario implementar,ya que esta comprobacion es hecha por el juego, no por
            // el jugador.
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
        val arrowUp1 = view.findViewById<ImageButton>(R.id.arrowUp1)
        val arrowUp2 = view.findViewById<ImageButton>(R.id.arrowUp2)
        val arrowUp3 = view.findViewById<ImageButton>(R.id.arrowUp3)
        val arrowUp4 = view.findViewById<ImageButton>(R.id.arrowUp4)
        val arrowDown1 = view.findViewById<ImageButton>(R.id.arrowDown1)
        val arrowDown2 = view.findViewById<ImageButton>(R.id.arrowDown2)
        val arrowDown3 = view.findViewById<ImageButton>(R.id.arrowDown3)
        val arrowDown4 = view.findViewById<ImageButton>(R.id.arrowDown4)
        val arrowLeft1 = view.findViewById<ImageButton>(R.id.arrowLeft1)
        val arrowLeft2 = view.findViewById<ImageButton>(R.id.arrowLeft2)
        val arrowLeft3 = view.findViewById<ImageButton>(R.id.arrowLeft3)
        val arrowLeft4 = view.findViewById<ImageButton>(R.id.arrowLeft4)
        val arrowRight1 = view.findViewById<ImageButton>(R.id.arrowRight1)
        val arrowRight2 = view.findViewById<ImageButton>(R.id.arrowRight2)
        val arrowRight3 = view.findViewById<ImageButton>(R.id.arrowRight3)
        val arrowRight4 = view.findViewById<ImageButton>(R.id.arrowRight4)
        val saveButton = view.findViewById<ImageButton>(R.id.saveButton)
        val loadButton = view.findViewById<ImageButton>(R.id.loadButton)
        val shuffleButton = view.findViewById<ImageButton>(R.id.shuffleButton)
        val iniciarCronometro = view.findViewById<ImageButton>(R.id.iniciarCronometro)
        val pausarCronometro = view.findViewById<ImageButton>(R.id.pausarCronometro)

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
            Cuadro(listaCuadros[0], f1)
        )
        posicionesCuadros.set(f2,
            Cuadro(listaCuadros[1], f2)
        )
        posicionesCuadros.set(f3,
            Cuadro(listaCuadros[2], f3)
        )
        posicionesCuadros.set(f4,
            Cuadro(listaCuadros[3], f4)
        )
        posicionesCuadros.set(f5,
            Cuadro(listaCuadros[4], f5)
        )
        posicionesCuadros.set(f6,
            Cuadro(listaCuadros[5], f6)
        )
        posicionesCuadros.set(f7,
            Cuadro(listaCuadros[6], f7)
        )
        posicionesCuadros.set(f8,
            Cuadro(listaCuadros[7], f8)
        )
        posicionesCuadros.set(f9,
            Cuadro(listaCuadros[8], f9)
        )
        posicionesCuadros.set(f10,
            Cuadro(listaCuadros[9], f10)
        )
        posicionesCuadros.set(f11,
            Cuadro(listaCuadros[10], f11)
        )
        posicionesCuadros.set(f12,
            Cuadro(listaCuadros[11], f12)
        )
        posicionesCuadros.set(f13,
            Cuadro(listaCuadros[12], f13)
        )
        posicionesCuadros.set(f14,
            Cuadro(listaCuadros[13], f14)
        )
        posicionesCuadros.set(f15,
            Cuadro(listaCuadros[14], f15)
        )
        posicionesCuadros.set(f16,
            Cuadro(listaCuadros[15], f16)
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

        // Listeners para los botones

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

        val orientation = resources.configuration.orientation
        ajustarBarraJuego(orientation)
        ajustarTextoJuego(orientation)
                                       
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

                    // Esto increiblemente elimina bugs.
                    pausarCronometro()
                    iniciarCronometro()
                    pausarCronometro()
                }
            }
            // Retorno obligatorio del touchListener
            v?.onTouchEvent(event) ?: true
        }

        reiniciarJuego.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    reiniciarPartida()
                }
            }
            // Retorno obligatorio del touchListener
            v?.onTouchEvent(event) ?: true
        }

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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)
        // TODO: Use the ViewModel
    }

    fun iniciarCronometro(){

        if(!isRunning && seHizoLoad){
            cronometro!!.start()
            seHizoLoad = false
            isRunning = true
        }

        if(!isRunning){
            if(pauseTime > 0) {
                startTime = SystemClock.elapsedRealtime()
                cronometro!!.base += (startTime - pauseTime)
            } else {
                cronometro!!.base = SystemClock.elapsedRealtime()
            }
            cronometro!!.start()
            isRunning = true
        }
    }

    fun pausarCronometro(){
        if(isRunning){
            cronometro!!.stop()
            pauseTime = SystemClock.elapsedRealtime()
            isRunning = false
        }
    }

    fun reiniciarCronometro(){
        cronometro!!.base = SystemClock.elapsedRealtime()
        pauseTime = 0
        startTime = 0
    }

    // funcion para obtener el tiempo jugado
    fun tiempoCronometro(): Long {
        return SystemClock.elapsedRealtime() - cronometro!!.base
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
        hayGuardado = true
        Toast.makeText(this.activity,"Partida Guardada", Toast.LENGTH_SHORT).show()

    }

    fun cargarPartida(){
        if(hayGuardado) {
            // Se recuperan imagenes
            recuperarImagen()

            // Se recuperan cronometro y cantidad de movimientos
//            pausarCronometro()
            cronometro!!.base =
                SystemClock.elapsedRealtime() - readFromInternalStorage("cronometro")

            movimientosRealizados = readFromInternalStorage("movimientosRealizados")
            seHizoLoad = true
            Toast.makeText(this.activity,"Partida Cargada", Toast.LENGTH_SHORT).show()
            updateGameView()
        }

    }

    fun reiniciarPartida(){

        // Reiniciar Imagenes
        val keys = posicionesCuadros.keys.toMutableList()
        var i = 0
        while(i < 16){
            posicionesCuadros[keys[i]] = Cuadro(listaCuadros[i],keys[i])
            ++i
        }

        // Reiniciar Otros
        reiniciarCronometro()
        movimientosRealizados = 0

        updateGameView()
    }


    // Esta funcion realiza un movimiento y mueve a la posiciones afectadas por este.
    // Primer parametro, posicion desde la cual se inicia el movimiento.
    // Segundo parametro, direccion del movimiento, arriba/abajo/derecha/izquierda
    // Tercer parametro, true si el moviemiento es vertical, false si es horizontal

    fun rotar(view: ImageView, direccion: String, isVertical: Boolean): Boolean {
        if(isRunning) {
            if (isVertical) {
                if (direccion == "Arriba") {
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
                    val indexArray = movimientosVertical.getValue(view)
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
                    val indexArray = movimientosHorizontal.getValue(view)
                    val aux = posicionesCuadros.getValue(indexArray[3])
                    posicionesCuadros.set(indexArray[3], posicionesCuadros.getValue(indexArray[2]))
                    posicionesCuadros.set(indexArray[2], posicionesCuadros.getValue(indexArray[1]))
                    posicionesCuadros.set(indexArray[1], posicionesCuadros.getValue(indexArray[0]))
                    posicionesCuadros.set(indexArray[0], aux)
                    movimientosRealizados++
                    return true

                }
                if (direccion == "Izquierda") {
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
            pausarCronometro()
            Toast.makeText(this.activity, "YOU WIN", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    // Actualiza la vista del juego
    fun updateGameView() {
        for (vista in posicionesCuadros.keys) {
            vista.setImageBitmap(posicionesCuadros[vista]!!.imageResource)
        }
        nromov.text = movimientosRealizados.toString()
    }

    // Guarda al almacenamiento interno
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

    // Lee del almacenamiento interno
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

    @SuppressLint("ClickableViewAccessibility")
    fun ImageButton.setOnTouch(f:ImageView, direccion:String) {
        this.setOnTouchListener { view, event->
            when(Pair(event?.action, direccion)) {
                Pair(MotionEvent.ACTION_DOWN, "Arriba") -> rotar(f, direccion, true)
                Pair(MotionEvent.ACTION_DOWN, "Abajo") -> rotar(f, direccion, true)
                Pair(MotionEvent.ACTION_DOWN, "Derecha") -> rotar(f, direccion, false)
                Pair(MotionEvent.ACTION_DOWN, "Izquierda") -> rotar(f, direccion, false)
            }
            updateGameView()
            gameOver()
            view?.onTouchEvent(event) ?: true
        }
    }

    fun getDisplaySize() {
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        displaySize = Point()
        display.getSize(displaySize)
    }

    fun ajustarBarraJuego(orientation: Int) {
        var barraJuego = requireView().findViewById<LinearLayoutCompat>(R.id.barraJuego)
        barraJuego.invalidate()
        var divider = resources.getDrawable(R.drawable.divider_barra, null)
        val resize: (Point, Double) -> Int
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            resize = calcHeight
            barraJuego.layoutParams.height = resize(displaySize, 0.9255)
            divider.setBounds(0, 0, 0, resize(displaySize, 0.02))
        } else {
            resize = calcWidth
            barraJuego.layoutParams.width = resize(displaySize, 0.9255)
            divider.setBounds(0, 0, resize(displaySize, 0.02), 0)
        }
        var imageButtons = barraJuego.children.toList().filterIsInstance<ImageButton>()
        for (ib in imageButtons) {
            var percentage = 0.13
            var ibSize = resize(displaySize, percentage)
            ib.layoutParams.width = ibSize
            ib.layoutParams.height = ibSize
            ib.scaleType = ImageView.ScaleType.CENTER_INSIDE
            ib.requestLayout()
        }
        val padding = resize(displaySize, 0.02)
        barraJuego.setPadding(padding, padding, padding, padding)
        barraJuego.showDividers = LinearLayoutCompat.SHOW_DIVIDER_MIDDLE
        barraJuego.dividerDrawable = divider
        barraJuego.requestLayout()
    }

    fun ajustarTextoJuego(orientation: Int) {
        var comp: Boolean
        var factor: Float
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            comp = defDeviceWidth != displaySize.y
            factor = (displaySize.x.toFloat() / defDeviceWidth)
        } else {
            comp = defDeviceWidth != displaySize.x
            factor = (displaySize.y.toFloat() / defDeviceWidth)
        }

        if (comp) {
            var titulo = requireView().findViewById<TextView>(R.id.textTituloGame)
            var textMov = requireView().findViewById<TextView>(R.id.textMovimientos)
            var countMov = requireView().findViewById<TextView>(R.id.nromov)
            var chrono = requireView().findViewById<Chronometer>(R.id.cronometro)
            textMov.invalidate()
            countMov.invalidate()
            titulo.invalidate()
            chrono.invalidate()
            textMov.textSize *= factor
            textMov.setShadowLayer(textMov.shadowRadius * factor, 0.0f, textMov.shadowDx * factor, textMov.shadowColor)
            countMov.textSize *= factor
            countMov.setShadowLayer(countMov.shadowRadius * factor, 0.0f, countMov.shadowDx * factor, countMov.shadowColor)
            titulo.textSize *= factor
            titulo.setShadowLayer(titulo.shadowRadius * factor, 0.0f, titulo.shadowDx * factor, titulo.shadowColor)
            chrono.textSize *= factor
            chrono.setShadowLayer(chrono.shadowRadius * factor, 0.0f, chrono.shadowDx * factor, chrono.shadowColor)
            textMov.requestLayout()
            countMov.requestLayout()
            titulo.requestLayout()
            chrono.requestLayout()
        }
    }
}

