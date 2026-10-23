package org.example

import edu.umg.controlgastos.datos.MovimientoRepositoryEnMemoria
import edu.umg.controlgastos.modelo.Categoria
import edu.umg.controlgastos.modelo.Gasto
import edu.umg.controlgastos.modelo.Ingreso
import servicio.ControlFinanciero
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    val repositorio = MovimientoRepositoryEnMemoria()
    val control = ControlFinanciero()

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