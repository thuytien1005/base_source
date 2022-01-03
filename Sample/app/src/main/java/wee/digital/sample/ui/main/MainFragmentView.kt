package wee.digital.sample.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import wee.digital.library.extension.*
import wee.digital.sample.R
import wee.digital.sample.ui.base.FragmentView
import wee.digital.sample.ui.fragment.dialog.DialogVM
import wee.digital.sample.ui.fragment.dialog.alert.AlertArg
import wee.digital.sample.ui.fragment.dialog.alert.alertCameraPermission
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable
import wee.digital.sample.ui.fragment.dialog.selectable.SelectableArg
import wee.digital.sample.ui.fragment.dialog.tip.TipArg
import wee.digital.sample.ui.fragment.dialog.tip.TipVM
import wee.digital.sample.ui.fragment.dialog.web.WebArg
import wee.digital.widget.custom.InputView
import wee.digital.widget.extension.*
import java.io.IOException
import java.util.*

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

    fun showAlertMessage(block: AlertArg.() -> Unit) {
        if (dialogVM.alertLiveData.value != null) return
        val arg = AlertArg()
        arg.block()
        dialogVM.alertLiveData.value = arg
        mainNavigate(R.id.action_global_alertFragment)
    }

    fun showWebView(block: WebArg.() -> Unit) {
        if (dialogVM.webLiveData.value != null) return
        val arg = WebArg()
        arg.block()
        dialogVM.webLiveData.value = arg
        mainNavigate(R.id.action_global_webFragment)
    }

    fun showTip(v: View?) {
        v ?: return
        dialogVM.tipViewLiveData.value = -1
        activityVM(TipVM::class).tipArgLiveData.postValue(TipArg.sample(v))
        mainNavigate(R.id.action_global_tipFragment)
    }

    fun dismissDialogs() {
        dialogVM.tipViewLiveData.value = -1
        dialogVM.alertLiveData.value = null
        dialogVM.webLiveData.value = null
        dialogVM.selectableLiveData.value = null
    }

    fun showSelectableList(block: SelectableArg.() -> Unit) {
        hideKeyboard()
        val arg = SelectableArg()
        arg.block()
        val liveData = MutableLiveData(arg.selectedItem)
        liveData.observe {
            it ?: return@observe
            arg.onItemSelected?.invoke(it)
        }
        dialogVM.selectableMap[arg.key] = liveData
        dialogVM.selectableLiveData.value = arg
        if (arg.itemList.isNullOrEmpty()) return
        dialogVM.showDialogJob?.cancel()
        dialogVM.showDialogJob = launch(300) {
            mainNavigate(R.id.action_global_selectableFragment)
        }
    }

    val isCameraGranted: Boolean get() = isGranted(android.Manifest.permission.CAMERA)

    fun observerCameraPermission(onGranted: () -> Unit, onDenied: (() -> Unit)? = null) {
        fragment.observerPermission(
            android.Manifest.permission.CAMERA,
            onGranted = onGranted,
            onDenied = {
                onDenied?.invoke()
                alertCameraPermission()
            })
    }

    fun requestCameraPermission(onGranted: () -> Unit) {
        fragment.onGrantedPermission(
            android.Manifest.permission.CAMERA,
            onGranted = onGranted,
            onDenied = {
                launch(1000) {
                    alertCameraPermission()
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
                mainNavigate(R.id.action_global_selectableFragment)
            }
        })
    }

}