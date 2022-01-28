package wee.digital.sample.ui.base

import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import wee.digital.sample.ui.fragment.dialog.alert.alertCameraPermissionDenied


interface FragmentView : BaseView {

    val fragment: Fragment get() = this as Fragment

    override val baseActivity: BaseActivity<*>? get() = fragment.activity as? BaseActivity<*>

    override val lifecycleOwner: LifecycleOwner get() = fragment.viewLifecycleOwner

    override fun activityNavController(): NavController? {
        return baseActivity?.activityNavController()
    }

    fun <T : ViewBinding> viewBinding(block: (LayoutInflater) -> ViewBinding): Lazy<T> {
        return lazy {
            @Suppress("UNCHECKED_CAST")
            block.invoke(fragment.layoutInflater) as T
        }
    }

    fun onCreateView() = Unit

    fun requestFocus(v: View?) {
        launch(1000) { v?.requestFocus() }
    }

    /**
     * Back press handle
     */
    val backPressedCallback: OnBackPressedCallback

    fun getBackPressCallBack(): OnBackPressedCallback {
        return object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }

    fun onBackPressed()

    /**
     * LifecycleScope
     */
    val lifecycleScope get() = fragment.lifecycleScope

    fun addObserver(observer: LifecycleObserver) {
        lifecycleOwner.lifecycle.addObserver(observer)
    }


    /**
     * Navigation
     */
    fun childNavigate(@IdRes actionId: Int, block: (NavBuilder.() -> Unit)? = null) {
        fragment.findNavController().navigate(actionId, block)
    }

    fun childPopBackStack(@IdRes fragmentId: Int = 0, inclusive: Boolean = false) {
        if (fragmentId != 0) {
            fragment.findNavController().popBackStack(fragmentId, inclusive)
        } else {
            fragment.findNavController().navigateUp()
        }
    }

    fun mainNavigate(@IdRes actionId: Int, block: (NavBuilder.() -> Unit)? = null) {
        activityNavController()?.navigate(actionId, block)
    }

    fun mainPopBackStack(@IdRes fragmentId: Int = 0, inclusive: Boolean = false) {
        if (fragmentId != 0) {
            activityNavController()?.popBackStack(fragmentId, inclusive)
        } else {
            activityNavController()?.navigateUp()
        }
    }

    /**
     * SoftInputMode
     */
    fun inputModeAdjustResize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            fragment.view?.setOnApplyWindowInsetsListener { _, windowInsets ->
                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                fragment.view?.setPadding(0, 0, 0, imeHeight)
                windowInsets
            }
        } else {
            @Suppress("DEPRECATION")
            baseActivity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    fun inputModeAdjustNothing() {
        baseActivity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    /**
     *
     */
    fun observerCameraPermission(onGranted: () -> Unit) {
        val request = ActivityResultContracts.RequestPermission()
        val permissionLauncher = fragment.registerForActivityResult(request) { isGranted: Boolean ->
            if (isGranted) {
                onGranted()
            }
        }
        val permission = android.Manifest.permission.CAMERA
        val selfPms = ContextCompat.checkSelfPermission(fragment.requireContext(), permission)
        when {
            selfPms == PackageManager.PERMISSION_GRANTED -> {
                onGranted()
            }
            fragment.shouldShowRequestPermissionRationale(permission) -> {
                alertCameraPermissionDenied()
            }
            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }
}