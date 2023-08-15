package com.example.gpsdraw.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsdraw.databinding.ItemViewBinding

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    lateinit var items: ArrayList<String>

    fun build(i: ArrayList<String>): ViewPagerAdapter {
        items = i
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerAdapter.ViewHolder =
        ViewHolder(ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewPagerAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }
    class ViewHolder(val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.tvColor.text = item
        }
    }
}
