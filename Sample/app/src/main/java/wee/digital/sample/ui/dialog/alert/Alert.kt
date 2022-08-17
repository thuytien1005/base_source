package wee.digital.sample.ui.dialog.alert

import wee.digital.library.extension.navigateAppSettings
import wee.digital.library.extension.navigateWifiSettings
import wee.digital.sample.R
import wee.digital.sample.ui.base.BaseView
import wee.digital.sample.ui.dialog.DialogVM

typealias AlertBlock = (AlertArg.() -> Unit)?

fun BaseView.showAlert(block: AlertBlock) {
    val arg = AlertArg()
    block?.invoke(arg)
    val vm = activityVM(DialogVM::class)
    vm.alertLiveData.value = arg
    baseActivity?.show(AlertFragment(),"alert")
}

fun BaseView.alertNetworkError() {
    showAlert {
        dismissWhenTouchOutside = false
        icon = R.drawable.ic_check
        title = "Không tìm thấy kết nối internet"
        message = "Vui lòng kiểm tra kết nối của thiết bị"
        cancelLabel = "Đóng"
        acceptLabel = "Cài đặt"
        acceptOnClick = { navigateWifiSettings() }
    }
}

fun BaseView.alertCameraPermissionDenied() {
    showAlert {
        icon = R.drawable.ic_check
        title = "CameraApp chưa sẵn sàng"
        message = "Để tiếp tục, hãy cho phép ứng dụng truy cập Máy ảnh của bạn."
        acceptLabel = "Cài đặt ứng dụng"
        acceptOnClick = { navigateAppSettings() }
        cancelLabel = "Từ chối"
    }
}


