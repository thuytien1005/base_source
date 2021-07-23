package wee.digital.sample.ui.fragment.contact

import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.viewModel
import wee.digital.sample.R
import wee.digital.sample.databinding.ContactBinding
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.ui.model.StoreUser

class ContactFragment : MainFragment<ContactBinding>() {

    private val adapter = ContactAdapter()

    private val vm by viewModel(ContactVM::class)

    private var searchJob: Job? = null

    override fun inflating(): (LayoutInflater) -> ContactBinding {
        return ContactBinding::inflate
    }

    override fun onViewCreated() {
        vm.syncContact(auth.uid)
        adapter.onItemClick = { it, _ -> userItemClick(it) }
        bind.inputViewSearch.onTextChanged = this::onSearchUser
        adapter.bind(bind.recyclerView,3)
    }

    override fun onLiveDataObserve() {
        vm.contactsSearchLiveData.observe {
            updateListUser(it)
        }
        vm.allListContacts.observe {
            updateListUser(it)
        }
    }

    /**
     *
     */
    private fun updateListUser(list: List<StoreUser>?) {
        list?.forEach { if (it.uid == auth.uid) (list as MutableList).remove(it) }
        when (bind.inputViewSearch.text.isNullOrEmpty()) {
            true -> adapter.set(vm.allListContacts.value)
            false -> adapter.set(list)
        }
    }

    private fun userItemClick(data: StoreUser) {
        mainVM.contactAdapterSelected = data
        vm.insertContact(auth.uid.toString(), data.uid)
        navigate(R.id.action_global_infoFragment)
    }

    private fun onSearchUser(searchText: String) {
        searchJob?.cancel()
        lifecycleScope.launch(Dispatchers.IO) {
            delay(500)
            vm.searchUserByName(searchText)
        }
    }

}