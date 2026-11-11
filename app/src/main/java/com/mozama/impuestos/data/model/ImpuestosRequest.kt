package com.mozama.impuestos.data.model

import com.google.gson.annotations.SerializedName

data class ImpuestosRequest(
    @SerializedName("token")
    val token: String,
)
