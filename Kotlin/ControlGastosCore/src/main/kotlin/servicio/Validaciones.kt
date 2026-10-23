package servicio

object Validaciones {

    fun validarMonto(monto: Double): Boolean {
        return monto.isFinite() && monto > 0
    }

    fun validarDescripcion(descripcion: String): Boolean {
        return descripcion.isNotBlank()
    }

    fun validarNombre(nombre: String): Boolean {
        return nombre.isNotBlank()
    }

    fun validarCorreo(correo: String): Boolean {
        return correo.matches(
            Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        )
    }

    fun validarCategoria(nombre: String): Boolean {
        return nombre.isNotBlank()
    }
}
