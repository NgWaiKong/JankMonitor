package ngwaikong.com.jankmonitor.core

import android.view.Choreographer

/**
 * Created by weijiangwu on 2018/1/27.
 */
abstract class BaseFrameCallback : Choreographer.FrameCallback {
    private val mChoreographer: Choreographer = Choreographer.getInstance()
    private var mStarted: Boolean = false

    override fun doFrame(frameTimeNanos: Long) {
        if (!mStarted) return
        onDoFrame(frameTimeNanos)
        mChoreographer.postFrameCallback(this)
    }

    open fun start() {
        mStarted = true
        mChoreographer.postFrameCallback(this)
    }

    open fun stop() {
        mStarted = false
        mChoreographer.removeFrameCallback(this)
    }

    abstract fun onDoFrame(frameTimeNanos: Long)

}