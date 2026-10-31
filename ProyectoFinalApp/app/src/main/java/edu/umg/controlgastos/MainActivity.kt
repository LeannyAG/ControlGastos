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
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gastoAdapter: GastoAdapter

    private val seleccionarFoto = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        if (uri != null) {
            binding.imgFotoPerfil.setImageURI(uri)

            getSharedPreferences("control_gastos", MODE_PRIVATE)
                .edit()
                .putString("foto_perfil", uri.toString())
                .apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Verificar si el usuario está registrado
        val preferencias = getSharedPreferences(
            "control_gastos",
            MODE_PRIVATE
        )

        if (!preferencias.getBoolean("registro_completado", false)) {
            startActivity(
                Intent(this, RegistroUsuarioActivity::class.java)
            )
            finish()
            return
        }

        // 2. Inicializar la interfaz
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Recuperar foto de perfil guardada
        val preferenciasFoto = getSharedPreferences(
            "control_gastos", ///controles para elegir foto de perfil
            MODE_PRIVATE
        )

        val fotoGuardada = preferenciasFoto.getString("foto_perfil", null)

        if (fotoGuardada != null) {
            binding.imgFotoPerfil.setImageURI(Uri.parse(fotoGuardada))
        }

// Abrir galería al tocar la foto
        binding.imgFotoPerfil.setOnClickListener {
            seleccionarFoto.launch("image/*")
        }

        // 3. Conectar base de datos
        val database = AppDatabase.obtenerBaseDeDatos(applicationContext)

        val gastoDao = database.gastoDao()
        val ingresoDao = database.ingresoDao()
        val usuarioDao = database.usuarioDao()
        val presupuestoDao = database.presupuestoDao()

        // 4. Mostrar saludo personalizado
        lifecycleScope.launch {
            val usuario = usuarioDao.obtenerPrimerUsuario()

            val nombre = usuario?.nombre
                ?.trim()
                ?.substringBefore(" ")
                ?: "Usuario"

            binding.txtSaludoUsuario.text = "¡Hola, $nombre!"
        }
        binding.cardPresupuesto.setOnClickListener {
            startActivity(
                Intent(this, PresupuestoActivity::class.java)
            )
        }
        // 5. Botón Ver todos
        binding.txtVerTodos.setOnClickListener {
            startActivity(
                Intent(this, MovimientosActivity::class.java)
            )
        }

        // 6. Configurar lista de gastos
        gastoAdapter = GastoAdapter(emptyList()) { gasto ->
            // Aquí podremos agregar la opción de editar gastos.
        }

        binding.recyclerGastos.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.recyclerGastos.adapter = gastoAdapter

        // 7. Registrar nuevos movimientos
        binding.btnNuevoGasto.setOnClickListener {

            val opciones = arrayOf(
                "Registrar gasto",
                "Registrar ingreso"
            )

            AlertDialog.Builder(this)
                .setTitle("¿Qué deseas registrar?")
                .setItems(opciones) { _, seleccion ->

                    when (seleccion) {
                        0 -> startActivity(
                            Intent(
                                this,
                                FormularioGastoActivity::class.java
                            )
                        )

                        1 -> startActivity(
                            Intent(
                                this,
                                FormularioIngresoActivity::class.java
                            )
                        )
                    }
                }
                .show()
        }

        // 8. Actualizar información automáticamente
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                combine(
                    gastoDao.listarTodos(),
                    ingresoDao.listarTodos(),
                    presupuestoDao.obtenerPresupuesto()
                ) { gastos, ingresos, presupuesto ->

                    Triple(gastos, ingresos, presupuesto)

                }.collect { (entidadesGasto, entidadesIngreso, presupuesto) ->

                    // Crear presupuesto inicial si todavía no existe
                    if (presupuesto == null) {
                        presupuestoDao.guardar(
                            PresupuestoEntity(monto = 3000.0)
                        )
                        return@collect
                    }

                    val limitePresupuesto = presupuesto.monto

                    // Convertir gastos para mostrarlos en la lista
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

                    // Calcular totales
                    val totalGastos =
                        entidadesGasto.sumOf { it.monto }

                    val totalIngresos =
                        entidadesIngreso.sumOf { it.monto }

                    val saldo = totalIngresos - totalGastos

                    // Mostrar ingresos
                    binding.txtIngresos.text = String.format(
                        Locale.US,
                        "Q %,.2f",
                        totalIngresos
                    )

                    // Mostrar gastos
                    binding.txtTotalGastos.text = String.format(
                        Locale.US,
                        "Q %,.2f",
                        totalGastos
                    )

                    // Mostrar saldo
                    binding.txtSaldo.text = String.format(
                        Locale.US,
                        "Q %,.2f",
                        saldo
                    )

                    // Mostrar presupuesto utilizado
                    binding.txtPresupuestoGastado.text =
                        String.format(
                            Locale.US,
                            "Q %,.2f",
                            totalGastos
                        )

                    // Mostrar límite del presupuesto
                    binding.txtLimitePresupuesto.text =
                        String.format(
                            Locale.US,
                            "de Q %,.2f",
                            limitePresupuesto
                        )

                    // Actualizar barra de progreso
                    binding.progresoPresupuesto.max =
                        limitePresupuesto.toInt().coerceAtLeast(1)

                    binding.progresoPresupuesto.progress =
                        totalGastos
                            .coerceIn(0.0, limitePresupuesto)
                            .toInt()
                }
            }
        }
    }
}