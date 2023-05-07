package com.example.gpsdraw.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsdraw.R
import com.example.gpsdraw.adapter.UserPostAdapter
import com.example.gpsdraw.data.UserPostData

class MainFragment : Fragment() {

    private var linearLayoutManager: RecyclerView.LayoutManager? = null
    private var recyclerAdapter: RecyclerView.Adapter<UserPostAdapter.ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var postList = arrayListOf<UserPostData>(
            UserPostData("nickname1","Hello, world!")
            ,UserPostData("nickname2", "Hello, world!"),
            UserPostData("nickname3", "Hello, world!"),
            UserPostData("nickname4", "Hello, world!"))


        val view = inflater!!.inflate(R.layout.fragment_main, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.userPostView)

        recyclerAdapter = UserPostAdapter(postList)
        linearLayoutManager =  LinearLayoutManager(activity)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = recyclerAdapter

        return view
    }

}