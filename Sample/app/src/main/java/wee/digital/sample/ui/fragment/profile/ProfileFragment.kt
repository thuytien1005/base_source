package wee.digital.sample.ui.fragment.profile

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.toast
import wee.digital.sample.databinding.ProfileBinding
import wee.digital.sample.ui.main.MainDialogFragment
import wee.digital.sample.ui.model.StoreUser
import wee.digital.widget.extension.load

class ProfileFragment : MainDialogFragment<ProfileBinding>() {

    private val vm by lazyActivityVM(ProfileVM::class)

    override fun inflating(): (LayoutInflater) -> ProfileBinding {
        return ProfileBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.viewAdd, bind.viewMessage)
    }

    override fun onLiveDataObserve() {
        vm.userLiveData.observe {
            bindUser(it)
        }
        vm.addContactSuccessLiveData.observe {
            dismiss()
        }
    }

    override fun onViewClick(v: View?) {
        when(v) {
            bind.viewAdd -> vm.insertContact()
            bind.viewMessage -> toast("viewMessage click")
        }
    }

    /**
     *
     */
    private fun bindUser(it: StoreUser?) {
        it ?: return
        bind.avatarView.load(it.photoDisplay)
        bind.textViewFullName.text = it.fullName()
    }

}