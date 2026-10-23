package edu.umg.controlgastos.modelo

data class Ingreso(
    val id: Int,
    val monto: Double,
    val descripcion: String,
    val tipoIngreso: String,
    val fecha: String
)