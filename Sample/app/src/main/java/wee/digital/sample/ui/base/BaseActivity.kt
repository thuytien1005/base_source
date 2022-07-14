package wee.digital.sample.ui.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job
import wee.digital.library.extension.hideSystemUI
import wee.digital.library.util.Logger

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), BaseView {

    protected val log: Logger by lazy { Logger(this::class) }

    val vb: VB by lazy {
        @Suppress("UNCHECKED_CAST")
        inflating().invoke(layoutInflater) as VB
    }

    abstract fun inflating(): Inflating

    protected open fun permission(): Array<String> {
        return arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    /**
     * [AppCompatActivity] override
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.d("onCreate")
        setContentView(vb.root)
        onViewCreated()
        onLiveDataObserve()
    }

    override fun onResume() {
        super.onResume()
        log.d("onResume")
        checkPermissionAndRequest()
        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        log.d("onPause")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemUI()
    }

    /**
     * [BaseView]
     */
    final override val baseActivity: BaseActivity<*>? get() = this

    final override val lifecycleOwner: LifecycleOwner get() = this

    final override val uiJobList: MutableList<Job> = mutableListOf()

    /**
     * [BaseActivity] properties
     */
    private fun checkPermissionAndRequest() {
        var isCheck = true
        for (model in permission()) {
            if (checkSelfPermission(model) != PackageManager.PERMISSION_GRANTED) isCheck = false
        }
        if (isCheck) {
            onPermissionGranted("")
        } else {
            requestPermissions(permission(), 1000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != 1000) return
        for (i in permissions.indices) {
            val permission = permissions[i]
            when (grantResults[i]) {
                PackageManager.PERMISSION_GRANTED -> {
                    onPermissionGranted(permission)
                }
                PackageManager.PERMISSION_DENIED -> {
                    val permissionAgain = shouldShowRequestPermissionRationale(permission)
                    if (!permissionAgain) {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null)
                        )
                        startActivity(intent)
                    }
                }
            }
        }
    }

    open fun onPermissionGranted(permission: String) {}

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            hideSystemUI()
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    /**
     * Keyboard utils
     */
    protected fun inputModeAdjustResize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vb.root.setOnApplyWindowInsetsListener { _, windowInsets ->
                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                vb.root.setPadding(0, 0, 0, imeHeight)
                windowInsets
            }
        } else {
            @Suppress("DEPRECATION")
            baseActivity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    protected fun inputModeAdjustNothing() {
        baseActivity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    protected fun hideKeyboard() {
        vb.root.hideKeyboard()
    }

    protected fun showKeyboard() {
        vb.root.showKeyboard()
    }

}