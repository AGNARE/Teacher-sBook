package com.geeksPro.teachersbook.ui.fragments.onBoardFragments.customanimations

import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.Transition
import android.transition.TransitionSet
import android.view.animation.DecelerateInterpolator

class CustomSharedElementTransitionFotButton : TransitionSet() {

    init {
        ordering = ORDERING_TOGETHER
        duration = 500
        interpolator = DecelerateInterpolator()

        addTransition(createChangeBounds())
        addTransition(createChangeTransform())
        addTransition(createChangeImageTransform())
    }

    private fun createChangeBounds(): Transition {
        val changeBounds = ChangeBounds()
        changeBounds.duration = duration
        changeBounds.interpolator = interpolator
        return changeBounds
    }

    private fun createChangeTransform(): Transition {
        val changeTransform = ChangeTransform()
        changeTransform.duration = duration
        changeTransform.interpolator = interpolator
        return changeTransform
    }

    private fun createChangeImageTransform(): Transition {
        val changeImageTransform = ChangeImageTransform()
        changeImageTransform.duration = duration
        changeImageTransform.interpolator = interpolator
        return changeImageTransform
    }
}