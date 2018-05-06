package ngwaikong.com.jankmonitor

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ngwaikong.com.jankmonitor.toolbox.extra.Config
import ngwaikong.com.jankmonitor.toolbox.printer.SimplePrinter


/**
 * Created by weijiangwu on 2018/2/5.
 */
class TestSimplePrinter {
    private lateinit var simplePrinter: SimplePrinter
    private lateinit var dispatchingLog: String
    private var count: Int = 0
    @Before
    fun init() {
        simplePrinter = SimplePrinter({ dispatchingTime, dispatchingLog ->
            this.dispatchingLog = dispatchingLog
            println("dispatchingTime =  $dispatchingTime , log:$dispatchingLog")
        }, { dispatchingLog, finishedLog, count, costTime ->
            this.count = count
            println("start = $dispatchingLog,log = $finishedLog,count = $count,time = $costTime")
        })
    }


    @Test
    fun testDispatchPrint() {
        dispatchAssert(Config.PRINTER_DISPATCHING + " test method")
        dispatchAssert(Config.PRINTER_DISPATCHING + " method")
    }

    @Test
    fun testFinishedPrint() {
        finishedAssert(Config.PRINTER_FINISHED + " test method", 1)
        finishedAssert(Config.PRINTER_FINISHED + " method", 2)
    }


    private fun dispatchAssert(log: String) {
        simplePrinter.println(log)
        assertEquals(this.dispatchingLog == log, true)
    }


    private fun finishedAssert(log: String, count: Int) {
        simplePrinter.println(log)
        assertEquals(this.count == count, true)
    }

}