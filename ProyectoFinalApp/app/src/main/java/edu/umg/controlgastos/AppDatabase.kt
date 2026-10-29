package edu.umg.controlgastos
        import android.content.Context
        import androidx.room.Database
        import androidx.room.Room
        import androidx.room.RoomDatabase
        import androidx.room.migration.Migration
        import androidx.sqlite.db.SupportSQLiteDatabase

        @Database(
            entities = [
                GastoEntity::class,
                IngresoEntity::class
            ],
            version = 2,
            exportSchema = false
        )
        abstract class AppDatabase : RoomDatabase() {

            abstract fun gastoDao(): GastoDao

            abstract fun ingresoDao(): IngresoDao

            companion object {

                @Volatile
                private var INSTANCE: AppDatabase? = null

                private val MIGRACION_1_2 = object : Migration(1, 2) {
                    override fun migrate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                    CREATE TABLE IF NOT EXISTS ingresos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        descripcion TEXT NOT NULL,
                        monto REAL NOT NULL,
                        categoria TEXT NOT NULL,
                        fecha TEXT NOT NULL
                    )
                    """.trimIndent()
                        )
                    }
                }

                fun obtenerBaseDeDatos(context: Context): AppDatabase {
                    return INSTANCE ?: synchronized(this) {
                        val instancia = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "control_gastos_db"
                        )
                            .addMigrations(MIGRACION_1_2)
                            .build()

                        INSTANCE = instancia
                        instancia
                    }
                }
    }
}