package com.example.gpsdraw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentLsit = listOf(menuFragment1(),menuFragment2())
        val viewPager_menu : ViewPager2 = findViewById(R.id.viewPager_menu)
        val adapter = ViewPagerAdapter(this) // 어댑터 생성
        adapter.fragments = fragmentLsit
        viewPager_menu.adapter = adapter
    }
}