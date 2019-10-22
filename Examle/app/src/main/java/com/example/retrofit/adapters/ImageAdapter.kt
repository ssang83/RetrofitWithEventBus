package com.example.retrofit.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.retrofit.R
import com.example.retrofit.log.Logger
import com.example.retrofit.network.response.submodel.ImageResult
import kotlinx.android.synthetic.main.fragment_image.view.*

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    private var mItems:MutableList<ImageResult> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_image, parent, false))

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        mItems[position].let { item ->
            holder.itemView.txtID.text = item.id.toString()
            holder.itemView.content.text = item.name

            Glide.with(holder.itemView.context)
                .load(item.img)
                .apply(RequestOptions.centerCropTransform().placeholder(R.mipmap.ic_launcher_round))
                .into(holder.itemView.imageView)
        }

        holder.itemView.setOnClickListener { Logger.d("position ${position}") }
    }

    override fun getItemCount() = mItems.size

    fun updateData(items:MutableList<ImageResult>) {
        mItems = items
        notifyDataSetChanged()
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}