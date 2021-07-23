package wee.digital.sample.ui.fragment.chat

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.toast
import wee.digital.sample.databinding.ChatBinding
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.main.MainFragment

class ChatFragment : MainFragment<ChatBinding>() {

    override fun inflating(): (LayoutInflater) -> ChatBinding {
        return ChatBinding::inflate
    }

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {}

}