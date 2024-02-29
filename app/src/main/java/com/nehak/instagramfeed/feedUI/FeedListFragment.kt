package com.nehak.instagramfeed.feedUI

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.nehak.instagramfeed.autoPlay.VideoAutoPlayHelper
import com.nehak.instagramfeed.dataModels.VideoRoot
import com.nehak.instagramfeed.databinding.FragmentFeedListBinding
import com.nehak.instagramfeed.player.InstaLikePlayerView
import java.io.File
import java.io.IOException

class FeedListFragment : Fragment() {
    private val TAG: String = "FeedListFragment"
    private lateinit var videoAutoPlayHelper: VideoAutoPlayHelper
    private var controlsVisibleShowHide: Boolean = false
    private val hideThreshold = 100
    private var isHeaderAlreadyHidden = false
    lateinit var binding: FragmentFeedListBinding
    private var scrolledDistance: Int = 0
    private lateinit var feedAdapter: FeedAdapter
    private var currentPlayingVideoItemPos = -1
    private val minVisibilityPercentage = 20
    private var lastPlayerView: InstaLikePlayerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )/*Helper class to provide AutoPlay feature inside cell*/
        videoAutoPlayHelper = VideoAutoPlayHelper(recyclerView = binding.recyclerView)
        PagerSnapHelper().attachToRecyclerView(binding.recyclerView)/* Set adapter (items are being used inside adapter, you can setup in your own way*/
        feedAdapter = FeedAdapter(requireContext())
        binding.adapter = feedAdapter
        getData()
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d(TAG, "onScrolled: scrolled")
                videoAutoPlayHelper.onScrolled(true)
            }
        })
        videoAutoPlayHelper.startObserving()

    }

    fun onScrolled(forHorizontalScroll: Boolean) {
        val firstVisiblePosition: Int = findFirstVisibleItemPosition()
        val lastVisiblePosition: Int = findLastVisibleItemPosition()
        val pos = getMostVisibleItem(firstVisiblePosition, lastVisiblePosition)

        if (pos == -1) {/*check if current view is more than MIN_LIMIT_VISIBILITY*/
            if (currentPlayingVideoItemPos != -1) {
                val viewHolder: RecyclerView.ViewHolder =
                    binding.recyclerView.findViewHolderForAdapterPosition(currentPlayingVideoItemPos)!!

                val currentVisibility = getVisiblePercentage(viewHolder)
                if (currentVisibility < minVisibilityPercentage) {
                    lastPlayerView?.removePlayer()
                }
                currentPlayingVideoItemPos = -1
            }
        } else {
            if (forHorizontalScroll || currentPlayingVideoItemPos != pos) {
                currentPlayingVideoItemPos = pos
//                attachVideoPlayerAt(pos)
            }
        }
    }

    private fun getMostVisibleItem(firstVisiblePosition: Int, lastVisiblePosition: Int): Int {
        var maxPercentage = -1
        var pos = 0
        for (i in firstVisiblePosition..lastVisiblePosition) {
            val viewHolder: RecyclerView.ViewHolder? =
                binding.recyclerView.findViewHolderForAdapterPosition(i)

            if (viewHolder != null) {
                val currentPercentage = getVisiblePercentage(viewHolder)
                if (currentPercentage > maxPercentage) {
                    maxPercentage = currentPercentage.toInt()
                    pos = i
                }
            }
        }

        if (maxPercentage == -1 || maxPercentage < minVisibilityPercentage) {
            return -1
        }
        return pos
    }

    private fun getVisiblePercentage(
        holder: RecyclerView.ViewHolder
    ): Float {
        val rectParent = Rect()
        binding.recyclerView.getGlobalVisibleRect(rectParent)
        val location = IntArray(2)
        holder.itemView.getLocationOnScreen(location)

        val rectChild = Rect(
            location[0],
            location[1],
            location[0] + holder.itemView.width,
            location[1] + holder.itemView.height
        )

        val rectParentArea =
            ((rectChild.right - rectChild.left) * (rectChild.bottom - rectChild.top)).toFloat()
        val xOverlap = 0.coerceAtLeast(
            rectChild.right.coerceAtMost(rectParent.right) - rectChild.left.coerceAtLeast(rectParent.left)
        ).toFloat()
        val yOverlap = 0.coerceAtLeast(
            rectChild.bottom.coerceAtMost(rectParent.bottom) - rectChild.top.coerceAtLeast(
                rectParent.top
            )
        ).toFloat()
        val overlapArea = xOverlap * yOverlap

        return overlapArea / rectParentArea * 100.0f
    }

    private fun findFirstVisibleItemPosition(): Int {
        if (binding.recyclerView.layoutManager is LinearLayoutManager) {
            return (binding.recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        }
        return -1
    }

    private fun findLastVisibleItemPosition(): Int {
        if (binding.recyclerView.layoutManager is LinearLayoutManager) {
            return (binding.recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }
        return -1
    }

    override fun onPause() {
        super.onPause()
        videoAutoPlayHelper.pause()
    }

    override fun onResume() {
        super.onResume()
        videoAutoPlayHelper.play()
    }

    fun getAssetVideoUri(context: Context, assetFileName: String): String? {
        return try {
            // Open the video file from the assets folder
            val assetManager = context.assets
            val inputStream = assetManager.open(assetFileName)

            // Create a temporary file to copy the video content
            val tempFile = File(context.cacheDir, assetFileName)
            tempFile.createNewFile()

            // Copy the video content from the assets folder to the temporary file
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Get the URI of the temporary file
            tempFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getData() {

        val videoList = ArrayList<VideoRoot>()
        videoList.add(
            VideoRoot(
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video16.mp4?alt=media&token=eb1018c5-c261-407d-8476-57f06d0970a3",
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video16.mp4?alt=media&token=eb1018c5-c261-407d-8476-57f06d0970a3"
            )
        )
        videoList.add(
            VideoRoot(
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video15.mp4?alt=media&token=3ce15048-9f39-40cd-a9dd-c3bf4e67ef84",
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video15.mp4?alt=media&token=3ce15048-9f39-40cd-a9dd-c3bf4e67ef84"
            )
        )
        videoList.add(
            VideoRoot(
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video14.mp4?alt=media&token=2c37462b-b633-45f5-aac2-6b9f58130125",
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video14.mp4?alt=media&token=2c37462b-b633-45f5-aac2-6b9f58130125"
            )
        )
        videoList.add(
            VideoRoot(
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video13.mp4?alt=media&token=504fe39b-129c-4cc1-a495-356be94beeba",
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video13.mp4?alt=media&token=504fe39b-129c-4cc1-a495-356be94beeba"
            )
        )
        videoList.add(
            VideoRoot(
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video12.mp4?alt=media&token=c55655ba-bf5f-4fd3-8acb-f311c9b5aa60",
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video12.mp4?alt=media&token=c55655ba-bf5f-4fd3-8acb-f311c9b5aa60"
            )
        )
        videoList.add(
            VideoRoot(
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video11.mp4?alt=media&token=2ce7432b-9043-4f02-9665-aa60ee9381ea",
                "https://firebasestorage.googleapis.com/v0/b/traveldemo-f9f0e.appspot.com/o/video11.mp4?alt=media&token=2ce7432b-9043-4f02-9665-aa60ee9381ea"
            )
        )
        feedAdapter.addData(videoList)
    }

}
