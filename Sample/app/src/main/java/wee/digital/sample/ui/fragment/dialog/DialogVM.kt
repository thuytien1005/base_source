package wee.digital.sample.ui.fragment.dialog

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Job
import wee.digital.sample.ui.base.BaseVM
import wee.digital.sample.ui.fragment.dialog.alert.AlertArg
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable
import wee.digital.sample.ui.fragment.dialog.selectable.SelectableArg
import wee.digital.sample.ui.fragment.dialog.web.WebArg

class DialogVM : BaseVM() {

    var tipViewLiveData = MutableLiveData<Int>()

    val alertLiveData = MutableLiveData<AlertArg?>()

    val webLiveData = MutableLiveData<WebArg?>()

    val selectableMap = mutableMapOf<Int, MutableLiveData<Selectable?>>()

    val selectableLiveData = MutableLiveData<SelectableArg?>()

    var showDialogJob: Job? = null
}
