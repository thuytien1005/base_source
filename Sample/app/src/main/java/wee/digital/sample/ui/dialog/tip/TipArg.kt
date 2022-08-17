package wee.digital.sample.ui.dialog.tip

import android.graphics.Bitmap
import android.graphics.Point
import android.view.View
import wee.digital.widget.extension.getBitmap
import wee.digital.widget.extension.getLocation

class TipArg {

    var title: String = ""

    var content: String = ""

    var iconWidth = 0

    var iconHeight = 0

    var image: Bitmap? = null

    var point: Point = Point()

    constructor(v: View) {
        iconWidth = v.width
        iconHeight = v.height
        image = v.getBitmap()
        point = v.getLocation()
    }

    companion object {
        fun sample(v: View) = TipArg(v).also {
            it.title = "Title"
            it.content = "Description"
        }
    }

}