package com.mozama.impuestos.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.mozama.impuestos.DriodApp
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.crashlytics.ktx.setCustomKeys
import com.google.firebase.ktx.Firebase
import com.mozama.impuestos.R
import com.mozama.impuestos.data.ImpuestosPolitica
import com.mozama.impuestos.data.model.ImpuestosResponse
import com.mozama.impuestos.utils.DialogFragment
import com.mozama.impuestos.utils.Operations
import com.mozama.impuestos.utils.UtilsGraphic
import kotlinx.coroutines.launch
import java.time.LocalDate

class UmaFragment : Fragment() {
    private var nResultados = 0
    private val limiteAds = 4
    private var mInterstitialAd: InterstitialAd? = null
    private var mAdIsLoading: Boolean = false

    private lateinit var txtUma: EditText
    private lateinit var txtPesos: EditText
    private lateinit var icInfo: TextView

    private lateinit var spinSalario : Spinner
    private lateinit var txtSalario: EditText
    private lateinit var txtPesosSalario: EditText
    private lateinit var icInfoSalario: TextView

    private var anioCurso = LocalDate.now().year
    private var anioAnterior = anioCurso - 1
    private var umaEnCurso = 0.0
    private var umaAnterior = 0.0
    private var anioVigenciaUMA = 0
    private var valorUma = 0.0
    private var valorSMG_ZLFN_ANTERIOR = 0.0
    private var valorSMG_ZLFN = 0.0
    private var valorSMG_ANTERIOR = 0.0
    private var valorSMG = 0.0
    private var anioVigenciaSMG = 0

    //no calcular con ceros provisionales mientras no haya información.
    private var impuestosLocales: ImpuestosResponse? = null
    private var datosDisponibles = false
    private var ultimaEntradaUma = 1
    private var ultimaEntradaSalario = 3
    private var avisoImpuestos: Snackbar? = null
    private var ultimoMensajeImpuestos: String? = null

    private var IN_OPTION = 0
    private val IN_UMA = 1
    private val IN_PESOS = 2
    private val IN_SALARIO = 3
    private val IN_PESOS_SALARIO = 4
    private val TAG_SYSTEM = "system"
    private val TAG_USER = "user"

    private var uma = 0.0
    private var pesos = 0.0
    private var nSalario = 0.0
    private var pesosSalario = 0.0
    private var valSalario = 0.0

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

