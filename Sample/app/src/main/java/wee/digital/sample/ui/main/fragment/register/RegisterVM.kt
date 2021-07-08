package wee.digital.sample.ui.main.fragment.register

import android.util.Patterns
import wee.digital.sample.ui.main.vm.BaseVM

class RegisterVM : BaseVM() {

    fun checkValidEmail(email: String?): Boolean {
        return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
