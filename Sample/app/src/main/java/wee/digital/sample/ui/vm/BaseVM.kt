package wee.digital.sample.ui.vm

import androidx.lifecycle.ViewModel
import wee.digital.library.util.Logger


open class BaseVM : ViewModel() {

    val log = Logger(this::class.java.name)

}