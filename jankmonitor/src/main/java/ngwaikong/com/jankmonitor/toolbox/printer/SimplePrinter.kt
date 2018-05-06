package ngwaikong.com.jankmonitor.toolbox.printer

import android.util.Printer
import ngwaikong.com.jankmonitor.toolbox.extra.Config

/**
 * Created by weijiangwu on 2018/3/27.
 */
class SimplePrinter(private val onDispatchingFunc: ((dispatchingTime: Long, dispatchingLog: String) -> Unit)?,
                    private val onFinishedFunc: ((dispatchingLog: String, finishedLog: String, count: Int, costTime: Long) -> Unit)?) : Printer {
    private var count = 0
    private var mLastTime: Long = 0
    private var mLastStr: String = ""

    override fun println(log: String?) {
        if (log == null) return
        if (log.isNullOrEmpty()) return

        if (log.contains(Config.PRINTER_DISPATCHING)) {
            mLastTime = System.currentTimeMillis()
            mLastStr = log

            onDispatchingFunc?.invoke(mLastTime, mLastStr)

        } else if (log.contains(Config.PRINTER_FINISHED)) {
            count++
            var cost = System.currentTimeMillis() - mLastTime
            if (mLastTime == 0L) {
                cost = -1
            }

            onFinishedFunc?.invoke(mLastStr, log, count, cost)
        }
    }
}


