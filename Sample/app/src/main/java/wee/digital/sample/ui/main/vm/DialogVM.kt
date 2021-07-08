package wee.digital.sample.ui.main.vm

import androidx.lifecycle.MutableLiveData
import wee.digital.sample.ui.main.fragment.alert.AlertArg
import wee.digital.sample.ui.main.fragment.selectable.SelectableArg
import wee.digital.sample.ui.main.fragment.web.WebArg

class DialogVM : BaseVM() {

    val alertLiveData = MutableLiveData<AlertArg?>()

    val webLiveData = MutableLiveData<WebArg>()

    val selectableLiveData = MutableLiveData<SelectableArg>()

}
