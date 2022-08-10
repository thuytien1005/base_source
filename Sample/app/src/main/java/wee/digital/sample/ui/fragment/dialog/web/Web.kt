package wee.digital.sample.ui.fragment.dialog.web

import wee.digital.sample.ui.base.BaseView
import wee.digital.sample.ui.fragment.dialog.DialogVM

typealias WebBlock = (WebArg.() -> Unit)?

fun BaseView.showWeb(block: WebBlock) {
    val vm = activityVM(DialogVM::class)
    if (vm.webLiveData.value != null) return
    val arg = WebArg()
    block?.invoke(arg)
    activityVM(DialogVM::class).webLiveData.value = arg
    baseActivity?.show(WebFragment(),"web")
}

