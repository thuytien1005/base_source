package wee.digital.library.adapter

import android.view.View

class BlankAdapter(
        private val layoutRes: Int,
        var onBind: (View) -> Unit = {},
) : BaseRecyclerAdapter<Int>() {

    override fun blankLayoutResource(): Int {
        return layoutRes
    }

    override fun layoutResource(model: Int, position: Int): Int {
        return 0
    }

    override fun View.onBindModel(model: Int, position: Int, layout: Int) {
    }

    override fun View.onBindBlank(layout: Int) {
        onBind(this)
    }

}