package wee.digital.sample.widget.attach

import android.view.ViewStub

class Stub<T> {

    private var viewStub: ViewStub?
    private var view: T? = null

    constructor(viewStub: ViewStub?){
        this.viewStub = viewStub
    }

    fun get(): T? {
        if (view == null) {
            view = viewStub!!.inflate() as T?
            viewStub = null
        }
        return view
    }

    fun resolved(): Boolean {
        return view != null
    }

}
