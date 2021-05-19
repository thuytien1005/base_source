package wee.digital.sample.ui.main

import android.os.Bundle
import android.view.View
import androidx.transition.TransitionInflater
import wee.digital.library.extension.backgroundColor
import wee.digital.library.extension.statusBarColor
import wee.digital.sample.ui.base.BaseFragment
import wee.digital.sample.ui.base.activityVM

abstract class MainFragment : BaseFragment() {

    val mainVM: MainVM by lazy { activityVM(MainVM::class) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        statusBarColor(view.backgroundColor)
        onViewCreated()
        onLiveDataObserve()
    }


}