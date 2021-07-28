package wee.digital.sample.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import wee.digital.sample.R
import wee.digital.sample.databinding.WidgetInputMessageBinding
import wee.digital.sample.utils.heightRecycler
import wee.digital.widget.base.AppCustomView
import wee.digital.widget.extension.SimpleTextWatcher
import wee.digital.widget.extension.addViewClickListener
import wee.digital.widget.extension.beginTransition

class WidgetChatInput : AppCustomView<WidgetInputMessageBinding>, SimpleTextWatcher {

    var listener: WidgetChatInputListener? = null

    private val viewTransition = ChangeBounds().apply { duration = 250 }

    private val heightRecycler get() = context.heightRecycler()

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun inflating(): (LayoutInflater, ViewGroup?, Boolean) -> WidgetInputMessageBinding {
        return WidgetInputMessageBinding::inflate
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onInitialize(context: Context, types: TypedArray) {
        bind.chatInputInput.addTextChangedListener(this)
        bind.chatInputParentRecycler.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, heightRecycler)
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
            configAdapterImage(View.VISIBLE)
        }
        bind.chatInputInput.setOnTouchListener { _, _ ->
            animViewFocus()
            configAdapterImage(View.GONE)
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

    private var iconAdd = true

    private fun animViewFocus() {
        iconAdd = false
        val view = bind.chatInputBg.id
        val viewAdd = bind.chatInputAdd.id
        viewTransition.beginTransition(bind.chatInputParentView) {
            this.clear(bind.chatInputBg.id, ConstraintSet.START)
            connect(view, ConstraintSet.START, viewAdd, ConstraintSet.END)
        }
        bind.chatInputAdd.setImageResource(R.drawable.ic_next)
    }

    private fun animViewUnFocus() {
        iconAdd = true
        val view = bind.chatInputBg.id
        val viewAudio = bind.chatInputAudio.id
        viewTransition.beginTransition(bind.chatInputParentView) {
            this.clear(view, ConstraintSet.START)
            connect(view, ConstraintSet.START, viewAudio, ConstraintSet.END)
        }
        bind.chatInputAdd.setImageResource(R.drawable.ic_plus)
    }

    private fun configAdapterImage(status: Int) {
        bind.chatInputParentRecycler.visibility = status
    }

    var text: String
        get() {
            val t = bind.chatInputInput.text.toString()
            return if (t.isEmpty()) "\uD83D\uDC4D" else t
        }
        set(value) {
            bind.chatInputInput.setText(value)
        }

    interface WidgetChatInputListener {
        fun onAddClick() {}
        fun onMicClick() {}
        fun onPhotoClick() {}
        fun onEmojiClick() {}
        fun onSendClick(mess: String) {}
    }

}