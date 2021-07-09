package wee.digital.sample.ui.fragment.contact

import android.view.LayoutInflater
import wee.digital.library.extension.viewModel
import wee.digital.sample.databinding.ContactBinding
import wee.digital.sample.repository.model.UserData
import wee.digital.sample.ui.main.MainFragment

class ContactFragment : MainFragment<ContactBinding>() {

    private val adapter = ContactAdapter()

    private val vm by viewModel(ContactVM::class)

    override fun inflating(): (LayoutInflater) -> ContactBinding {
        return ContactBinding::inflate
    }

    override fun onViewCreated() {
        bind.inputViewSearch.onTextChanged = {
            vm.searchUserByName(it)
        }
    }

    override fun onLiveDataObserve() {
        vm.usersLiveData.observe { updateListUser(it) }
    }

    private fun updateListUser(list: List<UserData>?) {
        adapter.set(list)
        adapter.bind(bind.recyclerView)
        adapter.notifyDataSetChanged()
    }


}