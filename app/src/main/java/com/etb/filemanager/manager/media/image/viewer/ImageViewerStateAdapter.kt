package com.etb.filemanager.manager.media.image.viewer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.nio.file.Path

class ImageViewerStateAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val imagePathList: List<Path>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = imagePathList.size

    override fun createFragment(position: Int): Fragment {
        return ImageViewerItemFragment.newInstance(imagePathList[position])
    }
}


