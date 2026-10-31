package edu.umg.controlgastos
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale

class MovimientosActivity : AppCompatActivity() {

    private lateinit var adaptador: MovimientoAdapter
    private lateinit var btnTodos: Button
    private lateinit var btnIngresos: Button
    private lateinit var btnGastos: Button
    private lateinit var txtResumenIngresos: TextView
    private lateinit var txtResumenGastos: TextView

    private var movimientosActuales = listOf<Movimiento>()
    private var filtroActual = "TODOS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movimientos)

        // Botón para regresar a Inicio
        findViewById<Button>(R.id.btnVolverInicio).setOnClickListener {
            finish()
        }

        val recycler = findViewById<RecyclerView>(
            R.id.recyclerMovimientos
        )

        btnTodos = findViewById(R.id.btnTodos)
        btnIngresos = findViewById(R.id.btnIngresos)
        btnGastos = findViewById(R.id.btnGastos)

        txtResumenIngresos = findViewById(R.id.txtResumenIngresos)
        txtResumenGastos = findViewById(R.id.txtResumenGastos)

        adaptador = MovimientoAdapter(emptyList())
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adaptador

        btnTodos.setOnClickListener {
            filtroActual = "TODOS"
            aplicarFiltro()
        }

        btnIngresos.setOnClickListener {
            filtroActual = "INGRESOS"
            aplicarFiltro()
        }

        btnGastos.setOnClickListener {
            filtroActual = "GASTOS"
            aplicarFiltro()
        }

        cargarMovimientos()
    }

    private fun cargarMovimientos() {
        val db = AppDatabase.obtenerBaseDeDatos(applicationContext)
        val gastoDao = db.gastoDao()
        val ingresoDao = db.ingresoDao()

        lifecycleScope.launch {
            combine(
                gastoDao.listarTodos(),
                ingresoDao.listarTodos()
            ) { gastos, ingresos ->

                val listaGastos = gastos.map {
                    Movimiento(
                        id = it.id,
                        descripcion = it.descripcion,
                        monto = it.monto,
                        categoria = it.categoria,
                        fecha = it.fecha,
                        esIngreso = false
                    )
                }

                val listaIngresos = ingresos.map {
                    Movimiento(
                        id = it.id,
                        descripcion = it.descripcion,
                        monto = it.monto,
                        categoria = it.categoria,
                        fecha = it.fecha,
                        esIngreso = true
                    )
                }

                Pair(listaGastos + listaIngresos, Pair(gastos, ingresos))

            }.collect { resultado ->

                movimientosActuales = resultado.first
                    .sortedByDescending { it.fecha }

                val gastos = resultado.second.first
                val ingresos = resultado.second.second

                val totalGastos = gastos.sumOf { it.monto }
                val totalIngresos = ingresos.sumOf { it.monto }

                txtResumenIngresos.text = String.format(
                    Locale.US,
                    "Ingresos: Q %,.2f",
                    totalIngresos
                )

                txtResumenGastos.text = String.format(
                    Locale.US,
                    "Gastos: Q %,.2f",
                    totalGastos
                )

                aplicarFiltro()
            }
        }
    }

    private fun aplicarFiltro() {
        val listaFiltrada = when (filtroActual) {
            "INGRESOS" -> movimientosActuales.filter {
                it.esIngreso
            }

            "GASTOS" -> movimientosActuales.filter {
                !it.esIngreso
            }

            else -> movimientosActuales
        }

        adaptador.actualizarLista(listaFiltrada)
    }
}