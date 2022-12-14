package com.fktimp.news.adapters

import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Point
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.fktimp.news.R
import com.fktimp.news.activities.VKState
import com.fktimp.news.models.VKSourceModel
import com.fktimp.news.models.VKWallPostModel


fun isPackageInstalled(
    packageName: String,
    packageManager: PackageManager
): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

internal class LoadingViewHolder(itemView: View) : ViewHolder(itemView) {
    var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
}


class WallAdapter(
    activity: Activity,
    private val clickListener: OnSaveWallPostClickListener,
    private var items: List<VKWallPostModel?>,
    private var srcInfo: List<VKSourceModel>
) : RecyclerView.Adapter<ViewHolder>() {

    init {
        VKState.isVKExist = isPackageInstalled(VK_APP_PACKAGE_ID, activity.packageManager)
        val display: Display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y
    }


    override fun getItemViewType(position: Int): Int {
        return if (items[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM)
            WallPostViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.wall_post_item_layout,
                    parent,
                    false
                )
            )
        else
            LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loading,
                    parent,
                    false
                )
            )
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true
            return
        }
        if (holder !is WallPostViewHolder) return
        val wallPost: VKWallPostModel = items[position] ?: return
        holder.bind(wallPost, srcInfo, clickListener)
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
        var screenWidth: Int = 0
            set(value) {
                repostScreenWidth = value - dpToPx(32)
                field = value
            }
        var screenHeight: Int = 0
        var repostScreenWidth: Int = 0
        private const val VK_APP_PACKAGE_ID = "com.vkontakte.android"

        // ?? ???????????? ?????????????? ?????????? ???? ??????????????????????, ?????????????? ?????? ???????????????????? ?? ?????? ???????? ????????????
        const val DIVIDER_WIDTH = 2
        fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()

    }
}

interface OnSaveWallPostClickListener {
    fun onSave(wallPost: VKWallPostModel, isNowChecked: Boolean)
}