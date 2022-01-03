package wee.digital.sample.ui.fragment.dialog.web

import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewbinding.ViewBinding
import wee.digital.sample.databinding.WebBinding
import wee.digital.sample.ui.main.MainDialogFragment
import wee.digital.widget.extension.editConstraint

class WebFragment : MainDialogFragment<WebBinding>() {

    private val arg get() = dialogVM.webLiveData.value

    private var webView: WebView? = null

    /**
     * [MainDialogFragment] implements
     */
    override fun inflating(): (LayoutInflater) -> ViewBinding {
        return WebBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(vb.viewClose)

    }

    override fun onLiveDataObserve() {
        dialogVM.webLiveData.observe {
            if (it != null) {
                onBindArg(it)
            }
        }
    }

    override fun onViewClick(v: View?) {
        dismiss()
    }

    override fun onDetach() {
        super.onDetach()
        arg?.also { onBindDismiss(it) }
        dialogVM.webLiveData.value = null
    }

    override fun onBackPressed() {
        clickedView = vb.viewClose
        super.onBackPressed()
    }

    /**
     *
     */
    private fun onBindArg(it: WebArg) {
        //showProgress
        vb.viewClose.isShow(it.dismissWhenTouchOutside)
        vb.viewClose.addViewClickListener { onBackPressed() }
        if (it.dismissWhenTouchOutside) {
            vb.layoutContent.addViewClickListener { onBackPressed() }
        }
        vb.textViewTitle.text = it.title
        loadUrl(it.url)
    }

    private fun onBindDismiss(it: WebArg) {
        when (clickedView) {
            vb.viewClose -> it.onCloseClick?.invoke()
        }
        it.onDismiss?.invoke()
    }

    private fun loadUrl(url: String?) {
        if (url.isNullOrEmpty()) return
        if (webView == null) {
            webView = initWebView()
        }
        webView?.loadUrl(url)
    }

    private fun initWebView(): WebView {
        val v = WebView(requireContext())
        v.id = View.generateViewId()
        v.overScrollMode = WebView.OVER_SCROLL_NEVER
        v.isVerticalScrollBarEnabled = false
        v.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        v.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        v.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            defaultFixedFontSize = 14
            defaultFontSize = 14
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
        v.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    //hideProgress
                }
            }
        }
        /*v.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, request)
            }
        }*/
        vb.layoutBottom.addView(v, 0)
        vb.layoutBottom.editConstraint {
            constrainDefaultWidth(v.id, ConstraintSet.MATCH_CONSTRAINT)
            constrainWidth(v.id, ConstraintSet.MATCH_CONSTRAINT)
            constrainDefaultHeight(v.id, ConstraintSet.MATCH_CONSTRAINT)
            constrainHeight(v.id, ConstraintSet.MATCH_CONSTRAINT)
            connect(v.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(v.id, ConstraintSet.TOP, vb.textViewTitle.id, ConstraintSet.BOTTOM)
            connect(v.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            connect(v.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        }
        return v
    }

}

