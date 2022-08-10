package wee.digital.sample.ui.fragment.dialog.tip

import android.view.View
import wee.digital.sample.ui.base.BaseView
import wee.digital.sample.ui.fragment.dialog.DialogVM

typealias TipBlock = (TipArg.() -> Unit)?

fun BaseView.showTip(v: View, block: TipBlock) {
    val vm = activityVM(DialogVM::class)
    if (vm.tipViewLiveData.value != null) return
    val arg = TipArg(v)
    block?.invoke(arg)
    vm.tipViewLiveData.value = arg
    baseActivity?.show(TipFragment(),"tip")
}

