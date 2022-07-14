package wee.digital.sample.ui.base

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.navigation.NavController
import kotlinx.coroutines.*
import wee.digital.library.extension.toast
import wee.digital.sample.app
import wee.digital.widget.R
import wee.digital.widget.extension.ViewClickListener
import kotlin.reflect.KClass


interface BaseView {

    val baseActivity: BaseActivity<*>? get() = null

    val lifecycleOwner: LifecycleOwner

    val uiJobList: MutableList<Job>

    fun launch(delayInterval: Long, block: suspend CoroutineScope.() -> Unit): Job {
        val job = lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            delay(delayInterval)
            block()
        }
        uiJobList.add(job)
        return job
    }

    fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        return lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            block()
        }
    }

    fun activityNavController(): NavController? = null

    fun onViewCreated()

    fun onLiveDataObserve() = Unit

    fun show(dialog: DialogFragment) {
        val sfm = (baseActivity as? FragmentActivity)?.supportFragmentManager ?: return
        dialog.show(sfm, dialog.tag)
    }

    fun dismissAllDialogs() {
        (baseActivity as? FragmentActivity)?.supportFragmentManager?.fragments?.forEach {
            if (it is DialogFragment) {
                it.dismissAllowingStateLoss()
            }
        }
    }

    fun addObserve(observer: LifecycleObserver): LifecycleObserver {
        lifecycleOwner.lifecycle.addObserver(observer)
        return observer
    }

    /**
     * [ViewModel] utils
     */
    fun <T : ViewModel> activityVM(cls: KClass<T>): T {
        return ViewModelProvider(baseActivity!!)[cls.java]
    }

    fun <T : ViewModel> lazyActivityVM(cls: KClass<T>): Lazy<T> {
        return lazy { ViewModelProvider(baseActivity!!)[cls.java] }
    }

    fun <T : ViewModel> ViewModelStoreOwner.lazyViewModel(cls: KClass<T>): Lazy<T> {
        return lazy { ViewModelProvider(this)[cls.java] }
    }

    fun <T : ViewModel> ViewModelStoreOwner.viewModel(cls: KClass<T>): T {
        return ViewModelProvider(this).get(cls.java)
    }

    /**
     * [LiveData] utils
     */
    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        removeObservers(lifecycleOwner)
        observe(lifecycleOwner, Observer(block))
    }

    fun <T> LiveData<T>.removeObservers() {
        removeObservers(lifecycleOwner)
    }

    fun <T> LiveData<T>.singleObserve(block: (T) -> Unit) {
        removeObservers()
        observe(lifecycleOwner, Observer {
            removeObservers()
            block(it)
        })
    }

    /**
     * [NavController] utils
     */
    fun NavController?.navigate(@IdRes actionId: Int, block: (NavBuilder.() -> Unit)? = null) {
        if (this == null) {
            toast("NavController is null")
            return
        }
        NavBuilder(this).also {
            block?.invoke(it)
            it.setVerticalAnim()
            it.navigate(actionId)
        }
    }

    fun NavController?.navigateNoAnim(
        @IdRes actionId: Int,
        block: (NavBuilder.() -> Unit)? = null
    ) {
        if (this == null) {
            toast("NavController is null")
            return
        }
        NavBuilder(this).also {
            block?.invoke(it)
            it.navigate(actionId)
        }
    }

    fun navigateSingleTop(@IdRes actionId: Int) {
        activityNavController().navigate(actionId) {
            clearBackStack()
        }
    }

    fun navigate(@IdRes actionId: Int, block: (NavBuilder.() -> Unit)? = null) {
        activityNavController().navigate(actionId, block)
    }

    fun popBackStack(@IdRes fragmentId: Int, inclusive: Boolean = false) {
        activityNavController()?.popBackStack(fragmentId, inclusive)
    }

    fun <T> navResultLiveData(key: String? = null): MutableLiveData<T>? {
        return activityNavController()?.currentBackStackEntry?.savedStateHandle?.getLiveData(
            key
                ?: ""
        )
    }

    fun <T> setNavResult(key: String? = null, result: T) {
        activityNavController()
            ?.previousBackStackEntry
            ?.savedStateHandle?.set(key ?: "", result)
    }

    fun <T> setNavResult(result: T) {
        activityNavController()
            ?.previousBackStackEntry
            ?.savedStateHandle?.set("", result)
    }

    fun startClear(cls: Class<*>) {
        baseActivity?.run {
            val intent = Intent(this, cls)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            this.startActivity(intent)
            this.finish()
        }
    }

    /**
     * [View] utils
     */
    fun View.toSharedElement(): Pair<View, String> {
        return Pair(this, this.transitionName)
    }

    fun View.show() {
        if (visibility != View.VISIBLE) visibility = View.VISIBLE
    }

    fun View.isShow(show: Boolean?) {
        visibility = if (show == true) View.VISIBLE
        else View.INVISIBLE
    }

    fun View.hide() {
        if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
    }

    fun View.isHide(hide: Boolean?) {
        visibility = if (hide == true) View.INVISIBLE
        else View.VISIBLE
    }

    fun View.gone() {
        if (visibility != View.GONE) visibility = View.GONE
    }

    fun View.isGone(gone: Boolean?) {
        visibility = if (gone == true) View.GONE
        else View.VISIBLE
    }

    fun View?.post(delayed: Long, runnable: Runnable) {
        this?.postDelayed(runnable, delayed)
    }

    fun View?.addClickListener(delayedInterval: Long, listener: ((View) -> Unit)? = null) {
        this ?: return
        if (listener == null) {
            setOnClickListener(null)
            if (this is EditText) {
                isFocusable = true
                isCursorVisible = true
            }
            return
        }
        this.isClickable = true
        setOnClickListener(object : ViewClickListener(delayedInterval, this.id) {
            override fun onClicks(v: View) {
                listener(v)
            }
        })
        if (this is EditText) {
            isFocusable = false
            isCursorVisible = false
        }
    }

    fun View?.addClickListener(listener: ((View) -> Unit)? = null) {
        this@addClickListener.addClickListener(0, listener)
    }

    fun TextView.setHyperText(s: String?) {
        post {
            text = try {
                when {
                    s.isNullOrEmpty() -> null
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(
                        s,
                        Html.FROM_HTML_MODE_LEGACY
                    )
                    else -> {
                        @Suppress("DEPRECATION")
                        Html.fromHtml(s)
                    }
                }
            } catch (e: Throwable) {
                s
            }
        }
    }

    fun CompoundButton.onCheckedChange(block: ((Boolean) -> Unit)?) {
        this.setOnCheckedChangeListener { _, isChecked -> block?.invoke(isChecked) }
    }

    fun addClickListener(vararg views: View?) {
        val listener = object : ViewClickListener() {
            override fun onClicks(v: View) {
                onViewClick(v)
            }
        }
        views.forEach { it?.setOnClickListener(listener) }
    }

    fun onViewClick(v: View?) = Unit

    fun show(vararg views: View) {
        for (v in views) v.show()
    }

    fun hide(vararg views: View) {
        for (v in views) v.hide()
    }

    fun gone(vararg views: View) {
        for (v in views) v.gone()
    }

    /**
     * Resources utils
     */
    fun color(@ColorRes res: Int): Int {
        return ContextCompat.getColor(app, res)
    }

    fun string(@StringRes res: Int, vararg args: Any?): String {
        return try {
            String.format(app.getString(res), *args)
        } catch (ignore: Exception) {
            ""
        }
    }

    /**
     * [String] utils
     */
    fun String.br(): String = "$this<br>"

    fun String.color(hexString: String): String {
        return "<font color=$hexString>$this</font>"
    }

    fun String.color(@ColorInt color: Int): String {
        val hexString = "#${Integer.toHexString(color and 0x00ffffff)}"
        return this.color(hexString)
    }

    fun String.colorRes(@ColorRes res: Int): String {
        return this.color(ContextCompat.getColor(app, res))
    }

    fun String.colorPrimary(): String {
        return colorRes(R.color.color_primary)
    }

    fun String.bold(): String {
        return "<b>$this</b>"
    }

    /**
     * Keyboard utils
     */
    fun View?.hideKeyboard() {
        this?.post {
            val imm = app.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this@hideKeyboard.windowToken, 0)
        }
    }

    fun View?.showKeyboard() {
        this?.post {
            val imm = app.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this@showKeyboard, InputMethodManager.SHOW_IMPLICIT)
        }
    }


}

