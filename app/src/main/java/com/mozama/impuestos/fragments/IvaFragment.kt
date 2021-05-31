package com.mozama.impuestos.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import com.mozama.impuestos.R
import com.mozama.impuestos.utils.Operations
import com.mozama.impuestos.utils.UtilsGraphic

class IvaFragment : Fragment() {
    private lateinit var txtSubtotal: EditText
    private lateinit var txtIva: EditText
    private lateinit var txtTotal: EditText
    private lateinit var spinIva : Spinner

    private var IN_OPTION = 0
    private val IN_SUBTOTAL = 0
    private val IN_IVA = 1
    private val IN_TOTAL = 3

    private var subtotal = 0.0
    private var iva = 0.0
    private var total = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_iva, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtSubtotal = view.findViewById(R.id.txtSubtotalI)
        txtTotal = view.findViewById(R.id.txtTotalI)
        txtIva = view.findViewById(R.id.txtIvaI)
        spinIva = view.findViewById(R.id.spinIvaI)

        setItemIva()
        changeElements()
    }

    private fun changeElements() {
        spinIva.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                calc()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { //Another interface callback
            }
        }

        txtSubtotal.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                IN_OPTION = IN_SUBTOTAL
                calc()
            }
        })
    }

    fun calc(){
        val percentIva = UtilsGraphic().getIvaSpinner(spinIva)
        when (IN_OPTION){
            IN_SUBTOTAL ->calcInputSubtotal( percentIva )
        }
    }

    private fun calcInputSubtotal( percentIva:Double ){
        if(txtSubtotal.text.toString().isNotEmpty() ){
            val sub = txtSubtotal.text.toString()
            val subNotComma = UtilsGraphic().deleteComma(sub)
            subtotal = subNotComma.toDouble()
            iva = Operations().calcValPercent( subtotal, percentIva )
            total = subtotal + iva
            setValuesEditText()
        }else cleaner()

    }

    fun setValuesEditText(){
        if(IN_OPTION != IN_SUBTOTAL){
            val subtotalStrig = Operations().round2Dec(subtotal)
            txtSubtotal.setText( subtotalStrig )
        }
        if(IN_OPTION != IN_IVA){
            val ivaStrig    = Operations().round2Dec(iva)
            txtIva.setText( ivaStrig )
        }
        if(IN_OPTION != IN_TOTAL ) {
            val totalString = Operations().round2Dec(total)
            txtTotal.setText( totalString )
        }
    }

    private fun cleaner(){
        if(IN_OPTION != IN_SUBTOTAL){
            subtotal = 0.0
            txtSubtotal.setText( "" )
        }
        if(IN_OPTION != IN_IVA){
            iva = 0.0
            txtIva.setText( "" )
        }
        if(IN_OPTION != IN_TOTAL ) {
            total = 0.0
            txtTotal.setText( "" )
        }
    }

    private fun setItemIva(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_iva, spinIva)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            IvaFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}