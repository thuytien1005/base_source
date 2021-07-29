package wee.digital.sample.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import wee.digital.library.extension.hideKeyboard
import wee.digital.sample.R
import wee.digital.sample.databinding.WidgetInputMessageBinding
import wee.digital.sample.utils.heightRecycler
import wee.digital.widget.base.AppCustomView
import wee.digital.widget.extension.SimpleTextWatcher
import wee.digital.widget.extension.addViewClickListener
import wee.digital.widget.extension.beginTransition


class WidgetChatInput : AppCustomView<WidgetInputMessageBinding>, SimpleTextWatcher {

    var listener: WidgetChatInputListener? = null

    var text: String
        get() {
            val t = bind.chatInputInput.text.toString()
            return if (t.isEmpty()) "\uD83D\uDC4D"/*like*/ else t
        }
        set(value) {
            bind.chatInputInput.setText(value)
        }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun inflating(): (LayoutInflater, ViewGroup?, Boolean) -> WidgetInputMessageBinding {
        return WidgetInputMessageBinding::inflate
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onInitialize(context: Context, types: TypedArray) {
        bind.chatInputInput.addTextChangedListener(this)
        bind.chatInputRecycler.layoutParams = LayoutParams(0, context.heightRecycler())

        bind.chatInputAudio.addViewClickListener {
            listener?.onMicClick()
        }
        bind.chatInputEmoji.addViewClickListener {
            listener?.onEmojiClick()
        }
        bind.chatInputSend.addViewClickListener {
            listener?.onSendClick(text)
        }
        bind.chatInputPhoto.addViewClickListener {
            bind.chatInputInput.hideKeyboard()
            listener?.onPhotoClick()
        }
        bind.chatInputInput.setOnTouchListener { _, _ ->
            animViewFocus()
            false
        }
        bind.chatInputInput.setOnFocusChangeListener { _, hasFocus ->
            when (hasFocus) {
                true -> animViewFocus()
                else -> animViewUnFocus()
            }
        }
        bind.chatInputAdd.addViewClickListener {
            when (iconAdd) {
                true -> listener?.onAddClick()
                else -> animViewUnFocus()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        when (s.toString().isEmpty()) {
            true -> bind.chatInputSend.setImageResource(R.drawable.ic_like)
            else -> bind.chatInputSend.setImageResource(R.drawable.ic_send)
        }
    }

    /**
     * config animation
     */
    private var iconAdd = true

    private fun animViewFocus() {
        iconAdd = false
        val view = bind.chatInputBg.id
        val viewAdd = bind.chatInputAdd.id
        ChangeBounds().apply { duration = 250 }.beginTransition(bind.chatInputParentView) {
            this.clear(bind.chatInputBg.id, ConstraintSet.START)
            connect(view, ConstraintSet.START, viewAdd, ConstraintSet.END)
        }
        bind.chatInputAdd.setImageResource(R.drawable.ic_next)
    }

    private fun animViewUnFocus() {
        iconAdd = true
        val view = bind.chatInputBg.id
        val viewAudio = bind.chatInputAudio.id
        ChangeBounds().apply { duration = 250 }.beginTransition(bind.chatInputParentView) {
            this.clear(view, ConstraintSet.START)
            connect(view, ConstraintSet.START, viewAudio, ConstraintSet.END)
        }
        bind.chatInputAdd.setImageResource(R.drawable.ic_plus)
    }

    interface WidgetChatInputListener {
        fun onAddClick() {}
        fun onMicClick() {}
        fun onPhotoClick() {}
        fun onEmojiClick() {}
        fun onSendClick(mess: String, typeData : String? = null) {}
    }

}