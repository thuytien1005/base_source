package wee.digital.sample.ui.fragment.contact

import androidx.viewbinding.ViewBinding
import wee.digital.library.adapter.BaseListAdapter
import wee.digital.library.adapter.ItemInflating
import wee.digital.library.extension.load
import wee.digital.sample.databinding.ContactItemBinding
import wee.digital.sample.repository.model.UserData

class ContactAdapter : BaseListAdapter<UserData>() {

    override fun itemInflating(item: UserData, position: Int): ItemInflating {
        return ContactItemBinding::inflate
    }

    override fun ViewBinding.onBindItem(item: UserData, position: Int) {
        if (this is ContactItemBinding) {
            itemContactAvatar.load(item.url)
            itemContactName.text = item.name
        }
    }


}