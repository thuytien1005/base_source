package wee.digital.sample.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import wee.digital.library.util.EventLiveData
import wee.digital.sample.ui.fragment.dialog.alert.Alert
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable
import wee.digital.sample.ui.fragment.dialog.selectable.SelectableArg
import wee.digital.sample.ui.fragment.dialog.web.WebArg

open class MainVM : ViewModel() {

    val dialogTag = mutableListOf<String>()

    val dialogLiveData = EventLiveData<NavDirections>()

    val selectableMap = mutableMapOf<Int, Selectable?>()

    val selectableLiveData = MutableLiveData<SelectableArg>()

    val alertLiveData = MutableLiveData<Alert.Arg?>()

    val webLiveData = MutableLiveData<WebArg>()

}