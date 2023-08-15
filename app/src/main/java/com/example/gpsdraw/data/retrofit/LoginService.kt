package com.example.gpsdraw.data.retrofit

import com.example.gpsdraw.data.LoginBody
import com.example.gpsdraw.data.SignInResultDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("/sign-api/sign-in/") // Make sure to provide the correct endpoint URL
    fun requestLogin(@Body jsonbody: LoginBody) : Call<SignInResultDto>


}