package com.example.gpsdraw.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.gpsdraw.R
import com.example.gpsdraw.data.UserPostData
import com.example.gpsdraw.databinding.ItemPostBinding


class UserPostAdapter : RecyclerView.Adapter<UserPostAdapter.ViewHolder>() {

    private val defaultColor = R.color.black
    private val clickedColor = R.color.red
    private var isClicked = false
    lateinit var items: ArrayList<UserPostData>


    fun build(i: ArrayList<UserPostData>): UserPostAdapter  {
        items = i
        return this
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostAdapter.ViewHolder =
        ViewHolder(
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            parent.context
        )

    override fun onBindViewHolder(holder: UserPostAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
    class ViewHolder(val binding: ItemPostBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: UserPostData) {
            with(binding)
            {
                nickname.text = item.nickname
                contentsSummary.text = item.content
                imgViewPager.adapter = ViewPagerAdapter().build(item.colors)
            }
        }
        init {
        }
    }
/*
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val heartImg: ImageView = itemView.findViewById(R.id.heartImg)
        val nickname: TextView = itemView.findViewById(R.id.nickname)
        val content: TextView = itemView.findViewById(R.id.contents_summary)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.imgViewPager)

        fun bind(userPostData: UserPostData) {
            // Set other data to views
            nickname.text = userPostData.nickname
            content.text = userPostData.content

            // Setup ViewPager
            val colorList = arrayListOf<Int>(
                R.color.black,
                R.color.red,
                R.color.green,
                R.color.yellow
            )
           // Log.d("뷰 ", "실행?")
           //val viewPagerAdapter = ViewPagerAdapter(colorList)
           // Log.d("뷰 ", "실행2")

            //viewPager.adapter = viewPagerAdapter
        }

        init {
            heartImg.setOnClickListener {
                isClicked = !isClicked // 클릭 상태 전환

                // 클릭 상태에 따라 색상 변경
                val context = itemView.context
                val colorRes = if (isClicked) clickedColor else defaultColor
                heartImg.setColorFilter(ContextCompat.getColor(context, colorRes))

                // 클릭 시 빨간색 하트 색상 초기화 (클릭 상태가 false인 경우만)
                if (!isClicked) {
                    heartImg.clearColorFilter()
                }
            }
        }
    }
 */
}
