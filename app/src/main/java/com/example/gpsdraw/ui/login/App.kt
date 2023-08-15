package com.example.gpsdraw.ui.login

import android.app.Application
import android.util.Log

class App : Application() {


    companion object{
        lateinit var prefs: Preference
        var nowLogin :Boolean = false
        //        var memberInfo : MemberInfo? = null
    }

    override fun onCreate() {
        Log.d("App 온크릿", "시작")
        prefs = Preference(applicationContext)
        val autoLogin = prefs.autoLogin
        if(autoLogin == true){
            val token = prefs.token
            if(token != null){
                nowLogin = true
            }
        }
        super.onCreate()
    }

}