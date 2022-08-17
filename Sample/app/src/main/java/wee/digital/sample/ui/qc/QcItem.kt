package wee.digital.sample.ui.qc

import androidx.recyclerview.widget.DiffUtil


class QcItem {

    var appName: String = ""
    var version: String = ""
    var packageName: String = ""
    var url: String = ""
    var isDownloading: Boolean = false

    companion object {
        val diffCallback
            get() = object : DiffUtil.ItemCallback<QcItem>() {
                override fun areItemsTheSame(oldItem: QcItem, newItem: QcItem): Boolean {
                    return oldItem.appName == newItem.appName
                }

                override fun areContentsTheSame(oldItem: QcItem, newItem: QcItem): Boolean {
                    return false
                }
            }
    }
}