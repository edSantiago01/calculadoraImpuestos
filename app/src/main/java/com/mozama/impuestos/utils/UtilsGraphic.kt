/*
 * This is the source code of Calculadora de Impuestos v. 1.x.x.
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Edgar Santiago, 2021.
 */
package com.mozama.impuestos.utils

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.material.textfield.TextInputLayout
import java.text.DecimalFormat

/**
 * Clase con funciones para modificar elementos visuales
 */
class UtilsGraphic {

    fun setItemSpin(context: Context, array:Int, spin: Spinner){
        ArrayAdapter.createFromResource(
            context,
            array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spin.adapter = adapter
        }
    }

    fun getIvaSpinner(spin:Spinner): Double{
        val position = spin.selectedItemPosition

        var porcentaje = 0.00
        if ( position == 0 ) porcentaje = 0.16
        else if ( position == 1 ) porcentaje = 0.08

        return porcentaje
    }



    fun hideCedular(fieldPercentCedular:TextInputLayout, fieldCedular:TextInputLayout){  //is active but dont use
        fieldPercentCedular.visibility = View.GONE
        fieldCedular.visibility = View.GONE
    }

    fun showCedular(fieldPercentCedular:TextInputLayout, fieldCedular:TextInputLayout){
        fieldPercentCedular.visibility = View.GONE
        fieldCedular.visibility = View.VISIBLE
    }

    fun showOtroCedular(fieldPercentCedular:TextInputLayout, fieldCedular:TextInputLayout){
        fieldPercentCedular.visibility = View.VISIBLE
        fieldCedular.visibility = View.VISIBLE
    }

    fun deleteComma(value:String):String{
        return value.replace(",", "")
    }

    fun round2Dec(valor: Double): String {
        val formato = DecimalFormat("###,###,###,###,###,###,##0.00")
        return formato.format(valor)
    }
}