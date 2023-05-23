package com.example.gpsdraw

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gpsdraw.fragment.MainFragment
import com.example.gpsdraw.fragment.MyInfoFragment
import com.example.gpsdraw.fragment.SearchFragment
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private var LOCATION_PERMISSION = 1004
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback //gps응답 값을 가져온다.
    private val PERMISSION = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION ,android.Manifest.permission.ACCESS_COARSE_LOCATION )
    private lateinit var fabbtn: FloatingActionButton
    private lateinit var fabstop: FloatingActionButton
    private lateinit var fabplay: FloatingActionButton
    private  var mylocList: MutableList<LatLng> = mutableListOf()
    private var isFabOpen = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        if (isPermitted()) {
            startProcess()
        }else{
            ActivityCompat.requestPermissions(this, PERMISSION, LOCATION_PERMISSION)
        }


        val homeFragment = MainFragment()
        val mypageFragment = MyInfoFragment()
        val searchFragment = SearchFragment()
        val slide = findViewById<SlidingUpPanelLayout>(R.id.slidingUpPanelLayout)


        // 네비게이션 바 설정
        var bnv = findViewById<BottomNavigationView>(R.id.bottomNavi)
        bnv.setOnItemSelectedListener { MenuItem ->
            when (MenuItem.itemId) {
                R.id.gpsFragment -> {
                    replaceFragment(homeFragment)
                    slide.isTouchEnabled = true
                }
                R.id.myPageFragment -> {
                    slide.isTouchEnabled = true
                    replaceFragment(mypageFragment)
                }
                R.id.homeFragment -> {
                    slide.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                    slide.isTouchEnabled = false
                }
            }
            true
        }

        fabbtn = findViewById<FloatingActionButton>(R.id.fab_draw)
        fabplay = findViewById<FloatingActionButton>(R.id.fab_draw_sub)
        fabstop = findViewById<FloatingActionButton>(R.id.fab_draw_sub2)

        val search = findViewById<SearchView>(R.id.searchView2)
        val tagScoll = findViewById<HorizontalScrollView>(R.id.tag_scroll)

        // 플로팅 버튼 이벤트 - 메인
        fabbtn.setOnClickListener{

            if (fabbtn.isClickable == true){
                fabplay.backgroundTintList =  ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            }
            else{
                fabplay.backgroundTintList =  ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))

            }

            fabbtn.setRippleColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)))

            fabbtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))

            toggleFab()


        }
        // 플로팅 버튼 클릭 이벤트 - 재생
        fabplay.setOnClickListener {
            if (search.visibility == View.VISIBLE) {
                search.visibility = View.GONE
                tagScoll.visibility = View.GONE
                onResume()
                Toast.makeText(this, "플레이", Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(this, "이미 플레이 중입니다", Toast.LENGTH_SHORT).show()

            }
        }
        // 플로팅 버튼 클릭 이벤트 - 종료
        fabstop.setOnClickListener {
            if (search.visibility == View.GONE) {
                search.visibility = View.VISIBLE
                tagScoll.visibility = View.VISIBLE
                Toast.makeText(this, "정지", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "아직 재생하지 않았습니다!", Toast.LENGTH_SHORT).show()

            }

        }





    }
    private fun isPermitted() : Boolean{
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    private  fun startProcess(){
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    @UiThread
    //NaverMap 객체는 오직 콜백 메서드를 이용해서 얻어올 수 있습니다.
    override fun onMapReady(map: NaverMap) {
        naverMap = map
        val locationOverlay = naverMap.locationOverlay

        val cameraPosition = CameraPosition(
            locationOverlay.position, // 대상 지점
            16.0, // 줌 레벨
            0.0, // 기울임 각도
            180.0 // 베어링 각도
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setUpdateLocationListner()
        locationSource = FusedLocationSource(this@MainActivity , LOCATION_PERMISSION)

        naverMap.cameraPosition = cameraPosition
        naverMap.locationSource = locationSource
        //위치추적
        ActivityCompat.requestPermissions(this, PERMISSION, LOCATION_PERMISSION)

        //ui 설정
        val uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = false
        uiSettings.isCompassEnabled = false
        uiSettings.isZoomControlEnabled = false


    }


    @SuppressLint("MissingPermission")
    fun setUpdateLocationListner() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY //높은 정확도
            interval = 1000 //1초에 한번씩 GPS 요청
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for ((i, location) in locationResult.locations.withIndex()) {
                    Log.d("location: ", "${location.latitude}, ${location.longitude}")
                    setLastLocation(location)
                }
            }
        }
        //location 요청 함수 호출 (locationRequest, locationCallback)

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }//좌표계를 주기적으로 갱신

    fun setLastLocation(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
        // 현재 갱신되는 gps의 좌표값이 부동시에도 일정하지 못한 문제. 좌표값 중 변동률이 심한 부분에 대한 라운딩 필요
        // 연결 받아와서 리스트에 추가시 오류 발생. -> 초기화 문제 -> 해결
        mylocList.add(myLocation)
        Log.d("위치 갱신", "${mylocList.size}}")
        // 현재
        mylocList.add(myLocation)
        val path = PathOverlay()
        path.coords = mylocList
        path.progress = 0.5
        path.map = naverMap
        //marker.map = null
    }
    /***
     *  플로팅 액션 버튼 클릭시 동작하는 애니메이션 효과 세팅
     */
    private fun toggleFab() {

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션 세팅
        if (isFabOpen) {
            ObjectAnimator.ofFloat(fabplay, "translationX", 0f).apply { start() }
            ObjectAnimator.ofFloat(fabstop, "translationX", 0f).apply { start() }

            // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션 세팅
        } else {
            ObjectAnimator.ofFloat(fabplay, "translationX", 150f).apply { start() }
            ObjectAnimator.ofFloat(fabstop, "translationX", 300f).apply { start() }
        }

        isFabOpen = !isFabOpen

    }

    private fun replaceFragment(fragment: Fragment){
        Log.d("MainActivity", "${fragment}")
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainerView, fragment)
                commit()
            }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when {
            requestCode != LOCATION_PERMISSION -> {
                return
            }
            else -> {
                when {
                    locationSource.onRequestPermissionsResult(requestCode,permissions,grantResults) -> {
                        if (!locationSource.isActivated){
                            naverMap.locationTrackingMode = LocationTrackingMode.None
                        }else{
                            naverMap.locationTrackingMode = LocationTrackingMode.Follow
                        }
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}