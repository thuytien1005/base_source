package wee.digital.camera.util

import wee.digital.camera.log

class TimeLogger(val name: String, var enable: Boolean) {

    private var startTime: Long = System.currentTimeMillis()
    var duration: Long = 0

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun end() {
        duration = System.currentTimeMillis() - startTime
    }

    fun print() {
        end()
        if (enable) {
            log.d("${name}: $duration ms")
        }
    }
}