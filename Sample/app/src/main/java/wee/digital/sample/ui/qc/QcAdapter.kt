package wee.digital.sample.ui.qc

import android.content.pm.PackageManager
import android.view.View
import androidx.viewbinding.ViewBinding
import wee.digital.library.app
import wee.digital.sample.R
import wee.digital.sample.databinding.QcItemBinding
import wee.digital.widget.adapter.BaseListAdapter
import wee.digital.widget.adapter.ItemOptions
import wee.digital.widget.adapter.addItemClickListener
import wee.digital.widget.extension.isShow
import wee.digital.widget.extension.textColorRes

class QcAdapter : BaseListAdapter<QcItem>(QcItem.diffCallback) {

    var onAppUpdateClick: ((QcItem) -> Unit)? = null

    override fun modelItemOptions(item: QcItem?, position: Int): ItemOptions? {
        return ItemOptions(R.layout.qc_item, QcItemBinding::bind)
    }

    override fun ViewBinding.onBindItem(item: QcItem, position: Int) {
        if (this !is QcItemBinding) {
            return
        }
        textViewAppName.text = item.appName
        textViewVersion.text = item.version
        viewProgress.isShow(item.isDownloading)
        try {
            val packageInfo = app.packageManager.getPackageInfo(item.packageName, 0)
            textViewVersion.text = "${packageInfo.versionName}"
            textViewVersion.textColorRes(R.color.color_primary)
            if (item.version != packageInfo.versionName){
                viewUpdate.visibility = View.VISIBLE
            }else{
                viewUpdate.visibility = View.GONE
            }

        } catch (e: PackageManager.NameNotFoundException) {
            textViewVersion.text = "application is not installed"
            textViewVersion.textColorRes(R.color.color_error)
            viewUpdate.visibility = View.VISIBLE
        }
        viewUpdate.addItemClickListener {
            onAppUpdateClick?.invoke(item)
        }
    }

}