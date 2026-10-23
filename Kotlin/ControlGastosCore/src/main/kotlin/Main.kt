package org.example

import edu.umg.controlgastos.datos.MovimientoRepositoryEnMemoria
import edu.umg.controlgastos.modelo.Categoria
import edu.umg.controlgastos.modelo.Gasto
import edu.umg.controlgastos.modelo.Ingreso
import servicio.ControlFinanciero
import kotlinx.coroutines.runBlocking
import servicio.RegistroMovimientos

fun main() = runBlocking {

    val repositorio = MovimientoRepositoryEnMemoria()
    val control = ControlFinanciero()
    val registro = RegistroMovimientos(repositorio)
    println("\n--- PRUEBA DE VALIDACIONES ---")

// Prueba 1: ingreso correcto
    try {
        registro.registrarIngreso(
            Ingreso(
                2,
                1500.0,
                "Venta de productos",
                "Emprendimiento",
                "2026-10-23"
            )
        )
        println("Ingreso correcto: REGISTRADO")
    } catch (e: IllegalArgumentException) {
        println("Error: ${e.message}")
    }

// Prueba 2: gasto con monto negativo
    try {
        registro.registrarGasto(
            Gasto(
                id = 3,
                monto = -200.0,
                descripcion = "Gasolina",
                tipoGasto = Categoria(1, "Gasolina", true),
                fecha = "2026-10-23"
            )
            
        )
        println("Gasto incorrecto: REGISTRADO")
    } catch (e: IllegalArgumentException) {
        println("Error esperado: ${e.message}")
    }

// Prueba 3: ingreso sin descripcion
    try {
        registro.registrarIngreso(
            Ingreso(
                3,
                500.0,
                "",
                "Salario",
                "2026-10-23"
            )
        )
        println("Ingreso sin descripcion: REGISTRADO")
    } catch (e: IllegalArgumentException) {
        println("Error esperado: ${e.message}")
    }

    val gasolina = Categoria(1, "Gasolina", true)
    val despensa = Categoria(2, "Despensa", true)

    repositorio.guardarIngreso(
        Ingreso(1, 5000.0, "Salario", "Salario", "2026-10-22")
    )

    repositorio.guardarGasto(
        Gasto(1, 300.0, "Combustible", gasolina, "2026-10-22")
    )

    repositorio.guardarGasto(
        Gasto(2, 500.0, "Supermercado", despensa, "2026-10-21")
    )

    val ingresos = repositorio.listarIngresos()
    val gastos = repositorio.listarGastos()

    println("Total ingresos: Q${control.calcularTotalIngresos(ingresos)}")
    println("Total gastos: Q${control.calcularTotalGastos(gastos)}")
    println("Balance: Q${control.calcularBalance(ingresos, gastos)}")

    println("Ultimos gastos:")
    repositorio.obtenerUltimosGastos().forEach {
        println("${it.descripcion}: Q${it.monto}")
    }
}