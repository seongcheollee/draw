package com.example.gpsdraw.data.retrofit

import com.google.gson.annotations.SerializedName

data class ClientErrorBody(
    @SerializedName("code")val code:String,
    @SerializedName("message") val message:String
)