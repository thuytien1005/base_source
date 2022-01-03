package wee.digital.sample.ui.fragment.dialog.selectable

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import wee.digital.sample.R
import wee.digital.sample.databinding.SelectableBinding
import wee.digital.sample.databinding.SelectableItemBinding
import wee.digital.sample.ui.main.MainDialogFragment
import wee.digital.widget.adapter.BaseSelectableAdapter
import wee.digital.widget.adapter.InflaterInvokerBinding
import wee.digital.widget.extension.color

class SelectableFragment : MainDialogFragment<SelectableBinding>() {

    private val arg get() = dialogVM.selectableLiveData.value

    /**
     * [MainDialogFragment] implements
     */
    override fun inflating(): (LayoutInflater) -> ViewBinding {
        return SelectableBinding::inflate
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        dialogVM.selectableLiveData.observe {
            if (it != null) {
                onBindArg(it)
            }
        }
    }

    override fun onBackPressed() {
        clickedView = vb.viewClose
        super.onBackPressed()
    }

    override fun onDetach() {
        super.onDetach()
        arg?.also { onBindDismiss(it) }
    }

    /**
     *
     */
    private fun onBindArg(it: SelectableArg) {
        //vb.viewClose.isShow(it.dismissWhenTouchOutside)
        vb.viewClose.addViewClickListener { onBackPressed() }
        if (it.dismissWhenTouchOutside) {
            vb.layoutContent.addViewClickListener { onBackPressed() }
        }
        vb.textViewTitle.isGone(it.title.isNullOrEmpty())
        vb.textViewTitle.text = it.title
        vb.textViewMessage.text = it.message
        vb.textViewMessage.isGone(it.message.isNullOrEmpty())
        SelectableAdapter().apply {
            select(selectedItem)
            setElseEmpty(it.itemList)
            bind(vb.recyclerView)
            onSelectionChanged { selectable ->
                clickedView = vb.recyclerView
                val liveData = dialogVM.selectableMap[it.key]
                liveData?.value = selectable
                launch(400) {
                    dismiss()
                }
            }
        }
    }

    private fun onBindDismiss(it: SelectableArg) {
        when (clickedView) {
            vb.viewClose -> it.onCloseClick?.invoke()
        }
        it.onDismiss?.invoke()
    }

    class SelectableAdapter : BaseSelectableAdapter<Selectable, SelectableItemBinding>() {

        override fun itemInflating(): InflaterInvokerBinding<SelectableItemBinding> {
            return SelectableItemBinding::inflate
        }

        override fun areSameItems(item: Selectable?, other: Selectable?): Boolean {
            return item?.id == selectedItem?.id
        }

        override fun SelectableItemBinding.onBindDefaultItem(item: Selectable, position: Int) {
            selectableTextView.apply {
                drawableStartRes = item.ic
                title = item.text
                text = item.description
                tint = item.icColor
            }

        }

        override fun SelectableItemBinding.onBindSelectedItem(item: Selectable, position: Int) {
            selectableTextView.strokeLineColor = color(R.color.colorPrimary)
            imageViewCheck.setImageResource(R.drawable.ic_checkbox_checked2)
        }

        override fun SelectableItemBinding.onBindUnselectedItem(item: Selectable, position: Int) {
            selectableTextView.strokeLineColor = color(R.color.colorHint)
            imageViewCheck.setImageResource(R.drawable.ic_checkbox_uncheck2)
        }

    }

}