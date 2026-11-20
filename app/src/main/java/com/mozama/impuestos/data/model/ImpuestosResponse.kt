package com.mozama.impuestos.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class ImpuestosResponse(
    @SerializedName("uma")
    val uma: Double? = null,

    @SerializedName("umaVigencia")
    val umaVigencia: String? = null,

    @SerializedName("smg")
    val smg: Double? = null,

    @SerializedName("zlfn")
    val zlfn: Double? = null,

    @SerializedName("smgVigencia")
    val smgVigencia: String? = null,

    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null
)
