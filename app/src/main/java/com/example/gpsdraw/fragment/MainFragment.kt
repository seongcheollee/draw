package com.example.gpsdraw.fragment

import android.annotation.SuppressLint
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

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        var nList = arrayListOf("1", "2", "3", "4")

        var postList = arrayListOf<UserPostData>(
            UserPostData("잔선철","맥주 ㄱ?!", nList),
            UserPostData("잔든스유니","아~..!",nList),
            UserPostData("잔자라잔수현","뭐",nList),
            UserPostData("멍멍이","Hello, world!",nList),
            )

        _binding!!.userPostView.apply {
            adapter = UserPostAdapter().build(postList)
            layoutManager =
                LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)

        }

        // NonSwipeableViewPager로 대체
        binding.userPostView.adapter = UserPostAdapter().build(postList)
        binding.userPostView.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)


        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}