package edu.umg.controlgastos.datos

import edu.umg.controlgastos.modelo.Gasto
import edu.umg.controlgastos.modelo.Ingreso

class MovimientoRepositoryEnMemoria : MovimientoRepository {

private val ingresos = mutableListOf<Ingreso>()
private val gastos = mutableListOf<Gasto>()

override suspend fun guardarIngreso(ingreso: Ingreso) {
    ingresos.add(ingreso)
}

override suspend fun guardarGasto(gasto: Gasto) {
    gastos.add(gasto)
}

override suspend fun listarIngresos(): List<Ingreso> {
    return ingresos.toList()
}

override suspend fun listarGastos(): List<Gasto> {
    return gastos.toList()
}

override suspend fun obtenerUltimosGastos(): List<Gasto> {
    return gastos
        .sortedWith(
            compareByDescending<Gasto> { it.fecha }
                .thenByDescending { it.id }
        )
        .take(3)
}
}