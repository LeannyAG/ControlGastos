package edu.umg.controlgastos.datos

import edu.umg.controlgastos.modelo.Gasto

class GastoRepositoryEnMemoria {

    private val  gastos = mutableListOf<Gasto>()

    fun guardar(gasto : Gasto) {
        gastos.add(gasto)
    }

    fun ListarTodos(): List<Gasto>{
        return gastos.toList()
    }

    fun contar(): Int {
        return gastos.size
    }

    fun calcularTotal(): Double {
        return gastos.sumOf { it.monto }
    }
}