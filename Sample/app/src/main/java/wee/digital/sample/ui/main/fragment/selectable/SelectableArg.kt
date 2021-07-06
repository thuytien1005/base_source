package wee.digital.sample.ui.main.fragment.selectable

class SelectableArg {

    var key: Int = 0

    var title: String? = null

    var message: String? = null

    var onDismiss: () -> Unit = {}

    var selectedItem: Selectable? = null

    var listItem: List<Selectable>? = null

    val itemClickList = mutableListOf<(Selectable) -> Unit>()

    fun addOnItemClick(block: (Selectable) -> Unit) {
        itemClickList.add(block)
    }

}