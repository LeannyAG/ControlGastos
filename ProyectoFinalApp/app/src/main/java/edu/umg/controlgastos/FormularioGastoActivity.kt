package edu.umg.controlgastos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormularioGastoActivity : AppCompatActivity() {

    private lateinit var descripcion: EditText
    private lateinit var monto: EditText
    private lateinit var categoria: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button

    private var gastoActual: GastoEntity? = null

    private val gastoId: Int
        get() = intent.getIntExtra(EXTRA_GASTO_ID, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_gasto)

        descripcion = findViewById(R.id.edtDescripcion)
        monto = findViewById(R.id.edtMonto)
        categoria = findViewById(R.id.spinnerCategoria)
        btnGuardar = findViewById(R.id.btnGuardarGasto)
        btnEliminar = findViewById(R.id.btnEliminarGasto)

        val modoEdicion = gastoId > 0

        if (modoEdicion) {
            btnGuardar.text = "Guardar cambios"
            btnEliminar.visibility = android.view.View.VISIBLE
            btnGuardar.isEnabled = false
            cargarGasto()
        } else {
            btnGuardar.text = "Guardar gasto"
            btnEliminar.visibility = android.view.View.GONE
        }

        btnGuardar.setOnClickListener {
            guardarGasto()
        }

        btnEliminar.setOnClickListener {
            confirmarEliminacion()
        }
    }

    private fun cargarGasto() {

        lifecycleScope.launch {

            try {
                val dao = AppDatabase
                    .obtenerBaseDeDatos(applicationContext)
                    .gastoDao()

                val gasto = dao.buscarPorId(gastoId)

                if (gasto == null) {

                    Toast.makeText(
                        this@FormularioGastoActivity,
                        "No se encontró el gasto",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                    return@launch
                }

                gastoActual = gasto

                descripcion.setText(gasto.descripcion)
                monto.setText(gasto.monto.toString())

                val categorias = resources.getStringArray(
                    R.array.categorias_gasto
                )

                val posicion = categorias.indexOf(gasto.categoria)

                if (posicion >= 0) {
                    categoria.setSelection(posicion)
                }

                btnGuardar.isEnabled = true

            } catch (e: Exception) {

                Toast.makeText(
                    this@FormularioGastoActivity,
                    "Error al cargar el gasto",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }
    }

    private fun guardarGasto() {

        val textoDescripcion = descripcion.text.toString().trim()
        val valorMonto = monto.text.toString().toDoubleOrNull()

        if (
            textoDescripcion.isEmpty() ||
            valorMonto == null ||
            !valorMonto.isFinite() ||
            valorMonto <= 0
        ) {

            Toast.makeText(
                this,
                "Ingresa una descripción y un monto válido",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val categoriaSeleccionada =
            categoria.selectedItem.toString()

        btnGuardar.isEnabled = false

        lifecycleScope.launch {

            try {

                val dao = AppDatabase
                    .obtenerBaseDeDatos(applicationContext)
                    .gastoDao()

                val gastoExistente = gastoActual

                if (gastoExistente != null) {

                    val gastoActualizado = GastoEntity(
                        id = gastoExistente.id,
                        descripcion = textoDescripcion,
                        monto = valorMonto,
                        categoria = categoriaSeleccionada,
                        fecha = gastoExistente.fecha
                    )

                    dao.actualizar(gastoActualizado)

                    Toast.makeText(
                        this@FormularioGastoActivity,
                        "Gasto actualizado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    val fechaActual = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Date())

                    val nuevoGasto = GastoEntity(
                        descripcion = textoDescripcion,
                        monto = valorMonto,
                        categoria = categoriaSeleccionada,
                        fecha = fechaActual
                    )

                    dao.insertar(nuevoGasto)

                    Toast.makeText(
                        this@FormularioGastoActivity,
                        "Gasto guardado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                finish()

            } catch (e: Exception) {

                btnGuardar.isEnabled = true

                Toast.makeText(
                    this@FormularioGastoActivity,
                    "Error al guardar el gasto",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun confirmarEliminacion() {

        val gasto = gastoActual ?: return

        AlertDialog.Builder(this)
            .setTitle("Eliminar gasto")
            .setMessage(
                "¿Estás seguro de que deseas eliminar este gasto?"
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->

                eliminarGasto(gasto)
            }
            .show()
    }

    private fun eliminarGasto(gasto: GastoEntity) {

        btnEliminar.isEnabled = false

        lifecycleScope.launch {

            try {

                val dao = AppDatabase
                    .obtenerBaseDeDatos(applicationContext)
                    .gastoDao()

                dao.eliminar(gasto)

                Toast.makeText(
                    this@FormularioGastoActivity,
                    "Gasto eliminado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } catch (e: Exception) {

                btnEliminar.isEnabled = true

                Toast.makeText(
                    this@FormularioGastoActivity,
                    "Error al eliminar el gasto",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        const val EXTRA_GASTO_ID = "gasto_id"
    }
}