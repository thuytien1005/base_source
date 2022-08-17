package wee.digital.sample.ui.dialog.tip

import android.view.View
import wee.digital.sample.ui.base.BaseView
import wee.digital.sample.ui.dialog.DialogVM

typealias TipBlock = (TipArg.() -> Unit)?

fun BaseView.showTip(v: View, block: TipBlock) {
    val arg = TipArg(v)
    block?.invoke(arg)
    val vm = activityVM(DialogVM::class)
    vm.tipViewLiveData.value = arg
    baseActivity?.show(TipFragment(),"tip")
}

