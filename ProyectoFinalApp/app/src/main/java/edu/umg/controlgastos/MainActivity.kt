
package edu.umg.controlgastos


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import edu.umg.controlgastos.databinding.ActivityMainBinding
import edu.umg.controlgastos.modelo.Categoria
import edu.umg.controlgastos.modelo.Gasto
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gastoAdapter: GastoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gastos de prueba para comprobar que la lista funciona
        val gastos = listOf(
            Gasto(
                id = 1,
                monto = 150.0,
                descripcion = "Gasolina",
                tipoGasto = Categoria(1, "Transporte"),
                fecha = "2026-10-23"
            ),
            Gasto(
                id = 2,
                monto = 75.0,
                descripcion = "Almuerzo",
                tipoGasto = Categoria(2, "Comida"),
                fecha = "2026-10-23"
            )
        )

        gastoAdapter = GastoAdapter(gastos) { gasto ->
            // Después abriremos el formulario para editar este gasto
        }

        binding.recyclerGastos.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerGastos.adapter = gastoAdapter


            binding.btnNuevoGasto.setOnClickListener {
                val intent = Intent(this, FormularioGastoActivity::class.java)
                startActivity(intent)
            }

        }
    }
