package ngwaikong.com.jankmonitor.toolbox.sampler

import android.os.Handler
import ngwaikong.com.jankmonitor.toolbox.extra.SampleThreadUtil
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by weijiangwu on 2018/3/26.
 */
abstract class BaseSampler {

    private val mNeedSample: AtomicBoolean = AtomicBoolean(false)

    open val mHandler: Handler = SampleThreadUtil.sWorkerHandler
    open val mRunnable = object : Runnable {
        override fun run() {
            onDoSample()

            if (!mNeedSample.get()) {
                return
            }
            SampleThreadUtil.sWorkerHandler.postDelayed(this, mSampleInterval)
        }
    }

    abstract fun onDoSample()
    abstract var mSampleInterval: Long
    abstract var mStartDelay: Long

    open fun start() {
        if (mNeedSample.get()) return
        mNeedSample.set(true)

        mHandler.removeCallbacks(mRunnable)
        mHandler.postDelayed(mRunnable, mStartDelay)
    }

    open fun stop() {
        if (!mNeedSample.get()) return
        mNeedSample.set(false)

        mHandler.removeCallbacks(mRunnable)
    }

    fun isRunning(): Boolean {
        return mNeedSample.get()
    }

}