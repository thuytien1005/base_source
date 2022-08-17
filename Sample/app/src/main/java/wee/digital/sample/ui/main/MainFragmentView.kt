package wee.digital.sample.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import wee.digital.library.extension.*
import wee.digital.sample.ui.base.FragmentView
import wee.digital.sample.ui.dialog.DialogVM
import wee.digital.sample.ui.dialog.alert.alertCameraPermissionDenied
import wee.digital.sample.ui.dialog.selectable.Selectable
import wee.digital.sample.ui.dialog.selectable.SelectableArg
import wee.digital.sample.ui.dialog.selectable.SelectableFragment
import wee.digital.widget.custom.InputView
import wee.digital.widget.extension.ViewClickListener
import wee.digital.widget.extension.networkErrorShown
import wee.digital.widget.extension.toastError
import java.io.IOException

interface MainFragmentView : FragmentView {

    val mainActivity: MainActivity?
    val self get() = this

    val mainVM: MainVM
    val dialogVM: DialogVM

    fun toastDev() {
        toast("Đang phát triển")
    }

    fun showProgress() = Unit

    fun hideProgress() = Unit

    fun showNetworkError(t: IOException) {
        if (nowInMillis - networkErrorShown > 5 * SECOND) {
            networkErrorShown = nowInMillis
            toastError("Mất kết nối mạng. Vui lòng thử lại")
        }
    }

    fun showHttpError(t: Throwable) {
        toastError(t.message ?: t.stackTraceToString())
    }

    fun onNotificationData(data: Map<String, String>) = Unit

    fun dismissDialogs() {
        dialogVM.tipViewLiveData.value = null
        dialogVM.alertLiveData.value = null
        dialogVM.webLiveData.value = null
        dialogVM.selectableLiveData.value = null
    }

    val isCameraGranted: Boolean get() = isGranted(android.Manifest.permission.CAMERA)

    fun observerCameraPermission(onGranted: () -> Unit, onDenied: (() -> Unit)? = null) {
        fragment.observerPermission(
            android.Manifest.permission.CAMERA,
            onGranted = onGranted,
            onDenied = {
                onDenied?.invoke()
                alertCameraPermissionDenied()
            })
    }

    fun requestCameraPermission(onGranted: () -> Unit) {
        fragment.onGrantedPermission(
            android.Manifest.permission.CAMERA,
            onGranted = onGranted,
            onDenied = {
                launch(1000) {
                    alertCameraPermissionDenied()
                }
            })
    }


    var InputView.selectedItem: Selectable?
        get() = dialogVM.selectableMap[id]?.value
        set(value) {
            dialogVM.selectableMap[id]?.value = value
            text = value?.text
        }

    fun InputView.attachSelectableList(block: SelectableArg.() -> Unit) {
        val arg = SelectableArg()
        arg.key = this.id
        arg.selectedItem = this.selectedItem
        arg.block()
        val liveData = MutableLiveData(arg.selectedItem)
        liveData.observe {
            this.error = null
            this.text = it?.text
            it ?: return@observe
            arg.onItemSelected?.invoke(it)
        }
        dialogVM.selectableMap[this.id] = liveData
        setOnClickListener(object : ViewClickListener() {
            override fun onClicks(v: View) {
                dialogVM.selectableLiveData.value = arg
                show(SelectableFragment())
            }
        })
    }

}