package edu.umg.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroUsuarioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        val nombre = findViewById<EditText>(R.id.edtNombreUsuario)
        val apellidos = findViewById<EditText>(R.id.edtApellidosUsuario)
        val correo = findViewById<EditText>(R.id.edtCorreoUsuario)
        val botonRegistrar = findViewById<Button>(R.id.btnRegistrarUsuario)

        botonRegistrar.setOnClickListener {
            val nombreTexto = nombre.text.toString().trim()
            val apellidosTexto = apellidos.text.toString().trim()
            val correoTexto = correo.text.toString().trim()

            if (
                nombreTexto.isBlank() ||
                apellidosTexto.isBlank() ||
                correoTexto.isBlank() ||
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(correoTexto).matches()
            ) {
                Toast.makeText(
                    this,
                    "Ingresa nombres, apellidos y un correo válido",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            botonRegistrar.isEnabled = false

            lifecycleScope.launch {
                try {
                    val dao = AppDatabase
                        .obtenerBaseDeDatos(applicationContext)
                        .usuarioDao()

                    val existente = dao.buscarPorCorreo(correoTexto)

                    if (existente != null) {
                        Toast.makeText(
                            this@RegistroUsuarioActivity,
                            "Este correo ya está registrado",
                            Toast.LENGTH_LONG
                        ).show()

                        botonRegistrar.isEnabled = true
                        return@launch
                    }

                    val fecha = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Date())

                    dao.insertar(
                        UsuarioEntity(
                            nombre = "$nombreTexto $apellidosTexto",
                            correo = correoTexto,
                            fechaRegistro = fecha
                        )
                    )

                    getSharedPreferences("control_gastos", MODE_PRIVATE)
                        .edit()
                        .putBoolean("registro_completado", true)
                        .apply()

                    Toast.makeText(
                        this@RegistroUsuarioActivity,
                        "Perfil creado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this@RegistroUsuarioActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()

                } catch (e: Exception) {
                    botonRegistrar.isEnabled = true

                    Toast.makeText(
                        this@RegistroUsuarioActivity,
                        "No se pudo guardar el perfil",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}