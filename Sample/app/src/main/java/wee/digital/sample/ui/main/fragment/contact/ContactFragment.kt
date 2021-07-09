package wee.digital.sample.ui.main.fragment.contact

import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.toast
import wee.digital.library.extension.viewModel
import wee.digital.sample.databinding.ContactBinding
import wee.digital.sample.repository.model.UserData
import wee.digital.sample.ui.main.MainFragment
import wee.digital.widget.custom.InputView

class ContactFragment : MainFragment<ContactBinding>(), InputView.InputListener {

    private val adapter = ContactAdapter()

    private val vm by viewModel(ContactVM::class)

    private var searchJob: Job? = null

    override fun inflating(): (LayoutInflater) -> ContactBinding {
        return ContactBinding::inflate
    }

    override fun onViewCreated() {
        bind.contactSearch.listener = this
    }

    override fun onLiveDataObserve() {
        vm.listCustomerEvent.observe { updateListUser(it) }
    }

    override fun textChangeListener(s: String) {
        searchCustomer(s)
    }

    private fun searchCustomer(it: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch(Dispatchers.IO) {
            log.d("searchCustomer $it")
            delay(500)
            vm.queryCustomer(it)
        }
    }

    private fun updateListUser(list: List<UserData>) {
        adapter.set(list)
        adapter.bind(bind.contactRecycler)
        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        searchJob?.cancel()
    }

}