package com.induztek.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.induztek.app.model.Equipo
import com.induztek.app.model.ProtocoloPrueba
import com.induztek.app.model.Rol
import com.induztek.app.model.Usuario

class InduztekDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_DB, null, VERSION_DB) {

    companion object {
        private const val NOMBRE_DB = "induztek.db"
        private const val VERSION_DB = 4

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
        private const val INST_CLIENTE_ID = "id_cliente_fk"

        //----------------- TABLA EQUIPOS -----------------//
        private const val TABLA_EQUIPOS = "equipos"
        private const val EQ_ID = "id_equipo"
        private const val EQ_CODIGO = "codigo_equipo"
        private const val EQ_TIPO = "tipo_equipo"
        private const val EQ_INST_ID = "id_instalacion_fk"

        //----------------- TABLA PROTOCOLOS / PRUEBAS -----------------//
        private const val TABLA_PROTOCOLOS = "protocolos_prueba"
        private const val PROT_ID = "id_protocolo"
        private const val PROT_EQ_ID = "id_equipo_fk"
        private const val PROT_USU_ID = "id_usuario_tecnico_fk"
        private const val PROT_FECHA = "fecha_hora"
        private const val PROT_TIPO = "tipo_prueba"
        private const val PROT_MEDICION = "valor_medicion"
        private const val PROT_RESULTADO = "resultado"
        private const val PROT_ESTADO = "estado_protocolo"
        private const val PROT_OBS = "observaciones"
        private const val PROT_FOTO_URI = "foto_uri"
        private const val PROT_LATITUD = "latitud"
        private const val PROT_LONGITUD = "longitud"

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
                    "$INST_CLIENTE_ID INTEGER NOT NULL REFERENCES $TABLA_CLIENTES($CLI_ID) ON DELETE CASCADE)"

        private const val SQL_CREAR_EQUIPOS =
            "CREATE TABLE $TABLA_EQUIPOS (" +
                    "$EQ_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$EQ_CODIGO TEXT NOT NULL UNIQUE, " +
                    "$EQ_TIPO TEXT NOT NULL, " +
                    "$EQ_INST_ID INTEGER NOT NULL REFERENCES $TABLA_INSTALACIONES($INST_ID) ON DELETE CASCADE)"

        private const val SQL_CREAR_PROTOCOLOS =
            "CREATE TABLE $TABLA_PROTOCOLOS (" +
                    "$PROT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$PROT_EQ_ID INTEGER NOT NULL REFERENCES $TABLA_EQUIPOS($EQ_ID) ON DELETE CASCADE, " +
                    "$PROT_USU_ID INTEGER NOT NULL REFERENCES $TABLA_USUARIOS($USU_ID), " +
                    "$PROT_FECHA TEXT NOT NULL, " +
                    "$PROT_TIPO TEXT NOT NULL, " +
                    "$PROT_MEDICION TEXT NOT NULL, " +
                    "$PROT_RESULTADO TEXT NOT NULL, " +
                    "$PROT_ESTADO TEXT NOT NULL DEFAULT 'REGISTRADO_TERRENO', " +
                    "$PROT_OBS TEXT, " +
                    "$PROT_FOTO_URI TEXT, " +
                    "$PROT_LATITUD REAL, " +
                    "$PROT_LONGITUD REAL)"

        //============================================================================================//

        //----------------- PRECARGAR LAS TABLAS -----------------//
        private const val SQL_PRECARGAR_ROLES =
            "INSERT INTO $TABLA_ROLES ($ROL_ID, $ROL_NOMBRE) VALUES " +
                    "(1, 'Tecnico de Terreno'), " +
                    "(2, 'Ingeniero de Pruebas'), " +
                    "(3, 'Jefe Técnico'), " +
                    "(4, 'Usuario Cliente')"

        private const val SQL_PRECARGAR_USUARIOS =
            "INSERT INTO $TABLA_USUARIOS ($USU_NOMBRE, $USU_CORREO, $USU_CONTRASENA, $USU_EMPRESA, $USU_ROL) VALUES " +
                    "('Matías Godoy Herrera', 'mgodoy@induztek.ejemplo', '123456789', NULL, 1), " +
                    "('Andrea Contreras Silva', 'acontreras@induztek.ejemplo', '987654321', NULL, 2), " +
                    "('Ricardo Espinoza Lagos', 'respinoza@induztek.ejemplo', '135792468', NULL, 3), " +
                    "('Marcela Fuentes Rojas', 'mfuentes@envpacifico.ejemplo', '246813579', 'Envasadora del Pacífico', 4)"

        private const val SQL_PRECARGAR_CLIENTES =
            "INSERT INTO $TABLA_CLIENTES ($CLI_ID, $CLI_RAZON_SOCIAL, $CLI_RUT, $CLI_GIRO, $CLI_CONTACTO, $CLI_TELEFONO, $CLI_CORREO) VALUES " +
                    "(1, 'Envasadora del Pacífico SpA', '76.543.210-9', 'Industrial / Envasado', 'Carlos Soto', '+56912345678', 'contacto@envasadora.ejemplo')"

        private const val SQL_PRECARGAR_INSTALACIONES =
            "INSERT INTO $TABLA_INSTALACIONES ($INST_NOMBRE, $INST_UBICACION, $INST_COMUNA, $INST_REGION, $INST_OBS, $INST_CLIENTE_ID) VALUES " +
                    "('Planta Lampa', 'Ruta 5 Norte Km 28', 'Lampa', 'Metropolitana', 'Requiere inducción previa', 1)"

        private const val SQL_PRECARGAR_EQUIPOS =
            "INSERT INTO $TABLA_EQUIPOS ($EQ_CODIGO, $EQ_TIPO, $EQ_INST_ID) VALUES " +
                    "('TR-001', 'Transformador de Potencia', 1), " +
                    "('RL-502', 'Relé de Protección', 1), " +
                    "('CB-103', 'Celda de M.T.', 1)"

        private const val SQL_SELECT_USUARIOS =
            "SELECT u.*, r.$ROL_NOMBRE AS $ROL_NOMBRE " +
                    "FROM $TABLA_USUARIOS u " +
                    "INNER JOIN $TABLA_ROLES r ON u.$USU_ROL = r.$ROL_ID"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_CREAR_CLIENTES)
        db.execSQL(SQL_CREAR_INSTALACIONES)
        db.execSQL(SQL_CREAR_EQUIPOS)
        db.execSQL(SQL_CREAR_PROTOCOLOS)

