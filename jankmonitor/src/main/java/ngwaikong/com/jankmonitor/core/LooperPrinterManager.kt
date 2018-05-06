package ngwaikong.com.jankmonitor.core

import android.os.Looper
import android.util.Printer
import ngwaikong.com.jankmonitor.toolbox.extra.getLogging

/**
 * Created by weijiangwu on 2018/1/27.
 */
class LooperPrinterManager(private val looper: Looper) {
    private val basePrinter: BasePrinter = BasePrinter()

    fun addPrinter(printer: Printer) {
        checkCover()
        looper.setMessageLogging(addToPrinterList(printer))
    }

    fun removePrinter(printer: Printer) {
        basePrinter.removePrinter(printer)
    }

    private fun checkCover() {
        val reflectLogging: Printer? = looper.getLogging()
        coverToPrinterList(reflectLogging)
    }

    private fun coverToPrinterList(reflectLogging: Printer?) {
        if (reflectLogging == null) return
        addToPrinterList(reflectLogging)
    }

    private fun addToPrinterList(printer: Printer): Printer {
        return basePrinter.apply {
            this.addPrinter(printer)
        }
    }
}