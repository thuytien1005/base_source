package wee.digital.sample.ui.main

import androidx.viewbinding.ViewBinding
import wee.digital.library.extension.activityVM
import wee.digital.library.util.Logger
import wee.digital.sample.ui.base.BaseDialogFragment
import wee.digital.sample.ui.vm.DialogVM

abstract class MainDialogFragment<B : ViewBinding> : BaseDialogFragment<B>(), MainView {

    protected val log by lazy { Logger(this::class) }

    protected val mainActivity get() = requireActivity() as? MainActivity

    protected val mainVM by activityVM(MainVM::class)

    protected val dialogVM by activityVM(DialogVM::class)

}