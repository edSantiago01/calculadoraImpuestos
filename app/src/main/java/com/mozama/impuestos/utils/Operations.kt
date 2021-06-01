package com.mozama.impuestos.utils

import android.util.Log
import java.text.DecimalFormat
import kotlin.math.absoluteValue

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
            "total" to total,
            "subtotal" to subtotal
        )
    }

    fun calSubtotalRetencionesTotal(total:Double, percentIva:Double, percentIvaRetenido:Double, percentIsrRetenido:Double): Map<String,Double>?{
        var subtotal = total + 1

//        var map /= calcAllRetenciones( subtotal, percentIva, percentIvaRetenido, percentIsrRetenido )
        var map: Map<String, Double>? = null
        var totalCiclo = 0.0
        var diferencia = 0.0
        var flag = 0

        while ( total != totalCiclo  && flag == 0){

            map = calcAllRetenciones( subtotal, percentIva, percentIvaRetenido, percentIsrRetenido )
            totalCiclo = map["total"]!!

            diferencia = total - totalCiclo

            if (diferencia > 0){
                if(diferencia >= 1000000 ) subtotal += 1000000
                else if (diferencia >= 100000 && diferencia < 1000000) subtotal += 100000
                else if (diferencia >= 10000  && diferencia < 100000) subtotal += 10000
                else if (diferencia >= 1000   && diferencia < 10000) subtotal += 1000
                else if (diferencia >= 100    && diferencia < 1000) subtotal += 100
                else if (diferencia >= 10     && diferencia < 100) subtotal += 10
                else if (diferencia >= 1      && diferencia < 10) subtotal += 1
                else if (diferencia >= 0.1    && diferencia < 1) subtotal += 0.1
                else if (diferencia >= 0.01   && diferencia < 0.1) subtotal += 0.01
                else if (diferencia >= 0.001  && diferencia < 0.01) subtotal += 0.001
                else flag = 1

            }else{
                if(diferencia <= -1000000 ) subtotal -= 1000000
                else if (diferencia > -1000000  && diferencia <= -100000) subtotal -= 100000
                else if (diferencia > -100000  && diferencia <= -10000) subtotal -= 10000
                else if (diferencia > -10000   && diferencia <= -1000) subtotal -= 1000
                else if (diferencia > -1000    && diferencia <= -100) subtotal -= 100
                else if (diferencia > -100     && diferencia <= -10) subtotal -= 10
                else if (diferencia > -10      && diferencia <= -1) subtotal -= 1
                else if (diferencia > -1       && diferencia <= -0.1) subtotal -= 0.1
                else if (diferencia > 0.1      && diferencia <= -0.01) subtotal -= 0.01
                else if (diferencia > 0.01     && diferencia <= -0.001) subtotal -= 0.001
                else flag = 1
            }

//            map = calcAllRetenciones(subtotal, percentIva, percentIvaRetenido, percentIsrRetenido)
            totalCiclo = map["total"]!!

            Log.d("TOTAL",total.toString())
            Log.d("SUBTOTAL",subtotal.toString())
            Log.d("TOTAL_C",totalCiclo.toString())
            Log.d("DIFERENCIA",diferencia.toString())
            Log.d("FLAG",flag.toString())
        }
        return map
    }

}