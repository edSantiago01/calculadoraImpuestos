package com.mozama.impuestos.api

import com.mozama.impuestos.data.model.ImpuestosRequest
import com.mozama.impuestos.data.model.ImpuestosResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("impuestos/appGetImpuestos")
    suspend fun getImpuestos(@Body request: ImpuestosRequest): Response<ImpuestosResponse>
}