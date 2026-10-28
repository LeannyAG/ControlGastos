
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gastoAdapter: GastoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar la lista de gastos
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

        // Leer los gastos guardados en SQLite
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

                    gastoAdapter.actualizarLista(gastos)
                }
            }
        }
    }
}

