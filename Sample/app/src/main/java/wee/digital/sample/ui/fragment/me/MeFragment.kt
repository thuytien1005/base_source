package wee.digital.sample.ui.fragment.me

import android.view.LayoutInflater
import android.view.View
import wee.digital.sample.data.repository.Backup
import wee.digital.sample.databinding.MeBinding
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.bind

class MeFragment : MainFragment<MeBinding>() {

    override fun inflating(): (LayoutInflater) -> MeBinding {
        return MeBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.viewSignOut, bind.viewBackup, bind.viewRestore)
    }

    override fun onLiveDataObserve() {
        userVM.storeUserLiveData.observe {
            bindUser(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.viewSignOut -> {
                auth.signOut()
            }
            bind.viewBackup -> {
                Backup.runBackupData()
            }
            bind.viewRestore -> {
                Backup.runRestoreData()
            }
        }
    }

    /**
     *
     */
    private fun bindUser(it: StoreUser?) {
        it ?: return
        bind.avatarView.bind(it)
        bind.textViewFullName.text = it.fullName()
    }

}