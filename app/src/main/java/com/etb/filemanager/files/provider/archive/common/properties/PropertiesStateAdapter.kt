package com.etb.filemanager.files.provider.archive.common.properties

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.etb.filemanager.manager.adapter.FileModel

class PropertiesStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, file: FileModel) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    val fileItem = file

    override fun getItemCount(): Int {
        return  1
    }


    override fun createFragment(position: Int): Fragment {
      if (position == 0){
          basicPropertiesFragment()
      }
        return BasicPropertiesFragment()
    }

    fun basicPropertiesFragment(): BasicPropertiesFragment {

        return basicPropertiesFragment()
    }
}