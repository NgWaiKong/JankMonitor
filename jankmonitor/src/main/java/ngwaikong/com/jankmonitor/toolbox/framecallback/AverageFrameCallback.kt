package ngwaikong.com.jankmonitor.toolbox.framecallback

import ngwaikong.com.jankmonitor.core.BaseFrameCallback
import ngwaikong.com.jankmonitor.toolbox.extra.nsToMs

/**
 * Created by weijiangwu on 2018/3/25.
 */
class AverageFrameCallback : BaseFrameCallback() {

    private val mFrameTimeList: ArrayList<Long> = ArrayList(100)
    private var mOnAverageFunc: ((average: Int, frameList: ArrayList<Long>) -> Unit)? = null

    fun setOnAverageListener(onAverageFunc: (average: Int, frameList: ArrayList<Long>) -> Unit) {
        this.mOnAverageFunc = onAverageFunc
    }

    override fun onDoFrame(frameTimeNanos: Long) {
        mFrameTimeList.add(frameTimeNanos)
    }

    override fun stop() {
        super.stop()
        val average = calcAverage(mFrameTimeList)
        mOnAverageFunc?.invoke(average, ArrayList(mFrameTimeList))
        mFrameTimeList.clear()
    }

    fun getAverage(): Int {
        return if (mFrameTimeList.size < 2) {
            -1
        } else {
            calcAverage(mFrameTimeList)
        }
    }

    private fun calcAverage(frameList: ArrayList<Long>): Int {
        val interval = (frameList.last() - frameList.first()).nsToMs()
        return ((frameList.size - 1) * 1000f / interval).toInt()
    }

}