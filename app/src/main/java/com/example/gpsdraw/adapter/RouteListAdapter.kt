package com.example.gpsdraw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsdraw.R
import com.example.gpsdraw.data.RoadImages

class RouteListAdapter(val imgs : ArrayList<RoadImages>) : RecyclerView.Adapter<RouteListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.route_item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.content.text = imgs[position].id
    }

    override fun getItemCount(): Int {
        return imgs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val content: TextView = itemView.findViewById(R.id.imgname)

    }

}