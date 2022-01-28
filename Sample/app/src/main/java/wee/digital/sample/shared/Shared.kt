package wee.digital.sample.shared

import wee.digital.library.util.SharedPref
import wee.digital.sample.BuildConfig

val pref by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SharedPref(appId) }

