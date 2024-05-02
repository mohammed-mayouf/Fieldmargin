package com.mayouf.fieldmargintest.data.model

import com.google.gson.annotations.SerializedName

data class Certificate(
    @SerializedName("id") var id: String? = null,
    @SerializedName("originator") var originator: String? = null,
    @SerializedName("originator-country") var originatorCountry: String? = null,
    @SerializedName("owner") var owner: String? = null,
    @SerializedName("owner-country") var ownerCountry: String? = null,
    @SerializedName("status") var status: String? = null

)