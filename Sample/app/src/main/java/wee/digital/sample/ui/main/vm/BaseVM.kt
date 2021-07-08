package wee.digital.sample.ui.main.vm

import androidx.lifecycle.ViewModel
import wee.digital.library.util.Logger

abstract class BaseVM : ViewModel() {

    val log by lazy { Logger(this::class) }
}