package wee.digital.sample.shared

import wee.digital.sample.pref


object Pref {
    var hadShownIntro: Boolean
        get() = pref.bool("intro")
        set(value) {
            pref.putBool("intro", value)
        }
}