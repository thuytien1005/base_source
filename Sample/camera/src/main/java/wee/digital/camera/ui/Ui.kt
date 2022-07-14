package wee.digital.camera.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

interface SimpleAnimationListener : Animation.AnimationListener {
    override fun onAnimationRepeat(animation: Animation) {
    }

    override fun onAnimationEnd(animation: Animation) {
    }

    override fun onAnimationStart(animation: Animation) {
    }
}

interface SimpleMotionTransitionListener : MotionLayout.TransitionListener {
    override fun onTransitionChange(layout: MotionLayout, startId: Int, endId: Int, progress: Float) {
    }

    override fun onTransitionStarted(layout: MotionLayout, startId: Int, endId: Int) {
    }

    override fun onTransitionCompleted(layout: MotionLayout, currentId: Int) {
    }

    override fun onTransitionTrigger(layout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) {
    }
}

fun ImageView.clear() {
    setImageResource(0)
}

fun View.show() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun show(vararg views: View) {
    for (v in views) v.show()
}

fun View.hide() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

fun hide(vararg views: View) {
    for (v in views) v.hide()
}

fun View.gone() {
    if (visibility != View.GONE) visibility = View.GONE
}

fun gone(vararg views: View) {
    for (v in views) v.gone()
}

fun Fragment.observerCameraPermission(onGranted: () -> Unit) {
    val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            onGranted()
        }
    }
    val permission = android.Manifest.permission.CAMERA
    when {
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
            onGranted()
        }
        shouldShowRequestPermissionRationale(permission) -> {
            AlertDialog.Builder(requireActivity())
                    .setMessage("Quyền truy cập camera bị từ chối")
                    .setPositiveButton("Đóng") { dialog, _ -> dialog.cancel() }
                    .setNegativeButton("Setting") { dialog, _ ->
                        dialog.cancel()
                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            it.data = Uri.fromParts("package", requireActivity().packageName, null)
                        })
                    }.show()
        }
        else -> {
            permissionLauncher.launch(permission)
        }
    }
}

fun View.animateAlpha() {
    val anim: Animation = AlphaAnimation(1F, 0F).apply {
        interpolator = DecelerateInterpolator()
        duration = 800
        repeatCount = Animation.INFINITE
        repeatMode = Animation.REVERSE
    }

    this.startAnimation(anim)
}

fun View.animateHide(onEnd: (() -> Unit)? = null) {
    val anim: Animation = AlphaAnimation(1F, 0F).apply {
        interpolator = DecelerateInterpolator()
        duration = 400
        if (onEnd != null) setAnimationListener(object : SimpleAnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                onEnd()
            }
        })
    }
    this.startAnimation(anim)
}

fun View.animateShow(onEnd: (() -> Unit)? = null) {
    val anim: Animation = AlphaAnimation(0F, 1F).apply {
        interpolator = AccelerateInterpolator()
        duration = 300
        if (onEnd != null) setAnimationListener(object : SimpleAnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                onEnd()
            }
        })
    }
    this.startAnimation(anim)
}