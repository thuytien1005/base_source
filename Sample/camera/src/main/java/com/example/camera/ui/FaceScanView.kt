package com.example.camera.ui

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.camera.R
import com.example.camera.databinding.FaceScanLayoutBinding

interface FaceScanView {

    fun FaceScanLayoutBinding.onViewCreated() {
        val anim: Animation = AlphaAnimation(1F, 0F).apply {
            interpolator = DecelerateInterpolator()
            duration = 800
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }
        imageViewPlus.startAnimation(anim)
    }

    fun FaceScanLayoutBinding.show() {
        motionLayoutScanWidgets.visibility = View.VISIBLE
    }

    fun FaceScanLayoutBinding.hide() {
        motionLayoutScanWidgets.visibility = View.INVISIBLE
    }

    fun FaceScanLayoutBinding.animateCaptured(onCompleted: () -> Unit) {
        motionLayoutScanXY.alpha = 1F
        motionLayoutScanXY.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

            override fun onTransitionCompleted(layout: MotionLayout, currentId: Int) {
                if (currentId == R.id.faceScanCompleted) {
                    onCompleted()
                }
            }
        })
        motionLayoutScanXY.transitionToState(R.id.faceScanStart)
    }

    fun FaceScanLayoutBinding.animateHide(onCompleted: () -> Unit) {
        val anim: Animation = AlphaAnimation(1F, 0F).apply {
            interpolator = DecelerateInterpolator()
            duration = 400
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation) {
                    motionLayoutScanXY.setTransition(R.id.faceScanBegin, R.id.faceScanStart)
                    onCompleted()
                }

            })
        }
        motionLayoutScanXY.startAnimation(anim)
    }

}
