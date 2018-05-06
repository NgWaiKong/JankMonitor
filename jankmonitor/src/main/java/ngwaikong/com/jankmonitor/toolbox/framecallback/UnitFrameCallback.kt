package ngwaikong.com.jankmonitor.toolbox.framecallback

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import ngwaikong.com.jankmonitor.core.BaseFrameCallback
import ngwaikong.com.jankmonitor.toolbox.extra.Config
import ngwaikong.com.jankmonitor.toolbox.extra.SampleThreadUtil

/**
 * Created by weijiangwu on 2018/5/4.
 */
class UnitFrameCallback(private val mThresholdInMs: Long = 1000L) : BaseFrameCallback() {

    companion object {
        private const val UNIT_FRAME_THREAD = "unit_frame_thread"
        private const val HANDLE_MSG = 0
    }

    private val mFrameTimeList = ArrayList<Long>()
    private var mOnUnitFrameFunc: ((unitFrame: Int, frameList: ArrayList<Long>) -> Unit?)? = null

    private var mUnitThread: HandlerThread? = null
    private var mUnitHandler: Handler? = null

    fun setOnUnitFrameFunc(onDoFrameListener: ((unitFrame: Int, frameList: ArrayList<Long>) -> Unit?)) {
        mOnUnitFrameFunc = onDoFrameListener
    }

    override fun onDoFrame(frameTimeNanos: Long) {
        mFrameTimeList.add(frameTimeNanos)
    }

    override fun start() {
        super.start()
        mFrameTimeList.clear()
        initHandlerThread()
    }

    override fun stop() {
        super.stop()
        mFrameTimeList.clear()
        clearHandlerThread()
    }

    private fun notifyListener() {
        mOnUnitFrameFunc?.invoke(mFrameTimeList.size, ArrayList(mFrameTimeList))
        mFrameTimeList.clear()
        mUnitHandler?.sendEmptyMessageDelayed(HANDLE_MSG, mThresholdInMs)
    }

    private fun initHandlerThread() {
        mUnitThread = SampleThreadUtil.newHandlerThread(Config.JM_TAG + UNIT_FRAME_THREAD)
        mUnitThread?.start()
        mUnitHandler = object : Handler(mUnitThread?.looper) {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when (msg?.what) {
                    HANDLE_MSG -> notifyListener()
                }
            }
        }
        mUnitHandler?.sendEmptyMessageDelayed(HANDLE_MSG, mThresholdInMs)
    }

    private fun clearHandlerThread() {
        mUnitHandler?.removeCallbacksAndMessages(null)
        mUnitThread?.quitSafely()
    }

}