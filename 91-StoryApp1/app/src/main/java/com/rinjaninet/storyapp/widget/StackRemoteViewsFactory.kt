package com.rinjaninet.storyapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.rinjaninet.storyapp.R
import com.rinjaninet.storyapp.preferences.ImagePreferences
import java.lang.Exception

internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private lateinit var mImagePreferences: ImagePreferences

    override fun onCreate() {
        mImagePreferences = ImagePreferences(mContext)
    }

    override fun onDataSetChanged() {
        val urls = mImagePreferences.getImageUrls()
        try {
            for (url in urls) {
                val futureTarget: FutureTarget<Bitmap> =
                    Glide.with(mContext)
                        .asBitmap()
                        .load(url)
                        .submit(400, 300)
                val bitmap: Bitmap = futureTarget.get()
                mWidgetItems.add(bitmap)
                Glide.with(mContext).clear(futureTarget)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.iv_widget_item_image, mWidgetItems[position])
        val extras = bundleOf(
            ImagesBannerWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.iv_widget_item_image, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}