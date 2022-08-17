package wee.digital.sample.ui.qc

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import wee.digital.library.extension.foreachCatching
import wee.digital.sample.data.firebase.EventListener
import wee.digital.sample.data.firebase.dbRef
import wee.digital.sample.data.firebase.setEventListener
import wee.digital.sample.ui.base.BaseVM

class QcVM : BaseVM() {

    val appListLiveData = MutableLiveData<List<QcItem>?>()

    fun sync() {
        dbRef.child("qcApp")
            .setEventListener(object : EventListener() {
                override fun onDataChanged(snapshot: DataSnapshot) {
                    val entries = (snapshot.value as? HashMap<*, *>)?.entries ?: return
                    val list = mutableListOf<QcItem>()
                    entries.foreachCatching {
                        val map = it.value as HashMap<*, *>
                        val item = QcItem()
                        item.appName = map["name"] as String
                        item.version = map["version"] as String
                        item.packageName = map["packageName"] as String
                        item.url = map["url"] as String
                        list.add(item)
                    }
                    appListLiveData.postValue(list)
                }
            })
    }
}