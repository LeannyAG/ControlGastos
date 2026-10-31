package edu.umg.controlgastos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Locale

class PresupuestoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presupuesto)

        val edtPresupuesto = findViewById<android.widget.EditText>(
            R.id.edtPresupuesto
        )

        val btnGuardar = findViewById<android.widget.Button>(
            R.id.btnGuardarPresupuesto
        )

        val btnVolver = findViewById<android.widget.Button>(
            R.id.btnVolverPresupuesto
        )

        val presupuestoDao = AppDatabase
            .obtenerBaseDeDatos(applicationContext)
            .presupuestoDao()

        // Mostrar el presupuesto guardado
        lifecycleScope.launch {
            presupuestoDao.obtenerPresupuesto().collect { presupuesto ->
                if (presupuesto != null &&
                    !edtPresupuesto.hasFocus()
                ) {
                    edtPresupuesto.setText(
                        String.format(
                            Locale.US,
                            "%.2f",
                            presupuesto.monto
                        )
                    )
                }
            }
        }

        // Guardar nuevo presupuesto
        btnGuardar.setOnClickListener {

            val montoTexto = edtPresupuesto.text.toString().trim()
            val monto = montoTexto.toDoubleOrNull()

            if (monto == null || monto <= 0) {
                edtPresupuesto.error = "Ingresa un monto válido"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                presupuestoDao.guardar(
                    PresupuestoEntity(monto = monto)
                )

                Toast.makeText(
                    this@PresupuestoActivity,
                    "Presupuesto guardado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

        // Volver a la pantalla principal
        btnVolver.setOnClickListener {
            finish()
        }
    }
}