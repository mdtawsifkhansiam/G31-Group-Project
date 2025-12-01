package com.example.restaurantguide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.restaurantguide.data.Restaurant
import com.example.restaurantguide.databinding.ItemRestaurantBinding

class RestaurantListAdapter(val listener: OnItemClickListener) :
    ListAdapter<Restaurant, RestaurantListAdapter.ViewHolder>(DIFF) {

    interface OnItemClickListener {
        fun onItemClick(restaurantId: Long)
    }

    inner class ViewHolder(val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(adapterPosition).id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(b)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = getItem(position)
        holder.binding.tvName.text = r.name
        holder.binding.tvAddress.text = r.address
        holder.binding.tvTags.text = r.tags ?: ""
        holder.binding.ratingBar.rating = r.rating.toFloat()
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Restaurant>() {
            override fun areItemsTheSame(old: Restaurant, new: Restaurant) = old.id == new.id
            override fun areContentsTheSame(old: Restaurant, new: Restaurant) = old == new
        }
    }
}
