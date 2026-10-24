package edu.umg.controlgastos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FormularioGastoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_formulario_gasto)

        val btnGuardar = findViewById<android.widget.Button>(R.id.btnGuardarGasto)

        btnGuardar.setOnClickListener {
            val descripcion = findViewById<android.widget.EditText>(
                R.id.edtDescripcion
            ).text.toString().trim()

            val monto = findViewById<android.widget.EditText>(
                R.id.edtMonto
            ).text.toString().toDoubleOrNull()

            if (descripcion.isBlank() || monto == null || !monto.isFinite() || monto <= 0) {
                android.widget.Toast.makeText(
                    this,
                    "Ingresa una descripción y un monto válido",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            android.widget.Toast.makeText(
                this,
                "Datos válidos. Falta conectar el guardado.",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
}