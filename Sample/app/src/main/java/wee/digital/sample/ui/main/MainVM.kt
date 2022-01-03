package wee.digital.sample.ui.main

import androidx.lifecycle.MutableLiveData
import wee.digital.sample.ui.base.BaseVM
import wee.digital.sample.ui.model.AppBarArg

class MainVM : BaseVM() {

    val appBarArgLiveData = MutableLiveData<AppBarArg?>()
}