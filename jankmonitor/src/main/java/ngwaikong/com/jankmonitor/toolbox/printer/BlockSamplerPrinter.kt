package ngwaikong.com.jankmonitor.toolbox.printer

import android.os.Debug
import android.os.SystemClock
import android.util.Printer

/**
 * Created by weijiangwu on 2018/3/27.
 */
class BlockSamplerPrinter(private val mBlockThresholdMillis: Long,
                          private val mStopWhenDebugging: Boolean,
                          private val blockListener: BlockListener) : Printer {
    private var mStartTimestamp: Long = 0
    private var mStartThreadTimestamp: Long = 0
    private var mPrintingStarted = false

    override fun println(log: String?) {
        if (log == null) return

        if (mStopWhenDebugging && Debug.isDebuggerConnected()) return

        if (!mPrintingStarted) {
            mStartTimestamp = System.currentTimeMillis()
            mStartThreadTimestamp = SystemClock.currentThreadTimeMillis()
            mPrintingStarted = true
            blockListener.startDump()
        } else {
            val endTimestamp = System.currentTimeMillis()
            val endThreadTimestamp = SystemClock.currentThreadTimeMillis()
            mPrintingStarted = false
            if (isBlock(endTimestamp)) {
                blockListener.notifyBlockEvent(mStartTimestamp, endTimestamp, mStartThreadTimestamp, endThreadTimestamp)
            }
            blockListener.stopDump()
        }
    }

    private fun isBlock(endTime: Long): Boolean {
        return endTime - mStartTimestamp > mBlockThresholdMillis
    }

    interface BlockListener {
        fun startDump()
        fun stopDump()
        fun notifyBlockEvent(startTimestamp: Long, endTime: Long, startThreadTimestamp: Long, endThreadTime: Long)
    }
}
