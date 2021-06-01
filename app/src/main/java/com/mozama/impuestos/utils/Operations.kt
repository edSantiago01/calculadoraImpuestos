package com.mozama.impuestos.utils

import java.text.DecimalFormat

class Operations {

    fun calcValPercentTotal(total: Double, percent: Double): Double {
        return total * percent
    }

    fun round2Dec(valor: Double): String {
        val formato = DecimalFormat("###,###,###,###,###,###,##0.00")
        return formato.format(valor)
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
            "total" to total
        )
    }
}