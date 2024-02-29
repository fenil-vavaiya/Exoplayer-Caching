package com.nehak.instagramfeed.feedUI.holders

import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.nehak.instagramfeed.databinding.FeedItemLayoutBinding
import com.nehak.instagramfeed.player.InstaLikePlayerView

/**
 * Create By Neha Kushwah
 */
class FeedViewHolder(root: View) :  RecyclerView.ViewHolder(root) {
    lateinit var recyclerViewHorizontal: InstaLikePlayerView
    lateinit var binding: FeedItemLayoutBinding

    constructor(binding: FeedItemLayoutBinding) : this(binding.root) {
        this.binding = binding
        recyclerViewHorizontal =
            binding.feedPlayerView

        /** Keep the item center aligned**/
    }
}