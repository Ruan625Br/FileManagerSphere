package com.etb.filemanager.ui.view


import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import com.etb.filemanager.R
import com.etb.filemanager.manager.util.FileUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FabMenu(

    private var mContext: Context?,
    private var mFab: FloatingActionButton?,
    private var fabCreateFile: FloatingActionButton?,
    private var fabCreateFolder: FloatingActionButton?
) {

    private var mIsOpen = false

    fun isOpen(): Boolean {
        return mIsOpen
    }

    fun toggle() {
        if (mIsOpen) {
            close()
        } else {
            open()
        }
        setVisibility(mIsOpen)
        setAnimation(mIsOpen)
    }

    fun onCreateFileClick() {
        setVisibility(mIsOpen)
        setAnimation(mIsOpen)
    }

    fun onCreateFolderClick() {
        setVisibility(mIsOpen)
        setAnimation(mIsOpen)

    }

    private fun setVisibility(open: Boolean) {
        if (open) {
            fabCreateFile!!.visibility = View.VISIBLE
            fabCreateFolder!!.visibility = View.VISIBLE
        } else {
            fabCreateFile!!.visibility = View.GONE
            fabCreateFolder!!.visibility = View.GONE
        }
    }

    private fun setAnimation(open: Boolean) {
        if (open) {

            fabCreateFile!!.startAnimation(
                AnimationUtils.loadAnimation(mContext, R.anim.fab_from_bottom_anim)
            )
            fabCreateFolder!!.startAnimation(
                AnimationUtils.loadAnimation(mContext, R.anim.fab_from_bottom_anim)
            )
        } else {
            fabCreateFile!!.startAnimation(
                AnimationUtils.loadAnimation(mContext, R.anim.fab_to_bottom_anim)
            )
            fabCreateFolder!!.startAnimation(
                AnimationUtils.loadAnimation(mContext, R.anim.fab_to_bottom_anim)
            )
        }
    }

    private fun open() {
        mFab!!.startAnimation(
            AnimationUtils.loadAnimation(mContext, R.anim.anim_fab_rotate_backward)
        )
        mFab!!.setExpanded(true)
        mIsOpen = true
        //  val item_create_file = (mContext as Activity?)!!.findViewById<View>(R.id.action_create_file)

        val alphaAnimation = AlphaAnimation(0.0f, 0.8f)
        alphaAnimation.duration = 300
        alphaAnimation.fillAfter = true

    }

    private fun close() {
        mFab!!.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_fab_rotate_forward))
        mFab!!.setExpanded(false)
        mIsOpen = false


    }
    private fun createDialog(){

    }

    companion object {
        private var instance: FabMenu? = null

        fun getInstance(): FabMenu {
            if (instance == null) {
                instance = FabMenu.instance
            }
            return instance!!
        }
    }


}