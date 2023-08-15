package com.example.gpsdraw.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsdraw.PostCreateActivity
import com.example.gpsdraw.ProfileEditActivity
import com.example.gpsdraw.R
import com.example.gpsdraw.SettingActivity
import com.example.gpsdraw.adapter.GridAdapter
import com.example.gpsdraw.adapter.OnImageClickListener
import com.example.gpsdraw.adapter.OnItemClickListener
import com.example.gpsdraw.adapter.RouteListAdapter
import com.example.gpsdraw.data.RoadImages

class MyInfoFragment : Fragment() {

    private var gridLayoutManager: RecyclerView.LayoutManager? = null
    private var horLayoutManager: RecyclerView.LayoutManager? = null
    private var setButton : ImageButton? = null
    private var bookmarkButton : Button? = null
    private var profileSetButton : Button? = null

    private var gridrecyclerAdapter: RecyclerView.Adapter<GridAdapter.ViewHolder>? = null
    private var routerecyclerAdapter: RecyclerView.Adapter<RouteListAdapter.ViewHolder>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var imglist = arrayListOf<RoadImages>(
            RoadImages(""),
            RoadImages(""),
            RoadImages(""),
            RoadImages(""),
            RoadImages(""),
            RoadImages(""),
            RoadImages(""),
            RoadImages(""),
            RoadImages(""),
            RoadImages(""),
            )


        val view = inflater!!.inflate(R.layout.fragment_my_info, container, false)
        val gridRecyclerView: RecyclerView = view.findViewById(R.id.myGridView)
        val routeRecyclerView: RecyclerView = view.findViewById(R.id.myRouteView)


        val gridrecyclerAdapter = GridAdapter(imglist, object : OnItemClickListener {
            override fun onItemClick(roadImage: RoadImages) {
                // 클릭시 PostFragment로 이동
                val postFragment = PostFragment()

                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, postFragment)
                    .addToBackStack(null) // 뒤로가기 버튼을 눌렀을 때 이전 Fragment로 돌아갈 수 있도록
                    .commit()
            }
        })
        gridLayoutManager =  GridLayoutManager(activity, 3,RecyclerView.HORIZONTAL, false)

        gridRecyclerView.layoutManager = gridLayoutManager
        gridRecyclerView.adapter = gridrecyclerAdapter

        routerecyclerAdapter = RouteListAdapter(imglist)
        horLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        routeRecyclerView.layoutManager = horLayoutManager
        routeRecyclerView.adapter = routerecyclerAdapter

        setButton = view.findViewById(R.id.settingBtn)
        setButton?.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            startActivity(intent)
        }


        bookmarkButton = view.findViewById(R.id.bookmarkBtn)



        setButton?.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            startActivity(intent)
        }
        profileSetButton = view.findViewById(R.id.profileEdit)

        profileSetButton?.setOnClickListener {
            val intent = Intent(activity, ProfileEditActivity::class.java)
            startActivity(intent)
        }
        return view
    }


}