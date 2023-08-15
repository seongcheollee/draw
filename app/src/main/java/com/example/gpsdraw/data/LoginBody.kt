package com.example.gpsdraw.data

import com.google.gson.annotations.SerializedName

data class LoginBody(
    @SerializedName("id") val uid:String,
    @SerializedName("password") val password:String,
    )