        spinSalario = view.findViewById(R.id.spinSalario)
        txtSalario = view.findViewById(R.id.txtSalario)
        txtPesosSalario = view.findViewById(R.id.txtPesosSmg)
        icInfoSalario = view.findViewById(R.id.icInfoSalario)
        setItemSalario()
        setChangeElements()
        setup()
        observarImpuestos()
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
                cleanerSalario()
                true
            }
            R.id.menu_share -> {
                shareInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setup(){
        validaValorUma()
        validaValorSalarioMinimo()
        txtUma.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard()
                nResultados++
                validarIntersticial()
                true
            }else false
        }

        txtPesos.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard()
                nResultados++
                validarIntersticial()
                true
            }else false
        }

        txtSalario.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard()
                nResultados++
                validarIntersticial()
                true
            }else false
        }

        txtPesosSalario.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard()
                nResultados++
                validarIntersticial()
                true
            }else false
        }

    }

    private fun goEnlacePlay(idApp:String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=$idApp")
                setPackage("com.android.vending")
            }
            startActivity(intent)
        }catch (e: Exception){
            val crashlytics = Firebase.crashlytics
            crashlytics.setCustomKeys {
                key("screen", "abrir $idApp UMA")
                key("exception_message", e.message.toString()+" "+e.cause.toString())
            }
            UtilsGraphic().showToast("Problemas al abrir enlace", requireContext())
        }
    }

    private fun validaValorUma(){
//        if(!Operations().fechaMayorA("01", "02", anioCurso.toString())){
//            valorUma = umaAnterior
//            anioVigenciaUMA = anioAnterior
//        }else {
//            valorUma = umaEnCurso.toDouble()
//            anioVigenciaUMA = anioCurso
//        }
        val datos = impuestosLocales ?: return
        valorUma = requireNotNull(datos.uma)
        umaEnCurso = valorUma
        anioVigenciaUMA = requireNotNull(ImpuestosPolitica.fecha(datos.umaVigencia)).year
        anioCurso = LocalDate.now().year
        anioAnterior = anioCurso - 1
    }

    private fun validaValorSalarioMinimo(){
//        if(!Operations().fechaMayorA("01", "01", anioCurso.toString())){
//            valorSMG_ZLFN = valorSMG_ZLFN_ANTERIOR
//            valorSMG = valorSMG_ANTERIOR
//            anioVigenciaSMG = anioAnterior
//        }
        val datos = impuestosLocales ?: return
        valorSMG = requireNotNull(datos.smg)
        valorSMG_ZLFN = requireNotNull(datos.zlfn)
        anioVigenciaSMG = requireNotNull(ImpuestosPolitica.fecha(datos.smgVigencia)).year
    }

    private fun observarImpuestos() {
        val repository = (requireActivity().application as DriodApp).impuestosRepository
        //hasta recibir el estado, no calcular con valores provisionales.
        habilitarCalculos(datosDisponibles)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.estado.collect { estado ->
                    val datos = estado.datos
                    val cambiaron = datos != impuestosLocales
                    impuestosLocales = datos

                    datosDisponibles = datos != null
                    habilitarCalculos(datosDisponibles)

                    if (datosDisponibles) {
                        validaValorUma()
                        validaValorSalarioMinimo()
                       // recalcular ambos grupos conservando la última dirección
                        // introducida por el usuario (unidades -> pesos o pesos -> unidades).

                        if (cambiaron) {
                            val opcion = IN_OPTION

                            calc(ultimaEntradaUma)
                            calc(ultimaEntradaSalario)

                            IN_OPTION = opcion
                        }

                    }

                    val mensaje = if (estado.cargando && !datosDisponibles) {
                        "Obteniendo valores de UMA y salarios…"
                    } else {
                        estado.mensaje
                    }

                    if (mensaje != ultimoMensajeImpuestos) {
                        avisoImpuestos?.dismiss()
                        ultimoMensajeImpuestos = mensaje

                        avisoImpuestos = mensaje?.let {
                            // con datos locales, mostrar un aviso temporal.
                            // Los controles permanecen habilitados.
                            val duracion = if (datosDisponibles) {
                                Snackbar.LENGTH_LONG
                            } else {
                                Snackbar.LENGTH_INDEFINITE
                            }

                            Snackbar.make(requireView(), it, duracion)
                                .also { aviso -> aviso.show() }
                        }
                    }

                }
            }
        }
    }

    private fun habilitarCalculos(habilitar: Boolean) {
        txtUma.isEnabled = habilitar
        txtPesos.isEnabled = habilitar
        txtSalario.isEnabled = habilitar
        txtPesosSalario.isEnabled = habilitar
        spinSalario.isEnabled = habilitar
        icInfo.isEnabled = habilitar
        icInfoSalario.isEnabled = habilitar
    }

    override fun onDestroyView() {
        // liberar aviso ligado a esta vista y permitir recalcular tras restaurarla.
        avisoImpuestos?.dismiss()
        avisoImpuestos = null
        ultimoMensajeImpuestos = null
        impuestosLocales = null
        datosDisponibles = false
        super.onDestroyView()
    }

    private fun shareInfo() {
        //compartir el contenido de texto
        var text = ""

        if(txtUma.text.isNotEmpty()){
            val valUma = UtilsGraphic().round2Dec(uma)
            val valPesos = UtilsGraphic().round2Dec(pesos)
            text += "UMA: $valUma \nPESOS:  $ $valPesos \n\nValor UMA 2021: $$valorUma"
        }

        if (txtSalario.text.isNotEmpty()){
            val valNSalario = UtilsGraphic().round2Dec(nSalario)
            val valPesosSalario = UtilsGraphic().round2Dec(pesosSalario)
            text += "\n\nSalarios Mínimos: $valNSalario \nPESOS:  $ $valPesosSalario \n\nValor SMG: $$valSalario"
        }

        try {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

        }catch (e: Exception){
            val crashlytics = Firebase.crashlytics
            crashlytics.setCustomKeys {
                key("screen", "compartir UMA")
                key("exception_message", e.message.toString()+" "+e.cause.toString())
            }
            UtilsGraphic().showToast("Problemas al compartir información", requireContext())
        }

    }

    private fun setChangeElements(){
        txtUma.tag = TAG_USER
        txtPesos.tag = TAG_USER
        txtSalario.tag = TAG_USER
        txtPesosSalario.tag = TAG_USER

        txtUma.addTextChangedListener(generalTextWatcher)
        txtPesos.addTextChangedListener(generalTextWatcher)

        txtSalario.addTextChangedListener(generalTextWatcher)
        txtPesosSalario.addTextChangedListener(generalTextWatcher)

        icInfo.setOnClickListener{ showDialogInfo("uma") }
        icInfoSalario.setOnClickListener{ showDialogInfo("salario") }

        spinSalario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                hideKeyboard()
                calc(IN_SALARIO)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun calc( option:Int){
//        IN_OPTION = option
//        when (IN_OPTION){
//            IN_UMA ->calcInputUma()
//            IN_PESOS -> calcInputPesos()
//            IN_SALARIO -> calcInputSalario()
//            IN_PESOS_SALARIO -> calcInputPesosSalario()
//        }

        if (!datosDisponibles) return // NUEVO: evita calcular o dividir con valores provisionales.
        if (option == IN_UMA || option == IN_PESOS) ultimaEntradaUma = option
        if (option == IN_SALARIO || option == IN_PESOS_SALARIO) ultimaEntradaSalario = option

        IN_OPTION = option
        when (IN_OPTION){
            IN_UMA ->calcInputUma()
            IN_PESOS -> calcInputPesos()
            IN_SALARIO -> calcInputSalario()
            IN_PESOS_SALARIO -> calcInputPesosSalario()
        }
    }

    private fun setItemSalario(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_salarios, spinSalario)
    }

    private fun calcInputSalario(){
        if (txtSalario.text.isNotEmpty()){
            val text = txtSalario.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                nSalario = temp
                valSalario = UtilsGraphic().getValSalarioMinimoSpinner(spinSalario, valorSMG, valorSMG_ZLFN)
                pesosSalario = Operations().calPesosSalario( nSalario, valSalario )
//                Log.d("CALC***",pesosSalario.toString())
                setValuesEditTextSalarios()
            }
            else cleanerSalario()
        }else cleanerSalario()
    }

    private fun calcInputPesosSalario(){
        if (txtPesosSalario.text.isNotEmpty()){
            val text = txtPesosSalario.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                pesosSalario = temp
                valSalario = UtilsGraphic().getValSalarioMinimoSpinner(spinSalario, valorSMG, valorSMG_ZLFN)
                nSalario = Operations().calSalarioPesos( pesosSalario, valSalario )
                setValuesEditTextSalarios()
            }
            else cleanerSalario()
        }else cleanerSalario()
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

    private fun showDialogInfo(tipo:String){
        if (!datosDisponibles) return //información solo con datos disponibles.
        var tit: String
        var mensaje: String

        if (tipo == "uma") {
            tit =  resources.getString(R.string.unidad_ma)
            val umaString = valorUma.toString()
            mensaje = resources.getString(R.string.uma_info, anioVigenciaUMA.toString(), umaString )
        }else  {
            tit =  resources.getString(R.string.smg)
            val smgString = valorSMG.toString()
            val smgZFString = valorSMG_ZLFN.toString()
            mensaje = resources.getString(R.string.salario_info, smgZFString, smgString, anioVigenciaSMG.toString())
        }

        //mostrar la fecha exacta y advertir si falta la vigencia esperada.
        val datos = impuestosLocales
        if (datos != null) {
            val vigencia = if (tipo == "uma") datos.umaVigencia else datos.smgVigencia
            mensaje += "\n\nVigente desde: $vigencia"
            if (ImpuestosPolitica.vencidos(datos, LocalDate.now())) {
                mensaje += "\nHay valores pendientes de actualización."
            }
        }
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
            val pesos = UtilsGraphic().round2Dec(pesos)
            txtPesos.tag = TAG_SYSTEM
            txtPesos.setText( pesos )
            txtPesos.tag = TAG_USER
        }
    }

    private fun setValuesEditTextSalarios(){
        if( IN_OPTION != IN_PESOS_SALARIO ){
            val pesos = UtilsGraphic().round2Dec(pesosSalario)
            txtPesosSalario.tag = TAG_SYSTEM
            txtPesosSalario.setText( pesos )
            txtPesosSalario.tag = TAG_USER
        }
        if( IN_OPTION != IN_SALARIO ){
            val salario = UtilsGraphic().round2Dec(nSalario)
            txtSalario.tag = TAG_SYSTEM
            txtSalario.setText( salario )
            txtSalario.tag = TAG_USER
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

    private fun cleanerSalario(){
        if( IN_OPTION != IN_SALARIO ){
            pesosSalario = 0.0
            txtSalario.tag = TAG_SYSTEM //La app es quien modifica el valor
            txtSalario.setText("")
            txtSalario.tag = TAG_USER // Regresa al supuesto de que el usuario modificará el siguiente valor
        }
        if( IN_OPTION != IN_PESOS_SALARIO ){
            uma = 0.0
            txtPesosSalario.tag = TAG_SYSTEM
            txtPesosSalario.setText("")
            txtPesosSalario.tag = TAG_USER
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
            }else if (txtSalario.text.hashCode() == s.hashCode() && txtSalario.tag == TAG_USER) {
                calc(IN_SALARIO)
            }else if (txtPesosSalario.text.hashCode() == s.hashCode() && txtPesosSalario.tag == TAG_USER){
                calc(IN_PESOS_SALARIO)
            }
        }
    }

    private fun validarIntersticial(){
        if( nResultados == limiteAds-1) loadInterstitial()
        else if ( nResultados == limiteAds ) {
            showInterstitial()
            nResultados = 0
        }
    }
    private fun loadInterstitial() {
//        Log.d("ADS***", "LOAD***")
        val adRequest = AdRequest.Builder().build()
        val idAds = resources.getString(R.string.imp_inter_uma)

        InterstitialAd.load(
            requireContext(), idAds, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    Log.d(TAG, adError?.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
                }
            }
        )
    }
    private fun showInterstitial() {
//        Log.d("ADS***", "SHOW***")
        if ( mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
//                    Log.d(TAG, "Ad was dismissed.")
                    mInterstitialAd = null
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                    Log.d(TAG, "Ad failed to show.")
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
//                    Log.d("ADS***", "Ad showed fullscreen content.")
                }
            }
            mInterstitialAd?.show(requireActivity())
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