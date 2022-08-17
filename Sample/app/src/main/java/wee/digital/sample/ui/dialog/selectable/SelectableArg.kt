package wee.digital.sample.ui.dialog.selectable

import wee.digital.sample.ui.dialog.DialogArg

class SelectableArg : DialogArg() {
    var key: Int = 0
    var title: String? = null
    var message: String? = null
    var selectedItem: Selectable? = null
    var itemList: List<Selectable>? = null
    var onItemSelected: ((Selectable) -> Unit)? = null
}