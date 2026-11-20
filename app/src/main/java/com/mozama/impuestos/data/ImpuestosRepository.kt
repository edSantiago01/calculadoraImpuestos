package com.mozama.impuestos.data

import com.mozama.impuestos.api.ApiService
import com.mozama.impuestos.data.model.ImpuestosRequest
import com.mozama.impuestos.data.model.ImpuestosResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import java.time.Clock
import java.time.LocalDate
import android.util.Log
import com.mozama.impuestos.BuildConfig

// estado observado por la pantalla; nunca contiene importes de prueba.
data class ImpuestosEstado(
    val datos: ImpuestosResponse? = null,
    val cargando: Boolean = true,
    val pendientes: Boolean = false,
    val mensaje: String? = null
)

/**
 * consume el servicio para obtener la información de SMG, UMA, ZLFN
 */
class ImpuestosRepository(
    private val local: ImpuestosLocal,
    private val api: ApiService,
    private val obtenerToken: () -> String,
    private val reloj: Clock = Clock.systemDefaultZone()
) {
    private val mutex = Mutex()
    private val _estado = MutableStateFlow(ImpuestosEstado())
    val estado = _estado.asStateFlow()

    suspend fun actualizarSiCorresponde() {
        // no encola varias solicitudes al recrear Activity o volver a la app.
        if (!mutex.tryLock()) return
        try {
            val ahora = reloj.instant()
            val hoy = LocalDate.now(reloj)
            val guardados = local.leer()
            val datos = guardados.datos?.takeIf { ImpuestosPolitica.validos(it, hoy) }
            val pendientes = datos != null && ImpuestosPolitica.vencidos(datos, hoy)

            val debeConsultar = ImpuestosPolitica.consultar(
                datos,
                guardados.ultimaConsultaExitosa,
                guardados.ultimoIntentoConsulta,
                ahora,
                reloj.zone
            )

            if (BuildConfig.DEBUG) {
                Log.d(
                    "IMPUESTOS_SYNC",
                    "hoy=$hoy, " +
                            "hayDatosGuardados=${guardados.datos != null}, " +
                            "datosValidos=${datos != null}, " +
                            "ultimaConsultaExitosa=${guardados.ultimaConsultaExitosa}, " +
                            "ultimoIntentoConsulta=${guardados.ultimoIntentoConsulta}, " +
                            "debeConsultar=$debeConsultar"
                )
            }

            _estado.value = ImpuestosEstado(
                datos = datos,
                cargando = debeConsultar,
                pendientes = pendientes,
                mensaje = when {
                    // sin datos, consultar() ahora devuelve true.
                    // UmaFragment mostrará “Obteniendo valores de UMA y salarios…”.
                    datos == null -> null

                    pendientes ->
                        "Valores pendientes de actualización; los cálculos usan los últimos valores guardados."

                    else -> null
                }
            )

            if (!debeConsultar) return

            val token = obtenerToken()
            if (BuildConfig.DEBUG) {
                Log.d(
                    "IMPUESTOS_SYNC",
                    "Token disponible=${token.isNotBlank()}"
                )
            }

            if (token.isBlank()) {
                _estado.value = _estado.value.copy(
                    cargando = false,
                    mensaje = "Falta configurar el token del servicio."
                )
                return
            }

            local.guardarIntento(ahora.toEpochMilli())
            _estado.value = _estado.value.copy(cargando = true)

            val response = api.getImpuestos(ImpuestosRequest(token))
            if (!response.isSuccessful) {
                error("El servicio respondió HTTP ${response.code()}.")
            }

            val nuevos = response.body()
            if (BuildConfig.DEBUG) {
                Log.d(
                    "IMPUESTOS_SYNC",
                    "HTTP=${response.code()}, " +
                            "hoy=${LocalDate.now(reloj)}, " +
                            "uma=${nuevos?.uma}, " +
                            "umaVigencia=${nuevos?.umaVigencia}, " +
                            "smg=${nuevos?.smg}, " +
                            "zlfn=${nuevos?.zlfn}, " +
                            "smgVigencia=${nuevos?.smgVigencia}"
                )
            }
            // PHP puede devolver status/message con HTTP 200: no sustituir la caché.
            check(ImpuestosPolitica.validos(nuevos, LocalDate.now(reloj))) {
                "El servicio devolvió un error, campos incompletos o vigencias futuras."
            }

            val comprobados = requireNotNull(nuevos)
            // No retroceder a una vigencia anterior por una respuesta del servidor.
            if (datos != null) {
                check(ImpuestosPolitica.fecha(comprobados.umaVigencia)!! >=
                    ImpuestosPolitica.fecha(datos.umaVigencia)!! &&
                    ImpuestosPolitica.fecha(comprobados.smgVigencia)!! >=
                    ImpuestosPolitica.fecha(datos.smgVigencia)!!) {
                    "El servicio devolvió vigencias anteriores a las guardadas."
                }
            }

            local.guardar(comprobados, reloj.millis())
            if (BuildConfig.DEBUG) {
                Log.d("IMPUESTOS_SYNC", "Valores guardados correctamente en DataStore.")
            }

            val siguenPendientes = ImpuestosPolitica.vencidos(comprobados, LocalDate.now(reloj))
            _estado.value = ImpuestosEstado(comprobados, false, siguenPendientes,
                if (siguenPendientes) "El servidor aún no tiene las nuevas vigencias. Se conservan los últimos valores disponibles."
                else null)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    "IMPUESTOS_SYNC",
                    "Error al consultar, interpretar, validar o guardar los impuestos.",
                    e
                )
            }

            // un fallo de red, JSON o disco no borra valores ya disponibles.
            _estado.value = _estado.value.copy(
                cargando = false,
                mensaje = if (_estado.value.datos != null) {
                    "Validar conexión a internet para recibir actualizaciones"
                } else {
                    "No hay valores guardados. Se necesita conexión a internet para obtenerlos."
                }
            )
        } finally {
            _estado.value = _estado.value.copy(cargando = false)
            mutex.unlock()
        }
    }
}
