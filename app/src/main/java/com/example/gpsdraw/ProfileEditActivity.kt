package com.example.gpsdraw

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.naver.maps.map.LocationTrackingMode

class ProfileEditActivity : AppCompatActivity() {
    val STORAGE_CODE = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        val picbtn : TextView = findViewById(R.id.editPicbtn)
        picbtn.setOnClickListener { GetAlbum()}


        val backMainBtn: ImageView = findViewById(R.id.backMainbtn2)
        backMainBtn.setOnClickListener {
            finish() // 현재 액티비티 종료
        }


        val editbtn: Button = findViewById(R.id.editbtn)
        editbtn.setOnClickListener {
            finish() // 현재 액티비티 종료
        }
    }



    fun GetAlbum() {
        try {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_CODE) && checkPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_CODE)) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, STORAGE_CODE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 예외 처리를 위한 다른 로직 추가
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView = findViewById<ImageView>(R.id.profileImg)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                STORAGE_CODE -> {
                    val uri = data?.data
                    imageView.setImageURI(uri)
                }
            }
        }
    }

}