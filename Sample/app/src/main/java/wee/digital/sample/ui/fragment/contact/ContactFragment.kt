package wee.digital.sample.ui.fragment.contact

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.viewModel
import wee.digital.sample.databinding.ContactBinding
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
        adapter.bind(bind.recyclerView)
        bind.inputViewSearch.onTextChanged = this::onSearchUser
    }

    override fun onLiveDataObserve() {
        vm.contactsLiveData.observe {
            updateListUser(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {

        }
    }

    /**
     *
     */
    private fun updateListUser(list: List<StoreUser>?) {
        adapter.set(list)
    }

    private fun onSearchUser(searchText: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(500)
            vm.searchUserByName(searchText)
        }
    }

}