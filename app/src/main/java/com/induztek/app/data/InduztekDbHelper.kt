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
import com.induztek.app.utils.SeguridadUtils

class InduztekDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_DB, null, VERSION_DB) {

    companion object {
        private const val NOMBRE_DB = "induztek.db"
        private const val VERSION_DB = 7 // Subimos la versión para forzar actualización de esquema

        //----------------- TABLA ROLES -----------------//
        private const val TABLA_ROLES = "roles"
        private const val ROL_ID = "id_rol"
        private const val ROL_NOMBRE = "nombre_rol"

        //----------------- TABLA EMPRESAS CLIENTES -----------------//
        private const val TABLA_EMPRESAS_CLIENTES = "empresas_clientes"
        private const val EMP_ID = "id_empresa"
        private const val EMP_RAZON_SOCIAL = "razon_social"
        private const val EMP_RUT = "rut"
        private const val EMP_GIRO = "giro"
        private const val EMP_CONTACTO = "contacto"
        private const val EMP_TELEFONO = "telefono"
        private const val EMP_CORREO = "correo"

        //----------------- TABLA USUARIOS -----------------//
        private const val TABLA_USUARIOS = "usuarios"
        private const val USU_ID = "id_usuario"
        private const val USU_NOMBRE = "nombre"
        private const val USU_CORREO = "correo"
        private const val USU_CONTRASENA = "contrasena"
        private const val USU_ROL = "id_rol"
        private const val USU_TIPO_PREGUNTA = "tipo_pregunta_clave"
        private const val USU_RESPUESTA_PREGUNTA = "respuesta_pregunta_clave"
        private const val USU_FOTO_PERFIL = "foto_perfil" // Nueva columna para persistencia
        private const val USU_EMPRESA_ID_FK = "id_empresa_fk"


        //----------------- TABLA INSTALACIONES -----------------//
        private const val TABLA_INSTALACIONES = "instalaciones"
        private const val INST_ID = "id_instalacion"
        private const val INST_NOMBRE = "nombre_instalacion"
        private const val INST_UBICACION = "ubicacion"
        private const val INST_COMUNA = "comuna"
        private const val INST_REGION = "region"
        private const val INST_OBS = "observaciones_acceso"
        private const val INST_EMPRESA_ID_FK = "id_empresa_fk"

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

        //----------------- CREACION TABLAS -----------------//
        private const val SQL_CREAR_ROLES =
            "CREATE TABLE $TABLA_ROLES (" +
                    "$ROL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$ROL_NOMBRE TEXT NOT NULL UNIQUE)"

        private const val SQL_CREAR_EMPRESAS =
            "CREATE TABLE $TABLA_EMPRESAS_CLIENTES (" +
                    "$EMP_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$EMP_RAZON_SOCIAL TEXT NOT NULL, " +
                    "$EMP_RUT TEXT NOT NULL UNIQUE, " +
                    "$EMP_GIRO TEXT, " +
                    "$EMP_CONTACTO TEXT, " +
                    "$EMP_TELEFONO TEXT, " +
                    "$EMP_CORREO TEXT)"

        private const val SQL_CREAR_USUARIOS =
            "CREATE TABLE $TABLA_USUARIOS (" +
                    "$USU_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$USU_NOMBRE TEXT NOT NULL, " +
                    "$USU_CORREO TEXT NOT NULL UNIQUE, " +
                    "$USU_CONTRASENA TEXT NOT NULL, " +
                    "$USU_TIPO_PREGUNTA INTEGER, " +
                    "$USU_RESPUESTA_PREGUNTA TEXT, " +
                    "$USU_FOTO_PERFIL TEXT, " +
                    "$USU_EMPRESA_ID_FK INTEGER, " +
                    "$USU_ROL INTEGER NOT NULL REFERENCES $TABLA_ROLES($ROL_ID), " +
                    "FOREIGN KEY ($USU_EMPRESA_ID_FK) REFERENCES $TABLA_EMPRESAS_CLIENTES($EMP_ID) ON DELETE SET NULL)"

        private const val SQL_CREAR_INSTALACIONES =
            "CREATE TABLE $TABLA_INSTALACIONES (" +
                    "$INST_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$INST_NOMBRE TEXT NOT NULL, " +
                    "$INST_UBICACION TEXT, " +
                    "$INST_COMUNA TEXT, " +
                    "$INST_REGION TEXT, " +
                    "$INST_OBS TEXT, " +
                    "$INST_EMPRESA_ID_FK INTEGER NOT NULL REFERENCES $TABLA_EMPRESAS_CLIENTES($EMP_ID) ON DELETE CASCADE)"

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

        //----------------- PRECARGAR TABLAS -----------------//
        private const val SQL_PRECARGAR_ROLES =
            "INSERT INTO $TABLA_ROLES ($ROL_ID, $ROL_NOMBRE) VALUES " +
                    "(1, 'Tecnico de Terreno'), " +
                    "(2, 'Ingeniero de Pruebas'), " +
                    "(3, 'Jefe Técnico'), " +
                    "(4, 'Representante de Empresa')"

        private const val SQL_PRECARGAR_EMPRESAS =
            "INSERT INTO $TABLA_EMPRESAS_CLIENTES ($EMP_ID, $EMP_RAZON_SOCIAL, $EMP_RUT, $EMP_GIRO, $EMP_CONTACTO, $EMP_TELEFONO, $EMP_CORREO) VALUES " +
                    "(1, 'Envasadora del Pacífico SpA', '76.543.210-9', 'Industrial / Envasado', 'Carlos Soto', '+56912345678', 'contacto@envasadora.ejemplo')"

        private const val SQL_PRECARGAR_USUARIOS =
            "INSERT INTO $TABLA_USUARIOS ($USU_NOMBRE, $USU_CORREO, $USU_CONTRASENA, $USU_EMPRESA_ID_FK, $USU_ROL, $USU_TIPO_PREGUNTA, $USU_RESPUESTA_PREGUNTA) VALUES " +
                    "('Matías Godoy Herrera', 'mgodoy@induztek.ejemplo', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225', NULL, 1, 1, 'toby'), " +
                    "('Andrea Contreras Silva', 'acontreras@induztek.ejemplo', 'bfb032d1f73602f314810c9c7f212f86a9782559595a89ee1d56e18f259b360a', NULL, 2, 2, 'los prisioneros'), " +
                    "('Ricardo Espinoza Lagos', 'respinoza@induztek.ejemplo', 'a768d60ef4fb3d73516f48105d10d185ef35bfae1bc98a58f4c39999496660fb', NULL, 3, 3, 'azul'), " +
                    "('Marcela Fuentes Rojas', 'mfuentes@envpacifico.ejemplo', 'dfb97a760ef164c48f219d3f1146747127e5eb562efee76dbfc42d93d14467c6', 1, 4, 1, 'cachupin')"

        private const val SQL_PRECARGAR_INSTALACIONES =
            "INSERT INTO $TABLA_INSTALACIONES ($INST_NOMBRE, $INST_UBICACION, $INST_COMUNA, $INST_REGION, $INST_OBS, $INST_EMPRESA_ID_FK) VALUES " +
                    "('Planta Lampa', 'Ruta 5 Norte Km 28', 'Lampa', 'Metropolitana', 'Requiere inducción previa', 1)"

        private const val SQL_PRECARGAR_EQUIPOS =
            "INSERT INTO $TABLA_EQUIPOS ($EQ_CODIGO, $EQ_TIPO, $EQ_INST_ID) VALUES " +
                    "('TR-001', 'Transformador de Potencia', 1), " +
                    "('RL-502', 'Relé de Protección', 1), " +
                    "('CB-103', 'Celda de M.T.', 1)"

        private const val SQL_SELECT_USUARIOS =
            "SELECT u.*, r.$ROL_NOMBRE AS $ROL_NOMBRE, e.$EMP_RAZON_SOCIAL AS nombre_empresa " +
                    "FROM $TABLA_USUARIOS u " +
                    "INNER JOIN $TABLA_ROLES r ON u.$USU_ROL = r.$ROL_ID " +
                    "LEFT JOIN $TABLA_EMPRESAS_CLIENTES e ON u.$USU_EMPRESA_ID_FK = e.$EMP_ID"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_EMPRESAS)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_CREAR_INSTALACIONES)
        db.execSQL(SQL_CREAR_EQUIPOS)
        db.execSQL(SQL_CREAR_PROTOCOLOS)

        db.execSQL(SQL_PRECARGAR_ROLES)
        db.execSQL(SQL_PRECARGAR_EMPRESAS)
        db.execSQL(SQL_PRECARGAR_USUARIOS)
        db.execSQL(SQL_PRECARGAR_INSTALACIONES)
        db.execSQL(SQL_PRECARGAR_EQUIPOS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_PROTOCOLOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_EQUIPOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_INSTALACIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_EMPRESAS_CLIENTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ROLES")
        onCreate(db)
    }

    //----------------- USUARIOS & FOTO PERFIL -----------------//
    fun actualizarFotoPerfilUsuario(idUsuario: Long, rutaFoto: String): Boolean {
        val valores = ContentValues().apply {
            put(USU_FOTO_PERFIL, rutaFoto)
        }
        val filas = writableDatabase.update(TABLA_USUARIOS, valores, "$USU_ID = ?", arrayOf(idUsuario.toString()))
        return filas > 0
    }

    fun insertarUsuario(
        nombre: String,
        correo: String,
        contrasena: String,
        tipoPregunta: Int,
        respuestaPregunta: String,
        rolId: Int,
        empresaId: Long? = null // Es opcional, por defecto es null si no se envía
    ): Boolean {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put(USU_NOMBRE, nombre) // Usa tus constantes como USU_NOMBRE
            put(USU_CORREO, correo)
            put(USU_CONTRASENA, contrasena)
            put(USU_TIPO_PREGUNTA, tipoPregunta)
            put(USU_RESPUESTA_PREGUNTA, respuestaPregunta.trim().lowercase())
            put(USU_ROL, rolId)

            // Por si el usuario que se registre sea un Representante de Empresa
            if (empresaId != null) {
                put(USU_EMPRESA_ID_FK, empresaId)
            } else {
                putNull(USU_EMPRESA_ID_FK)
            }
        }

        val resultado = db.insert(TABLA_USUARIOS, null, valores)
        db.close()

        return resultado != -1L
    }

    fun autenticarUsuario(correo: String, contrasenaPlana: String): Usuario? {
        val claveHash = SeguridadUtils.hashSHA256(contrasenaPlana.trim())
        val sql = "$SQL_SELECT_USUARIOS WHERE u.$USU_CORREO = ? AND u.$USU_CONTRASENA = ?"
        return consultarUsuarios(sql, arrayOf(correo.trim(), claveHash)).firstOrNull()
    }

    fun listarUsuarios(): List<Usuario> =
        consultarUsuarios("$SQL_SELECT_USUARIOS ORDER BY u.$USU_NOMBRE", null)

    fun buscarPorId(id: Long): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIOS WHERE u.$USU_ID = ?", arrayOf(id.toString()))
            .firstOrNull()

    fun buscarPorCorreo(correo: String): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIOS WHERE u.$USU_CORREO = ?", arrayOf(correo.trim()))
            .firstOrNull()

    fun actualizarContrasena(idUsuario: Long, nuevaContrasenaPlana: String): Int {
        val claveHash = SeguridadUtils.hashSHA256(nuevaContrasenaPlana.trim())
        val valores = ContentValues().apply {
            put(USU_CONTRASENA, claveHash)
        }
        return writableDatabase.update(
            TABLA_USUARIOS, valores, "$USU_ID = ?", arrayOf(idUsuario.toString())
        )
    }

    //----------------- EMPRESAS CLIENTES -----------------//
    fun insertarEmpresaConInstalacion(
        razonSocial: String, rut: String, giro: String, contacto: String, telefono: String, correo: String,
        nombreInstalacion: String, ubicacion: String, comuna: String, region: String, observaciones: String
    ): Long //Diras, porque un long? porque retornara la empresaId para la siguiente pantalla, si falla es un -1L
    {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val valoresEmpresa = ContentValues().apply {
                put(EMP_RAZON_SOCIAL, razonSocial)
                put(EMP_RUT, rut)
                put(EMP_GIRO, giro)
                put(EMP_CONTACTO, contacto)
                put(EMP_TELEFONO, telefono)
                put(EMP_CORREO, correo)
            }
            val idEmpresaGenerada = db.insert(TABLA_EMPRESAS_CLIENTES, null, valoresEmpresa)
            if (idEmpresaGenerada == -1L) return -1L

            val valoresInstalacion = ContentValues().apply {
                put(INST_NOMBRE, nombreInstalacion)
                put(INST_UBICACION, ubicacion)
                put(INST_COMUNA, comuna)
                put(INST_REGION, region)
                put(INST_OBS, observaciones)
                put(INST_EMPRESA_ID_FK, idEmpresaGenerada)
            }
            val idInstalacionGenerada = db.insert(TABLA_INSTALACIONES, null, valoresInstalacion)
            if (idInstalacionGenerada == -1L) return -1L

            db.setTransactionSuccessful()
            return idEmpresaGenerada
        } catch (e: Exception) {
            return -1L
        } finally {
            db.endTransaction()
        }
    }

    fun listarEmpresas(): List<EmpresaConInstalacion> {
        val lista = mutableListOf<EmpresaConInstalacion>()
        val sql = """
            SELECT e.*, 
                   i.$INST_NOMBRE, 
                   i.$INST_UBICACION, 
                   i.$INST_COMUNA, 
                   i.$INST_REGION 
            FROM $TABLA_EMPRESAS_CLIENTES e 
            INNER JOIN $TABLA_INSTALACIONES i ON e.$EMP_ID = i.$INST_EMPRESA_ID_FK 
            ORDER BY e.$EMP_RAZON_SOCIAL
        """.trimIndent()

        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    EmpresaConInstalacion(
                        idEmpresa = cursor.getLong(cursor.getColumnIndexOrThrow(EMP_ID)),
                        razonSocial = cursor.getString(cursor.getColumnIndexOrThrow(EMP_RAZON_SOCIAL)),
                        rut = cursor.getString(cursor.getColumnIndexOrThrow(EMP_RUT)),
                        giro = cursor.getString(cursor.getColumnIndexOrThrow(EMP_GIRO)) ?: "",
                        contacto = cursor.getString(cursor.getColumnIndexOrThrow(EMP_CONTACTO)) ?: "",
                        telefono = cursor.getString(cursor.getColumnIndexOrThrow(EMP_TELEFONO)) ?: "",
                        correo = cursor.getString(cursor.getColumnIndexOrThrow(EMP_CORREO)) ?: "",
                        nombreInstalacion = cursor.getString(cursor.getColumnIndexOrThrow(INST_NOMBRE)),
                        ubicacion = cursor.getString(cursor.getColumnIndexOrThrow(INST_UBICACION)) ?: "",
                        comuna = cursor.getString(cursor.getColumnIndexOrThrow(INST_COMUNA)) ?: "",
                        region = cursor.getString(cursor.getColumnIndexOrThrow(INST_REGION)) ?: ""
                    )
                )
            }
        }
        return lista
    }
    fun eliminarEmpresa(id: Long): Int {
        return writableDatabase.delete(TABLA_EMPRESAS_CLIENTES, "$EMP_ID = ?", arrayOf(id.toString()))
    }

    //----------------- EQUIPOS Y PROTOCOLOS -----------------//
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

    private fun consultarUsuarios(sql: String, args: Array<String>?): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerUsuario(cursor))
            }
        }
        return lista
    }

    private fun leerUsuario(cursor: Cursor): Usuario {
        val idxFoto = cursor.getColumnIndex(USU_FOTO_PERFIL)
        val fotoPath = if (idxFoto != -1 && !cursor.isNull(idxFoto)) cursor.getString(idxFoto) else null

        val idxEmpresaId = cursor.getColumnIndex(USU_EMPRESA_ID_FK)
        val empresaId = if (idxEmpresaId != -1 && !cursor.isNull(idxEmpresaId)) cursor.getLong(idxEmpresaId) else null

        val idxEmpresaNombre = cursor.getColumnIndex("nombre_empresa")
        val empresaNombre = if (idxEmpresaNombre != -1 && !cursor.isNull(idxEmpresaNombre)) cursor.getString(idxEmpresaNombre) else null

        val idxTipoPregunta = cursor.getColumnIndex(USU_TIPO_PREGUNTA)
        val tipoPregunta = if (idxTipoPregunta != -1 && !cursor.isNull(idxTipoPregunta)) cursor.getInt(idxTipoPregunta) else 0

        val idxRespPregunta = cursor.getColumnIndex(USU_RESPUESTA_PREGUNTA)
        val respPregunta = if (idxRespPregunta != -1 && !cursor.isNull(idxRespPregunta)) cursor.getString(idxRespPregunta) ?: "" else ""

        return Usuario(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(USU_ID)),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(USU_NOMBRE)),
            correo = cursor.getString(cursor.getColumnIndexOrThrow(USU_CORREO)),
            contrasena = cursor.getString(cursor.getColumnIndexOrThrow(USU_CONTRASENA)),
            rolId = cursor.getLong(cursor.getColumnIndexOrThrow(USU_ROL)),
            rolNombre = cursor.getString(cursor.getColumnIndexOrThrow(ROL_NOMBRE)),
            tipoPreguntaClave = tipoPregunta,
            respuestaPreguntaClave = respPregunta,
            empresaId = empresaId,
            empresaNombre = empresaNombre,
            fotoPerfilPath = fotoPath
        )
    }
}

// Data class para listar empresas junto con sus instalaciones
data class EmpresaConInstalacion(
    val idEmpresa: Long,
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

