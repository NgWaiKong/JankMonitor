package ngwaikong.com.jankmonitor

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ngwaikong.com.jankmonitor.toolbox.extra.Config
import qqmusic.tencent.com.jankmonitor.toolbox.printer.BlockListener
import ngwaikong.com.jankmonitor.toolbox.printer.BlockSamplerPrinter


/**
 * Created by weijiangwu on 2018/2/5.
 */
class TestBlockSamplerPrinter {
    private lateinit var blockSamplerPrinter: BlockSamplerPrinter
    private var notify: Boolean = false
    @Before
    fun init() {
        blockSamplerPrinter = BlockSamplerPrinter(Config.DEFAULT_SAMPLE_INTERVAL, true, object : BlockListener {


            override fun startDump() {
                println("startDump")
            }

            override fun stopDump() {
                println("stopDump")
            }

            override fun notifyBlockEvent(startTimestamp: Long, endTime: Long, startThreadTimestamp: Long, endThreadTime: Long) {
                notify = true
                println("startTimestamp: $startTimestamp ,endTime: $endTime , startThreadTimestamp: $startThreadTimestamp,endThreadTime:$endThreadTime")
            }

        })
    }

    @Test
    fun testDispatchPrint() {
        dispatchAssert("", 400, true)
        dispatchAssert("", 300, false)
        dispatchAssert("", 100, false)
    }

    @Test
    fun testFinishedPrint() {
        finishedAssert(Config.PRINTER_FINISHED + " test method", 1)
        finishedAssert(Config.PRINTER_FINISHED + " method", 2)
    }


    private fun dispatchAssert(log: String, sleep: Long, except: Boolean) {
        blockSamplerPrinter.println(log)
        Thread.sleep(sleep)
        blockSamplerPrinter.println(log)
        assertEquals(this.notify, except)
        notify = false
    }


    private fun finishedAssert(log: String, count: Int) {
        blockSamplerPrinter.println(log)
    }

}