package ngwaikong.com.jankmonitor

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ngwaikong.com.jankmonitor.toolbox.extra.clearOverSize
import ngwaikong.com.jankmonitor.toolbox.sampler.StackSampler


/**
 * Created by weijiangwu on 2018/2/5.
 */
class TestStackSampler {
    private lateinit var mStackSampler: StackSampler
    @Before
    fun init() {
        mStackSampler = StackSampler(Thread.currentThread(), 100, 300, 0)
    }


    @Test
    fun testSplit() {
        splitAssert(9, 5, 10, 4)
        splitAssert(9, 0, 10, 9)
        splitAssert(0, 0, 10, 0)
    }

    @Test
    fun testClearOverSize() {
        clearOverSizeAssert(10, 10, 10)
        clearOverSizeAssert(9, 10, 9)
        clearOverSizeAssert(8, 10, 9)
        clearOverSizeAssert(8, 0, 9)
    }


    @Test
    fun testGenerateStackTrace() {
        generateStackTraceAssert(Thread.currentThread(), "generateStackTrace")
    }


    private fun splitAssert(settingSize: Int, startTimeMs: Long, endTimeMs: Long, except: Int) {
        val map: LinkedHashMap<Long, String> = LinkedHashMap()
        for (i in 0..settingSize) {
            map[i.toLong()] = i.toString()
        }
        val list = mStackSampler.split(map, startTimeMs, endTimeMs)
        assertEquals(list.size, except)
    }

    private fun generateStackTraceAssert(thread: Thread, except: String) {
        val generateStackTrace = mStackSampler.generateStackTrace(thread)
        print(generateStackTrace)
        assertEquals(generateStackTrace.contains(except), true)
    }

    private fun clearOverSizeAssert(settingSize: Int, maxCount: Int, except: Int) {
        val map: LinkedHashMap<Long, String> = LinkedHashMap()
        for (i in 0..settingSize) {
            map[i.toLong()] = i.toString()
        }
        mStackSampler.clearOverSize(map, maxCount)

        assertEquals(map.size, except)
    }
}