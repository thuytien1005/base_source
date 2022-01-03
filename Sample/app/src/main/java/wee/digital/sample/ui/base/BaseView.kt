package wee.digital.sample.ui.base

import android.os.Build
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.sample.R
import wee.digital.widget.app
import wee.digital.widget.extension.ViewClickListener
import kotlin.reflect.KClass

interface BaseView {

    val baseActivity: BaseActivity<*>? get() = null

    val lifecycleOwner: LifecycleOwner

    val uiJobList: MutableList<Job>

    fun launch(delayInterval: Long, block: suspend CoroutineScope.() -> Unit): Job {
        val job = lifecycleOwner.lifecycleScope.launch {
            delay(delayInterval)
            block()
        }
        uiJobList.add(job)
        return job
    }

    fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        val job = lifecycleOwner.lifecycleScope.launch {
            block()
        }
        uiJobList.add(job)
        return job
    }

    fun activityNavController(): NavController?

    fun onViewCreated()

    fun onLiveDataObserve() = Unit

    fun addClickListener(vararg views: View?) {
        addClickListener(delayedInterval = 400, views = views)
    }

    fun addClickListener(delayedInterval: Long, vararg views: View?) {
        val listener = object : ViewClickListener(delayedInterval) {
            override fun onClicks(v: View) {
                onViewClick(v)
            }
        }
        views.forEach {
            it?.isClickable = true
            it?.setOnClickListener(listener)
        }
    }

    fun onViewClick(v: View?) = Unit

    fun NavController?.navigate(
        @IdRes actionId: Int,
        block: (NavigationBuilder.() -> Unit)? = null
    ) {
        this ?: return
        NavigationBuilder(this).also {
            it.setVerticalAnim()
            block?.invoke(it)
            it.navigate(actionId)
        }
    }

    fun navigate(@IdRes actionId: Int, block: (NavigationBuilder.() -> Unit)? = null) {
        activityNavController().navigate(actionId, block)
    }

    fun popBackStack(@IdRes fragmentId: Int, inclusive: Boolean = false) {
        activityNavController()?.popBackStack(fragmentId, inclusive)
    }

    fun <T> LiveData<T>.singleObserve(block: (T) -> Unit) {
        removeObservers()
        observe(lifecycleOwner, Observer {
            removeObservers()
            block(it)
        })
    }

    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(lifecycleOwner, Observer(block))
    }

    fun <T> LiveData<T>.removeObservers() {
        removeObservers(lifecycleOwner)
    }

    fun <T : ViewModel> ViewModelStoreOwner.lazyViewModel(cls: KClass<T>): Lazy<T> {
        return lazy { ViewModelProvider(this).get(cls.java) }
    }

    fun <T : ViewModel> ViewModelStoreOwner.viewModel(cls: KClass<T>): T {
        return ViewModelProvider(this).get(cls.java)
    }

    fun View.toSharedElement(): Pair<View, String> {
        return Pair(this, this.transitionName)
    }

    fun View.show() {
        if (visibility != View.VISIBLE) post {
            visibility = View.VISIBLE
        }
    }

    fun View.isShow(show: Boolean?) {
        visibility = if (show == true) View.VISIBLE
        else View.INVISIBLE
    }

    fun View.hide() {
        if (visibility != View.INVISIBLE) post {
            visibility = View.INVISIBLE
        }
    }

    fun View.isHide(hide: Boolean?) {
        visibility = if (hide == true) View.INVISIBLE
        else View.VISIBLE
    }

    fun View.gone() {
        if (visibility != View.GONE) post {
            visibility = View.GONE
        }
    }

    fun View.isGone(gone: Boolean?) {
        post {
            visibility = if (gone == true) View.GONE
            else View.VISIBLE
        }
    }

    fun View?.post(delayed: Long, runnable: Runnable) {
        this?.postDelayed(runnable, delayed)
    }

    fun View?.addViewClickListener(delayedInterval: Long, listener: ((View?) -> Unit)? = null) {
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
        setOnClickListener(object : ViewClickListener(delayedInterval) {
            override fun onClicks(v: View) {
                listener(v)
            }
        })
        if (this is EditText) {
            isFocusable = false
            isCursorVisible = false
        }
    }

    fun View?.addViewClickListener(listener: ((View?) -> Unit)? = null) {
        addViewClickListener(0, listener)
    }

    fun CompoundButton.onCheckedChange(block: ((Boolean) -> Unit)?) {
        this.setOnCheckedChangeListener { _, isChecked -> block?.invoke(isChecked) }
    }

    fun show(vararg views: View) {
        for (v in views) v.show()
    }

    fun hide(vararg views: View) {
        for (v in views) v.hide()
    }

    fun gone(vararg views: View) {
        for (v in views) v.gone()
    }

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
        return colorRes(R.color.colorPrimary)
    }

    fun String.bold(): String {
        return "<b>$this</b>"
    }

    fun TextView.setHyperText(@StringRes res: Int, vararg args: Any) {
        setHyperText(string(res), * args)
    }

    fun TextView.setHyperText(s: String?, vararg args: Any) {
        post {
            text = try {
                when {
                    s.isNullOrEmpty() -> null
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(
                        s.format(*args),
                        Html.FROM_HTML_MODE_LEGACY
                    )
                    else -> {
                        @Suppress("DEPRECATION")
                        Html.fromHtml(s.format(*args))
                    }
                }
            } catch (e: Throwable) {
                s
            }
        }
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

    fun inputModeAdjustResize() {
        baseActivity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    fun inputModeAdjustNothing() {
        baseActivity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }
}