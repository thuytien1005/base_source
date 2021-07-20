package wee.digital.sample.ui.fragment.info

import android.view.LayoutInflater
import wee.digital.library.extension.activityVM
import wee.digital.sample.R
import wee.digital.sample.databinding.InfoBinding
import wee.digital.sample.ui.base.BaseDialogFragment
import wee.digital.sample.ui.base.BaseFragment
import wee.digital.sample.ui.fragment.contact.ContactVM
import wee.digital.sample.ui.main.MainVM
import wee.digital.sample.utils.bind


class InfoFragment : BaseDialogFragment<InfoBinding>() {

    override fun dialogStyle(): Int {
        return R.style.App_Dialog_FullScreen_Transparent
    }

    private val vm by activityVM(MainVM::class)

    override fun inflating(): (LayoutInflater) -> InfoBinding {
        return InfoBinding::inflate
    }

    override fun onViewCreated() {
        vm.contactAdapterSelected.also {
            bind.infoAvatar.bind(it)
            bind.infoName.text = "${it.firstName} ${it.lastName}"
        }
    }

    override fun onLiveDataObserve() {

    }

}