package wee.digital.sample.ui.main.fragment.selectable

import android.view.LayoutInflater
import android.view.View
import wee.digital.sample.databinding.SelectableBinding
import wee.digital.sample.ui.main.MainDialogFragment

class SelectableFragment : MainDialogFragment<SelectableBinding>() {

    override fun inflating(): (LayoutInflater) -> SelectableBinding {
        return SelectableBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.dialogView, bind.viewDismiss)
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
            bind.dialogView, bind.viewDismiss -> dismissAllowingStateLoss()
        }
    }

    private fun onBindArg(arg: SelectableArg?) {
        arg ?: return
        bind.dialogTextViewTitle.text = arg.title
        SelectableAdapter().also {
            it.set(arg.listItem)
            it.selectedItem = arg.selectedItem
            it.bind(bind.selectableRecyclerView)
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