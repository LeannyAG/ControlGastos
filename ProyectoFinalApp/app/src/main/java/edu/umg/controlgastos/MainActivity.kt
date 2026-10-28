
package edu.umg.controlgastos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import edu.umg.controlgastos.databinding.ActivityMainBinding
import edu.umg.controlgastos.modelo.Categoria
import edu.umg.controlgastos.modelo.Gasto
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gastoAdapter: GastoAdapter

    private val presupuestoMensual = 3000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar la lista horizontal de gastos
        gastoAdapter = GastoAdapter(emptyList()) { gasto ->
            // Después agregaremos la opción para editar un gasto
        }

        binding.recyclerGastos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerGastos.adapter = gastoAdapter

        // Abrir el formulario para registrar un gasto
        binding.btnNuevoGasto.setOnClickListener {
            val intent = Intent(this, FormularioGastoActivity::class.java)
            startActivity(intent)
        }

        // Observar los gastos guardados en SQLite
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val dao = AppDatabase
                    .obtenerBaseDeDatos(applicationContext)
                    .gastoDao()

                dao.listarTodos().collect { entidades ->

                    val gastos = entidades.map { entidad ->
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

                    // Actualizar las tarjetas de gastos recientes
                    gastoAdapter.actualizarLista(gastos)

                    // Calcular el total de gastos registrados
                    val totalGastos = entidades.sumOf { it.monto }

                    // Mostrar los importes en quetzales
                    binding.txtTotalGastos.text =
                        String.format(Locale.getDefault(), "Q %.2f", totalGastos)

                    // Todavía no hay ingresos conectados
                    val totalIngresos = 0.0

                    binding.txtIngresos.text =
                        String.format(Locale.getDefault(), "Q %.2f", totalIngresos)

                    // Saldo provisional: ingresos menos gastos
                    val saldo = totalIngresos - totalGastos

                    binding.txtSaldo.text =
                        String.format(Locale.getDefault(), "Q %.2f", saldo)

                    // Actualizar el presupuesto mensual
                    binding.txtPresupuestoGastado.text =
                        String.format(Locale.getDefault(), "Q %.2f", totalGastos)

                    binding.progresoPresupuesto.max =
                        presupuestoMensual.toInt()

                    binding.progresoPresupuesto.progress =
                        totalGastos.coerceIn(0.0, presupuestoMensual).toInt()
                }
            }
        }
    }
}