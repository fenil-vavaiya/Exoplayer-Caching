package com.nehak.instagramfeed

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.google.gson.reflect.TypeToken
import com.nehak.instagramfeed.dataModels.VideoRoot
import com.nehak.instagramfeed.databinding.ActivityMainBinding
import com.nehak.instagramfeed.other.readJSONFromAssets
import java.lang.reflect.Type

open class MainActivity : FragmentActivity() {

    private val TAG: String? = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val decorView = window.decorView
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            decorView.setOnApplyWindowInsetsListener { v: View, insets: WindowInsets? ->
                val defaultInsets = v.onApplyWindowInsets(insets)
                defaultInsets.replaceSystemWindowInsets(
                    defaultInsets.systemWindowInsetLeft,
                    0,
                    defaultInsets.systemWindowInsetRight,
                    defaultInsets.systemWindowInsetBottom
                )
            }
        }
        ViewCompat.requestApplyInsets(decorView)
        val jsonString = readJSONFromAssets(baseContext, "feed_data.json")
        val listType: Type = object : TypeToken<List<VideoRoot>>() {}.type

//        dataList = (Gson().fromJson(jsonString, listType) as List<VideoRoot>)
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this@MainActivity,
            R.layout.activity_main
        )

    }


}
