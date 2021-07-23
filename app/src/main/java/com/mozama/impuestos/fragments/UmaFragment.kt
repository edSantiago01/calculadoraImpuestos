package com.mozama.impuestos.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.mozama.impuestos.R
import com.mozama.impuestos.utils.DialogFragment
import com.mozama.impuestos.utils.Operations
import com.mozama.impuestos.utils.UtilsGraphic

class UmaFragment : Fragment() {
    private lateinit var txtUma: EditText
    private lateinit var txtPesos: EditText
    private lateinit var icInfo: ImageView
    private lateinit var mAdView : AdView
    private val valorUma = 89.62

    private var IN_OPTION = 0
    private val IN_UMA = 1
    private val IN_PESOS = 2
    private val TAG_SYSTEM = "system"
    private val TAG_USER = "user"

    private var uma = 0.0
    private var pesos = 0.0

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
        return inflater.inflate(R.layout.fragment_uma, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtUma = view.findViewById(R.id.txtUma)
        txtPesos = view.findViewById(R.id.txtPesos)
        icInfo = view.findViewById(R.id.icInfo)

        MobileAds.initialize(requireContext()) {}
        mAdView = view.findViewById(R.id.adUma)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        setChangeElements()
    }

    override fun onResume() {
        super.onResume()
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

    private fun shareInfo() {
        //compartir el contenido de texto
        val valUma = UtilsGraphic().round2Dec(uma)
        val valPesos = UtilsGraphic().round2Dec(pesos)

        val text = "UMA: $valUma \nPESOS:  $ $valPesos \n\nValor UMA 2021: $$valorUma"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun setChangeElements(){
        txtUma.tag = TAG_USER
        txtPesos.tag = TAG_USER

        txtUma.addTextChangedListener(generalTextWatcher)
        txtPesos.addTextChangedListener(generalTextWatcher)

        icInfo.setOnClickListener{ showDialogInfo() }
    }

    fun calc( option:Int){
        IN_OPTION = option
        when (IN_OPTION){
            IN_UMA ->calcInputUma()
            IN_PESOS -> calcInputPesos()
        }
    }

    private fun calcInputUma(){
        if (txtUma.text.isNotEmpty()){
            val text = txtUma.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                uma = temp
                pesos = Operations().calPesosUma( uma, valorUma )
                setValuesEditText()
            }
            else cleaner()
        }else cleaner()
    }

    private fun calcInputPesos(){
        if (txtPesos.text.isNotEmpty()){
            val text = txtPesos.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                pesos = temp
                uma = Operations().calUmaPesos( pesos, valorUma )
                setValuesEditText()
            }
            else cleaner()
        }else cleaner()
    }

    private fun showDialogInfo(){
        val tit = resources.getString(R.string.unidad_ma)
        val umaString = valorUma.toString()
        val mensaje = resources.getString(R.string.uma_info,umaString)

        context?.let {
            DialogFragment().showDialogNeutral(it, tit, mensaje)
        }
    }

    private fun setValuesEditText(){
        if(IN_OPTION != IN_UMA){
            val subtotalStrig = UtilsGraphic().round2Dec(uma)
            txtUma.tag = TAG_SYSTEM
            txtUma.setText( subtotalStrig )
            txtUma.tag = TAG_USER
        }
        if(IN_OPTION != IN_PESOS){
            val ivaStrig = UtilsGraphic().round2Dec(pesos)
            txtPesos.tag = TAG_SYSTEM
            txtPesos.setText( ivaStrig )
            txtPesos.tag = TAG_USER
        }
    }

    private fun cleaner(){
        if(IN_OPTION != IN_UMA){
            pesos = 0.0
            txtUma.tag = TAG_SYSTEM //La app es quien modifica el valor
            txtUma.setText("")
            txtUma.tag = TAG_USER // Regresa al supuesto de que el usuario modificará el siguiente valor
        }
        if(IN_OPTION != IN_PESOS){
            uma = 0.0
            txtPesos.tag = TAG_SYSTEM
            txtPesos.setText("")
            txtPesos.tag = TAG_USER
        }
    }

    private val generalTextWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged( s: CharSequence, start: Int, before: Int,count: Int) {
        }
        override fun beforeTextChanged( s: CharSequence, start: Int, count: Int,after: Int ) {
        }
        override fun afterTextChanged(s: Editable) {
            if (txtUma.text.hashCode() == s.hashCode() && txtUma.tag == TAG_USER) {
                calc(IN_UMA)
            } else if (txtPesos.text.hashCode() == s.hashCode() && txtPesos.tag == TAG_USER) {
                calc(IN_PESOS)
            }

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
            UmaFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}