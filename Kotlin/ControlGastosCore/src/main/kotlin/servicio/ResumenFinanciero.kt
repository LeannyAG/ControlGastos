package servicio

import edu.umg.controlgastos.modelo.Gasto

data class ResumenFinanciero(
    val totalIngresos: Double,
    val totalGastos: Double,
    val saldoDisponible: Double,
    val ultimosGastos: List<Gasto>
)