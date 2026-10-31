package edu.umg.controlgastos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var btnBuscarGasto: Button
    private lateinit var edtBuscarGastoId: EditText

    private lateinit var txtResumenIngresos: TextView
    private lateinit var txtResumenGastos: TextView

    private var movimientosActuales = listOf<Movimiento>()
    private var filtroActual = "TODOS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movimientos)

        findViewById<Button>(R.id.btnVolverInicio).setOnClickListener {
            finish()
        }

        val recycler = findViewById<RecyclerView>(
            R.id.recyclerMovimientos
        )

        btnTodos = findViewById(R.id.btnTodos)
        btnIngresos = findViewById(R.id.btnIngresos)
        btnGastos = findViewById(R.id.btnGastos)

        btnBuscarGasto = findViewById(R.id.btnBuscarGasto)
        edtBuscarGastoId = findViewById(R.id.edtBuscarGastoId)

        txtResumenIngresos = findViewById(R.id.txtResumenIngresos)
        txtResumenGastos = findViewById(R.id.txtResumenGastos)

        adaptador = MovimientoAdapter(emptyList()) { movimiento ->

            if (movimiento.esIngreso) {
                Toast.makeText(
                    this,
                    "Esta opción solo está disponible para gastos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = android.content.Intent(
                    this,
                    FormularioGastoActivity::class.java
                )

                intent.putExtra(
                    FormularioGastoActivity.EXTRA_GASTO_ID,
                    movimiento.id
                )

                startActivity(intent)
            }
        }

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

        btnBuscarGasto.setOnClickListener {
            buscarGastoPorId()
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

                Pair(
                    listaGastos + listaIngresos,
                    Pair(gastos, ingresos)
                )

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

    private fun buscarGastoPorId() {

        val textoId = edtBuscarGastoId.text.toString().trim()

        if (textoId.isEmpty()) {
            Toast.makeText(
                this,
                "Ingresa el ID del gasto",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val id = textoId.toIntOrNull()

        if (id == null || id <= 0) {
            Toast.makeText(
                this,
                "Ingresa un ID válido",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        lifecycleScope.launch {

            try {
                val dao = AppDatabase
                    .obtenerBaseDeDatos(applicationContext)
                    .gastoDao()

                val gasto = dao.buscarPorId(id)

                if (gasto != null) {

                    val movimientoEncontrado = Movimiento(
                        id = gasto.id,
                        descripcion = gasto.descripcion,
                        monto = gasto.monto,
                        categoria = gasto.categoria,
                        fecha = gasto.fecha,
                        esIngreso = false
                    )

                    adaptador.actualizarLista(
                        listOf(movimientoEncontrado)
                    )

                } else {

                    Toast.makeText(
                        this@MovimientosActivity,
                        "No se encontró un gasto con ese ID",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@MovimientosActivity,
                    "Error al buscar el gasto",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}