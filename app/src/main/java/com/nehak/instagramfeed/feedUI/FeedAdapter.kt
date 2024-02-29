package com.nehak.instagramfeed.feedUI

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nehak.instagramfeed.dataModels.VideoRoot
import com.nehak.instagramfeed.databinding.FeedItemLayoutBinding
import com.nehak.instagramfeed.feedUI.holders.FeedViewHolder


/**
 * Create By Neha Kushwah
 */
class FeedAdapter(private val context: Context) : RecyclerView.Adapter<FeedViewHolder>() {
    private val TAG: String? = "FeedAdapter"
    var dataList: ArrayList<VideoRoot> = ArrayList();

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(
            FeedItemLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    fun addData(data: List<VideoRoot>) {
        val startPosition = dataList.size
        this.dataList.addAll(data)
        notifyItemRangeInserted(startPosition, data.size)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.recyclerViewHorizontal.reset()
        holder.recyclerViewHorizontal.id = View.generateViewId()
        Log.d(TAG, "onBindViewHolder: dataList[position].video === " + dataList[position].link)
        holder.recyclerViewHorizontal.setVideoUri(Uri.parse(dataList[position].link))
        Glide.with(context).load(dataList[position].thumbnail).centerCrop()
            .into(holder.binding.backBlurImage)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}
