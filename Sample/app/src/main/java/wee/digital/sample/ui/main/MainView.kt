package wee.digital.sample.ui.main

import wee.digital.sample.ui.main.vm.DialogVM
import wee.digital.sample.ui.main.vm.MainVM


interface MainView {

    val mainActivity: MainActivity?

    val mainVM: MainVM

    val dialogVM: DialogVM

}