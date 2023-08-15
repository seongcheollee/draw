package com.example.gpsdraw.ui.login

import LoginServiceCreator
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


class LoginViewModel : ViewModel() {
    val client : LoginServiceCreator
    lateinit var token : LiveData<String>


    init {
        client = LoginServiceCreator()
    }

    fun requestLogin(id:String,password:String){
        token = client.requestLogin(id,password)
    }



}