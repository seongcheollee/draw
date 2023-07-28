package com.example.gpsdraw

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gpsdraw.fragment.MainFragment
import com.example.gpsdraw.fragment.MyInfoFragment
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.io.FileOutputStream
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private var LOCATION_PERMISSION = 1004
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback //gps응답 값을 가져온다.
    private val PERMISSION = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION ,android.Manifest.permission.ACCESS_COARSE_LOCATION )
    private lateinit var fabbtn: View
    private lateinit var fabstop: View
    private lateinit var fabpause: View
    private lateinit var fabplay: View
    private  var mylocList: MutableList<LatLng> = mutableListOf()
    private var isFabOpen = false
    // 보정
    private val FILTER_SIZE = 10 // 이동 평균 필터 크기
    private val locationFilter = mutableListOf<LatLng>() // GPS 좌표 필터링을 위한 리스트
    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99

    private var isDrawing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        //
        if (isPermitted()) startProcess() else ActivityCompat.requestPermissions(this, PERMISSION, LOCATION_PERMISSION)

        setupBottomNavigationView()
        setupFabButton()

    }//onCreate





    private fun setupBottomNavigationView() {

        val homeFragment = MainFragment()
        val mypageFragment = MyInfoFragment()
        val slide = findViewById<SlidingUpPanelLayout>(R.id.slidingUpPanelLayout)
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavi)

        bnv.setOnItemSelectedListener { MenuItem ->
            when (MenuItem.itemId) {
                R.id.gpsFragment -> {
                    replaceFragment(homeFragment)
                    slide.isTouchEnabled = true
                    slide.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                }
                R.id.myPageFragment -> {
                    slide.isTouchEnabled = true
                    replaceFragment(mypageFragment)
                    slide.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                }
                R.id.homeFragment -> {
                    slide.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                    slide.isTouchEnabled = false
                    CallCamera()
                }
            }
            true
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


    // Naver map 연결
    private  fun startProcess(){
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    @UiThread
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
            interval = 10000 //10초에 한번씩 GPS 요청
        }

        locationCallback = object : LocationCallback() {
            var counter = 0

            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                counter++
                if (counter % 2 == 0) { // 10초마다 한 번씩 처리
                    val location = locationResult.lastLocation
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
        // 최초 위치 설정
        if (mylocList.size < 2) {
            mylocList.add(myLocation)
            return
        }
        // 이동 거리 계산
        val previousLocation = mylocList.last()
        val distance = previousLocation.distanceTo(myLocation)
        Log.d("이동 거리", "$distance")
        // 이동 거리가 10미터 이상인 경우에만 좌표 추가
        if (distance >= 10) { mylocList.add(myLocation) }
        Log.d("위치 갱신", "${mylocList.size}")
        drawPath(mylocList)
    }
    private fun drawPath(coords: List<LatLng>) {
        val path = PathOverlay().apply {
            this.coords = coords
            this.progress = 0.5
            this.map = naverMap
        }

        if (coords.size >= FILTER_SIZE) {
            val filteredLocation = getFilteredLocation()
            val filteredPath = PathOverlay().apply {
                this.coords = filteredLocation
                this.progress = 0.5
                this.map = naverMap
            }
        }
    }

    private fun getFilteredLocation(): List<LatLng> {
        val filteredLocation = mutableListOf<LatLng>()

        for (i in FILTER_SIZE - 1 until mylocList.size) {
            val latSum = mylocList.subList(i - FILTER_SIZE + 1, i + 1).sumByDouble { it.latitude }
            val lonSum = mylocList.subList(i - FILTER_SIZE + 1, i + 1).sumByDouble { it.longitude }
            val latAvg = latSum / FILTER_SIZE
            val lonAvg = lonSum / FILTER_SIZE
            val filteredLatLng = LatLng(latAvg, lonAvg)
            filteredLocation.add(filteredLatLng)
        }

        return filteredLocation
    }




    private fun setupFabButton() {
        fabbtn = findViewById(R.id.fab_draw)
        fabplay = findViewById(R.id.fab_draw_sub)
        fabstop = findViewById(R.id.fab_draw_sub2)
        fabpause = findViewById(R.id.fab_draw_sub3)

        val search = findViewById<SearchView>(R.id.searchView2)
        val tagScroll = findViewById<HorizontalScrollView>(R.id.tag_scroll)

        fabbtn.setOnClickListener { view ->
            if (fabbtn.isClickable) {
                fabplay.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            } else {
                fabplay.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))
            }
            toggleFab()
        }

        fabplay.setOnClickListener { view ->
            if (search.visibility == View.VISIBLE) {
                search.visibility = View.GONE
                tagScroll.visibility = View.GONE
                isDrawing = true
                onResume()
                Snackbar.make(view, "그리기 시작", Snackbar.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, "이미 그리고 있습니다", Toast.LENGTH_SHORT).show()
            }
        }

        fabstop.setOnClickListener { view ->
            if (search.visibility == View.GONE) {
                search.visibility = View.VISIBLE
                tagScroll.visibility = View.VISIBLE
                Snackbar.make(view, "종료!", Snackbar.LENGTH_LONG)
                    .setAction("자랑하기") {}
                    .setActionTextColor(Color.YELLOW)
                    .show()
            } else {
                Toast.makeText(this, "아직 그리지 않았습니다!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /***
     *  플로팅 액션 버튼 클릭시 동작하는 애니메이션 효과 세팅
     */
    private fun toggleFab() {

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션 세팅
        if (isFabOpen) {
            ObjectAnimator.ofFloat(fabplay, "translationX", 0f).apply { start() }
            ObjectAnimator.ofFloat(fabstop, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(fabpause, "translationY", 0f).apply { start() }


            // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션 세팅
        } else {
            ObjectAnimator.ofFloat(fabplay, "translationX", -150f).apply { start() }
            ObjectAnimator.ofFloat(fabstop, "translationY", -300f).apply { start() }
            ObjectAnimator.ofFloat(fabpause, "translationY", -150f).apply { start() }

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                    if (!locationSource.isActivated) {
                        naverMap.locationTrackingMode = LocationTrackingMode.None
                    } else {
                        naverMap.locationTrackingMode = LocationTrackingMode.Follow
                    }
                }
            }
            CAMERA_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "카메라 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }
            }
            STORAGE_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
                return false
            }
        }
        return true
    }





    // 사진 저장
    fun saveFile(fileName:String, mimeType:String, bitmap: Bitmap): Uri?{

        var CV = ContentValues()

        // MediaStore 에 파일명, mimeType 을 지정
        CV.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        CV.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        // 안정성 검사
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            CV.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        // MediaStore 에 파일을 저장
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CV)
        if(uri != null){
            var scriptor = contentResolver.openFileDescriptor(uri, "w")

            val fos = FileOutputStream(scriptor?.fileDescriptor)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                CV.clear()
                // IS_PENDING 을 초기화
                CV.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, CV, null, null)
            }
        }
        return uri
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    if (data?.extras?.get("data") != null) {
                        val img = data?.extras?.get("data") as Bitmap
                        val uri = saveFile(RandomFileName(), "image/jpeg", img)
//                        imageView.setImageURI(uri)
                    }
                }
                // 필요한 경우 다른 요청 코드도 여기에서 처리합니다.
            }
        }
    }



    // 파일명을 날짜 저장
    fun RandomFileName() : String{
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
        return fileName
    }


    private fun CallCamera() {
        try {
            if (checkPermission(Manifest.permission.CAMERA, CAMERA_CODE) && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_CODE) && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_CODE)) {
                val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(itt, CAMERA_CODE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 예외 처리를 위한 다른 로직 추가
        }
    }
}