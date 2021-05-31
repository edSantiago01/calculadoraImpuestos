package com.mozama.impuestos.utils

import java.text.DecimalFormat

class Operations {

    fun calcValPercent(total: Double, percent: Double): Double {
        return total * percent
    }

    fun round2Dec(valor: Double): String {
        val formato = DecimalFormat("###,###,###,###,###,###,###.00")
        return formato.format(valor)
    }
}