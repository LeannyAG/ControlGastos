package edu.umg.controlgastos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import edu.umg.controlgastos.databinding.ActivityMainBinding
import edu.umg.controlgastos.modelo.Categoria
import edu.umg.controlgastos.modelo.Gasto
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gastoAdapter: GastoAdapter

    private val presupuestoMensual = 3000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //para que el usuario ya registrado entre de una ve a la pantalla princiapl
        val preferencias = getSharedPreferences("control_gastos", MODE_PRIVATE)

        if (!preferencias.getBoolean("registro_completado", false)) {
            startActivity(Intent(this, RegistroUsuarioActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            val usuarioDao = AppDatabase
                .obtenerBaseDeDatos(applicationContext)
                .usuarioDao()

            val usuario = usuarioDao.obtenerPrimerUsuario()

            val nombre = usuario?.nombre
                ?.trim()
                ?.substringBefore(" ")
                ?: "Usuario"

            binding.txtSaludoUsuario.text = "¡Hola, $nombre!"
        }

        gastoAdapter = GastoAdapter(emptyList()) { gasto ->
            // Después agregaremos la opción para editar un gasto.
        }

        binding.recyclerGastos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerGastos.adapter = gastoAdapter

        binding.btnNuevoGasto.setOnClickListener {
            val opciones = arrayOf("Registrar gasto", "Registrar ingreso")

            AlertDialog.Builder(this)
                .setTitle("¿Qué deseas registrar?")
                .setItems(opciones) { _, seleccion ->
                    when (seleccion) {
                        0 -> startActivity(
                            Intent(this, FormularioGastoActivity::class.java)
                        )

                        1 -> startActivity(
                            Intent(this, FormularioIngresoActivity::class.java)
                        )
                    }
                }
                .show()
        }

        val database = AppDatabase.obtenerBaseDeDatos(applicationContext)
        val gastoDao = database.gastoDao()
        val ingresoDao = database.ingresoDao()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    gastoDao.listarTodos(),
                    ingresoDao.listarTodos()
                ) { gastos, ingresos ->
                    Pair(gastos, ingresos)
                }.collect { (entidadesGasto, entidadesIngreso) ->

                    val gastos = entidadesGasto.map { entidad ->
                        Gasto(
                            id = entidad.id,
                            monto = entidad.monto,
                            descripcion = entidad.descripcion,
                            tipoGasto = Categoria(
                                id = 0,
                                nombre = entidad.categoria
                            ),
                            fecha = entidad.fecha
                        )
                    }

                    gastoAdapter.actualizarLista(gastos)

                    val totalGastos = entidadesGasto.sumOf { it.monto }
                    val totalIngresos = entidadesIngreso.sumOf { it.monto }
                    val saldo = totalIngresos - totalGastos

                    binding.txtIngresos.text =
                        String.format(java.util.Locale.US, "Q %,.2f", totalIngresos)

                    binding.txtTotalGastos.text =
                        String.format(java.util.Locale.US, "Q %,.2f", totalGastos)

                    binding.txtSaldo.text =
                        String.format(java.util.Locale.US, "Q %,.2f", saldo)

                    binding.txtPresupuestoGastado.text =
                        String.format(java.util.Locale.US, "Q %,.2f", totalGastos)

                    binding.progresoPresupuesto.max = presupuestoMensual.toInt()

                    binding.progresoPresupuesto.progress =
                        totalGastos.coerceIn(0.0, presupuestoMensual).toInt()
                }
            }
        }
    }
}