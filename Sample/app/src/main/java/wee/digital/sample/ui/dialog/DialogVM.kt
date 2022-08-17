package wee.digital.sample.ui.dialog

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Job
import wee.digital.sample.ui.base.BaseVM
import wee.digital.sample.ui.dialog.alert.AlertArg
import wee.digital.sample.ui.dialog.selectable.Selectable
import wee.digital.sample.ui.dialog.selectable.SelectableArg
import wee.digital.sample.ui.dialog.tip.TipArg
import wee.digital.sample.ui.dialog.web.WebArg

class DialogVM : BaseVM() {

    var tipEventLiveData = MutableLiveData<Int>()

    var tipViewLiveData = MutableLiveData<TipArg?>()

    val alertLiveData = MutableLiveData<AlertArg?>()

    val webLiveData = MutableLiveData<WebArg?>()

    val selectableMap = mutableMapOf<Int, MutableLiveData<Selectable?>>()

    val selectableLiveData = MutableLiveData<SelectableArg?>()

    var showDialogJob: Job? = null
}
