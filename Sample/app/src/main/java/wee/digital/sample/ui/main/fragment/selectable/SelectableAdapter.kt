package wee.digital.sample.ui.main.fragment.selectable

import androidx.viewbinding.ViewBinding
import wee.digital.library.adapter.BaseListAdapter
import wee.digital.library.adapter.ItemInflating
import wee.digital.sample.databinding.SelectableItemBinding
import wee.digital.widget.extension.bold
import wee.digital.widget.extension.isGone
import wee.digital.widget.extension.regular

open class SelectableAdapter : BaseListAdapter<Selectable>() {

    var selectedItem: Selectable? = null

    override fun itemInflating(item: Selectable, position: Int): ItemInflating {
        return SelectableItemBinding::inflate
    }

    override fun ViewBinding.onBindItem(item: Selectable, position: Int) {
        (this as? SelectableItemBinding)?.apply {
            selectableImageView.isGone(item.icon == 0)
            selectableImageView.setImageResource(item.icon)
            selectableTextViewItem.text = item.text
            if (item.id != selectedItem?.id) {
                selectableTextViewItem.regular()
            } else {
                selectableTextViewItem.bold()
            }
        }

    }

}