package com.example.gpsdraw.adapter

import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsdraw.R
import com.example.gpsdraw.data.RoadImages


interface OnItemClickListener {
    fun onItemClick(roadImage: RoadImages)
}
class GridAdapter(val imgs : ArrayList<RoadImages>,private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<GridAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.content.text = imgs[position].id
    }

    override fun getItemCount(): Int {
        return imgs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val content: TextView = itemView.findViewById(R.id.imgname)
        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(itemView.context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    val animation = AlphaAnimation(1.0f, 0.0f)
                    animation.duration = 200 // 깜빡거리는 시간 (밀리초)
                    itemView.startAnimation(animation)

                    return true
                }
            })

            itemView.setOnTouchListener(this)
        }
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            gestureDetector.onTouchEvent(event)

            // 클릭 이벤트 처리
            if (event?.action == MotionEvent.ACTION_UP) {
                itemClickListener.onItemClick(imgs[adapterPosition])
            }

            return true
        }
    }





}

