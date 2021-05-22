package wee.digital.sample.ui.fragment.com

import android.Manifest
import android.graphics.Bitmap
import android.os.Environment
import android.view.View
import kotlinx.android.synthetic.main.z.*
import wee.digital.library.extension.onGranted
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment
import wee.digital.widget.extension.getBitmap
import wee.digital.widget.extension.post
import java.io.File
import java.io.FileOutputStream


class ComFragment : MainFragment() {

    override fun layoutResource(): Int {
        return R.layout.z
    }

    override fun onViewCreated() {
        addClickListener(view0)
        onGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) {}
    }

    override fun onLiveDataObserve() {
        //textViewPlaceHolder.setTextColorRes(R.color.colorPrimary, R.color.colorAccent)
    }


    override fun onViewClick(v: View?) {
        when (v) {
            view0 -> {
                imageView.setImageResource(array[i])
                var bmp: Bitmap? = null
                imageView.post(1000, Runnable {
                    bmp = view0.getBitmap()
                })
                imageView.post(2000, Runnable {
                    bmp ?: return@Runnable
                    saveImage(bmp!!)
                })
                i++
            }
        }
    }

    var array = arrayOf(R.drawable.z, R.drawable.z1, R.drawable.z2, R.drawable.z3,
            R.drawable.z4, R.drawable.z5, R.drawable.z6, R.drawable.z7, R.drawable.z8, R.drawable.z9)
    var i = 0
    private fun saveImage(finalBitmap: Bitmap) {
        val root: String = Environment.getExternalStorageDirectory().toString()
        val myDir = File("/sdcard/Download")
        myDir.mkdirs()
        val name = "${System.currentTimeMillis()}.jpg"
        val file = File(myDir, name)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            toast("saved")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}