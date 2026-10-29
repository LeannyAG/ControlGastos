package edu.umg.controlgastos
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormularioIngresoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_ingreso)

        val descripcion = findViewById<EditText>(R.id.edtDescripcionIngreso)
        val monto = findViewById<EditText>(R.id.edtMontoIngreso)
        val categoria = findViewById<Spinner>(R.id.spinnerCategoriaIngreso)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarIngreso)

        btnGuardar.setOnClickListener {
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
                return@setOnClickListener
            }

            val fechaActual = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())

            val ingreso = IngresoEntity(
                descripcion = textoDescripcion,
                monto = valorMonto,
                categoria = categoria.selectedItem.toString(),
                fecha = fechaActual
            )

            btnGuardar.isEnabled = false

            lifecycleScope.launch {
                try {
                    val dao = AppDatabase
                        .obtenerBaseDeDatos(applicationContext)
                        .ingresoDao()

                    dao.insertar(ingreso)

                    Toast.makeText(
                        this@FormularioIngresoActivity,
                        "Ingreso guardado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                } catch (e: Exception) {
                    btnGuardar.isEnabled = true

                    Toast.makeText(
                        this@FormularioIngresoActivity,
                        "Error al guardar el ingreso",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}