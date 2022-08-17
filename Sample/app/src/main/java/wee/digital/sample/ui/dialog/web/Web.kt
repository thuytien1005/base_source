package wee.digital.sample.ui.dialog.web

import wee.digital.sample.ui.base.BaseView
import wee.digital.sample.ui.dialog.DialogVM

typealias WebBlock = (WebArg.() -> Unit)?

fun BaseView.showWeb(block: WebBlock) {
    val arg = WebArg()
    block?.invoke(arg)
    activityVM(DialogVM::class).webLiveData.value = arg
    baseActivity?.show(WebFragment(),"web")
}

