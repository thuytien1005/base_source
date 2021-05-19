package wee.digital.sample.ui.main

import androidx.fragment.app.FragmentManager
import wee.digital.sample.ui.base.BaseDialog
import wee.digital.sample.ui.base.activityVM

abstract class MainDialog : BaseDialog() {

    val mainVM: MainVM by lazy { activityVM(MainVM::class) }

    final override fun onDestroy() {
        super.onDestroy()
        mainVM.dialogTag.remove(this::class.simpleName)
    }

    final override fun show(manager: FragmentManager, tag: String?) {
        (tag ?: this::class.simpleName!!).also {
            if (mainVM.dialogTag.indexOf(it) != -1) return
            mainVM.dialogTag.add(it)
            super.show(manager, it)
        }
    }

    fun show(manager: FragmentManager) {
        show(manager, this::class.simpleName!!)
    }

}