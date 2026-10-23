package edu.umg.controlgastos.datos

import edu.umg.controlgastos.modelo.Gasto
import edu.umg.controlgastos.modelo.Ingreso

interface MovimientoRepository {


    interface MovimientoRepository

    suspend fun guardarIngreso(ingreso: Ingreso)

    suspend fun guardarGasto(gasto: Gasto)

    suspend fun listarIngresos(): List<Ingreso>

    suspend fun listarGastos(): List<Gasto>

    suspend fun obtenerUltimosGastos(): List<Gasto>

}



