package wee.digital.sample.ui.fragment.dialog.selectable

import android.view.View
import kotlinx.android.synthetic.main.selectable_item.view.*
import wee.digital.library.adapter.BaseRecyclerAdapter
import wee.digital.library.extension.bold
import wee.digital.widget.extension.isGone
import wee.digital.widget.extension.regular
import wee.digital.sample.R
import wee.digital.widget.extension.bold

open class SelectableAdapter : BaseRecyclerAdapter<Selectable>() {

    var selectedItem: Selectable? = null

    override fun layoutResource(model: Selectable, position: Int): Int {
        return R.layout.selectable_item
    }

    override fun View.onBindModel(model: Selectable, position: Int, layout: Int) {
        selectableImageView.isGone(model.icon == 0)
        selectableImageView.setImageResource(model.icon)
        selectableTextViewItem.text = model.text
        if (model.id != selectedItem?.id) {
            selectableTextViewItem.regular()
        } else {
            selectableTextViewItem.bold()
        }
    }

}