package wee.digital.sample.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.Rect
import android.text.Editable
import android.text.InputFilter
import android.util.AttributeSet
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.android.synthetic.main.widget_text_input.view.*
import wee.digital.library.extension.*
import wee.digital.library.widget.AppCustomView
import wee.digital.sample.R
import wee.digital.sample.shared.Ui


class AppInputView : AppCustomView,
        SimpleMotionTransitionListener,
        OnFocusChangeListener,
        SimpleTextWatcher {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    override fun layoutResource(): Int {
        return R.layout.widget_text_input
    }

    override fun onInitialize(context: Context, types: TypedArray) {
        hint = types.hint
        inputEditText.setText(types.text)
        inputEditText.addTextChangedListener(this)
        onIconInitialize(inputImageViewIcon, types)
        onEditTextInitialize(inputEditText, types)
        inputViewLayout.addTransitionListener(this)
    }

    private fun onIconInitialize(it: AppCompatImageView, types: TypedArray) {
        val color = types.getColor(R.styleable.CustomView_android_tint, -1)
        if (color != -1) {
            it.setColorFilter(color)
        }
        src = types.srcRes
    }

    private fun onEditTextInitialize(it: AppCompatEditText, types: TypedArray) {
        it.onFocusChangeListener = this
        it.paintFlags = it.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

        it.maxLines = 1
        it.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(256))

        // Text filter
        val sFilters = arrayListOf<InputFilter>()

        val textAllCaps = types.getBoolean(R.styleable.CustomView_android_textAllCaps, false)
        if (textAllCaps) sFilters.add(InputFilter.AllCaps())

        val sMaxLength = types.getInt(R.styleable.CustomView_android_maxLength, 256)
        sFilters.add(InputFilter.LengthFilter(sMaxLength))

        val array = arrayOfNulls<InputFilter>(sFilters.size)
        it.filters = sFilters.toArray(array)

        // Input type
        val customInputType = types.getInt(R.styleable.CustomView_android_inputType, EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        if (customInputType == EditorInfo.TYPE_NULL) {
            disableFocus()
        } else {
            it.inputType = customInputType or
                    EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS or
                    EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or
                    EditorInfo.TYPE_TEXT_VARIATION_FILTER or
                    EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
        }

        it.maxLines = types.getInt(R.styleable.CustomView_android_maxLines, 1)

        // Ime option
        val imeOption = types.getInt(R.styleable.CustomView_android_imeOptions, -1)
        if (imeOption != -1) it.imeOptions = imeOption

        it.privateImeOptions = "nm,com.google.android.inputmethod.latin.noMicrophoneKey"

        // Gesture
        it.setOnLongClickListener {
            return@setOnLongClickListener true
        }
        it.setTextIsSelectable(false)
        it.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false
            override fun onDestroyActionMode(mode: ActionMode) {}
            override fun onCreateActionMode(mode: ActionMode, menu: Menu) = false
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = false
        }
        it.isLongClickable = false
        it.setOnCreateContextMenuListener { menu, _, _ -> menu.clear() }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        if (null == listener) {
            enableFocus()
            editText.addViewClickListener(null)
        } else {
            disableFocus()
            editText.addViewClickListener {
                listener.onClick(this)
            }
        }
    }

    override fun performClick(): Boolean {
        return editText.performClick()
    }

    override fun onDetachedFromWindow() {
        inputViewLayout.clearAnimation()
        onFocusChange.clear()
        super.onDetachedFromWindow()
    }

    /**
     * [AppInputView] properties
     */
    private val editText: EditText get() = inputEditText

    var text: String?
        get() {
            val s = editText.text?.toString()?.trimIndent()?.trim()?.replace("\\s+".toRegex(), " ")
            isSilent = true
            editText.setText(s)
            if (hasFocus()) {
                editText.setSelection(s?.length ?: 0)
            }
            isSilent = false
            return s
        }
        set(value) {
            isSilent = true
            editText.setText(value)
            error = null
            onFocusChange(null, isFocused)
            isSilent = false
        }

    val trimText: String
        get() {
            return editText.trimText
        }

    var hint: String?
        get() = inputTextViewHint.text?.toString()
        set(value) {
            inputTextViewHint.text = value
        }

    var error: String?
        get() = inputTextViewError.text?.toString()
        set(value) {
            inputTextViewError.text = value
            if (value.isNullOrEmpty()) {
                updateUiOnFocusChanged()
            } else {
                setBorderColor(R.color.colorInputError)
                setIconColor(R.color.colorInputError)
            }
        }

    @DrawableRes
    var src: Int = 0
        set(value) {
            val isGone = value <= 0
            inputImageViewIcon.isGone(isGone)
            inputImageViewIcon.setImageResource(value)
        }

    var isSilent: Boolean = false

    val isTextEmpty: Boolean get() = text.isNullOrEmpty()

    val hasError: Boolean get() = !error.isNullOrEmpty()


    /**
     * [OnFocusChangeListener] implements
     */
    private val onFocusChange = mutableListOf<(Boolean) -> Unit>()

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        onFocusChange.iterator().forEach {
            it(hasFocus)
        }
        updateUiOnFocusChanged(hasFocus)
    }

    override fun hasFocusable(): Boolean {
        return false
    }

    override fun isFocused(): Boolean {
        return false
    }

    override fun hasFocus(): Boolean {
        return false
    }

    override fun clearFocus() {
        editText.clearFocus()
        hideKeyboard()
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        editText.post {
            if (!editText.isFocused) {
                editText.requestFocus(FOCUS_DOWN)
                showKeyboard()
            }
        }
        return true
    }

    fun addOnFocusChangeListener(block: (Boolean) -> Unit) {
        onFocusChange.add(block)
    }

    var onTextChanged: () -> Unit = { }

    fun addActionDoneListener(block: (String?) -> Unit) {
        editText.addActionDoneListener(block)
    }

    fun addActionNextListener(block: (String?) -> Unit) {
        editText.addActionNextListener(block)
    }

    fun disableFocus() {
        editText.also {
            it.isFocusable = false
            it.isCursorVisible = false
        }
    }

    fun enableFocus() {
        editText.also {
            it.isFocusable = true
            it.isCursorVisible = true
        }
    }

    fun clear() {
        editText.text = null
        error = null
        setBorderColor(R.color.colorInputDefault)
        setInputBackground(R.drawable.drw_app_input_bg)
        inputTextViewHint.textColor(R.color.colorInputUnfocused)
        inputTextViewHint.background = null
    }

    /**
     * Util
     */
    fun showKeyboard() {
        editText.post {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    fun hideKeyboard() {
        post {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

    fun addDateWatcher() {
        addOnFocusChangeListener {
            editText.hint = if (it) {
                "day/month/year"
            } else {
                null
            }
        }
        editText.addDateWatcher()
    }

    /**
     * [SimpleTextWatcher] implements
     */
    override fun afterTextChanged(s: Editable?) {
        onTextChanged()
        when {
            isSilent -> {
                return
            }
            hasError -> {
                error = null
                updateUiOnFocusChanged()
            }
        }
    }

    /**
     * ui state on focus change, error change, text change
     */
    fun updateUiOnFocusChanged(hasFocus: Boolean = editText.hasFocus()) {
        when {
            //
            hasFocus -> {
                Ui.currentFocus = this
                if (editText.isFocusable) {
                    editText.select()
                    showKeyboard()
                }
                setInputBackground(0)
                setHintBackground(R.color.colorWhite)
                setMotionState(R.id.focused)
                if (error.isNullOrEmpty()) {
                    setBorderColor(R.color.colorInputFocused)
                    setIconColor(R.color.colorInputFocused)
                    setInputBackground(0)
                }
            }
            //
            !hasFocus && text.isNullOrEmpty() -> {
                setInputBackground(R.color.colorInputDefault)
                setHintBackground(R.color.colorInputDefault)
                setMotionState(R.id.unfocused)
                if (error.isNullOrEmpty()) {
                    setBorderColor(R.color.colorInputDefault)
                    setIconColor(R.color.colorInputFocused)
                    setInputBackground(R.drawable.drw_app_input_bg)
                }
            }
            //
            !hasFocus && !text.isNullOrEmpty() -> {
                setInputBackground(0)
                setHintBackground(R.color.colorWhite)
                setMotionState(R.id.focused)
                if (error.isNullOrEmpty()) {
                    setBorderColor(R.color.colorInputUnfocused)
                    setIconColor(R.color.colorInputUnfocused)
                    setInputBackground(0)
                }
            }
        }
    }

    private fun setMotionState(id: Int) {
        inputViewLayout.transitionToState(id)
    }

    private fun setInputBackground(@DrawableRes res: Int) {
        inputViewBackground.setBackgroundResource(res)
    }

    private fun setBorderColor(@ColorRes res: Int) {
        inputEditText.backgroundTintRes(res)
    }

    private fun setIconColor(@ColorRes res: Int) {
        inputImageViewIcon.tintRes(res)
    }

    private fun setHintBackground(@ColorRes res: Int) {
        inputTextViewHint.setBackgroundResource(res)
    }

}