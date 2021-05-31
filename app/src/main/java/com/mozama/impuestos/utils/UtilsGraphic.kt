package com.mozama.impuestos.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.mozama.impuestos.R

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

    fun deleteComma(value:String):String{
        return value.replace(",", "")
    }
}