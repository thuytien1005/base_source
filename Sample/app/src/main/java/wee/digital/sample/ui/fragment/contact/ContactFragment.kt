package wee.digital.sample.ui.fragment.contact

import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.sample.R
import wee.digital.sample.databinding.ContactBinding
import wee.digital.sample.ui.fragment.profile.ProfileVM
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.ui.model.StoreUser

class ContactFragment : MainFragment<ContactBinding>() {

    private val contactAdapter = ContactAdapter()

    private val searchAdapter = ContactAdapter()

    private val vm by lazyViewModel(ContactVM::class)

    private var searchJob: Job? = null

    override fun inflating(): (LayoutInflater) -> ContactBinding {
        return ContactBinding::inflate
    }

    override fun onViewCreated() {
        contactAdapter.onItemClick = { it, _ -> userItemClick(it) }
        contactAdapter.bind(bind.recyclerView, 3)
        searchAdapter.onItemClick = { it, _ -> userItemClick(it) }
        bind.inputViewSearch.onTextChanged = this::onSearchUser
    }

    override fun onLiveDataObserve() {
        vm.contactsLiveData.observe {
            contactAdapter.set(it)
        }
        vm.searchLiveData.observe {
            searchAdapter.set(it)
        }
    }

    private fun userItemClick(data: StoreUser) {
        mainVM.contactAdapterSelected = data
        activityVM(ProfileVM::class).userLiveData.value = data
        navigate(R.id.action_global_profileFragment)
    }

    private fun onSearchUser(searchText: String) {
        searchJob?.cancel()
        lifecycleScope.launch(Dispatchers.IO) {
            delay(500)
            when {
                searchText.isNullOrEmpty() -> {
                    setContactAdapter(contactAdapter)
                }
                else -> {
                    setContactAdapter(searchAdapter)
                    vm.search(searchText)
                }
            }
        }
    }

    private fun setContactAdapter(adapter: RecyclerView.Adapter<*>) {
        if(bind.recyclerView.adapter != adapter) {
            bind.recyclerView.adapter = adapter
        }
    }

}