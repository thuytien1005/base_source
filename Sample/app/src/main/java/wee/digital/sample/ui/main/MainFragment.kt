package wee.digital.sample.ui.main

import androidx.viewbinding.ViewBinding
import wee.digital.library.extension.activityVM
import wee.digital.library.util.Logger
import wee.digital.sample.ui.base.BaseFragment
import wee.digital.sample.ui.vm.DialogVM
import wee.digital.sample.ui.vm.UserVM

abstract class MainFragment<B : ViewBinding> : BaseFragment<B>(), MainView {

    protected val log by lazy { Logger(this::class) }

    protected val mainActivity get() = requireActivity() as? MainActivity

    protected val mainVM by activityVM(MainVM::class)

    protected val dialogVM by activityVM(DialogVM::class)

    protected val userVM by activityVM(UserVM::class)


}