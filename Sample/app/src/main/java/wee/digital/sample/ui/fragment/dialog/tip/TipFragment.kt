package wee.digital.sample.ui.fragment.dialog.tip

import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import wee.digital.sample.R
import wee.digital.sample.databinding.TipBinding
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.main.MainDialogFragment
import wee.digital.widget.extension.animationFadeIn
import wee.digital.widget.extension.beginTransition

class TipFragment : MainDialogFragment<TipBinding>() {

    /**
     * [MainDialogFragment] implements
     */
    override fun getTheme(): Int {
        return R.style.App_Dialog_FullScreen
    }

    override fun inflating(): Inflating = TipBinding::inflate

    override fun onViewCreated() {
        dialog?.window?.also { it.attributes.windowAnimations = R.style.App_DialogAnim_Alpha }
    }

    override fun onLiveDataObserve() {
        dialogVM.tipViewLiveData.observe {
            if (it != null) {
                onBindArg(it)
            } else {
                dismiss()
            }
        }
    }

    private fun onBindArg(it: TipArg) {
        vb.title.text = it.title
        vb.content.text = it.content
        vb.image.x = it.point.x.toFloat()
        vb.image.y = it.point.y.toFloat()
        vb.image.setImageBitmap(it.image)
        animationFadeIn(600, vb.title, vb.content, vb.image)
        ChangeBounds().apply { duration = 300 }.beginTransition(vb.tipView) {
            constrainDefaultHeight(vb.image.id, it.iconHeight)
            constrainDefaultWidth(vb.image.id, it.iconWidth)

            val roundedId = vb.viewRounded.id
            this.clear(roundedId, ConstraintSet.BOTTOM)
            this.clear(roundedId, ConstraintSet.START)
            this.connect(roundedId, ConstraintSet.START, vb.guide1.id, ConstraintSet.START)
            this.connect(roundedId, ConstraintSet.BOTTOM, vb.guide3.id, ConstraintSet.BOTTOM)
        }
    }

}