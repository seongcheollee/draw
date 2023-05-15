package com.example.gpsdraw.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsdraw.R
import com.example.gpsdraw.adapter.GridAdapter
import com.example.gpsdraw.adapter.UserPostAdapter
import com.example.gpsdraw.data.RoadImages
import com.example.gpsdraw.data.UserPostData

class MyInfoFragment : Fragment() {

    private var gridLayoutManager: RecyclerView.LayoutManager? = null
    private var recyclerAdapter: RecyclerView.Adapter<GridAdapter.ViewHolder>? = null

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
        val recyclerView: RecyclerView = view.findViewById(R.id.myGridView)

        recyclerAdapter = GridAdapter(imglist)
        gridLayoutManager =  GridLayoutManager(activity, 3)

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = recyclerAdapter
        return view
    }

}