package com.example.gpsdraw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)



        val backMainBtn: ImageView = findViewById(R.id.backMainbtn)
        backMainBtn.setOnClickListener {
            finish() // 현재 액티비티 종료

        }
    }

    override fun onStart() {
        super.onStart()
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up)
        val backMainBtn: ImageView = findViewById(R.id.backMainbtn)
        backMainBtn.startAnimation(slideUpAnimation) // 애니메이션 실행
    }
}