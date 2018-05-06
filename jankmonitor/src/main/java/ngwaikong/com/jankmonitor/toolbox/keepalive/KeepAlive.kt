package ngwaikong.com.jankmonitor.toolbox.keepalive

import android.os.Handler
import android.os.Looper
import android.util.Printer
import ngwaikong.com.jankmonitor.core.BasePrinter
import ngwaikong.com.jankmonitor.toolbox.extra.getLogging

/**
 * Created by weijiangwu on 2018/4/30.
 */
class KeepAlive(private val targetLooper: Looper, private val intervalMs: Long, private val printerDied: ((printer: Printer?) -> Unit?)?) {
    private val mHandler: Handler = Handler(targetLooper)
    private val mRunnable = object : Runnable {
        override fun run() {
            val isAliveInfo = heartbeatInner()
            if (!isAliveInfo.isAlive) {
                printerDied?.invoke(isAliveInfo.printer)
            }
            mHandler.postDelayed(this, intervalMs)
        }
    }

    fun isAlive(): Boolean {
        val reflectLogging = targetLooper.getLogging()
        return reflectLogging != null && reflectLogging is BasePrinter
    }

    fun heartbeat() {
        mHandler.post { mRunnable }
    }

    private fun heartbeatInner(): AliveInfo {
        val reflectLogging = targetLooper.getLogging()
        return AliveInfo((reflectLogging != null && reflectLogging is BasePrinter), reflectLogging)
    }

    data class AliveInfo(val isAlive: Boolean, val printer: Printer?)
}