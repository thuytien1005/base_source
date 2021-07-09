package wee.digital.sample.ui.vm

import androidx.lifecycle.MutableLiveData
import wee.digital.sample.ui.fragment.alert.AlertArg
import wee.digital.sample.ui.fragment.selectable.SelectableArg
import wee.digital.sample.ui.fragment.web.WebArg

class DialogVM : BaseVM() {

    val alertLiveData = MutableLiveData<AlertArg?>()

    val webLiveData = MutableLiveData<WebArg>()

    val selectableLiveData = MutableLiveData<SelectableArg>()

}
