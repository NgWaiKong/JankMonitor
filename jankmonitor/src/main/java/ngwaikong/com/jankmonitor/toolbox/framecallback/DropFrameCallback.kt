package ngwaikong.com.jankmonitor.toolbox.framecallback

import ngwaikong.com.jankmonitor.core.BaseFrameCallback
import ngwaikong.com.jankmonitor.toolbox.extra.Config
import ngwaikong.com.jankmonitor.toolbox.extra.dropCount
import java.util.concurrent.TimeUnit

/**
 * Created by weijiangwu on 2018/1/27.
 */

class DropFrameCallback(private val mThresholdInMs: Long) : BaseFrameCallback() {

    private val mFrameTimeList: ArrayList<Long> = ArrayList(100)

    private var mLatestFrameTime: Long = 0L
    private var mOnDropFunc: ((interval: Long, cur: Long, last: Long) -> Unit)? = null

    fun setOnDropFramesListener(onDropFunc: (interval: Long, cur: Long, last: Long) -> Unit) {
        this.mOnDropFunc = onDropFunc
    }

    override fun onDoFrame(frameTimeNanos: Long) {
        mFrameTimeList.add(frameTimeNanos)
        if (mLatestFrameTime == 0L) mLatestFrameTime = frameTimeNanos

        if (needSample(frameTimeNanos, mLatestFrameTime, mThresholdInMs)) {
            notifyDropFrames(frameTimeNanos, mLatestFrameTime, mOnDropFunc)
            mFrameTimeList.clear()
        }

        mLatestFrameTime = frameTimeNanos
    }

    override fun start() {
        super.start()
        mFrameTimeList.clear()
    }

    override fun stop() {
        super.stop()
        mFrameTimeList.clear()
    }

    fun notifyDropFrames(frameTimeNanos: Long, lastFrameTimeNanos: Long, onDropFunc: ((interval: Long, cur: Long, last: Long) -> Unit)?) {
        onDropFunc?.invoke(
                frameTimeNanos.dropCount(lastFrameTimeNanos, Config.DEVICE_REFRESH_RATE_IN_MS_FLOAT).toLong(),
                frameTimeNanos,
                lastFrameTimeNanos)
    }

    fun needSample(frameTimeNanos: Long, mLatestFrameTime: Long, threshold: Long): Boolean {
        return TimeUnit.MILLISECONDS.convert((frameTimeNanos - mLatestFrameTime), TimeUnit.NANOSECONDS) > threshold
    }
}