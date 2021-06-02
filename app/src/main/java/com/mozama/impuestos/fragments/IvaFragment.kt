package com.mozama.impuestos.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mozama.impuestos.R
import com.mozama.impuestos.utils.Operations
import com.mozama.impuestos.utils.UtilsGraphic

class IvaFragment : Fragment() {
    private lateinit var txtSubtotal: EditText
    private lateinit var txtIva: EditText
    private lateinit var txtTotal: EditText
    private lateinit var spinIva : Spinner

    private var IN_OPTION = 0
    private val IN_SUBTOTAL = 1
    private val IN_IVA = 2
    private val IN_TOTAL = 3

    private var subtotal = 0.0
    private var percentIva = 0.0
    private var iva = 0.0
    private var total = 0.0

    //Para identificar quien modifica el valor de los EditText
    //evitar ciclo infinito en addTextChangedListener
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
        return inflater.inflate(R.layout.fragment_iva, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtSubtotal = view.findViewById(R.id.txtSubtotalI)
        txtTotal = view.findViewById(R.id.txtTotalI)
        txtIva = view.findViewById(R.id.txtIvaI)
        spinIva = view.findViewById(R.id.spinIvaI)

        setItemIva()
        setChangeElements()
        hideKeyboard()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Detectar la opción del menú seleccionado
        return when (item.itemId) {
            R.id.menu_delete -> {
                IN_OPTION = 0
                hideKeyboard()
                cleaner()
                true
            }
            R.id.menu_share -> {
                shareInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
    }

    private fun setChangeElements() {
        spinIva.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                hideKeyboard()
                calc(IN_OPTION)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { //Another interface callback
            }
        }
        txtSubtotal.tag = TAG_USER
        txtTotal.tag = TAG_USER
        txtIva.tag = TAG_USER

        txtSubtotal.addTextChangedListener(generalTextWatcher)
        txtTotal.addTextChangedListener(generalTextWatcher)
        txtIva.addTextChangedListener(generalTextWatcher)
    }
    

    fun calc( option:Int){
        IN_OPTION = option
        percentIva = UtilsGraphic().getIvaSpinner(spinIva)
        when (IN_OPTION){
            IN_SUBTOTAL ->calcInputSubtotal()
            IN_IVA -> calcInputIva()
            IN_TOTAL -> calcInputTotal()
        }
    }

    private fun calcInputSubtotal(){
        if (txtSubtotal.text.isNotEmpty()){
            val text = txtSubtotal.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                subtotal = temp
                iva = Operations().calcValPercentTotal( subtotal, percentIva )
                total = subtotal + iva
                setValuesEditText()
            }
            else cleaner()
        }else cleaner()
    }

    private fun calcInputIva(){
        if (txtIva.text.isNotEmpty()){
            val text = txtIva.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                iva = temp
                subtotal = Operations().calcValSubtotalIva( iva, percentIva )
                total = subtotal + iva
                setValuesEditText()
            }
            else cleaner()
        }else cleaner()
    }

    private fun calcInputTotal() {
        if (txtTotal.text.isNotEmpty()){
            val text = txtTotal.text.toString()
            val subNotComma = UtilsGraphic().deleteComma(text)
            val temp = subNotComma.toDoubleOrNull()
            if( temp != null ){
                total = temp
                subtotal = Operations().calcValSubtotalTotal(total, percentIva)
                iva = Operations().calcValPercentTotal( subtotal, percentIva )
                setValuesEditText()
            }
            else cleaner()
        }else cleaner()
    }



    private fun setValuesEditText(){
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
            txtSubtotal.tag = TAG_SYSTEM //La app es quien modifica valor
            txtSubtotal.setText( "" )
            txtSubtotal.tag = TAG_USER // Supuesto en el que el usuario modificará el siguiente valor
        }
        if(IN_OPTION != IN_IVA){
            iva = 0.0
            txtIva.tag = TAG_SYSTEM
            txtIva.setText( "")
            txtIva.tag = TAG_USER
        }
        if(IN_OPTION != IN_TOTAL ) {
            total = 0.0
            txtTotal.tag = TAG_SYSTEM
            txtTotal.setText( "" )
            txtTotal.tag = TAG_USER
        }
    }

    private fun setItemIva(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_iva, spinIva)
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
            }else if (txtIva.text.hashCode() == s.hashCode() && txtIva.tag == TAG_USER) {
                calc(IN_IVA)
            }

        }
    }

    private fun shareInfo(){
        //compartir el contenido de texto
        val valIva = percentIva * 100
        val valIvaInt = valIva.toInt()
        val subtotalRound = Operations().round2Dec(subtotal)
        val ivaRound = Operations().round2Dec(iva)
        val totalRound = Operations().round2Dec(total)

        val text = "Subtotal: $ $subtotalRound \n IVA $valIvaInt%:  $ $ivaRound \n\n TOTAL:  $ $totalRound"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
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
            IvaFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}