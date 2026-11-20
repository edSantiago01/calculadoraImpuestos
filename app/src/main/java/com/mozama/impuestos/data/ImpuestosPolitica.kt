    package com.mozama.impuestos.data

    import com.mozama.impuestos.data.model.ImpuestosResponse
    import com.mozama.impuestos.utils.Constants.Companion.DAYS_TO_UPDATE_TAX
    import java.time.Duration
    import java.time.Instant
    import java.time.LocalDate
    import java.time.ZoneId
    import java.time.format.DateTimeFormatter

    // reglas puras, independientes de las pantallas y de la conexión.
    object ImpuestosPolitica {
        private val formato = DateTimeFormatter.ISO_LOCAL_DATE

        fun fecha(texto: String?): LocalDate? = try {
            texto?.let { LocalDate.parse(it.trim(), formato) }
        } catch (_: java.time.DateTimeException) {
            null
        }

        fun salarioEsperado(hoy: LocalDate): LocalDate = LocalDate.of(hoy.year, 1, 1)
        fun umaEsperada(hoy: LocalDate): LocalDate =
            LocalDate.of(if (hoy.monthValue == 1) hoy.year - 1 else hoy.year, 2, 1)

        fun validos(datos: ImpuestosResponse?, hoy: LocalDate): Boolean {
            //validar los campos presentes en la respuesta del servicio.
            if (datos == null) return false

            if (listOf(datos.uma, datos.smg, datos.zlfn).any {
                    it == null || !it.isFinite() || it <= 0.0
                }) return false

            //  fecha() interpreta yyyy-MM-dd.
            val uma = fecha(datos.umaVigencia) ?: return false
            val salario = fecha(datos.smgVigencia) ?: return false

            return !uma.isAfter(hoy) && !salario.isAfter(hoy)
        }

        fun vencidos(datos: ImpuestosResponse, hoy: LocalDate): Boolean =
            (fecha(datos.umaVigencia) ?: LocalDate.MIN) < umaEsperada(hoy) ||
            (fecha(datos.smgVigencia) ?: LocalDate.MIN) < salarioEsperado(hoy)

        @Suppress("UNUSED_PARAMETER")
        fun consultar(
            datos: ImpuestosResponse?,
            ultimaConsultaExitosa: Long,
            ultimoIntentoConsulta: Long,
            ahora: Instant,
            zona: ZoneId
        ): Boolean {
            val hoy = ahora.atZone(zona).toLocalDate()
            val utilizables = datos?.takeIf { validos(it, hoy) }
            val ahoraMs = ahora.toEpochMilli()

            // si faltan datos válidos, consultar sin esperar
            if (utilizables == null) return true

            // si falta una vigencia esperada, consultar
            if (vencidos(utilizables, hoy)) return true

            // Se conserva el intervalo periódico desde la última consulta exitosa.
            val periodoCumplido =
                ultimaConsultaExitosa <= 0L ||
                        ahoraMs < ultimaConsultaExitosa ||
                        ahoraMs - ultimaConsultaExitosa >=
                        Duration.ofDays(DAYS_TO_UPDATE_TAX).toMillis()

            return periodoCumplido
        }
    }
