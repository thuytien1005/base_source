package wee.digital.sample.ui.main

import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.vm.BaseVM

class MainVM : BaseVM() {

    var contactAdapterSelected = StoreUser()

    var chatAdapterSelected :StoreChat? = null

    var userLogin = StoreUser()

}