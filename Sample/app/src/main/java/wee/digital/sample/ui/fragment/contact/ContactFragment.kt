package wee.digital.sample.ui.fragment.contact

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.viewModel
import wee.digital.sample.databinding.ContactBinding
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.ui.model.StoreUser

class ContactFragment : MainFragment<ContactBinding>() {

    private val adapter = ContactAdapter()

    private val vm by viewModel(ContactVM::class)

    override fun inflating(): (LayoutInflater) -> ContactBinding {
        return ContactBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.viewSearch)
        adapter.bind(bind.recyclerView)
        bind.inputViewSearch.onTextChanged = {
            vm.searchUserByName(it)
        }
    }

    override fun onLiveDataObserve() {
        vm.contactsLiveData.observe {
            updateListUser(it)
        }
    }

    override fun onViewClick(v: View?) {
        when(v){
            bind.viewSearch -> vm.searchUserByName("bao-bao")
        }
    }

    private fun updateListUser(list: List<StoreUser>?) {
        adapter.set(list)
    }


}