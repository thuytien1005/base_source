package wee.digital.sample.shared

import wee.digital.sample.BuildConfig

const val appId = BuildConfig.APPLICATION_ID

const val dbVersion = 1

const val serviceUrl = ""

val hasSample: Boolean get() = false

val sampleDelayed: Long get() = 2000

val isDebug get() = BuildConfig.DEBUG

val flavor get() = BuildConfig.FLAVOR

val versionCode get() = BuildConfig.VERSION_CODE

val versionName get() = BuildConfig.VERSION_NAME