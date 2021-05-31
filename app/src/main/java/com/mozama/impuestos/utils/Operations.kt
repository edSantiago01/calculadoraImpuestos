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

    fun calcValSubtotalTotal(total: Double, percent:Double):Double{
        // subtotal (16 %) = (100/116) * total
        val denominador = (percent * 100) + 100
        val subtotal = (total * 100 ) / denominador
        return subtotal
    }

    fun calcValSubtotalIva(iva:Double, percent: Double):Double{
        val constant = 100 / percent
        val subtotal = iva * constant
        return subtotal
    }
}