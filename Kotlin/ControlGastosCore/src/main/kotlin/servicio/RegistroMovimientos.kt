package servicio

import edu.umg.controlgastos.datos.MovimientoRepository
import edu.umg.controlgastos.modelo.Gasto
import edu.umg.controlgastos.modelo.Ingreso

class RegistroMovimientos(
    private val repositorio: MovimientoRepository
    ){

        suspend fun registrarIngreso(ingreso: Ingreso) {
            require(Validaciones.validarMonto(ingreso.monto)) {
                "El monto debe ser mayor que cero"
            }

            require(Validaciones.validarDescripcion(ingreso.descripcion)) {
                "La descripcion no puede estar vacia"
            }

            require(Validaciones.validarCategoria(ingreso.tipoIngreso)) {
                "Debes indicar un tipo de ingreso"
            }

            repositorio.guardarIngreso(ingreso)
        }

        suspend fun registrarGasto(gasto: Gasto) {
            require(Validaciones.validarMonto(gasto.monto)) {
                "El monto debe ser mayor que cero"
            }

            require(Validaciones.validarDescripcion(gasto.descripcion)) {
                "La descripcion no puede estar vacia"
            }

            require(Validaciones.validarCategoria(gasto.tipoGasto.nombre)) {
                "Debes indicar una categoria"
            }

            repositorio.guardarGasto(gasto)
        }
    }
