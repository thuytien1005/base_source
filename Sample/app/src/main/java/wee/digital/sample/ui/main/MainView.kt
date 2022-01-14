package wee.digital.sample.ui.main

import androidx.appcompat.app.AppCompatActivity
import wee.digital.sample.databinding.MainAppBarBinding
import wee.digital.sample.ui.model.AppBarArg
import wee.digital.widget.extension.*

interface MainView {

    var arg: AppBarArg?

    fun MainAppBarBinding.init(activity: AppCompatActivity) {
        statusBarHolderView.observer(activity)
        viewLeftButton1.addClickListener { arg?.leftButton1onClick?.invoke(it) }
        viewRightButton1.addClickListener { arg?.rightButton1onClick?.invoke(it) }
    }

    fun MainAppBarBinding.bindArg(it: AppBarArg?) {
        arg = it
        it ?: return
        root.show()
        if (!it.isVisible) {
            root.gone()
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
    }

}