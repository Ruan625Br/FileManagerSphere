package com.etb.filemanager.files.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager

fun RecyclerView.animateSpanChange(newSpanCount: Int) {
    val recyclerView = this
    val transition: Transition = ChangeBounds()

    val oldSpanCount = (recyclerView.layoutManager as? GridLayoutManager)?.spanCount ?: return
    val oldItemAnimator = recyclerView.itemAnimator
    val oldLayoutAnimation = recyclerView.layoutAnimation
    (recyclerView.layoutManager as? GridLayoutManager)?.spanCount = newSpanCount
    val newLayoutAnimation = recyclerView.layoutAnimation

    val animators = ArrayList<Animator>()
    val childCount = recyclerView.childCount
    for (i in 0 until childCount) {
        val child = recyclerView.getChildAt(i)
        val lp = child.layoutParams as RecyclerView.LayoutParams
        val oldPosition = lp.viewAdapterPosition
        val newPosition =
            (recyclerView.layoutManager as? GridLayoutManager)?.getPosition(child) ?: continue

        if (oldPosition != RecyclerView.NO_POSITION && newPosition != RecyclerView.NO_POSITION) {
            val deltaX = (newPosition % oldSpanCount - oldPosition % oldSpanCount) * child.width
            val deltaY = (newPosition / oldSpanCount - oldPosition / oldSpanCount) * child.height

            val translationXHolder =
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, deltaX.toFloat())
            val translationYHolder =
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, deltaY.toFloat())
            val animator =
                ObjectAnimator.ofPropertyValuesHolder(child, translationXHolder, translationYHolder)
            animator.duration = transition.duration / 4
            animators.add(animator)
        }
    }

    val animatorSet = AnimatorSet()
    animatorSet.playTogether(animators)
    animatorSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            recyclerView.itemAnimator = oldItemAnimator
            recyclerView.layoutAnimation = oldLayoutAnimation
        }
    })

    TransitionManager.beginDelayedTransition(recyclerView, transition)
    recyclerView.layoutManager = recyclerView.layoutManager
    recyclerView.itemAnimator = null
    recyclerView.layoutAnimation = newLayoutAnimation
    animatorSet.start()
}
