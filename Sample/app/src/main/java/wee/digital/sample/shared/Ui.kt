package wee.digital.sample.shared

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task

val progressLiveData by lazy {
    MutableLiveData<Boolean?>()
}

fun <T> onProgress(task: () -> Task<T>): Task<T> {
    progressLiveData.value = true
    return task().addOnCompleteListener {
        progressLiveData.value = false
    }
}


