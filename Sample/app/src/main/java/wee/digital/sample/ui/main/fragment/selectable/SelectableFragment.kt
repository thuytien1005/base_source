package wee.digital.sample.ui.main.fragment.selectable

import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.selectable.*
import wee.digital.sample.databinding.SelectableBinding
import wee.digital.sample.ui.main.MainDialogFragment

class SelectableFragment : MainDialogFragment<SelectableBinding>() {

    override fun inflating(): (LayoutInflater) -> SelectableBinding {
        return SelectableBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(dialogView, dialogViewDismiss)
    }

    override fun onLiveDataObserve() {
        dialogVM.selectableLiveData.observe(this::onBindArg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBindDismiss(dialogVM.selectableLiveData.value)
    }

    override fun onViewClick(v: View?) {
        when (v) {
            dialogView, dialogViewDismiss -> dismissAllowingStateLoss()
        }
    }

    private fun onBindArg(arg: SelectableArg?) {
        arg ?: return
        dialogTextViewTitle.text = arg.title
        SelectableAdapter().also {
            it.set(arg.listItem)
            it.selectedItem = arg.selectedItem
            it.bind(selectableRecyclerView)
            it.onItemClick = { model, _ ->
                arg.selectedItem = model
                dismissAllowingStateLoss()
            }
        }
    }

    private fun onBindDismiss(arg: SelectableArg?) {
        arg ?: return
        arg.onDismiss()
        arg.selectedItem?.also { selectedItem ->
            arg.itemClickList.forEach {
                it(selectedItem)
            }
        }
    }

}