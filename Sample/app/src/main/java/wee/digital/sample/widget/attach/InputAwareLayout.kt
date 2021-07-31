package wee.digital.sample.widget.attach

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText

class InputAwareLayout(context: Context, attrs: AttributeSet?) :
    KeyboardAwareLinearLayout(context, attrs), KeyboardAwareLinearLayout.OnKeyboardShownListener {

    private var current: InputView? = null

    init {
        addOnKeyboardShownListener(this)
    }

    override fun onKeyboardShown() {
    }

    fun show(imeTarget: EditText, listener: InputView) {
        if (isKeyboardOpen()) {
            hideSoftKey(imeTarget) {
                hideAttachedInput(true)
                listener.show(getKeyboardHeight(), true)
                current = listener
            }
        } else {
            current?.hide(true)
            listener.show(getKeyboardHeight(), current != null)
            current = listener
        }
    }

    fun hideAttachedInput(bool: Boolean) {
        current?.hide(bool)
        current = null
    }

    fun getCurrentInput(): InputView? {
        return current
    }

    fun hideCurrentInput(imeTarget: EditText) {
        if (isKeyboardOpen()) hideSoftKey(imeTarget) { } else hideAttachedInput(false)
    }

    fun isInputOpen(): Boolean {
        return (isKeyboardOpen() || (current != null && current?.isShowing() ?: false))
    }

    fun showSoftKey(inputTarget: EditText) {
        postOnKeyboardOpen { hideAttachedInput(true) }
        inputTarget.post {
            inputTarget.requestFocus()
            context.inputMethodManager().showSoftInput(inputTarget, 0)
        }
    }

    fun hideSoftKey(inputTarget: EditText, block: () -> Unit) {
        postOnKeyboardClose { block() }
        context.inputMethodManager().hideSoftInputFromWindow(inputTarget.windowToken, 0)
    }

    interface InputView {
        fun show(height: Int, immediate: Boolean)
        fun hide(immediate: Boolean)
        fun isShowing(): Boolean
    }

}