package com.example.gpsdraw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.gpsdraw.adapter.ViewPagerAdapter
import com.example.gpsdraw.fragment.GpsFragment
import com.example.gpsdraw.fragment.MainFragment
import com.example.gpsdraw.fragment.MyInfoFragment
import com.example.gpsdraw.fragment.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = MainFragment()
        val gpsFragment = GpsFragment()
        val mypageFragment = MyInfoFragment()
        val searchFragment = SearchFragment()

        // 네비게이션 바 설정
        var bnv = findViewById<BottomNavigationView>(R.id.bottomNavi)
        bnv.setOnItemSelectedListener { MenuItem ->
            when (MenuItem.itemId) {
                R.id.homeFragment -> replaceFragment(homeFragment)
                R.id.gpsFragment -> replaceFragment(gpsFragment)
                R.id.myPageFragment -> replaceFragment(mypageFragment)
                R.id.searchFragment -> replaceFragment(searchFragment)

            }
            true
        }


    }

    private fun replaceFragment(fragment: Fragment){
        Log.d("MainActivity", "${fragment}")
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainerView, fragment)
                commit()
            }
    }
}