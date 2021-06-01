package com.mozama.impuestos.fragments
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.mozama.impuestos.R
import com.mozama.impuestos.utils.Operations
import com.mozama.impuestos.utils.UtilsGraphic

class RetencionFragment : Fragment() {
//    private lateinit var fieldSubtotal: TextInputLayout
    private lateinit var txtSubtotal: EditText
    private lateinit var fieldIva : TextInputLayout    
    private lateinit var txtIva: EditText
//    private lateinit var fieldIsrR: TextInputLayout
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

    //Para identificar quien modifica el valor de los EditText
    //evitar ciclo infinito en addTextChangedListener
    private val TAG_SYSTEM = "system"
    private val TAG_USER = "user"

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
                calc(IN_OPTION)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { //Another interface callback
            }
        }

        txtSubtotal.tag = TAG_USER
        txtIva.tag = TAG_USER
        txtIsrR.tag = TAG_USER
        txtIvaR.tag = TAG_USER
        txtTotal.tag = TAG_USER

        txtSubtotal.addTextChangedListener(generalTextWatcher)
        txtTotal.addTextChangedListener(generalTextWatcher)
    }

    fun calc( option:Int ){
        IN_OPTION = option
        val percentIva = UtilsGraphic().getIvaSpinner(spinIva)
        when (IN_OPTION){
            IN_SUBTOTAL ->calcInputSubtotal( percentIva )
            IN_TOTAL -> cacInputTotal(percentIva)
        }
    }

    private fun cacInputTotal(percentIva: Double) {
        //IVA retenido a 2/3
        val percentIvaRetenido = ( percentIva / 3 ) * 2
        val percentIsrRetenido = 0.10

        if(txtTotal.text.toString().isNotEmpty() ){
            val text = txtTotal.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                total = temp
                val map = Operations().calSubtotalRetencionesTotal(total, percentIva, percentIvaRetenido, percentIsrRetenido )
                iva = map?.get("iva")!!
                ivaR = map["ivaR"]!!
                isrR = map["isrR"]!!
                subtotal = map["subtotal"]!!
                setValuesEditText()
            }else cleaner()
        }else cleaner()
    }

    private fun calcInputSubtotal( percentIva:Double ){
        //IVA retenido a 2/3
        val percentIvaRetenido = ( percentIva / 3 ) * 2
        val percentIsrRetenido = 0.10

        if(txtSubtotal.text.toString().isNotEmpty() ){
            val text = txtSubtotal.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                subtotal = temp
                val map = Operations().calcAllRetenciones(subtotal, percentIva, percentIvaRetenido, percentIsrRetenido )
                iva = map["iva"]!!
                ivaR = map["ivaR"]!!
                isrR = map["isrR"]!!
                total = map["total"]!!
                setValuesEditText()
            }else cleaner()
        }else cleaner()

    }

    fun setValuesEditText(){

        if(IN_OPTION != IN_SUBTOTAL){
            val subtotalStrig = Operations().round2Dec(subtotal)
            txtSubtotal.tag = TAG_SYSTEM
            txtSubtotal.setText( subtotalStrig )
            txtSubtotal.tag = TAG_USER
        }
        if(IN_OPTION != IN_IVA){
            val ivaStrig    = Operations().round2Dec(iva)
            txtIva.tag = TAG_SYSTEM
            txtIva.setText( ivaStrig )
            txtIva.tag = TAG_USER
        }
        if(IN_OPTION != IN_ISR_R){
            val isrRStrig   = Operations().round2Dec(isrR)
            txtIsrR.tag = TAG_SYSTEM
            txtIsrR.setText( isrRStrig )
            txtIsrR.tag = TAG_USER
        }
        if(IN_OPTION != IN_IVA_R){
            val ivaRStrig   = Operations().round2Dec(ivaR)
            txtIvaR.tag = TAG_SYSTEM
            txtIvaR.setText( ivaRStrig )
            txtIvaR.tag = TAG_USER
        }
        if(IN_OPTION != IN_TOTAL ) {
            val totalString = Operations().round2Dec(total)
            txtTotal.tag = TAG_SYSTEM
            txtTotal.setText( totalString )
            txtTotal.tag = TAG_USER
        }

    }
    
    private fun cleaner(){
        if(IN_OPTION != IN_SUBTOTAL){
            subtotal = 0.0
            txtSubtotal.tag = TAG_SYSTEM
            txtSubtotal.setText( "" )
            txtSubtotal.tag = TAG_USER
        }
        if(IN_OPTION != IN_IVA){
            iva = 0.0
            txtIva.tag = TAG_SYSTEM
            txtIva.setText( "" )
            txtIva.tag = TAG_USER
        }
        if(IN_OPTION != IN_ISR_R){
            isrR = 0.0
            txtIsrR.tag = TAG_SYSTEM
            txtIsrR.setText( "" )
            txtIsrR.tag = TAG_USER
        }
        if(IN_OPTION != IN_IVA_R){
            ivaR = 0.0
            txtIvaR.tag = TAG_SYSTEM
            txtIvaR.setText( "" )
            txtIvaR.tag = TAG_USER
        }
        if(IN_OPTION != IN_TOTAL ) {
            total = 0.0
            txtTotal.tag = TAG_SYSTEM
            txtTotal.setText( "" )
            txtTotal.tag = TAG_USER
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

    private val generalTextWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged( s: CharSequence, start: Int, before: Int,count: Int) {
        }
        override fun beforeTextChanged( s: CharSequence, start: Int, count: Int,after: Int ) {
        }
        override fun afterTextChanged(s: Editable) {
            if (txtSubtotal.text.hashCode() == s.hashCode() && txtSubtotal.tag == TAG_USER) {
                calc(IN_SUBTOTAL)
            } else if (txtTotal.text.hashCode() == s.hashCode() && txtTotal.tag == TAG_USER) {
                calc(IN_TOTAL)
            }/*else if (txtIva.text.hashCode() == s.hashCode() && txtIva.tag == TAG_USER) {
                calc(IN_IVA)
            }*/

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
        fun newInstance() =
            RetencionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}