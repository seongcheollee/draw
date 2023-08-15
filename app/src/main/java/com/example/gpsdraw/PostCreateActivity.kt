package com.example.gpsdraw

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsdraw.adapter.MultiphotoAdapter

class PostCreateActivity : AppCompatActivity() {
    val STORAGE_CODE = 99
    private val selectedPhotos = mutableListOf<Uri>() // 선택한 이미지들을 저장할 리스트
    private lateinit var photoAdapter: MultiphotoAdapter
    lateinit var gBtn : ImageButton
    lateinit var okBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_create)

        gBtn = findViewById(R.id.galleryBtn)
        gBtn.setOnClickListener {
            GetAlbum()
        }

        photoAdapter = MultiphotoAdapter(this, selectedPhotos)
        val recyclerView: RecyclerView = findViewById(R.id.multiPhotorecyclerView)
        recyclerView.adapter = photoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        okBtn = findViewById(R.id.okBtn)
        okBtn.setOnClickListener {
            finish()
        }


    }


    fun GetAlbum() {
        try {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_CODE) && checkPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_CODE)) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_PICK
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

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                STORAGE_CODE -> {
                    Log.d("사진 넣기", "시작")
                    val clipData = data?.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val uri = clipData.getItemAt(i).uri
                            Log.d("사진 추가",uri.toString())

                            selectedPhotos.add(uri) // 선택한 이미지들을 리스트에 추가
                        }
                    } else {
                        val uri = data?.data
                        if (uri != null) {
                            selectedPhotos.add(uri) // 선택한 이미지를 리스트에 추가
                        }
                    }
                    Log.d("어댑터 새로고침", "!!")
                    photoAdapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알리기

                }
            }
        }
    }
}





