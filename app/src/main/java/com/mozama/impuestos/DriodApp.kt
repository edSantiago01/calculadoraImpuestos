package com.mozama.impuestos

import android.app.Application
import android.util.Log
import com.mozama.impuestos.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DriodApp: Application() {
    fun getRetrofitCatalogos(): Retrofit {
        val url = Constants.urlBaseMozama

        val logging = HttpLoggingInterceptor { message ->
            Log.d("API_HTTP", message)
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .apply { addInterceptor(logging) } // siempre, pero en release es NONE
            .connectTimeout(Constants.TIME_OUT, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(Constants.TIME_OUT, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl( url )
            .addConverterFactory( GsonConverterFactory.create() )
            .build()
    }
}