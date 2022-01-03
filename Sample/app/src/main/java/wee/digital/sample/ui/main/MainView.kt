package wee.digital.sample.ui.main

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import wee.digital.sample.databinding.MainAppBarBinding
import wee.digital.sample.ui.model.AppBarArg
import wee.digital.widget.extension.*

interface MainView {

    var arg: AppBarArg?

    fun MainAppBarBinding.init(activity: AppCompatActivity) {
        statusBarHolderView.observer(activity)
        viewLeftButton1.addViewClickListener { arg?.leftButton1onClick?.invoke(it) }
        viewRightButton1.addViewClickListener { arg?.rightButton1onClick?.invoke(it) }
        viewRightButton2.addViewClickListener { arg?.rightButton2onClick?.invoke(it) }
    }

    fun MainAppBarBinding.bindArg(it: AppBarArg?) {
        arg = it
        it ?: return
        if (it.isGone) {
            root.gone()
            return
        }
        root.show()
        if (!it.isVisible) {
            viewBackground.setBackgroundColor(Color.TRANSPARENT)
            hide(viewLeftButton1, viewRightButton1, viewRightButton2, imageViewLogo)
            return
        }
        imageViewLogo.isShow(it.logoVisible)
        viewBackground.setBackgroundColor(it.appBarColor)
        if (it.progress < 0) {
            progressBarLayout.hide()
            progressBar.setProgress(0, false)
        } else {
            progressBarLayout.show()
            progressBar.setProgress(it.progress, true)
        }
        textViewAppBarTitle.text = it.title
        statusBarHolderView.setExpanded(it.isFullScreen, true)
        viewLeftButton1.isHide(it.leftButton1 == 0)
        viewLeftButton1.setImageResource(it.leftButton1)
        viewRightButton1.isHide(it.rightButton1 == 0)
        viewRightButton1.setImageResource(it.rightButton1)
        viewRightButton2.isHide(it.rightButton2 == 0)
        viewRightButton2.setImageResource(it.rightButton2)
    }

}