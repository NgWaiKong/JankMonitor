package ngwaikong.com.jankmonitor

import org.junit.Assert.*
import org.junit.Test
import ngwaikong.com.jankmonitor.toolbox.extra.compareWithValue

/**
 * Created by weijiangwu on 2018/2/11.
 */
class TestExtends {

    @Test
    fun testLongCompare() {

        longCompare(1000, 1000, false)
        longCompare(1000, 2000, false)
        longCompare(1000, 100, true)
    }

    private fun longCompare(sleep: Long, threshold: Long, except: Boolean) {
        val currentTimeMillis = System.currentTimeMillis()
        Thread.sleep(sleep)
        val compareWithValue = System.currentTimeMillis().compareWithValue(currentTimeMillis, threshold)
        assertEquals(compareWithValue, except)
    }


}