package wee.digital.widget.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.*
import androidx.viewbinding.ViewBinding

class ViewBindingHolder<VB : ViewBinding>(val vb: VB) : RecyclerView.ViewHolder(vb.root)

class BaseViewHolder : RecyclerView.ViewHolder {
    constructor(v: View) : super(v)

    constructor(parent: ViewGroup, @LayoutRes layoutId: Int) :
            super(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
}

class GoneViewHolder : RecyclerView.ViewHolder {
    constructor(parent: ViewGroup) :
            super(View(parent.context).also { it.visibility = View.GONE })
}

class ItemOptions(val layoutId: Int = 1, val inflaterInvoker: (View) -> ViewBinding)

typealias InflaterInvoker = InflaterInvokerBinding<ViewBinding>

typealias InflaterInvokerBinding<VB> = (LayoutInflater, ViewGroup, Boolean) -> VB

fun <VB : ViewBinding> InflaterInvokerBinding<VB>.invokeItem(parent: ViewGroup): VB {
    return this.invoke(LayoutInflater.from(parent.context), parent, false)
}

typealias OnItemClick<T> = ((T) -> Unit)?

typealias OnItemViewClick<T> = ((T, ViewBinding) -> Unit)?

typealias OnItemPositionClick<T> = ((T, Int) -> Unit)?

open class DiffItemCallback<T> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return false
    }
}

fun RecyclerView.setItemAppearAnimation() {
    val screenHeight = 1700F//(context as? Activity)?.screenHeight?.toFloat() ?: 0F
    val set = AnimationSet(true)
    set.addAnimation(AlphaAnimation(0.0F, 1.0F).also {
        it.duration = 300
        it.fillAfter = true
    })
    set.addAnimation(TranslateAnimation(0F, 0F, screenHeight, 0F).also {
        it.interpolator = DecelerateInterpolator(4F)
        it.duration = 300
    })
    layoutAnimation = LayoutAnimationController(set, 0.2F)
}

private var lastClickTime: Long = 0

private var lastClickViewId: Int = 0

abstract class ItemClickListener(private val delayedInterval: Long = 320) : View.OnClickListener {

    abstract fun onClicks(v: View?)

    private val View?.isAcceptClick: Boolean get() = this?.id != lastClickViewId && delayedInterval == 0L

    private val isDelayed: Boolean get() = System.currentTimeMillis() - lastClickTime > delayedInterval

    private var hasDelayed: Boolean = false

    final override fun onClick(v: View?) {
        if (isDelayed || v.isAcceptClick) {
            lastClickViewId = v?.id ?: -1
            lastClickTime = 0
            hasDelayed = false
            onClicks(v)
        }
        if (!hasDelayed) {
            hasDelayed = true
            lastClickTime = System.currentTimeMillis()
        }
    }

}

fun View?.addItemClickListener(delayedInterval: Long, listener: ((View?) -> Unit)? = null) {
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
    this.isEnabled = true
    setOnClickListener(object : ItemClickListener(delayedInterval) {
        override fun onClicks(v: View?) {
            listener(v)
        }
    })
    if (this is EditText) {
        isFocusable = false
        isCursorVisible = false
    }
}

fun View?.addItemClickListener(listener: ((View?) -> Unit)? = null) {
    addItemClickListener(300, listener)
}

fun RecyclerView.smoothScrollToCenter(position: Int) {
    if (position < 0) return
    post {
        val smoothScroller = CenterLayoutManager.CenterSmoothScroller(context)
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }
}

fun RecyclerView.scrollToCenter(position: Int) {
    if (position < 0) return
    post {
        val smoothScroller = CenterLayoutManager.CenterSmoothScroller(context)
        smoothScroller.targetPosition = position
        layoutManager?.scrollToPosition(position)
    }
}

fun RecyclerView.invalidateLayout() {
    smoothScrollBy(1, 0)
}

fun <T : SnapHelper> T.attach(v: RecyclerView) {
    v.setHasFixedSize(true)
    v.onFlingListener = null
    this.attachToRecyclerView(v)
}

class CenterLayoutManager(
    context: Context,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        if (position !in 0 until (recyclerView.adapter?.itemCount ?: 0)) return
        val centerSmoothScroller = CenterSmoothScroller(recyclerView.context)
        centerSmoothScroller.targetPosition = position
        startSmoothScroll(centerSmoothScroller)
    }

    class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int = (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
    }

}

class CachingLinearLayoutManager(
    context: Context,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {

    var extraLayoutSpace = 30

    override fun getExtraLayoutSpace(state: RecyclerView.State): Int {
        return extraLayoutSpace
    }


}

class CachingGridLayoutManager(
    context: Context,
    spanCount: Int,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {

    var extraLayoutSpace = 30

    override fun getExtraLayoutSpace(state: RecyclerView.State): Int {
        return extraLayoutSpace
    }

}

