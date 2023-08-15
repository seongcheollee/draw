package com.example.gpsdraw.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.gpsdraw.MainActivity
import androidx.activity.viewModels
import com.example.gpsdraw.R
import com.example.gpsdraw.SignUpFormActivity
import com.example.gpsdraw.Utility

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var loginId : EditText
    private lateinit var loginPw : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginId = findViewById(R.id.loginid)
        loginPw = findViewById(R.id.loginPassword)

        val loginBtn = findViewById<Button>(R.id.login)
        val signup = findViewById<Button>(R.id.signup)


        loginBtn.setOnClickListener{
            if(Utility.istNetworkConnected(this)) {
                when {
                    loginId.text.isEmpty() -> {
                        showToastMsg(getString(R.string.login_empty_email))
                        Utility.focusEditText(this,loginId)
                    }
                    loginPw.text.isEmpty() -> {
                        showToastMsg(getString(R.string.login_empty_password))
                        Utility.focusEditText(this,loginPw)
                    }
                    else -> {
                        loginProcess()
                        showToastMsg("로그인 되었습니다.")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(intent)

                    }
                }
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }

        signup.setOnClickListener{
            val intent = Intent(this, SignUpFormActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }


    }


    // 수정
    private fun loginProcess(){
        viewModel.requestLogin(loginId.text.toString(), loginPw.text.toString())
        viewModel.token.observe(this
        ) {
            when (it) {

                "not registered id" -> {
                    showToastMsg(getString(R.string.login_not_registered_email))
                    Utility.focusEditText(this, loginId)
                }

                "incorrect password" -> {
                    showToastMsg(getString(R.string.login_incorrect_password))
                    Utility.focusEditText(this, loginPw)
                }

                "server error" -> {
                    showToastMsg(getString(R.string.login_server_error))
                }

                else -> { //토큰을 정상적으로 받았을 때.
                    App.prefs.token = it
                    App.nowLogin = true
                    Log.d("로그인", "${App.prefs.token}")

                }
            }
        }
    }
    companion object {
        fun newIntent(context: Context): Intent{
            return Intent(context, LoginActivity::class.java)
        }
    }
    private fun showToastMsg(msg:String){ Toast.makeText(this,msg, Toast.LENGTH_SHORT).show() }


}

