package wee.digital.sample.ui.fragment.profile

import android.view.LayoutInflater
import android.view.View
import wee.digital.sample.R
import wee.digital.sample.data.firebase.auth
import wee.digital.sample.databinding.ProfileBinding
import wee.digital.sample.ui.main.MainDialogFragment
import wee.digital.sample.ui.model.StoreChat
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
        vm.chatStoreSingle.observe {
            navigateConversation(it)
        }
        vm.chatStoreEmptySingle.observe {
            navigateConversation(null)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.viewAdd -> vm.insertContact()
            bind.viewMessage -> {
                val uidLogin = auth.uid.toString()
                val uidContact = mainVM.contactAdapterSelected.uid
                vm.checkConversationExists(uidLogin, uidContact)
            }
        }
    }

    private fun navigateConversation(it: StoreChat?) {
        dismiss()
        mainVM.chatAdapterSelected = it
        navigate(R.id.action_global_conversationFragment)
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