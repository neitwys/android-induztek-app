package com.induztek.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.induztek.app.model.Rol
import com.induztek.app.model.Usuario

class InduztekDbHelper(context: Context) :
        SQLiteOpenHelper(context, NOMBRE_DB, null, VERSION_DB) {

    companion object {
        private const val NOMBRE_DB = "induztek.db"
        private const val VERSION_DB = 3

        //----------------- TABLA ROLES -----------------//
        private const val TABLA_ROLES = "roles"
        private const val ROL_ID = "id_rol"
        private const val ROL_NOMBRE = "nombre_rol"

        //----------------- TABLA USUARIOS -----------------//
        private const val TABLA_USUARIOS = "usuarios"
        private const val USU_ID = "id_usuario"
        private const val USU_NOMBRE = "nombre"
        private const val USU_CORREO = "correo"
        private const val USU_CONTRASENA = "contrasena"
        private const val USU_EMPRESA = "empresa_cliente"
        private const val USU_ROL = "id_rol"

        //----------------- TABLA CLIENTES -----------------//
        private const val TABLA_CLIENTES = "clientes"
        private const val CLI_ID = "id_cliente"
        private const val CLI_RAZON_SOCIAL = "razon_social"
        private const val CLI_RUT = "rut"
        private const val CLI_GIRO = "giro"
        private const val CLI_CONTACTO = "contacto"
        private const val CLI_TELEFONO = "telefono"
        private const val CLI_CORREO = "correo"

        //----------------- TABLA INSTALACIONES -----------------//
        private const val TABLA_INSTALACIONES = "instalaciones"
        private const val INST_ID = "id_instalacion"
        private const val INST_NOMBRE = "nombre_instalacion"
        private const val INST_UBICACION = "ubicacion"
        private const val INST_COMUNA = "comuna"
        private const val INST_REGION = "region"
        private const val INST_OBS = "observaciones_acceso"
        private const val INST_CLIENTE_ID = "id_cliente_fk" //Clave foranea, es con la tabla Clientes.

        //============================================================================================//

        //----------------- CREACION TABLAS -----------------//
        private const val SQL_CREAR_ROLES =
            "CREATE TABLE $TABLA_ROLES (" +
                    "$ROL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$ROL_NOMBRE TEXT NOT NULL UNIQUE)"

        private const val SQL_CREAR_USUARIOS =
            "CREATE TABLE $TABLA_USUARIOS (" +
                    "$USU_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$USU_NOMBRE TEXT NOT NULL, " +
                    "$USU_CORREO TEXT NOT NULL UNIQUE, " +
                    "$USU_CONTRASENA TEXT NOT NULL, " +
                    "$USU_EMPRESA TEXT, " +
                    "$USU_ROL INTEGER NOT NULL REFERENCES $TABLA_ROLES($ROL_ID))"

        private const val SQL_CREAR_CLIENTES =
            "CREATE TABLE $TABLA_CLIENTES (" +
                    "$CLI_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$CLI_RAZON_SOCIAL TEXT NOT NULL, " +
                    "$CLI_RUT TEXT NOT NULL UNIQUE, " +
                    "$CLI_GIRO TEXT, " +
                    "$CLI_CONTACTO TEXT, " +
                    "$CLI_TELEFONO TEXT, " +
                    "$CLI_CORREO TEXT)"

        private const val SQL_CREAR_INSTALACIONES =
            "CREATE TABLE $TABLA_INSTALACIONES (" +
                    "$INST_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$INST_NOMBRE TEXT NOT NULL, " +
                    "$INST_UBICACION TEXT, " +
                    "$INST_COMUNA TEXT, " +
                    "$INST_REGION TEXT, " +
                    "$INST_OBS TEXT, " +
                    "$INST_CLIENTE_ID INTEGER NOT NULL REFERENCES $TABLA_CLIENTES($CLI_ID))"

        //============================================================================================//

        //----------------- PRECARGAR LAS TABLAS -----------------//
        private const val SQL_PRECARGAR_ROLES =
            "INSERT INTO $TABLA_ROLES ($ROL_ID, $ROL_NOMBRE) VALUES " +
                    "(1, 'Tecnico de Terreno'), " +
                    "(2, 'Ingeniero de Pruebas'), " +
                    "(3, 'Jefe Técnico'), " +
                    "(4, 'Usuario Cliente')"

        //Precargar los cuatro usuarios de prueba con sus respectivos roles.
        private const val SQL_PRECARGAR_USUARIOS =
            "INSERT INTO $TABLA_USUARIOS ($USU_NOMBRE, $USU_CORREO, $USU_CONTRASENA, $USU_EMPRESA, $USU_ROL) VALUES " +
                    "('Matías Godoy Herrera', 'mgodoy@induztek.ejemplo', '123456789', NULL, 1), " +
                    "('Andrea Contreras Silva', 'acontreras@induztek.ejemplo', '987654321', NULL, 2), " +
                    "('Ricardo Espinoza Lagos', 'respinoza@induztek.ejemplo', '135792468',NULL, 3), " +
                    "('Marcela Fuentes Rojas', 'mfuentes@envpacifico.ejemplo', '246813579','Envasadora del Pacífico', 4)"

        //Precargar la empresa cliente de prueba.
        private const val SQL_PRECARGAR_CLIENTES =
            "INSERT INTO $TABLA_CLIENTES ($CLI_ID, $CLI_RAZON_SOCIAL, $CLI_RUT, $CLI_GIRO, $CLI_CONTACTO, $CLI_TELEFONO, $CLI_CORREO) VALUES " +
                    "(1, 'Envasadora del Pacífico SpA', '76.543.210-9', 'Industrial / Envasado', 'Carlos Soto', '+56912345678', 'contacto@envasadora.ejemplo')"

        //Precargar su instalación amarrada a ese cliente (id_cliente_fk = 1)
        private const val SQL_PRECARGAR_INSTALACIONES =
            "INSERT INTO $TABLA_INSTALACIONES ($INST_NOMBRE, $INST_UBICACION, $INST_COMUNA, $INST_REGION, $INST_OBS, $INST_CLIENTE_ID) VALUES " +
                    "('Planta Lampa', 'Ruta 5 Norte Km 28', 'Lampa', 'Metropolitana', 'Requiere inducción previa', 1)"

        //Une cada usuario con SU rol, selecciona el id del usuario, el nombre,  el correo, la empresa (si tiene), el id_rol y nombre del rol.
        private const val SQL_SELECT_USUARIOS =
            "SELECT u.*, r.$ROL_NOMBRE AS $ROL_NOMBRE " +
                    "FROM $TABLA_USUARIOS u " +
                    "INNER JOIN $TABLA_ROLES r ON u.$USU_ROL = r.$ROL_ID"
        }

    //----------------- EVENTOS PARA CONFIGURAR, CARGAR Y ACTUALIZAR TABLAS -----------------//

    //onConfigure, se ejecuta cada vez que se se abre la conexión, antes del onCreate
    //SQLite trae las claves foraneas apagadas, con esto se activa.
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    //Una sola vez, cuando el induztek.db no existe.
    //Se carga de arriba hacia abajo, asi que primero las tablas padres y de ahí las hijas.
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_CREAR_CLIENTES)
        db.execSQL(SQL_CREAR_INSTALACIONES)

        db.execSQL(SQL_PRECARGAR_ROLES)
        db.execSQL(SQL_PRECARGAR_USUARIOS)
        db.execSQL(SQL_PRECARGAR_CLIENTES)
        db.execSQL(SQL_PRECARGAR_INSTALACIONES)
    }

    //Si se sube la VERSION.DB, ejecuta lo que esta, en este caso borra las tablas, y las vuelve a crear como nuevas.
    //Se carga de abajo hacia arriba, procurar eliminar primero tablas hijas que padres.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_INSTALACIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_CLIENTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ROLES")
        onCreate(db)
    }

    //==================================== FUNCIONES ======================================//

    //----------------- ROLES -----------------//

    //READ de la tabla Roles.
    fun listarRoles(): List<Rol> {
        val roles = mutableListOf<Rol>()
        readableDatabase.rawQuery(
            "SELECT $ROL_ID, $ROL_NOMBRE FROM $TABLA_ROLES ORDER BY $ROL_ID", null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                roles.add(
                    Rol(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(ROL_ID)),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow(ROL_NOMBRE))
                    )
                )
            }
        }
        return roles
    }

    //----------------- USUARIOS -----------------//

    //Inserta un usuario
    fun insertarUsuario(usuario: Usuario): Long =
        writableDatabase.insert(TABLA_USUARIOS, null, valoresDe(usuario))

    //Lista los usuarios
    fun listarUsuarios(): List<Usuario> =
        consultarUsuarios("$SQL_SELECT_USUARIOS ORDER BY u.$USU_NOMBRE", null)

    //Busca un usuario en especifico, por su nombre o por su correo.
    fun buscarUsuarios(texto: String): List<Usuario> {
        val patron = "%$texto%"
        return consultarUsuarios(
            "$SQL_SELECT_USUARIOS WHERE u.$USU_NOMBRE LIKE ? OR u.$USU_CORREO LIKE ? " +
                    "ORDER BY u.$USU_NOMBRE",
            arrayOf(patron, patron)
        )
    }

    //Busca un usuario por su ID (READ)
    fun buscarPorId(id: Long): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIOS WHERE u.$USU_ID = ?", arrayOf(id.toString()))
            .firstOrNull()

    //Util para el Login: buscar un usuario directamente por su correo
    fun buscarPorCorreo(correo: String): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIOS WHERE u.$USU_CORREO = ?", arrayOf(correo))
            .firstOrNull()

    //Actualizar la contrasena, es parecido al actualizar usuario
    fun actualizarContrasena(idUsuario: Long, nuevaContrasena: String): Int {
        val valores = ContentValues().apply {
            put(USU_CONTRASENA, nuevaContrasena)
        }
        return writableDatabase.update(
            TABLA_USUARIOS, valores, "$USU_ID = ?", arrayOf(idUsuario.toString())
        )
    }

    //Actualiza un usuario, solo actualiza el en el que coincida su ID.
    fun actualizarUsuario(usuario: Usuario): Int =
        writableDatabase.update(
            TABLA_USUARIOS, valoresDe(usuario), "$USU_ID = ?", arrayOf(usuario.id.toString())
        )

    //Elimina un usuario, solo elimina en el que coincida su ID.
    fun eliminarUsuario(id: Long): Int =
        writableDatabase.delete(TABLA_USUARIOS, "$USU_ID = ?", arrayOf(id.toString()))

    //----------------- CLIENTES E INSTALACIONES -----------------//
    fun insertarClienteConInstalacion(
        razonSocial: String, rut: String, giro: String, contacto: String, telefono: String, correo: String,
        nombreInstalacion: String, ubicacion: String, comuna: String, region: String, observaciones: String
    ): Boolean {
        val db = writableDatabase
        db.beginTransaction() //Para que no se corrompa la database si es que hay un error de por medio
        try {
            // 1. Insertamos el Cliente
            val valoresCliente = ContentValues().apply {
                put(CLI_RAZON_SOCIAL, razonSocial)
                put(CLI_RUT, rut)
                put(CLI_GIRO, giro)
                put(CLI_CONTACTO, contacto)
                put(CLI_TELEFONO, telefono)
                put(CLI_CORREO, correo)
            }
            val idClienteGenerado = db.insert(TABLA_CLIENTES, null, valoresCliente)
            if (idClienteGenerado == -1L) return false

            // 2. Insertamos la Instalación vinculada al ID del cliente recién creado (Clave Foránea)
            val valoresInstalacion = ContentValues().apply {
                put(INST_NOMBRE, nombreInstalacion)
                put(INST_UBICACION, ubicacion)
                put(INST_COMUNA, comuna)
                put(INST_REGION, region)
                put(INST_OBS, observaciones)
                put(INST_CLIENTE_ID, idClienteGenerado)
            }
            val idInstalacionGenerada = db.insert(TABLA_INSTALACIONES, null, valoresInstalacion)
            if (idInstalacionGenerada == -1L) return false

            db.setTransactionSuccessful() //Ya la transaccion es correcta. asi que se guardan los cambios.
            return true
        } catch (e: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }

    //Para listar los clientes con sus instalaciones.
    fun listarClientes(): List<ClienteConInstalacion> {
        val lista = mutableListOf<ClienteConInstalacion>()
        val sql = "SELECT c.*, i.$INST_NOMBRE, i.$INST_UBICACION, i.$INST_COMUNA, i.$INST_REGION " +
                "FROM $TABLA_CLIENTES c INNER JOIN $TABLA_INSTALACIONES i ON c.$CLI_ID = i.$INST_CLIENTE_ID " +
                "ORDER BY c.$CLI_RAZON_SOCIAL"
        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    ClienteConInstalacion(
                        idCliente = cursor.getLong(cursor.getColumnIndexOrThrow(CLI_ID)),
                        razonSocial = cursor.getString(cursor.getColumnIndexOrThrow(CLI_RAZON_SOCIAL)),
                        rut = cursor.getString(cursor.getColumnIndexOrThrow(CLI_RUT)),
                        giro = cursor.getString(cursor.getColumnIndexOrThrow(CLI_GIRO)),
                        contacto = cursor.getString(cursor.getColumnIndexOrThrow(CLI_CONTACTO)),
                        telefono = cursor.getString(cursor.getColumnIndexOrThrow(CLI_TELEFONO)),
                        correo = cursor.getString(cursor.getColumnIndexOrThrow(CLI_CORREO)),
                        nombreInstalacion = cursor.getString(cursor.getColumnIndexOrThrow(INST_NOMBRE)),
                        ubicacion = cursor.getString(cursor.getColumnIndexOrThrow(INST_UBICACION)),
                        comuna = cursor.getString(cursor.getColumnIndexOrThrow(INST_COMUNA)),
                        region = cursor.getString(cursor.getColumnIndexOrThrow(INST_REGION))
                    )
                )
            }
        }
        return lista
    }


    fun eliminarCliente(id: Long): Int {
        // Gracias a las restricciones de clave foránea y onDelete, puedes borrar el cliente
        return writableDatabase.delete(TABLA_CLIENTES, "$CLI_ID = ?", arrayOf(id.toString()))
    }

    //----------------- AYUDANTES -----------------//

    //Valores, para los INSERT y UPDATE.
    private fun valoresDe(usuario: Usuario) = ContentValues().apply {
        put(USU_NOMBRE, usuario.nombre)
        put(USU_CORREO, usuario.correo)
        put(USU_CONTRASENA, usuario.contrasena)
        put(USU_EMPRESA, usuario.empresaCliente)
        put(USU_ROL, usuario.rolId)
    }

    //Ejecuta un SELECT y lo recorre. Para listar, buscar, y buscarPorId.
    private fun consultarUsuarios(sql: String, args: Array<String>?): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerUsuario(cursor))
            }
        }
        return lista
    }

    //Convierte la fila actual del cursor en un usuario.
    private fun leerUsuario(cursor: Cursor) = Usuario(
        nombre = cursor.getString(cursor.getColumnIndexOrThrow(USU_NOMBRE)),
        correo = cursor.getString(cursor.getColumnIndexOrThrow(USU_CORREO)),
        contrasena = cursor.getString(cursor.getColumnIndexOrThrow(USU_CONTRASENA)),
        id = cursor.getLong(cursor.getColumnIndexOrThrow(USU_ID)),
        rolId = cursor.getLong(cursor.getColumnIndexOrThrow(USU_ROL)),
        rolNombre = cursor.getString(cursor.getColumnIndexOrThrow(ROL_NOMBRE)),
        empresaCliente = cursor.getString(cursor.getColumnIndexOrThrow(USU_EMPRESA))
    )
}


data class ClienteConInstalacion(
    val idCliente: Long,
    val razonSocial: String,
    val rut: String,
    val giro: String,
    val contacto: String,
    val telefono: String,
    val correo: String,
    val nombreInstalacion: String,
    val ubicacion: String,
    val comuna: String,
    val region: String
)