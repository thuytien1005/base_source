package wee.digital.sample.ui.vm

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import wee.digital.library.util.Logger
import wee.digital.sample.shared.progressLiveData


abstract class BaseVM : ViewModel() {

    val log by lazy { Logger(this::class.java.name) }

    fun <T> onProgress(task: () -> Task<T>): Task<T> {
        progressLiveData.value = true
        return task().addOnCompleteListener {
            progressLiveData.value = false
        }
    }

}