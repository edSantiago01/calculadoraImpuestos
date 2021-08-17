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
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.mozama.impuestos.R
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

    fun getIvaPercentSpinner(spin:Spinner): Double{
        val position = spin.selectedItemPosition

        var porcentaje = 0.00
        if ( position == 0 ) porcentaje = 0.16
        else if ( position == 1 ) porcentaje = 0.08

        return porcentaje
    }

    fun getPercentCedularSpinner(spin: Spinner): Double {
        return when (spin.selectedItemPosition) {
            0 -> 0.0
            1 -> 0.02
            2 -> 0.03
            3 -> 0.04
            4 -> 0.05
            else -> -1.0
        }
    }

    fun getStringShareCedular(configLocales:Int, cedular:Double, spinCedular:Spinner, txtPercentCedular:EditText):String{
        var cedularString = ""
        if(configLocales == 1){
            val valCedular = round2Dec(cedular)

            val optionCedular = spinCedular.selectedItemPosition
            if(optionCedular != 0){
                cedularString = if(optionCedular == 5){
                    val valPercent = txtPercentCedular.text.toString().toFloatOrNull()
                    "\n Imp. local $valPercent% $ $valCedular"
                }
                else{
                    val valPercent = getPercentCedularSpinner(spinCedular)
                    val percent = round0Dec(valPercent * 100)
                    "\n Cedular $percent%:  $ $valCedular"
                }
            }
        }
        return cedularString
    }

    // -1.0 is value not valid
    fun getPercentCedularEditText(editText: EditText, context: Context): Double{
        var percent = -1.0
        if(editText.text.isNotEmpty()){
            val valEditText: Double? = editText.text.toString().toDoubleOrNull()
            if(valEditText != null) percent = valEditText/100
        }else{
            editText.requestFocus()
            showToast(context.resources.getString(R.string.verifica_cedular), context)
        }

        return percent
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

    private fun round0Dec(valor: Double): String {
        val formato = DecimalFormat("###,###,###,###,###,###,##0.##")
        return formato.format(valor)
    }

    fun showToast(msg:String, context:Context){
        Toast.makeText(context,
            msg,
            Toast.LENGTH_LONG).show()
    }
}