package wee.digital.sample.ui.fragment.com

import android.view.View
import kotlinx.android.synthetic.main.com.*
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class ComFragment : MainFragment() {

    override fun layoutResource(): Int {
        return R.layout.com
    }

    override fun onViewCreated() {
        addClickListener(comViewNext)
    }

    override fun onLiveDataObserve() {
        textViewPlaceHolder.setTextColorRes(R.color.colorPrimary, R.color.colorAccent)
    }

    override fun onViewClick(v: View?) {
        when (v) {
            comViewNext -> {
            }
        }
    }

}