package wee.digital.sample.ui.fragment.contact

import androidx.viewbinding.ViewBinding
import wee.digital.library.adapter.BaseListAdapter
import wee.digital.library.adapter.ItemInflating
import wee.digital.sample.databinding.ContactItemBinding
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.bind


class ContactAdapter : BaseListAdapter<StoreUser>(StoreUser.itemDiffer) {

    override fun itemInflating(item: StoreUser, position: Int): ItemInflating {
        return ContactItemBinding::inflate
    }

    override fun ViewBinding.onBindItem(item: StoreUser, position: Int) {
        if (this is ContactItemBinding) {
            itemContactAvatar.bind(item)
            itemContactName.text = item.fullName()
        }
    }


}