        db.execSQL(SQL_PRECARGAR_ROLES)
        db.execSQL(SQL_PRECARGAR_USUARIOS)
        db.execSQL(SQL_PRECARGAR_CLIENTES)
        db.execSQL(SQL_PRECARGAR_INSTALACIONES)
        db.execSQL(SQL_PRECARGAR_EQUIPOS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_PROTOCOLOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_EQUIPOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_INSTALACIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_CLIENTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ROLES")
        onCreate(db)
    }

    //==================================== FUNCIONES ======================================//

    //----------------- ROLES -----------------//
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
    fun insertarUsuario(usuario: Usuario): Long =
        writableDatabase.insert(TABLA_USUARIOS, null, valoresDe(usuario))

    fun listarUsuarios(): List<Usuario> =
        consultarUsuarios("$SQL_SELECT_USUARIOS ORDER BY u.$USU_NOMBRE", null)

    fun buscarUsuarios(texto: String): List<Usuario> {
        val patron = "%$texto%"
        return consultarUsuarios(
            "$SQL_SELECT_USUARIOS WHERE u.$USU_NOMBRE LIKE ? OR u.$USU_CORREO LIKE ? " +
                    "ORDER BY u.$USU_NOMBRE",
            arrayOf(patron, patron)
        )
    }

    fun buscarPorId(id: Long): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIOS WHERE u.$USU_ID = ?", arrayOf(id.toString()))
            .firstOrNull()

    fun buscarPorCorreo(correo: String): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIOS WHERE u.$USU_CORREO = ?", arrayOf(correo))
            .firstOrNull()

    fun actualizarContrasena(idUsuario: Long, nuevaContrasena: String): Int {
        val valores = ContentValues().apply {
            put(USU_CONTRASENA, nuevaContrasena)
        }
        return writableDatabase.update(
            TABLA_USUARIOS, valores, "$USU_ID = ?", arrayOf(idUsuario.toString())
        )
    }

    fun actualizarUsuario(usuario: Usuario): Int =
        writableDatabase.update(
            TABLA_USUARIOS, valoresDe(usuario), "$USU_ID = ?", arrayOf(usuario.id.toString())
        )

    fun eliminarUsuario(id: Long): Int =
        writableDatabase.delete(TABLA_USUARIOS, "$USU_ID = ?", arrayOf(id.toString()))

