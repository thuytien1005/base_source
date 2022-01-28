package wee.digital.sample.shared

import wee.digital.library.util.SharedPref

val pref by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SharedPref(appId) }

