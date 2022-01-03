package wee.digital.sample.ui.fragment.dialog.alert

import wee.digital.library.extension.navigateAppSettings
import wee.digital.library.extension.navigateWifiSettings
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragmentView


fun MainFragmentView.alertCameraPermission() {
    showAlertMessage {
        icon = R.drawable.ic_check
        title = "Camera chưa sẵn sàng"
        message = "Để tiếp tục, hãy cho phép ứng dụng AllSafe Mobile truy cập Máy ảnh của bạn."
        acceptLabel = "Cài đặt ứng dụng"
        acceptOnClick = { navigateAppSettings() }
        cancelLabel = "Từ chối"
    }
}

fun MainFragmentView.alertNetworkError() {
    showAlertMessage {
        icon = R.drawable.ic_check
        title = "Không tìm thấy kết nối internet"
        message = "Vui lòng kiểm tra kết nối của thiết bị"
        cancelLabel = "Đóng"
        acceptLabel = "Cài đặt"
        acceptOnClick = { navigateWifiSettings() }
    }
}
