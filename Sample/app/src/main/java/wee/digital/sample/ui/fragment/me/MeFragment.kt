package wee.digital.sample.ui.fragment.me

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.onGranted
import wee.digital.library.extension.toast
import wee.digital.sample.data.Backup
import wee.digital.sample.data.repository.auth
import wee.digital.sample.databinding.MeBinding
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.bind


class MeFragment : MainFragment<MeBinding>() {

    private val vm by lazyViewModel(MeVM::class)

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        lifecycleScope.launch(Dispatchers.IO) {
            when (result.isSuccessful) {
                true -> handleImageResult(result.uriContent)
                else -> toast("${result.error?.message}")
            }
        }
    }

    override fun inflating(): (LayoutInflater) -> MeBinding {
        return MeBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.viewSignOut, bind.viewBackup, bind.viewRestore, bind.meCamera)
    }

    override fun onLiveDataObserve() {
        userVM.storeUserLiveData.observe {
            bindUser(it)
        }
        vm.updateAvatarSingle.observe {
            syncDataAvatar(it)
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
            bind.meCamera -> {
                onGranted(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) {
                    cropImage.launch(
                        options { setGuidelines(CropImageView.Guidelines.ON) }
                    )
                }
            }
        }
    }

    private fun handleImageResult(uri: Uri?) {
        uri ?: return
        vm.uploadAvatar(uri, auth.uid.toString())
    }

    private fun syncDataAvatar(bool: Boolean) {
        when (bool) {
            true -> userVM.syncUser()
            else -> toast("update avatar fail")
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