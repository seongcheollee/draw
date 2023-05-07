package com.example.gpsdraw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsdraw.R
import com.example.gpsdraw.data.UserPostData


class UserPostAdapter(var userPostList :ArrayList<UserPostData>) : RecyclerView.Adapter<UserPostAdapter.ViewHolder>() {
    private val postList = userPostList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]
        holder.nickname.text = post.nickname
        holder.content.text = post.content
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nickname: TextView = itemView.findViewById(R.id.nickname)
        val content: TextView = itemView.findViewById(R.id.contents_summary)

    }
}
