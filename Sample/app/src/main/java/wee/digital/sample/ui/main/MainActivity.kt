package wee.digital.sample.ui.main

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.delay
import wee.digital.library.extension.*
import wee.digital.sample.R
import wee.digital.sample.databinding.MainBinding
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.model.AppBarArg
import wee.digital.widget.extension.tintRes


class MainActivity : BaseActivity<MainBinding>(),
    MainView {

    private val mainVM by lazyViewModel(MainVM::class)

    override var arg: AppBarArg? = null

    override fun activityNavController(): NavController? {
        return findNavController(R.id.mainFragment)
    }

    override fun inflating(): (LayoutInflater) -> ViewBinding {
        return MainBinding::inflate
    }

    override fun onViewCreated() {
        vb.keyboardView.observer(this)
        window.enterTransition = null
        window.exitTransition = null
        vb.includeAppBar.also {
            it.init(this)
            it.progressBar.progressDrawable.tintRes(R.color.colorPrimary)
        }
    }

    override fun onLiveDataObserve() {
        mainVM.appBarArgLiveData.observe {
            it ?: return@observe
            launch {
                if (it.delay > 0) delay(it.delay)
                statusBarColor(it.statusBarColor)
                navBarColor(it.navBarColor)
                vb.layoutContent.setBackgroundColor(it.mainBackgroundColor)
                vb.includeAppBar.bindArg(it)
                if (it.lightStatusWidgets) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        lightStatusBarWidgets()
                    }
                } else {
                    darkStatusBarWidgets()
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action === MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


}