    //----------------- CLIENTES E INSTALACIONES -----------------//
    fun insertarClienteConInstalacion(
        razonSocial: String, rut: String, giro: String, contacto: String, telefono: String, correo: String,
        nombreInstalacion: String, ubicacion: String, comuna: String, region: String, observaciones: String
    ): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
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

            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }

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
        return writableDatabase.delete(TABLA_CLIENTES, "$CLI_ID = ?", arrayOf(id.toString()))
    }

    //----------------- EQUIPOS Y PROTOCOLOS DE PRUEBA -----------------//
    fun listarEquipos(): List<Equipo> {
        val lista = mutableListOf<Equipo>()
        val sql = "SELECT $EQ_ID, $EQ_CODIGO, $EQ_TIPO, $EQ_INST_ID FROM $TABLA_EQUIPOS ORDER BY $EQ_CODIGO"
        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Equipo(
                        idEquipo = cursor.getLong(cursor.getColumnIndexOrThrow(EQ_ID)),
                        codigoEquipo = cursor.getString(cursor.getColumnIndexOrThrow(EQ_CODIGO)),
                        tipoEquipo = cursor.getString(cursor.getColumnIndexOrThrow(EQ_TIPO)),
                        idInstalacion = cursor.getLong(cursor.getColumnIndexOrThrow(EQ_INST_ID))
                    )
                )
            }
        }
        return lista
    }

    fun insertarProtocolo(protocolo: ProtocoloPrueba): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(PROT_EQ_ID, protocolo.idEquipo)
            put(PROT_USU_ID, protocolo.idUsuarioTecnico)
            put(PROT_FECHA, protocolo.fechaHora)
            put(PROT_TIPO, protocolo.tipoPrueba)
            put(PROT_MEDICION, protocolo.valorMedicion)
            put(PROT_RESULTADO, protocolo.resultado)
            put(PROT_ESTADO, protocolo.estadoProtocolo)
            put(PROT_OBS, protocolo.observaciones)
            put(PROT_FOTO_URI, protocolo.fotoUri)
            put(PROT_LATITUD, protocolo.latitud)
            put(PROT_LONGITUD, protocolo.longitud)
        }
        val idGenerado = db.insert(TABLA_PROTOCOLOS, null, valores)
        return idGenerado != -1L
    }

    fun obtenerProtocolosPorEquipo(codigoEquipo: String): List<ProtocoloPrueba> {
        val lista = mutableListOf<ProtocoloPrueba>()
        val patron = "%$codigoEquipo%"
        val sql = """
            SELECT p.$PROT_ID, p.$PROT_EQ_ID, e.$EQ_CODIGO, e.$EQ_TIPO,
                   p.$PROT_USU_ID, u.$USU_NOMBRE, p.$PROT_FECHA, p.$PROT_TIPO,
                   p.$PROT_MEDICION, p.$PROT_RESULTADO, p.$PROT_ESTADO, p.$PROT_OBS,
                   p.$PROT_FOTO_URI, p.$PROT_LATITUD, p.$PROT_LONGITUD
            FROM $TABLA_PROTOCOLOS p
            INNER JOIN $TABLA_EQUIPOS e ON p.$PROT_EQ_ID = e.$EQ_ID
            INNER JOIN $TABLA_USUARIOS u ON p.$PROT_USU_ID = u.$USU_ID
            WHERE e.$EQ_CODIGO LIKE ?
            ORDER BY p.$PROT_ID DESC
        """.trimIndent()

        readableDatabase.rawQuery(sql, arrayOf(patron)).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    ProtocoloPrueba(
                        idProtocolo = cursor.getLong(cursor.getColumnIndexOrThrow(PROT_ID)),
                        idEquipo = cursor.getLong(cursor.getColumnIndexOrThrow(PROT_EQ_ID)),
                        codigoEquipo = cursor.getString(cursor.getColumnIndexOrThrow(EQ_CODIGO)),
                        tipoEquipo = cursor.getString(cursor.getColumnIndexOrThrow(EQ_TIPO)),
                        idUsuarioTecnico = cursor.getLong(cursor.getColumnIndexOrThrow(PROT_USU_ID)),
                        nombreTecnico = cursor.getString(cursor.getColumnIndexOrThrow(USU_NOMBRE)),
                        fechaHora = cursor.getString(cursor.getColumnIndexOrThrow(PROT_FECHA)),
                        tipoPrueba = cursor.getString(cursor.getColumnIndexOrThrow(PROT_TIPO)),
                        valorMedicion = cursor.getString(cursor.getColumnIndexOrThrow(PROT_MEDICION)),
                        resultado = cursor.getString(cursor.getColumnIndexOrThrow(PROT_RESULTADO)),
                        estadoProtocolo = cursor.getString(cursor.getColumnIndexOrThrow(PROT_ESTADO)),
                        observaciones = cursor.getString(cursor.getColumnIndexOrThrow(PROT_OBS)) ?: "",
                        fotoUri = cursor.getString(cursor.getColumnIndexOrThrow(PROT_FOTO_URI)),
                        latitud = if (cursor.isNull(cursor.getColumnIndexOrThrow(PROT_LATITUD))) null else cursor.getDouble(cursor.getColumnIndexOrThrow(PROT_LATITUD)),
                        longitud = if (cursor.isNull(cursor.getColumnIndexOrThrow(PROT_LONGITUD))) null else cursor.getDouble(cursor.getColumnIndexOrThrow(PROT_LONGITUD))
                    )
                )
            }
        }
        return lista
    }

    //----------------- AYUDANTES -----------------//
    private fun valoresDe(usuario: Usuario) = ContentValues().apply {
        put(USU_NOMBRE, usuario.nombre)
        put(USU_CORREO, usuario.correo)
        put(USU_CONTRASENA, usuario.contrasena)
        put(USU_EMPRESA, usuario.empresaCliente)
        put(USU_ROL, usuario.rolId)
    }

    private fun consultarUsuarios(sql: String, args: Array<String>?): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerUsuario(cursor))
            }
        }
        return lista
    }

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