package com.mozama.impuestos.fragments

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.textfield.TextInputLayout
import com.mozama.impuestos.R
import com.mozama.impuestos.utils.DialogFragment
import com.mozama.impuestos.utils.UtilsGraphic

/**
 * Fragment visualizar calculos RESICO (Régimen Simplificado de Confianza)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2022  Edgar Santiago
 */

class ResicoFragment : Fragment() {

    private lateinit var txtSubtotal: EditText
    private lateinit var fieldIva : TextInputLayout
    private lateinit var txtIva: EditText
    private lateinit var txtIsrR: EditText
    private lateinit var lyCedular: LinearLayout
    private lateinit var spinCedular : Spinner
    private lateinit var fieldCedular: TextInputLayout
    private lateinit var fieldPercentCedular: TextInputLayout
    private lateinit var txtPercentCedular: EditText
    private lateinit var txtCedular: EditText
    private lateinit var txtTotal: EditText
    private lateinit var spinIva : Spinner
    private lateinit var icInfoRetenciones: ImageView

    private var configLocales = 0

    private var IN_OPTION = 0
    private val IN_SUBTOTAL = 1
    private val IN_IVA = 2
    private val IN_ISR_R = 3
    private val IN_TOTAL = 5
    private var percentIva = 0.0
    private var percentCedular = 0.0

    private var subtotal = 0.0
    private var iva = 0.0
    private var isrR = 0.0
    private var cedular = 0.0
    private var total = 0.0

    private val TAG_SYSTEM = "system"
    private val TAG_USER = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(view)
    }

    private fun setup(view: View){
        fieldIva = view.findViewById(R.id.fieldIvaResico)

        txtSubtotal = view.findViewById(R.id.txtSubtotalResico)
        txtIva = view.findViewById(R.id.txtIvaResico)
        txtIsrR = view.findViewById(R.id.txtIsrResico)
        txtTotal = view.findViewById(R.id.txtTotalResico)
        spinIva = view.findViewById(R.id.spinIvaResico)
        icInfoRetenciones = view.findViewById(R.id.icInfoResico)

        lyCedular    = view.findViewById(R.id.lyCedularResico)
        spinCedular  = view.findViewById(R.id.spinCedularResico)
        fieldCedular = view.findViewById(R.id.fieldCedularResico)
        fieldPercentCedular = view.findViewById(R.id.fieldPercentCedularResico)
        txtPercentCedular = view.findViewById(R.id.txtPercentCedularResico)
        txtCedular = view.findViewById(R.id.txtCedularResico)

        setItemIva()
        setItemCedular()
        changeElements()
        hideKeyboard()
        verificViewCedular()

        txtSubtotal.tag = TAG_USER
        txtIva.tag = TAG_USER
        txtIsrR.tag = TAG_USER
        txtTotal.tag = TAG_USER

//        txtSubtotal.addTextChangedListener(generalTextWatcher)
//        txtTotal.addTextChangedListener(generalTextWatcher)

        icInfoRetenciones.setOnClickListener{ showDialogInfo() }
        txtIva.setOnClickListener{hideKeyboard()}
        txtIsrR.setOnClickListener{hideKeyboard()}
        txtCedular.setOnClickListener{hideKeyboard()}
    }

    private fun verificViewCedular(){
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val configKeyLocales = resources.getString(R.string.imp_config)

        configLocales = sharedPref!!.getInt(configKeyLocales, 0)
        if(configLocales == 0){
            lyCedular.visibility = View.GONE
        }else{
            lyCedular.visibility = View.VISIBLE
        }
    }

    private fun setItemIva(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_iva_r, spinIva)
    }

    private fun setItemCedular(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_cedular, spinCedular)
    }

    private fun calc(option:Int ){

    }

    private fun changeElements(){
        spinIva.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 2) hideIva()
                else showIva()
                calc(IN_OPTION)
                hideKeyboard()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { //Another interface callback
            }
        }

        spinCedular.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        UtilsGraphic().hideCedular( fieldPercentCedular, fieldCedular)
                        cedular = 0.0
                        percentCedular = 0.0
                    }
                    5 -> UtilsGraphic().showOtroCedular( fieldPercentCedular, fieldCedular )
                    else -> UtilsGraphic().showCedular( fieldPercentCedular, fieldCedular )
                }
                calc(IN_OPTION)
                hideKeyboard()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        txtPercentCedular.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (txtPercentCedular.text.toString().isNotEmpty()) {
                    val valorString = txtPercentCedular.text.toString()
                    val valorF: Float?  = valorString.toFloatOrNull()
                    valorF?.let { calc(IN_OPTION) }
                        ?:run{UtilsGraphic().showToast(resources.getString(R.string.verifica_cedular), requireContext())}
                }else UtilsGraphic().showToast(resources.getString(R.string.verifica_cedular), requireContext())
                hideKeyboard()
            }
            false
        }

    }

    private fun hideIva(){
        fieldIva.visibility = View.GONE
        iva = 0.0
    }

    private fun showIva(){
        fieldIva.visibility = View.VISIBLE
    }

    private fun showDialogInfo(){
        val tit = resources.getString(R.string.titulo_dialog)
        val mensaje = resources.getString(R.string.resico_info)

        context?.let {
            DialogFragment().showDialogNeutral(it, tit, mensaje)
        }
    }

    private fun Fragment.hideKeyboard() {
        val activity = this.activity
        if (activity is AppCompatActivity) {
            activity.hideKeyboard()
        }
    }
    private fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResicoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}