package edu.umg.controlgastos.modelo

data class Gasto(
    val id: Int,
    val monto: Double,
    val descripcion: String,
    val tipoGasto: Categoria,
    val fecha: String
)