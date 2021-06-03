/*
 * This is the source code of Calculadora de Impuestos v. 1.x.x.
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Edgar Santiago, 2021.
 */
package com.mozama.impuestos.utils


/**
 * Clase con la lógica y operaciones matemáticas para obtener los montos
 */

class Operations {

    fun calcValPercentTotal(total: Double, percent: Double): Double {
        return total * percent
    }

    fun calcValSubtotalTotal(total: Double, percent: Double): Double {
        // subtotal (16 %) = (100/116) * total
        val denominador = (percent * 100) + 100
        return (total * 100) / denominador
    }

    fun calcValSubtotalIva(iva: Double, percent: Double): Double {
        val constant = 100 / (percent * 100)
        return iva * constant
    }

    fun calcAllRetenciones(subtotal:Double, percentIva:Double, percentIvaRetenido:Double, percentIsrRetenido:Double): Map<String,Double>{
        val iva = calcValPercentTotal( subtotal, percentIva )
        val ivaR = calcValPercentTotal( subtotal, percentIvaRetenido )
        val isrR = calcValPercentTotal( subtotal, percentIsrRetenido )
        val total = subtotal + iva - ivaR - isrR

        return mapOf(
            "iva" to iva,
            "ivaR" to ivaR,
            "isrR" to isrR,
            "total" to total,
            "subtotal" to subtotal
        )
    }

    fun calSubtotalRetencionesTotal(total:Double, percentIva:Double, percentIvaRetenido:Double, percentIsrRetenido:Double): Map<String,Double>?{
        var subtotal = total + 1
        var map: Map<String, Double>? = null
        var totalCiclo = 0.0
        var diferencia: Double
        var diferenciaAnterior = 0.0
        var flag = 0
        var dif: Double

        while ( total != totalCiclo  && flag == 0){

            map = calcAllRetenciones( subtotal, percentIva, percentIvaRetenido, percentIsrRetenido )
            totalCiclo = map["total"]!!

            diferencia = total - totalCiclo
            subtotal += diferencia
            dif = diferenciaAnterior + diferencia

            if ( diferencia > 0 && diferencia <= 0.001 ) flag = 1
            else if ( diferencia < 0 && diferencia >= -0.001 ) flag = 1
            if ( dif == 0.0) flag = 1

            diferenciaAnterior = diferencia
        }
        return map
    }
}