package wee.digital.sample.ui.main

import androidx.viewbinding.ViewBinding
import wee.digital.sample.ui.base.BaseDialogFragment
import wee.digital.sample.ui.fragment.dialog.DialogVM

abstract class MainDialogFragment<B : ViewBinding> : BaseDialogFragment<B>(), MainFragmentView {

    override val mainActivity get() = requireActivity() as? MainActivity
    override val mainVM by lazyActivityVM(MainVM::class)
    override val dialogVM by lazyActivityVM(DialogVM::class)

}