package com.mozama.impuestos.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.emptyPreferences
import com.google.gson.Gson
import com.mozama.impuestos.data.model.ImpuestosResponse
import kotlinx.coroutines.flow.first

// NUEVO: una sola instancia de DataStore por archivo, a nivel superior.
private val Context.impuestosDataStore by preferencesDataStore(
    name = "impuestos_local",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

data class ImpuestosGuardados(
    val datos: ImpuestosResponse?,
    val ultimaConsultaExitosa: Long,
    val ultimoIntentoConsulta: Long
)

class ImpuestosLocal(context: Context) {
    private val store = context.applicationContext.impuestosDataStore
    private val gson = Gson()
    private val datosKey = stringPreferencesKey("datos")
    private val exitoKey = longPreferencesKey("ultimaConsultaExitosa")
    private val intentoKey = longPreferencesKey("ultimoIntentoConsulta")

    suspend fun leer(): ImpuestosGuardados {
        val prefs = store.data.first()
        val datos = try {
            prefs[datosKey]?.let { gson.fromJson(it, ImpuestosResponse::class.java) }
        } catch (_: com.google.gson.JsonParseException) { null }
        return ImpuestosGuardados(datos, prefs[exitoKey] ?: 0L, prefs[intentoKey] ?: 0L)
    }

    suspend fun guardarIntento(instante: Long) {
        store.edit { it[intentoKey] = instante }
    }

    suspend fun guardar(datos: ImpuestosResponse, instante: Long) {
        // AJUSTE: guardar valores y fecha de éxito en una sola operación atómica.
        store.edit {
            it[datosKey] = gson.toJson(datos)
            it[exitoKey] = instante
        }
    }
}
