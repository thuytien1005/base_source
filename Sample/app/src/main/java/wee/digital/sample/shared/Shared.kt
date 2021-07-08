package wee.digital.sample.shared

import com.google.firebase.auth.FirebaseAuth
import wee.digital.library.util.SharedPref
import wee.digital.sample.BuildConfig

val appId get() = BuildConfig.APPLICATION_ID

val pref by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SharedPref(appId) }

