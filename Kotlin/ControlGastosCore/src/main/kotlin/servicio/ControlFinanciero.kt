package servicio

import edu.umg.controlgastos.datos.MovimientoRepository
import edu.umg.controlgastos.datos.MovimientoRepositoryEnMemoria
import edu.umg.controlgastos.modelo.Gasto
import edu.umg.controlgastos.modelo.Ingreso

class ControlFinanciero {
    fun calcularTotalIngresos(ingresos: List<Ingreso>): Double {
        return ingresos.sumOf { it.monto }
    }

    fun calcularTotalGastos(gastos: List<Gasto>): Double {
        return gastos.sumOf { it.monto }
    }

    fun calcularBalance(
        ingresos: List<Ingreso>,
        gastos: List<Gasto>
    ): Double {
        val totalIngresos = calcularTotalIngresos(ingresos)
        val totalGastos = calcularTotalGastos(gastos)

        return totalIngresos - totalGastos
    }

    suspend fun generarResumen(
        repositorio: MovimientoRepository
    ): ResumenFinanciero {

        val ingresos = repositorio.listarIngresos()
        val gastos = repositorio.listarGastos()

        return ResumenFinanciero(
            totalIngresos = calcularTotalIngresos(ingresos),
            totalGastos = calcularTotalGastos(gastos),
            saldoDisponible = calcularBalance(ingresos, gastos),
            ultimosGastos = repositorio.obtenerUltimosGastos()
        )
    }
}



