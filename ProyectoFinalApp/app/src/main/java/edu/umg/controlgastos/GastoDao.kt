package edu.umg.controlgastos



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {

    // guardar un gasto
    @Insert
    suspend fun insertar(gasto: GastoEntity)

    // mostrar todos los gastos
    @Query("SELECT * FROM gastos ORDER BY id DESC")
    fun listarTodos(): Flow<List<GastoEntity>>

    // buscar un gasto por su ID
    @Query("SELECT * FROM gastos WHERE id = :id")
    suspend fun buscarPorId(id: Int): GastoEntity?

    // actualizar un gasto
    @Update
    suspend fun actualizar(gasto: GastoEntity)

    // eliminar un gasto
    @Delete
    suspend fun eliminar(gasto: GastoEntity)
}