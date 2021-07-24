package wee.digital.sample.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import wee.digital.library.adapter.addViewClickListener
import wee.digital.sample.R
import wee.digital.sample.databinding.WidgetInputMessageBinding
import wee.digital.widget.base.AppCustomView

class WidgetChatInput : AppCustomView<WidgetInputMessageBinding> {

    var listener: WidgetChatInputListener? = null

    private var iconSend = false

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun inflating(): (LayoutInflater, ViewGroup?, Boolean) -> WidgetInputMessageBinding {
        return WidgetInputMessageBinding::inflate
    }

    override fun onInitialize(context: Context, types: TypedArray) {
        bind.chatInputInput.setOnFocusChangeListener { _, hasFocus ->
            when (hasFocus) {
                true -> {
                    iconSend = true
                    bind.chatInputAdd.setImageResource(R.drawable.ic_send)
                    bind.chatInputRoot.transitionToState(R.id.focus)
                }
                else -> {
                    iconSend = false
                    bind.chatInputAdd.setImageResource(R.drawable.ic_plus)
                    bind.chatInputRoot.transitionToState(R.id.unFocus)
                }
            }
        }
        bind.chatInputCamera.addViewClickListener {
            listener?.onCameraClick()
        }
        bind.chatInputPhoto.addViewClickListener {
            listener?.onPhotoClick()
        }
        bind.chatInputAdd.addViewClickListener {
            when (iconSend) {
                true -> listener?.onSendClick(text)
                else -> listener?.onAddClick()
            }
        }
        bind.chatInputAudio.addViewClickListener {
            listener?.onMicClick()
        }
        bind.chatInputEmoji.addViewClickListener {
            listener?.onEmojiClick()
        }
    }

    /**
     * method click on view
     */
    fun onEmojiClick(block: () -> Unit) {

    }

    var text: String
        get() = bind.chatInputInput.text.toString()
        set(value) {
            bind.chatInputInput.setText(value)
        }

    interface WidgetChatInputListener {
        fun onAddClick() {}
        fun onMicClick() {}
        fun onPhotoClick() {}
        fun onEmojiClick() {}
        fun onCameraClick() {}
        fun onSendClick(mess: String) {}
    }

}