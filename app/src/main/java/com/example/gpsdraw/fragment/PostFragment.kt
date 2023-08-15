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
import com.example.gpsdraw.databinding.FragmentMainBinding
import com.example.gpsdraw.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPostBinding.inflate(inflater, container, false)
        val view = binding.root

        var nList = arrayListOf("1", "2", "3", "4")

        var postList = arrayListOf<UserPostData>(
            UserPostData("nickname1","Hello, world!", nList),
            UserPostData("nickname1","Hello, world!",nList),
            UserPostData("nickname1","Hello, world!",nList),
            UserPostData("nickname1","Hello, world!",nList),
        )


        _binding!!.userPostView2.apply {
            adapter = UserPostAdapter().build(postList)
            layoutManager =
                    //LinearLayoutManager(this@MainFragment, LinearLayoutManager.VERTICAL, false)
                LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)

        }

        return view
    }
    

}