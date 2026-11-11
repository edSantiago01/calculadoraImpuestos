package com.mozama.impuestos.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class ImpuestosResponse(
    @SerializedName("uma")
    val uma: Float,

    @SerializedName("umaVigencia")
    val umaVigencia: String,

    @SerializedName("smg")
    val smg: Float,

    @SerializedName("zlfn")
    val zlfn: Float,

    @SerializedName("smgVigencia")
    val smgVigencia: String,
)
