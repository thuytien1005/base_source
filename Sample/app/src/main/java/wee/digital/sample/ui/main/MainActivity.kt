package wee.digital.sample.ui.main

import androidx.navigation.NavController
import androidx.navigation.findNavController
import wee.digital.sample.R
import wee.digital.sample.databinding.MainBinding
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.base.Inflating


class MainActivity : BaseActivity<MainBinding>() {

    private val mainVM by lazyViewModel(MainVM::class)

    override fun activityNavController(): NavController? = findNavController(R.id.mainFragment)

    override fun inflating(): Inflating = MainBinding::inflate

    override fun onViewCreated() {
        vb.keyboardView.observer(this)
        window.enterTransition = null
        window.exitTransition = null

    }

    override fun onLiveDataObserve() {
    }

    /*override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
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
    }*/


}






