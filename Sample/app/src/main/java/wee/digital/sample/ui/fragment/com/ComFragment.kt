package wee.digital.sample.ui.fragment.com

import android.view.View
import kotlinx.android.synthetic.main.com.*
import wee.digital.library.extension.start
import wee.digital.ml.toast
import wee.digital.ml.ui.FaceAutoCaptureActivity
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment
import wee.digital.widget.ui.WidgetsActivity


class ComFragment : MainFragment() {

    override fun layoutResource(): Int {
        return R.layout.com
    }

    override fun onViewCreated() {
        addClickListener(comViewWidgets, comViewCamera, comViewNext)
    }

    override fun onLiveDataObserve() {
        textViewPlaceHolder.setTextColorRes(R.color.colorPrimary, R.color.colorAccent)
    }

    override fun onViewClick(v: View?) {
        when (v) {
            comViewWidgets -> {
                start(WidgetsActivity::class)
            }
            comViewCamera -> {
                start(FaceAutoCaptureActivity::class)
            }
            comViewNext -> {
                toast("next")
            }
        }
    }

}