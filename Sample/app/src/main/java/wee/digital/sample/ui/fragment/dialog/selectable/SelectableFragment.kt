package wee.digital.sample.ui.fragment.dialog.selectable

import android.view.View
import kotlinx.android.synthetic.main.selectable.*
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainDialog

class SelectableFragment : MainDialog() {

    private val liveData get() = mainVM.selectableLiveData

    override fun layoutResource(): Int {
        return R.layout.selectable
    }

    override fun onViewCreated() {
        addClickListener(dialogView, dialogViewDismiss)
    }

    override fun onLiveDataObserve() {
        liveData.observe(this::onBindArg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBindDismiss(liveData.value)
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