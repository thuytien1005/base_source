package wee.digital.library.extension

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

fun <T : ViewModel> ViewModelStoreOwner.viewModel(cls: KClass<T>): Lazy<T> {
    return lazy { ViewModelProvider(this).get(cls.java) }
}

fun <T : ViewModel> ViewModelStoreOwner.newVM(cls: KClass<T>): Lazy<T> {
    return lazy { ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[cls.java] }
}

fun <T : ViewModel> Fragment.activityVM(cls: KClass<T>): Lazy<T> {
    return lazy { ViewModelProvider(requireActivity()).get(cls.java) }
}

@Suppress("UNCHECKED_CAST")
fun <R, T : LiveData<R>> T.event(): T {
    val result = SingleLiveData<R>()
    result.addSource(this) {
        result.value = it as R
    }
    return result as T
}

inline fun <T> LiveData<T?>.observe(owner: LifecycleOwner, crossinline block: (t: T?) -> Unit) {
    this.observe(owner, Observer {
        block(it)
    })
}

/**
 * Live data only trigger when data change for multi observer
 */
open class SingleLiveData<T> : MediatorLiveData<T>() {

    private val observers = ConcurrentHashMap<LifecycleOwner, MutableSet<ObserverWrapper<T>>>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer)
        val set = observers[owner]
        set?.apply {
            @Suppress("UNCHECKED_CAST")
            add(wrapper as ObserverWrapper<T>)
        } ?: run {
            val newSet = Collections.newSetFromMap(ConcurrentHashMap<ObserverWrapper<T>, Boolean>())
            @Suppress("UNCHECKED_CAST")
            newSet.add(wrapper as ObserverWrapper<T>?)
            observers[owner] = newSet
        }
        super.observe(owner, wrapper)
    }

    override fun removeObservers(owner: LifecycleOwner) {
        observers.remove(owner)
        super.removeObservers(owner)
    }

    override fun removeObserver(observer: Observer<in T>) {
        observers.forEach {
            if (it.value.remove(observer as Observer<T>)) {
                if (it.value.isEmpty()) {
                    observers.remove(it.key)
                }
                return@forEach
            }
        }
        super.removeObserver(observer)
    }

    @MainThread
    override fun setValue(t: T?) {
        observers.forEach { it.value.forEach { wrapper -> wrapper.newValue() } }
        super.setValue(t)
    }

    protected open fun onDataChanged(t: T?) {
    }


    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    private inner class ObserverWrapper<R>(private val observer: Observer<R>) : Observer<R> {

        private val pending = AtomicBoolean(false)

        override fun onChanged(t: R?) {
            if (pending.compareAndSet(true, false)) {
                @Suppress("UNCHECKED_CAST")
                (t as? T)?.also { onDataChanged(it) }
                observer.onChanged(t)
            }
        }

        fun newValue() {
            pending.set(true)
        }
    }

}

open class EventLiveData : MediatorLiveData<Boolean?>() {

    private val observers = ConcurrentHashMap<LifecycleOwner, MutableSet<ObserverWrapper>>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in Boolean?>) {
        val wrapper = ObserverWrapper(observer)
        val set = observers[owner]
        set?.apply {
            @Suppress("UNCHECKED_CAST")
            add(wrapper)
        } ?: run {
            val newSet = Collections.newSetFromMap(ConcurrentHashMap<ObserverWrapper, Boolean>())
            @Suppress("UNCHECKED_CAST")
            newSet.add(wrapper as ObserverWrapper?)
            observers[owner] = newSet
        }
        super.observe(owner, wrapper)
    }

    override fun removeObservers(owner: LifecycleOwner) {
        observers.remove(owner)
        super.removeObservers(owner)
    }

    override fun removeObserver(observer: Observer<in Boolean?>) {
        observers.forEach {
            if (it.value.remove(observer as EventLiveData.ObserverWrapper)) {
                if (it.value.isEmpty()) {
                    observers.remove(it.key)
                }
                return@forEach
            }
        }
        super.removeObserver(observer)
    }

    @MainThread
    override fun setValue(t: Boolean?) {
        observers.forEach { it.value.forEach { wrapper -> wrapper.newValue() } }
        super.setValue(t)
    }

    protected open fun onDataChanged(t: Boolean?) {
    }


    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    private inner class ObserverWrapper(private val observer: Observer<in Boolean?>) : Observer<Boolean?> {

        private val pending = AtomicBoolean(false)

        override fun onChanged(t: Boolean?) {
            if (pending.compareAndSet(true, false)) {
                when (t) {
                    true -> {
                        onDataChanged(t)
                        observer.onChanged(t)
                    }
                }
            }
        }

        fun newValue() {
            pending.set(true)
        }
    }

}

/**
 * Live data only trigger when data change if value none null
 */
class NonNullLiveData<T> : MutableLiveData<T>() {

    private val observers = ConcurrentHashMap<LifecycleOwner, MutableSet<ObserverWrapper<T>>>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer)
        val set = observers[owner]
        set?.apply {
            @Suppress("UNCHECKED_CAST")
            add(wrapper as ObserverWrapper<T>)
        } ?: run {
            val newSet = Collections.newSetFromMap(ConcurrentHashMap<ObserverWrapper<T>, Boolean>())
            @Suppress("UNCHECKED_CAST")
            newSet.add(wrapper as ObserverWrapper<T>?)
            observers[owner] = newSet
        }
        super.observe(owner, wrapper)
    }

    override fun removeObservers(owner: LifecycleOwner) {
        observers.remove(owner)
        super.removeObservers(owner)
    }

    override fun removeObserver(observer: Observer<in T>) {
        observers.forEach {
            if (it.value.remove(observer as Observer<T>)) {
                if (it.value.isEmpty()) {
                    observers.remove(it.key)
                }
                return@forEach
            }
        }
        super.removeObserver(observer)
    }

    private inner class ObserverWrapper<R>(private val observer: Observer<R>) : Observer<R> {
        override fun onChanged(t: R?) {
            @Suppress("UNCHECKED_CAST")
            (t as? T)?.also {
                observer.onChanged(t)
            }
        }
    }

}
