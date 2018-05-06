package ngwaikong.com.jankmonitor

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ngwaikong.com.jankmonitor.toolbox.framecallback.DropFrameCallback
import qqmusic.tencent.com.jankmonitor.toolbox.framecallback.SamplingFrameCallback


/**
 * Created by weijiangwu on 2018/2/5.
 */
class TestDropFrameCallback{
    private lateinit var mSamplingCallback: DropFrameCallback
    private var threshold: Long = 0
    private var thresholdCount: Long = 0
    @Before
    fun init() {
        mSamplingCallback = DropFrameCallback()
        threshold = 16 * 1000000
        thresholdCount = 16
    }


    @Test
    fun testNotifyDropFrames() {
        notifyDropFramesAssert(0, 0, 0)
        notifyDropFramesAssert(1, 0, 1)
        notifyDropFramesAssert(2, 1, 2)
        notifyDropFramesAssert(10, 9, 10)
    }


    @Test
    fun testCalcSmoothness() {
        calcSmoothnessAssert(161, 10)
        calcSmoothnessAssert(1, 0)
        calcSmoothnessAssert(18, 10)
    }


    @Test
    fun testNeedSample() {
        needSampleAssert(1000, threshold, true)
        needSampleAssert(15, threshold, false)
        needSampleAssert(16, threshold, true)
    }

    @Test
    fun testCountDroppedFrames() {
        countDroppedFramesAssert(1600, thresholdCount, 100)
        countDroppedFramesAssert(16, thresholdCount, 1)
        countDroppedFramesAssert(10, thresholdCount, 0)
    }

    private fun notifyDropFramesAssert(size: Int, testPre: Int, testPost: Int) {
        val testList: ArrayList<Int> = ArrayList()
        testList += 0..size
        mSamplingCallback.notifyDropFrames(testList) { pre, post, _ ->
            assertEquals(pre, testPre)
            assertEquals(post, testPost)
        }
    }

    private fun calcSmoothnessAssert(threshold: Long, except: Int) {
        val mFrameTimeList = getTestList(threshold)
        val dropFrameList: ArrayList<Int> = ArrayList()
        mSamplingCallback.calcSmoothness(mFrameTimeList, dropFrameList)
        assertEquals(Math.abs(dropFrameList.size - except) <= 1, true)
    }

    private fun countDroppedFramesAssert(sleep: Long, threshold: Long, except: Int) {
        val longer = getNanoTime()
        Thread.sleep(sleep)
        val needSample = mSamplingCallback.countDroppedFrames(longer, getNanoTime(), threshold)
        assertEquals(Math.abs(needSample - except) <= 1, true)
    }


    private fun needSampleAssert(sleep: Long, threshold: Long, except: Boolean) {
        val longer = getNanoTime()
        Thread.sleep(sleep)
        val needSample = mSamplingCallback.needSample(getNanoTime(), longer, threshold)
        assertEquals(needSample, except)
    }


    private fun getNanoTime(): Long {
        return System.nanoTime()
    }

    private fun getTestList(threshold: Long): ArrayList<Long> {
        val mFrameTimeList: ArrayList<Long> = ArrayList()
        for (index in 0..10) {
            Thread.sleep(threshold)
            mFrameTimeList.add(System.nanoTime())
        }
        return mFrameTimeList
    }

}