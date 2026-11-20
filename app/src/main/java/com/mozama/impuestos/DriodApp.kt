package com.mozama.impuestos

import android.app.Application
import android.util.Log
import com.mozama.impuestos.api.ApiService
import com.mozama.impuestos.data.ImpuestosLocal
import com.mozama.impuestos.data.ImpuestosRepository
import com.mozama.impuestos.utils.Constants
import com.mozama.impuestos.utils.Constants.Companion.TOKEN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DriodApp: Application() {
    // el trabajo sobrevive a rotaciones; no conserva Activity ni Fragment.
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val impuestosRepository: ImpuestosRepository by lazy {
        ImpuestosRepository(ImpuestosLocal(this),
            getRetrofitCatalogos().create(ApiService::class.java), ::obtenerTokenImpuestos)
    }

    private fun obtenerTokenImpuestos(): String {
        return TOKEN
    }

    fun actualizarImpuestosSiCorresponde() {
        applicationScope.launch { impuestosRepository.actualizarSiCorresponde() }
    }

    private val retrofitCatalogosInstance: Retrofit by lazy {
        val url = Constants.urlBaseMozama
        val logging = HttpLoggingInterceptor { message -> Log.d("API_HTTP", message) }
            .apply {
                // BASIC evita imprimir el token del cuerpo JSON incluso en debug.
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
                else HttpLoggingInterceptor.Level.NONE
            }
        val okHttpClient = OkHttpClient.Builder()
            .apply { addInterceptor(logging) }
            .connectTimeout(Constants.TIME_OUT, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(Constants.TIME_OUT, TimeUnit.MINUTES)
            .callTimeout(45, TimeUnit.SECONDS) //límite total para no quedar cargando.
            .retryOnConnectionFailure(true)
            .build()
        Retrofit.Builder().client(okHttpClient).baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    fun getRetrofitCatalogos(): Retrofit = retrofitCatalogosInstance
}