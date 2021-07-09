package wee.digital.sample.ui.main.fragment.contact

import androidx.viewbinding.ViewBinding
import wee.digital.library.extension.load
import wee.digital.sample.databinding.ItemContactBinding
import wee.digital.sample.repository.model.UserData
import wee.digital.sample.ui.base.BaseBindRecyclerAdapter
import wee.digital.sample.ui.base.ItemInflating

class ContactAdapter : BaseBindRecyclerAdapter<UserData>() {

    override fun itemInflating(item: UserData, position: Int): ItemInflating {
        return ItemContactBinding::inflate
    }

    override fun ViewBinding.onBindItem(item: UserData, position: Int) {
        if (this is ItemContactBinding) {
            itemContactAvatar.load(item.url)
            itemContactName.text = item.name
        }
    }


}