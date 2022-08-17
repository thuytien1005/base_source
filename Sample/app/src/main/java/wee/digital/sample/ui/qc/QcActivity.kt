package wee.digital.sample.ui.qc

import android.content.pm.PackageManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import wee.digital.library.extension.*
import wee.digital.sample.app
import wee.digital.sample.databinding.QcBinding
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.base.Inflating
import java.io.File

class QcActivity : BaseActivity<QcBinding>() {

    private val adapter = QcAdapter()

    private val vm by lazyViewModel(QcVM::class)

    override fun inflating(): Inflating = QcBinding::inflate

    override fun onViewCreated() {
        vb.viewSettings.addClickListener {
            navigateSettings()
        }
        adapter.bind(vb.recyclerView)
            .onItemClick {
                try {
                    openApplicationOrThrow(it.packageName)
                } catch (e: Exception) {
                    toast("application is not installed")
                }
            }
        adapter.onAppUpdateClick = {
            try {
                val packageInfo = app.packageManager.getPackageInfo(it.packageName, 0)
                if (it.version != packageInfo.versionName) {
                    onDowmloadItem(it)
                } else {
                    toast("app had installed")
                }
            } catch (e: PackageManager.NameNotFoundException) {
                onDowmloadItem(it)
            }
        }
        vb.swipeRefreshLayout.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            launch(300) {
                vb.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onLiveDataObserve() {
        vm.sync()
        vm.appListLiveData.observe {
            adapter.set(it)
        }
    }

    /**
     *
     */
    private fun onDowmloadItem(item: QcItem) {
        onGrantedPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            onGranted = {
                download(item)
            },
            onDenied = {
                toast("permission denied")
            })
    }

    private fun download(item: QcItem) {
        val fileName = "${item.appName} (${item.version.replace(".", "")}).apk"
        val file = File("$externalDir/Downloads", fileName)
        if (file.exists()) {
            file.installApk {
                toast(it.message)
            }
            return
        }
        item.isDownloading = true
        DownloadBuilder()
            .title("Download: ${item.appName}")
            .url(item.url)
            .file(File("$externalDir/Downloads", fileName))
            .onSuccess {
                item.isDownloading = false
                adapter.notifyDataSetChanged()
                file.installApk {
                    toast(it.message)
                }
            }
            .onCancel {
                item.isDownloading = false
                adapter.notifyDataSetChanged()
            }
            .download()
    }
}