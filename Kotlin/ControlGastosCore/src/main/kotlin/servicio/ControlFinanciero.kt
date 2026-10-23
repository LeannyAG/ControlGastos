package servicio

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
}
