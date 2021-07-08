package wee.digital.sample.shared

import androidx.lifecycle.MutableLiveData

val progressLiveData by lazy {
    MutableLiveData<Boolean?>()
}