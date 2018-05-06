package ngwaikong.com.jankmonitor

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ngwaikong.com.jankmonitor.toolbox.sampler.CpuSampler
import java.io.BufferedReader
import java.io.StringReader


/**
 * Created by weijiangwu on 2018/2/5.
 */
class TestCpuSampler {
    private lateinit var mCpuSampler: CpuSampler
    @Before
    fun init() {
        mCpuSampler = CpuSampler(300 * 1.2f.toInt(), 100, 300, 0)
    }


    @Test
    fun testJoin() {
        joinAssert(10, "10")
    }

    @Test
    fun testRead() {
        val reader = StringReader("")
        val ret = readContent(BufferedReader(reader))
        assertEquals(ret, "")
    }

    @Test
    fun testIsOverInterval() {
        isOverIntervalAssert(10, 1, 1, 1, 1, false)
        isOverIntervalAssert(10, 3, 1, 5, 1, true)
    }

    private fun joinAssert(index: Int, except: String) {
        val map: LinkedHashMap<Long, String> = LinkedHashMap()
        for (i in 0..index) {
            map[i.toLong()] = i.toString()
        }

        val readContent = mCpuSampler.join(map)
        assertEquals(readContent.contains(except), true)
    }

    private fun isOverIntervalAssert(index: Int, step: Int, start: Long, interval: Long, threshold: Int, except: Boolean) {
        val map: LinkedHashMap<Long, String> = LinkedHashMap()
        for (i in 1..index step step) {
            map[i.toLong()] = i.toString()
        }
        val result = mCpuSampler.isOverInterval(start, interval, map, threshold)
        assertEquals(result, except)
    }

    private fun readContent(reader: BufferedReader): String {
        return reader.use {
            val content = it.readLine()
            if (content.isNullOrEmpty()) "" else content
        }
    }
}