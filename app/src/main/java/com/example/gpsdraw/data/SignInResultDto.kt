package com.example.gpsdraw.data

data class SignInResultDto(
    val token: String,
    val success: Boolean,
    val code: Int,
    val msg: String
)