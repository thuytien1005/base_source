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

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun inflating(): (LayoutInflater, ViewGroup?, Boolean) -> WidgetInputMessageBinding {
        return WidgetInputMessageBinding::inflate
    }

    override fun onInitialize(context: Context, types: TypedArray) {
        bind.chatInputInput.setOnFocusChangeListener { _, hasFocus ->
            when (hasFocus) {
                true -> bind.chatInputRoot.transitionToState(R.id.focus)
                else -> bind.chatInputRoot.transitionToState(R.id.unFocus)
            }
        }
    }

    /**
     * method click on view
     */
    fun onCameraClick(block: () -> Unit) {
        bind.chatInputCamera.addViewClickListener { block() }
    }

    fun onPhotoClick(block: () -> Unit) {
        bind.chatInputPhoto.addViewClickListener { block() }
    }

    fun onAddClick(block: () -> Unit) {
        bind.chatInputAdd.addViewClickListener { block() }
    }

    fun onMicClick(block: () -> Unit) {
//        bind.chatInputMic.addViewClickListener { block() }
    }

    fun onEmojiClick(block: () -> Unit) {
//        bind.chatInputEmoji.addViewClickListener { block() }
    }

}