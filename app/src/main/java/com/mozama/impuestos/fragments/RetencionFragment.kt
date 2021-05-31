package com.mozama.impuestos.fragments
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import com.mozama.impuestos.R
import com.mozama.impuestos.utils.Operations
import com.mozama.impuestos.utils.UtilsGraphic

/**
 * A simple [Fragment] subclass.
 * Use the [RetencionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RetencionFragment : Fragment() {
    private lateinit var fieldSubtotal: TextInputLayout
    private lateinit var txtSubtotal: EditText
    private lateinit var fieldIva : TextInputLayout    
    private lateinit var txtIva: EditText
    private lateinit var fieldIsrR: TextInputLayout
    private lateinit var txtIsrR: EditText
    private lateinit var fieldIvaR: TextInputLayout
    private lateinit var txtIvaR: EditText    
    private lateinit var txtTotal: EditText
    private lateinit var spinIva : Spinner

    private var IN_OPTION = 0
    private val IN_SUBTOTAL = 0
    private val IN_IVA = 1
    private val IN_ISR_R = 2
    private val IN_IVA_R = 3
    private val IN_TOTAL = 4

    private var subtotal = 0.0
    private var iva = 0.0
    private var isrR = 0.0
    private var ivaR = 0.0
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
        return inflater.inflate(R.layout.fragment_retencion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fieldIva = view.findViewById(R.id.fieldIva)
        fieldIvaR = view.findViewById(R.id.fieldIvaR)

        txtSubtotal = view.findViewById(R.id.txtSubtotal)
        txtIva = view.findViewById(R.id.txtIva)
        txtIsrR = view.findViewById(R.id.txtIsrR)
        txtIvaR = view.findViewById(R.id.txtIvaR)
        txtTotal = view.findViewById(R.id.txtTotal)
        spinIva = view.findViewById(R.id.spinIva)

        setItemIva()
        changeElements()
    }



    private fun changeElements() {
        spinIva.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position == 2) hideIva()
                else showIva()
                calc()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { //Another interface callback
            }
        }

        txtSubtotal.addTextChangedListener( object : TextWatcher  {
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
        //IVA retenido a 2/3
        val percentIvaRetenido = ( percentIva / 3 ) * 2
        val percentIsrRetenido = 0.10

        if(txtSubtotal.text.toString().isNotEmpty() ){
            val sub = txtSubtotal.text.toString()
            val subNotComma = UtilsGraphic().deleteComma(sub)
            subtotal = subNotComma.toDouble()
            iva = Operations().calcValPercent( subtotal, percentIva )
            ivaR = Operations().calcValPercent( subtotal, percentIvaRetenido )
            isrR = Operations().calcValPercent( subtotal, percentIsrRetenido )
            total = subtotal + iva - ivaR - isrR
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
        if(IN_OPTION != IN_ISR_R){
            val isrRStrig   = Operations().round2Dec(isrR)
            txtIsrR.setText( isrRStrig )
        }
        if(IN_OPTION != IN_IVA_R){
            val ivaRStrig   = Operations().round2Dec(ivaR)
            txtIvaR.setText( ivaRStrig )
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
        if(IN_OPTION != IN_ISR_R){
            isrR = 0.0
            txtIsrR.setText( "" )
        }
        if(IN_OPTION != IN_IVA_R){
            ivaR = 0.0
            txtIvaR.setText( "" )
        }
        if(IN_OPTION != IN_TOTAL ) {
            total = 0.0
            txtTotal.setText( "" )
        }
    }

    private fun hideIva(){
        fieldIva.visibility = View.GONE
        fieldIvaR.visibility = View.GONE
        iva = 0.0
        ivaR = 0.0
    }

    private fun showIva(){
        fieldIva.visibility = View.VISIBLE
        fieldIvaR.visibility = View.VISIBLE
    }

    private fun setItemIva(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_iva_r, spinIva)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RetencionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}