package ngwaikong.com.jankmonitor.toolbox.framecallback

import ngwaikong.com.jankmonitor.core.BaseFrameCallback
import ngwaikong.com.jankmonitor.toolbox.extra.Config
import ngwaikong.com.jankmonitor.toolbox.extra.dropCount

/**
 * Created by weijiangwu on 2018/1/27.
 */

class SkipFrameCallback : BaseFrameCallback() {
    private val mFrameTimeList: ArrayList<Long> = ArrayList(100)
    private var mLastFrameTimeNs: Long = 0L
    private var mOnDoFrame: ((skipFrame: Int, frameTimeNanos: Long, lastFrameTimeNs: Long, frameList: ArrayList<Long>) -> Unit)? = null

    fun setOnDoFrameListener(onDoFrameListener: ((skipFrame: Int, frameTimeNanos: Long, lastFrameTimeNs: Long, frameList: ArrayList<Long>) -> Unit)) {
        mOnDoFrame = onDoFrameListener
    }

    override fun onDoFrame(frameTimeNanos: Long) {
        mFrameTimeList.add(frameTimeNanos)
        val skipFrame = frameTimeNanos.dropCount(mLastFrameTimeNs, Config.DEVICE_REFRESH_RATE_IN_MS_FLOAT)
        mOnDoFrame?.invoke(skipFrame, frameTimeNanos, mLastFrameTimeNs, mFrameTimeList)
        mLastFrameTimeNs = frameTimeNanos
    }

    override fun start() {
        mFrameTimeList.clear()
        super.start()
    }

    override fun stop() {
        mFrameTimeList.clear()
        super.stop()
    }

}