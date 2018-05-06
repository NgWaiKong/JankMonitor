package ngwaikong.com.jankmonitor

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import qqmusic.tencent.com.jankmonitor.toolbox.framecallback.SamplingAverageCallback

/**
 * Created by weijiangwu on 2018/2/5.
 */
class TestSamplingAverageCallback {
    private lateinit var mAverageCallback: SamplingAverageCallback

    @Before
    fun init() {
        mAverageCallback = SamplingAverageCallback()
    }

    @Test
    fun testNeedSample() {
        calcAverageAssert(17, 58)
        calcAverageAssert(33, 30)

    }

    private fun calcAverageAssert(threshold: Long, except: Int) {
        val testList: ArrayList<Long> = ArrayList()
        for (i in 0..30) {
            val longer = getNanoTime()
            testList.add(longer)
            Thread.sleep(threshold)
        }

        val ret = mAverageCallback.calcAverage(testList)
        Assert.assertEquals(ret, except)
    }

    private fun getNanoTime(): Long {
        return System.nanoTime()
    }

}